package com.daisan.diariocp.repositories;

import com.daisan.diariocp.entities.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, String>{
    
}