package com.daisan.diariocp.controllers;

import com.daisan.diariocp.enums.UsuarioTag;
import com.daisan.diariocp.errors.ErrorService;
import com.daisan.diariocp.services.UsuarioServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
public class PortalController {
    @Autowired
    private UsuarioServices userService;
    
    @GetMapping("/")
    public String index() throws ErrorService{
        //userService.AddAdmin("Admin", "Istrador", "1234567", "Admin@daisan.com");
        return "index.html";
    }
    
    @GetMapping("/inner-page")
    public String innerPage() throws ErrorService{
        return "inner-page.html";
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
    @GetMapping("/admin_panel")
    public String admin_panel(Model model){
        model.addAttribute("usersByTag", userService.LoadUsuariosByTag(UsuarioTag.EDITOR));
        return "admin_panel.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/register")
    public String register(){
        return "register.html";
    }
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/adduser")
    public String adduser(@RequestParam String name, @RequestParam String lastName, @RequestParam String email, @RequestParam String password1, @RequestParam String password2){     
        if(password1.equals(password2)){
            try {
                userService.AddUser(name, lastName, password1, email);
            } catch (ErrorService ex) {
                System.out.println(ex.getMessage());
            }
        }
        else{
            System.out.println("passwords must be equals!");
        }
       
        return "admin_panel.html";
    }
}
