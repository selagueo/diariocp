package com.daisan.diariocp.controllers;

import com.daisan.diariocp.entities.Article;
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
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public String userProfileAndIndex(Model modelo, @PathVariable(required = false) String user) throws ErrorService, UnsupportedEncodingException{
        //CREAR Admin
        //userService.AddAdmin("admin","Admin", "Istrador", "1234567", "1234567", "admin@daisansf.com");
        if(user != null){
            Usuario usuario = userService.getUsuarioByUsername(user);
            if(usuario != null){
                modelo.addAttribute("inner", "Perfil de "+usuario.getName()+" "+usuario.getLastName());
                modelo.addAttribute("usuario", usuario);
                if(userService.base64EncoderId(usuario.getId()) != null){
                    modelo.addAttribute("photoUser", userService.base64EncoderId(usuario.getId()));
                }
                List<String> photos = new ArrayList();
                List<String> photosMime = new ArrayList(); 
                List<String> colors = new ArrayList();
                articleService.base64EncoderId(photos, photosMime, usuario.getId());
                for(Article article : articleService.GetArticlesFromUser(usuario.getId()))
                {
                    articleService.ColorArticle(article.getCategory(), colors);
                }
                modelo.addAttribute("articles", articleService.GetArticlesFromUser(usuario.getId()));
                modelo.addAttribute("images", photos);
                modelo.addAttribute("imageMine", photosMime);
                modelo.addAttribute("colors", colors);
                return "profile.html";
            }
        }
        modelo.addAttribute("inner", "Ultimas Noticias");
        return "index.html";
    }
          
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/admin_panel")
    public String admin_panel(Model modelo) {
        modelo.addAttribute("inner", "Panel de Administración");
        modelo.addAttribute("usersByTag", userService.LoadUsuariosByTag(UsuarioTag.EDITOR));
        return "admin_panel.html";
    }
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/admin_action")
    public String admin_action(Model modelo, @RequestParam String userId, @RequestParam String action){        
        if(userId != null & action != null){
            //NOTE(tomi): al perfil de cada usuario lo redirige java script
            switch(action){
                case "actionModificar":{
                    System.out.println("Usuario "+ userId +" action "+ action);
                }break;
                case "actionBaja":{
                    userService.swapDeadDate(userId);
                }break;

            }
        }
        return "action_success.html";
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
    
    @PreAuthorize("hasAnyRole('ROLE_EDITOR')")
    @GetMapping("/edit_user")
    public String edit_user(){
        return "edit_user.html";
    }
    
    @PreAuthorize("hasAnyRole('ROLE_EDITOR')")
    @PostMapping("/edit")
    public String edit(Model modelo, @RequestParam(required = false) String email, @RequestParam(required = false) String password1, @RequestParam(required = false) String password2, 
            @RequestParam(required = false) String urlInstagram, @RequestParam(required = false) String urlTwitter, @RequestParam(required = false) String urlLinkedIn, @RequestParam(required = false) MultipartFile photo){    
        
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(true);
        Usuario usuarioS = (Usuario)session.getAttribute("userSession");
        if(usuarioS != null){
            try {
                userService.edit(usuarioS.getId(), email, password1, password2, urlInstagram, urlTwitter, urlLinkedIn, photo);
            } catch (ErrorService ex) {
                modelo.addAttribute("errorDatos", ex.getMessage());
            }
        }
                return "edit_user.html";
    }
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/register")
    public String register(Model modelo) {
        modelo.addAttribute("inner", "Registrar Nuevo Editor");
        return "register.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/adduser")
    public String adduser(Model modelo, @RequestParam String name, @RequestParam String lastName, @RequestParam String email, @RequestParam String password1, @RequestParam String password2) {
        try {
            userService.AddUser(name, lastName, password1, password2, email);
        } catch (ErrorService ex) {
            modelo.addAttribute("errorDatos", ex.getMessage());
            return "register.html";
        }
        return "register_success.html";
    }
    
    @PreAuthorize("hasAnyRole('ROLE_EDITOR')")
    @GetMapping("/createArticle")
    public String createArticle(Model modelo) {
        modelo.addAttribute("inner", "Crear Noticia");
        return "createArticle.html";
    }
    
    @PreAuthorize("hasAnyRole('ROLE_EDITOR')")
    @PostMapping("/addArticle")
    public String addArticle(Model modelo, @RequestParam String title, @RequestParam String synthesis,
            @RequestParam String content, @RequestParam String tags,
            @RequestParam MultipartFile photo, @RequestParam String category) {

        try {
            articleService.AddPost(title, synthesis, content, tags, photo, category);

        } catch (ErrorService ex) {
            modelo.addAttribute("errorDatos", ex.getMessage());
            return "createArticle.html";
        }
        


        return "index.html";
    }
    
    @GetMapping("/showArticleFromCategory")
    public String showArticleFromCategory(Model model, @RequestParam String category) throws UnsupportedEncodingException{
        
        List<String> photos = new ArrayList();
        List<String> photosMime = new ArrayList();
        List<String> usuarios = new ArrayList();
        articleService.base64Encoder(photos, photosMime, category);
        
        String desCategory = null;
        String colorCategory = null;
        switch(category.toLowerCase()){
            case "argentina":{
                desCategory = "Argentina";
                colorCategory = "#087589";
            }break;
            case "africa":{
                desCategory = "AFRICA";
                colorCategory = "#A6570C";
            }break;
            case "asia":{
                desCategory = "ASIA Y MEDIO ORIENTE";
                colorCategory = "#54100D";
            }break;
            case "europa":{
                desCategory = "EUROPA";
                colorCategory = "#0F5A15";
            }break;
            case "eeuu":{
                desCategory = "ESTADOS UNIDOS";
                colorCategory = "#091851";
            }break;
            case "latinoamerica":{
                desCategory = "LATINOAMERICA";
                colorCategory = "#480072";
            }break;
            case "historia":{
                desCategory = "HISTORIA";
                colorCategory = "#000000";
            }break;
            case "uca":{
                desCategory = "AVISOS";
                colorCategory = "#484B44";
            }break;
            
        }
        
        for(Article article : articleService.GetArticlesFromCategory(category))
        {
            usuarios.add(article.getUsuario().getName() + " " + article.getUsuario().getLastName());
        }
        
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("articles", articleService.GetArticlesFromCategory(category));
        model.addAttribute("images", photos);
        model.addAttribute("imageMine", photosMime);
        model.addAttribute("inner", desCategory);
        model.addAttribute("color", colorCategory);

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
