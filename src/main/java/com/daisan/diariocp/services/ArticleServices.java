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
import java.util.Date;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
    
    @Transactional
    public void AddPost(String title, String synthesis, String content, String tags1) throws ErrorService{
       
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(true);
        Usuario usuario = (Usuario)session.getAttribute("userSession");
        if(usuario != null)
        {
            Validate(title, synthesis, content);

            Article arcticle = new Article();
            
            if(!tags1.isEmpty())
            {
                String[] tags2 = tags1.split(" ");
                for(String tag1 : tags2)
                {
                    Tags tag = new Tags();
                    tag.setArticle(arcticle);
                    tag.setTag(tag1);
                    tagsRepo.save(tag);
                }  
            }

            arcticle.setTitle(title);
            arcticle.setSynthesis(synthesis);
            arcticle.setContent(content);
            arcticle.setDate(new Date());
            arcticle.setUsuario(usuario);
            articleRepo.save(arcticle);
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
    
    
    private  void Validate(String title, String synthesis, String content) throws ErrorService{
        if(title == null || title.isEmpty()){
            throw new ErrorService("title cannot be empty");
        }
        if(synthesis == null || synthesis.isEmpty()){
            throw new ErrorService("synthesis cannot be empty");
        }
        if(content == null || content.isEmpty()){
            throw new ErrorService("content cannot be empty");
        }
 

    }
}
