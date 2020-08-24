/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.daisan.diariocp.repositories;

import com.daisan.diariocp.entities.Article;
import com.daisan.diariocp.entities.Tags;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author manuelcabrerizo
 */

@Repository
public interface TagsRepository extends JpaRepository<Tags, String>{
    @Query("SELECT c FROM Tags c WHERE c.article.id = :article")
    public List<Tags> GetTagsFromArticleId(@Param("article")String article);
}
