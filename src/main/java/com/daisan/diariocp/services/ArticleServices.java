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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.codec.Base64;
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
            Article article = new Article();
            Validate(title, synthesis, content, category, photo);
            

            if(!tags1.isEmpty())
            {
                String[] tags2 = tags1.split(" ");
                if(tags2.length > 10)
                {
                    throw new ErrorService("solo puedes poner una maximo de 10 tags");
                }
                for(String tag1 : tags2)
                {
                    Tags tag = new Tags();
                    tag.setArticle(article);
                    tag.setTag(tag1);
                    tagsRepo.save(tag);
                }  
            }
            
            article.setPhoto(photoService.save(photo));
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
    
    
    private  void Validate(String title, String synthesis, String content, String category, MultipartFile photo) throws ErrorService{
        if(title == null || title.isEmpty()){
            throw new ErrorService("debe completar el titulo");
        }
        if(synthesis == null || synthesis.isEmpty()){
            throw new ErrorService("debe completar la sintesis");
        }
        if(content == null || content.isEmpty()){
            throw new ErrorService("debe completar el cuerpo");
        }
        if(category == null || category.isEmpty()){
            throw new ErrorService("debe elegir una categoria");
        }
        
        
        if(photo.isEmpty() || (!photo.getContentType().equalsIgnoreCase("image/jpeg") &&
                               !photo.getContentType().equalsIgnoreCase("image/jpg")  &&
                               !photo.getContentType().equalsIgnoreCase("image/bmp")  &&
                               !photo.getContentType().equalsIgnoreCase("image/png")))
        {
            
            if(photo.isEmpty())
            {
                throw new ErrorService("debe elegir una foto");
            }
            else
            {
                throw new ErrorService("el formato de la imagenn debe se: jpeg, jpg, bmp o png");
            }

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
    
    public void base64Encoder(List<String> photos, List<String> photosMime, String category) throws UnsupportedEncodingException
    {
        for(Article article : articleRepo.GetPostFromCategory(searchCategory(category)))
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
    }
    
    public List<Article> GetArticlesFromCategory(String category)
    {
        return articleRepo.GetPostFromCategory(searchCategory(category));
    }
    
    

    
}
