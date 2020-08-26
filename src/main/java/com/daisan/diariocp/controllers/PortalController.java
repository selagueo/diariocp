package com.daisan.diariocp.controllers;

import com.daisan.diariocp.entities.Article;
import com.daisan.diariocp.entities.Photo;
import com.daisan.diariocp.entities.Usuario;

import com.daisan.diariocp.enums.UsuarioTag;

import com.daisan.diariocp.errors.ErrorService;
import com.daisan.diariocp.repositories.ArticleRepository;
import com.daisan.diariocp.services.ArticleServices;
import com.daisan.diariocp.services.PhotoServices;
import com.daisan.diariocp.services.UsuarioServices;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

@Controller
@RequestMapping("/")
public class PortalController {

    @Autowired
    private UsuarioServices userService;
    @Autowired
    private ArticleServices articleService;
    @Autowired
    private ArticleRepository articleRepo;
    @Autowired
    private PhotoServices photoService;

    @GetMapping({"/", "{user}"})
    public String userProfile(Model modelo, @PathVariable(required = false) String user) throws ErrorService{
        //CREAR Admin
        //userService.AddAdmin("admin","Admin", "Istrador", "1234567", "admin@daisansf.com");
        
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(true);
        Usuario usuarioS = (Usuario)session.getAttribute("userSession");
        
        if (user != null) {
            Usuario usuario = userService.getUsuarioByUsername(user);
            if (usuario != null) {
                if(usuarioS == null){
                    modelo.addAttribute("inner", "Perfil de "+usuario.getName()+" "+usuario.getLastName());
                    modelo.addAttribute("usuario", usuario);
                }
                else if(!usuario.getId().equals(usuarioS.getId())){
                    modelo.addAttribute("inner", "Perfil de "+usuario.getName()+" "+usuario.getLastName());
                    modelo.addAttribute("usuario", usuario);
                }
                
                return "profile.html";
            }
        }
        return "index.html";
    }

    @GetMapping({"/logout"})
    public String userLogOut() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(true);
        session.removeAttribute("userSession");
        return "index.html";
    }
    
    @GetMapping("/inner-page")
    public String innerPage() throws ErrorService {
        return "inner-page.html";
    }

    @GetMapping("/login")
    public String login(Model modelo) {
        modelo.addAttribute("inner", "Inicia Sesión");
        return "login.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_EDITOR')")
    @GetMapping("/profile")
    public String profile() {
        return "/profile.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/admin_panel")
    public String admin_panel(Model modelo) {
        modelo.addAttribute("inner", "Panel de Administración");
        modelo.addAttribute("usersByTag", userService.LoadUsuariosByTag(UsuarioTag.EDITOR));
        return "admin_panel.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/register")
    public String register(Model modelo) {
        modelo.addAttribute("inner", "Registrar Nuevo Editor");
        return "register.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/adduser")
    public String adduser(@RequestParam String name, @RequestParam String lastName, @RequestParam String email, @RequestParam String password1, @RequestParam String password2) {
        if (password1.equals(password2)) {
            try {
                userService.AddUser(name, lastName, password1, email);
            } catch (ErrorService ex) {
                System.out.println(ex.getMessage());
            }
        } else {
            System.out.println("passwords must be equals!");
        }

        return "register_success.html";
    }

    @GetMapping("/createArticle")
    public String createArticle(Model modelo) {
        modelo.addAttribute("inner", "Crear Noticia");
        return "createArticle.html";
    }
    
    @PreAuthorize("hasAnyRole('ROLE_EDITOR')")
    @PostMapping("/addArticle")
    public String addArticle(@RequestParam String title, @RequestParam String synthesis,
            @RequestParam String content, @RequestParam String tags,
            @RequestParam MultipartFile photo, @RequestParam String category) {
        
        String imgName = photo.getOriginalFilename();

        try {
            articleService.AddPost(title, synthesis, content, tags, photo, category);

        } catch (ErrorService ex) {
            System.out.println(ex.getMessage());
        }

        return "createArticle.html";
    }
    
    @GetMapping("/showArticleFromCategory")
    public String showArticleFromCategory(Model model, @RequestParam String category) throws UnsupportedEncodingException{
        
        List<String> photos = new ArrayList();
        List<String> photosMime = new ArrayList();
       
        for(Article article : articleRepo.GetPostFromCategory(articleService.searchCategory(category)))
        {
            Optional<Photo> tempPhoto = photoService.getFile(article.getPhoto().getId());
            if(tempPhoto.isPresent())
            {
                Photo photo = tempPhoto.get();
                byte[] encodeBase64 = Base64.encode(photo.getContent());
                String base64Encoded = new String(encodeBase64, "UTF-8");
                photos.add(base64Encoded);
            }
            photosMime.add(article.getPhoto().getMime());
        }
        
        model.addAttribute("articles", articleRepo.GetPostFromCategory(articleService.searchCategory(category)));
        model.addAttribute("images", photos);
        model.addAttribute("imageMine", photosMime);
        model.addAttribute("inner", "Noticias de " + StringUtils.capitalize(category.toLowerCase()));

        return "showArticleFromCategory.html";
        
    }
    
    @GetMapping("/test")
    public String test(){
       return "test.html";
    }
    
    @GetMapping("/equipo")
    public String equipo(Model modelo){
        modelo.addAttribute("inner", "Equipo de Conciencia Politica");
       return "equipo.html";
    }
}
