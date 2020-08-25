package com.daisan.diariocp.services;

import com.daisan.diariocp.entities.Usuario;
import com.daisan.diariocp.enums.UsuarioTag;
import com.daisan.diariocp.errors.ErrorService;
import com.daisan.diariocp.repositories.UsuarioRepository;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class UsuarioServices implements UserDetailsService {

    @Autowired
    private UsuarioRepository userRepo;

    //NOTE(tomi): temporal function to add admin users for testing
    @Transactional
    public void AddAdmin(String name, String lastname, String password, String mail) throws ErrorService {
        validate(name, lastname, mail, password);

        Usuario user = new Usuario();
        user.setName(name);
        user.setLastName(lastname);
        String encripPass = new BCryptPasswordEncoder().encode(password);
        user.setPassword(encripPass);
        user.setMail(mail);
        user.setRegistration(new Date());
        user.setUsuarioTag(UsuarioTag.ADMIN);

        userRepo.save(user);
    }

    //NOTE(tomi): this function only add an account if the user is an admin
    @Transactional
    public void AddUser(String name, String lastname, String password, String mail) throws ErrorService {
        validate(name, lastname, mail, password);
        Usuario user = userRepo.GetUserFromMail(mail);
        
        if (user == null) {        
            String userName = name + lastname;
            userName = userName.toLowerCase();
            Usuario usuarioTest = userRepo.GetUserFromUserName(userName);
            String newUserName = userName;
            int i = 1;
            while(usuarioTest != null){
                newUserName = userName + i;
                usuarioTest = userRepo.GetUserFromUserName(newUserName);
                i++;
            }
            userName = newUserName;
            usuarioTest = new Usuario();
            usuarioTest.setName(name);
            usuarioTest.setLastName(lastname);
            usuarioTest.setUserName(userName);
                        
            String encripPass = new BCryptPasswordEncoder().encode(password);
            usuarioTest.setPassword(encripPass);

            usuarioTest.setMail(mail);
            usuarioTest.setRegistration(new Date());
            usuarioTest.setUsuarioTag(UsuarioTag.EDITOR);

            userRepo.save(usuarioTest);
        } else {     
            throw new ErrorService("mail already in use!");
        }
    }

    public List<Usuario> LoadUsuariosByTag(UsuarioTag tag) {
        List<Usuario> usuarios = userRepo.GetUsuarioByUserTag(tag);
        return usuarios;
    }

    public Usuario getUsuarioByUsername(String username) {
        Usuario myUser = userRepo.GetUserFromMail(username);
        return myUser;
    }

    private void validate(String name, String lastName, String mail, String password) throws ErrorService {
        /*
         This method is for validating name, last name, mail and password; if any of those are not valid, this
         will throw an exception.
         */
        if (name == null || name.isEmpty()) {
            throw new ErrorService("The NAME field can't be null/empty.");
        }

        if (lastName == null || lastName.isEmpty()) {
            throw new ErrorService("The LASTNAME field can't be null/empty.");
        }

        if (mail == null || mail.isEmpty()) {
            throw new ErrorService("The MAIL field can't be null/empty.");
        }
        if (password == null || password.isEmpty() || password.length() <= 6) {
            throw new ErrorService("The PASSWORD field can't be null/empty, and must have 6 or more characters.");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario myUser = userRepo.GetUserFromMail(email);
        if (myUser != null) {

            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession(true);
            session.setAttribute("userSession", myUser);

            List<GrantedAuthority> perms = new ArrayList<>();

            GrantedAuthority p1 = new SimpleGrantedAuthority("ROLE_ADMIN");
            if (myUser.getUsuarioTag() == UsuarioTag.ADMIN) {
                perms.add(p1);
            }

            GrantedAuthority p2 = new SimpleGrantedAuthority("ROLE_EDITOR");
            perms.add(p2);

            User user = new User(myUser.getMail(), myUser.getPassword(), perms);
            return user;
        }
        System.out.println("Cannot find user");
        return null;
    }
}
