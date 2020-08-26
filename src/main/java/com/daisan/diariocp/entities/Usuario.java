package com.daisan.diariocp.entities;

import com.daisan.diariocp.enums.UsuarioTag;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.GenericGenerator;

@Entity
public class Usuario {
    
    //Id Identifier:
    @Id
    //Generating an UNIQUE ID for each object:
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    
    private String id;
    private String mail;
    private String userName;
    private String password;
    private String name;
    private String lastName;
    private String ulrInstagram = null;
    private String ulrLinkedin = null;
    private String ulrTwitter = null;
    
    @Enumerated(EnumType.STRING)
    private UsuarioTag usuarioTag;
    
    /*
     	Saying ONE TO ONE on our photo attribute, will means that every user will only have
     ONE picture.
     */
    @OneToOne
    private Photo photo;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date registration;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date unRegistration;

    //Getters and Setters:

    public String getUlrInstagram() {
        return ulrInstagram;
    }

    public void setUlrInstagram(String ulrInstagram) {
        this.ulrInstagram = ulrInstagram;
    }

    public String getUlrLinkedin() {
        return ulrLinkedin;
    }

    public void setUlrLinkedin(String ulrLinkedin) {
        this.ulrLinkedin = ulrLinkedin;
    }

    public String getUlrTwitter() {
        return ulrTwitter;
    }

    public void setUlrTwitter(String ulrTwitter) {
        this.ulrTwitter = ulrTwitter;
    }
    
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public UsuarioTag getUsuarioTag() {
        return usuarioTag;
    }

    public void setUsuarioTag(UsuarioTag usuarioTag) {
        this.usuarioTag = usuarioTag;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public Date getRegistration() {
        return registration;
    }

    public void setRegistration(Date registration) {
        this.registration = registration;
    }

    public Date getUnRegistration() {
        return unRegistration;
    }

    public void setUnRegistration(Date unRegistration) {
        this.unRegistration = unRegistration;
    }

}
