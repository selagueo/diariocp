package com.daisan.diariocp.repositories;

import com.daisan.diariocp.entities.Usuario;
import com.daisan.diariocp.enums.UsuarioTag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository("UsuarioRepository")
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    @Query("SELECT c FROM Usuario c WHERE c.mail = :mail")
    public Usuario GetUserFromMail(@Param("mail")String mail);
    
    @Query("SELECT c FROM Usuario c WHERE c.userName = :userName")
    public Usuario GetUserFromUserName(@Param("userName")String userName);
    
    @Query("SELECT c FROM Usuario c WHERE c.id = :id")
    public Usuario GetUserFromUserId(@Param("id")String id);
    
    @Query("SELECT c FROM Usuario c WHERE c.usuarioTag = :tag ORDER BY c.name ASC")
    public List<Usuario> GetUsuarioByUserTag(@Param("tag")UsuarioTag tag);
    
}
