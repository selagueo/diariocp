package com.daisan.diariocp.services;

import com.daisan.diariocp.entities.Article;
import com.daisan.diariocp.entities.Usuario;
import com.daisan.diariocp.enums.Category;
import com.daisan.diariocp.errors.ErrorService;
import com.daisan.diariocp.repositories.ArticleRepository;
import com.daisan.diariocp.repositories.UsuarioRepository;
import java.util.Date;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArticleServices {
 @Autowired
    private ArticleRepository articleRepo;
    @Autowired
    private UsuarioRepository usuarioRepo;
    
    @Transactional
    public void AddPost(String userId, String title, String synthesis, String content, Category category) throws ErrorService{
        
        Usuario user = usuarioRepo.findById(userId).get();
        Validate(title, synthesis, content);
        Article arcticle = new Article();
        arcticle.setTitle(title);
        arcticle.setSynthesis(synthesis);
        arcticle.setContent(content);
        arcticle.setCategory(category);
        arcticle.setDate(new Date());
        arcticle.setUsuario(user);
        
        articleRepo.save(arcticle);
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
