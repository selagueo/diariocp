package com.daisan.diariocp.controllers;

import com.daisan.diariocp.entities.Photo;
import com.daisan.diariocp.enums.Category;
import com.daisan.diariocp.errors.ErrorService;
import com.daisan.diariocp.services.ArticleServices;
import com.daisan.diariocp.services.UsuarioServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
public class PortalController {
    @Autowired
    private UsuarioServices userService;
    @Autowired
    private ArticleServices articleService;
    
    @GetMapping("/")
    public String index() throws ErrorService{
        //userService.AddAdmin("Admin", "Istrador", "1234567", "Admin@daisan.com");
        return "index.html";
    }
    
    @GetMapping("/login")
    public String login(){
        return "login.html";
    }
    
    @PreAuthorize("hasAnyRole('ROLE_EDITOR')")
    @GetMapping("/login_s")
    public String login_s(){
        return "login_s.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/singup")
    public String singup(){
        return "singup.html";
    }
    
    @PostMapping("/adduser")
    public String adduser(@RequestParam String name, @RequestParam String lastname, @RequestParam String email, @RequestParam String password1, @RequestParam String password2){
//        System.out.println("name:"+name);
//        System.out.println("lastname:"+lastname);
//        System.out.println("email:"+email);
//        System.out.println("password:"+password1);
        
        if(password1.equals(password2)){
            try {
                userService.AddUser(name, lastname, password1, email);
            } catch (ErrorService ex) {
                System.out.println(ex.getMessage());
            }
        }
        else{
            System.out.println("passwords must be equals!");
        }
       
        return "singup.html";
    }
    
    
    @GetMapping("/addArticle")
    public String addArticle(){
        
        return "createArticle.html";
    }
    
    @PostMapping("/createArticle")
    public String createArticle(@RequestParam String title, @RequestParam String synthesis,
                                @RequestParam String content,
                                @RequestParam String tags){
        
        try {
            articleService.AddPost(title, synthesis, content, tags);
     
        } catch (ErrorService ex) {
            System.out.println(ex.getMessage());
        }
        
        return "createArticle.html";
    }
    

}

