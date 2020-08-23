package com.daisan.diariocp.repositories;

import com.daisan.diariocp.entities.Article;
import com.daisan.diariocp.enums.Category;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article, String>{
    @Query("SELECT c FROM Article c WHERE c.category = :category ORDER BY c.date DESC")
    public List<Article> GetPostFromCategory(@Param("category")Category category);
    
    @Query("SELECT c FROM Article c WHERE c.usuario.id = :usuario ORDER BY c.date DESC")
    public List<Article> GetPostFromUserId(@Param("usuario")String usuario);
}