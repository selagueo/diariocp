package com.daisan.diariocp.services;

import com.daisan.diariocp.entities.Article;
import com.daisan.diariocp.entities.Photo;
import com.daisan.diariocp.entities.Usuario;
import com.daisan.diariocp.enums.UsuarioTag;
import com.daisan.diariocp.errors.ErrorService;
import com.daisan.diariocp.repositories.UsuarioRepository;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UsuarioServices implements UserDetailsService {

    @Autowired
    private UsuarioRepository userRepo;
    @Autowired
    private PhotoServices photoService;

    //NOTE(tomi): temporal function to add admin users for testing
    @Transactional
    public void AddAdmin(String username, String name, String lastname, String password, String password2, String mail) throws ErrorService {
        validate(name, lastname, mail, password, password2);

        Usuario user = new Usuario();
        user.setName(name);
        user.setLastName(lastname);
        String encripPass = new BCryptPasswordEncoder().encode(password);
        user.setPassword(encripPass);
        user.setMail(mail);
        user.setRegistration(new Date());
        user.setUsuarioTag(UsuarioTag.ADMIN);
        user.setUserName(username);

        userRepo.save(user);
    }

    //NOTE(tomi): this function only add an account if the user is an admin
    @Transactional
    public void AddUser(String name, String lastname, String password, String password2, String mail) throws ErrorService {
        validate(name, lastname, mail, password, password2);
        String userName = name + lastname;
        userName = userName.toLowerCase();
        Usuario usuarioTest = userRepo.GetUserFromUserName(userName);
        String newUserName = userName;
        int i = 1;
        while (usuarioTest != null) {
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
    }
    
    @Transactional
    public void edit(String userID, String email, String password1, String password2, String urlI, String urlT, String urlL, MultipartFile photo) throws ErrorService{
        
        validate(email, password1, password2, photo);
        
        boolean usuarioModificado = false;
        Usuario user = userRepo.findById(userID).get();
        if(email != null && !email.isEmpty()){
            user.setMail(email);
            usuarioModificado=true;
        }
        if(password1 != null && !password1.isEmpty()){
            String encripPass = new BCryptPasswordEncoder().encode(password1);
            user.setPassword(encripPass);
            usuarioModificado=true;
        }
        if(urlI != null && !urlI.isEmpty()){
            user.setUlrInstagram(urlI);
            usuarioModificado=true;
        }
        if(urlT != null && !urlT.isEmpty()){
            user.setUlrTwitter(urlT);
            usuarioModificado=true;
        }
        if(urlL != null && !urlL.isEmpty()){
            user.setUlrLinkedin(urlL);
            usuarioModificado=true;
        }
        if(photo != null && !photo.isEmpty())
        {
            user.setPhoto(photoService.save(photo));
            usuarioModificado=true;
        }
        if(usuarioModificado){
            System.out.println("Se han actualizado los datos del usuario");
        }
    }
    
    @Transactional
    public void swapDeadDate(String userID) {
        Usuario user = userRepo.findById(userID).get();
        if (user != null) {
            if (user.getUnRegistration() == null) {
                user.setUnRegistration(new Date());
            } else {
                user.setUnRegistration(null);
            }
        }
    }

    public List<Usuario> LoadUsuariosByTag(UsuarioTag tag) {
        List<Usuario> usuarios = userRepo.GetUsuarioByUserTag(tag);
        return usuarios;
    }

    public Usuario getUsuarioByUsername(String username) {
        Usuario myUser = userRepo.GetUserFromUserName(username);
        return myUser;
    }

    private void validate(String name, String lastName, String mail, String password, String password2) throws ErrorService {
        /*
         This method is for validating name, last name, mail and password; if any of those are not valid, this
         will throw an exception.
         */
        if (name == null || name.isEmpty()) {
            throw new ErrorService("El nombre no puede quedar vacio");
        }

        if (lastName == null || lastName.isEmpty()) {
            throw new ErrorService("El apellido no puede quedar vacio");
        }

        if (mail == null || mail.isEmpty()) {
            throw new ErrorService("El mail no puede quedar vacio");
        }
        if (password == null || password.isEmpty() || password.length() <= 6) {
            throw new ErrorService("La contrasena debe tener mas de 7 caracteres");
        }
        if (userRepo.GetUserFromMail(mail) != null) {
            throw new ErrorService("Ese mail ya se encuentra registrado");
        }
        if (!password.equals(password2)) {
            throw new ErrorService("Las contasenas deben ser iguales");
        }
    }
    
    private void validate(String mail, String password, String password2, MultipartFile photo) throws ErrorService {
         if (userRepo.GetUserFromMail(mail) != null) {
            throw new ErrorService("Ese mail ya se encuentra registrado");
        }
        if (!password.isEmpty() && password.length() <= 6) {
            throw new ErrorService("La contrasena debe tener mas de 7 caracteres");
        }
        if (!password.equals(password2)) {
            throw new ErrorService("Las contasenas deben ser iguales");
        }
        
        if(photo != null && (!photo.getContentType().equalsIgnoreCase("image/jpeg") &&
                             !photo.getContentType().equalsIgnoreCase("image/jpg")  &&
                             !photo.getContentType().equalsIgnoreCase("image/bmp")  &&
                             !photo.getContentType().equalsIgnoreCase("image/png")))
        {
           throw new ErrorService("el formato de la imagenn debe se: jpeg, jpg, bmp o png");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {
        Usuario myUser = userRepo.GetUserFromMail(mail);
        if (myUser != null) {
            if (myUser.getUnRegistration() == null) {
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
                System.out.println("usuario logeado " + user.getUsername());
                return user;
            } else {
                System.out.println("Usuario dado de baja");
                return null;
            }

        }
        System.out.println("Cannot find user");
        return null;
    }
    
    
    
    public List<String> base64EncoderId(String id) throws UnsupportedEncodingException
    {
        List<String> photos = new ArrayList();
        Usuario usuario = userRepo.GetUserFromUserId(id);
        if(usuario.getPhoto() != null){
            Optional<Photo> tempPhoto = photoService.getFile(usuario.getPhoto().getId());

            if(tempPhoto.isPresent())
            { 
                Photo photo1 = tempPhoto.get();
                byte[] encodeBase64 = Base64.encode(photo1.getContent());
                String base64Encoded = new String(encodeBase64, "UTF-8");
                photos.add(base64Encoded);
                photos.add(usuario.getPhoto().getMime()); 
                return photos;
            }
        }
        return null;
    }
    
    
}
