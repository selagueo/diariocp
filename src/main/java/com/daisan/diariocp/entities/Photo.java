package com.daisan.diariocp.entities;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import org.hibernate.annotations.GenericGenerator;

@Entity
public class Photo {
    
    //Id Identifier:
    @Id
    //Generating an UNIQUE ID for each object:
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    
    private String name;
    private String mime;
    
    /*
        This LOB type means that this @content it's probably LARGE, so we need to specify that on our
    database, plus we need to specify that the fetchtype is LAZY (Lazy means that the page will only
    get this image only if we specify that).
    */
    
    @Lob @Basic(fetch = FetchType.LAZY)
    private byte[] content;
    
    //Getters and Setters:
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

}
