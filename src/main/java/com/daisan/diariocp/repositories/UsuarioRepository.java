package com.daisan.diariocp.repositories;

import com.daisan.diariocp.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository("UsuarioRepository")
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    @Query("SELECT c FROM Usuario c WHERE c.mail = :mail")
    public Usuario GetUserFromMail(@Param("mail")String mail);
}
