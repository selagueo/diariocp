package com.daisan.diariocp.services;

import com.daisan.diariocp.entities.Article;
import com.daisan.diariocp.entities.Photo;
import com.daisan.diariocp.entities.Tags;
import com.daisan.diariocp.entities.Usuario;
import com.daisan.diariocp.enums.Category;
import com.daisan.diariocp.errors.ErrorService;
import com.daisan.diariocp.repositories.ArticleRepository;
import com.daisan.diariocp.repositories.PhotoRepository;
import com.daisan.diariocp.repositories.TagsRepository;
import com.daisan.diariocp.repositories.UsuarioRepository;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ArticleServices {
    @Autowired
    private ArticleRepository articleRepo;
    @Autowired
    private UsuarioRepository usuarioRepo;
    @Autowired
    private TagsRepository tagsRepo;
    @Autowired
    private PhotoRepository photoRepo;
    @Autowired
    private PhotoServices photoService;
    
    @Transactional
    public void AddPost(String title, String synthesis, String content, String tags1, MultipartFile photo, String category) throws ErrorService{
       
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(true);
        Usuario usuario = (Usuario)session.getAttribute("userSession");
        if(usuario != null)
        {
            Validate(title, synthesis, content, category);
            Article article = new Article();

            if(!photo.isEmpty() && (photo.getContentType().equalsIgnoreCase("image/jpeg") ||
                                    photo.getContentType().equalsIgnoreCase("image/jpg")  ||
                                    photo.getContentType().equalsIgnoreCase("image/bmp")  ||
                                    photo.getContentType().equalsIgnoreCase("image/png")))
            {
                    article.setPhoto(photoService.save(photo));
            }
            else
            {
                throw new ErrorService("the file has to be jpeg, jpg, png or bmp");
            }
          
            if(!tags1.isEmpty())
            {
                String[] tags2 = tags1.split(" ");
                for(String tag1 : tags2)
                {
                    Tags tag = new Tags();
                    tag.setArticle(article);
                    tag.setTag(tag1);
                    tagsRepo.save(tag);
                }  
            }
            
            article.setCategory(searchCategory(category));
            article.setTitle(title);
            article.setSynthesis(synthesis);
            article.setContent(content);
            article.setDate(new Date());
            article.setUsuario(usuario);
            articleRepo.save(article);
        }
        
    }
    
    @Transactional
    public void DeletePost(String id, Article article)throws ErrorService{
        Usuario user = usuarioRepo.findById(id).get();
        if(user.getId().equals(article.getUsuario().getId())) {
            articleRepo.delete(article);
        }
        else{
            throw new ErrorService("cannot delete an other person post");
        }
    }
    
    
    private  void Validate(String title, String synthesis, String content, String category) throws ErrorService{
        if(title == null || title.isEmpty()){
            throw new ErrorService("title cannot be empty");
        }
        if(synthesis == null || synthesis.isEmpty()){
            throw new ErrorService("synthesis cannot be empty");
        }
        if(content == null || content.isEmpty()){
            throw new ErrorService("content cannot be empty");
        }
        if(category == null || category.isEmpty()){
            throw new ErrorService("category cannot be empty");
        }

    }
    
    public Category searchCategory(String categoty)
    {
        switch (categoty)
        {
            case "ARGENTINA": return Category.ARGENTINA;
            case "ASIA": return Category.ASIA;
            case "AFRICA": return Category.AFRICA;
            case "EEUU": return Category.EEUU;
            case "EUROPA": return Category.EUROPA;
            case "HISTORIA": return Category.HISTORIA;
            case "LATINOAMERICA": return Category.LATINOAMERICA;
            case "UCA": return Category.UCA;
            default : return null;
        }
    }
}
