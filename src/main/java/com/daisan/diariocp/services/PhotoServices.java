package com.daisan.diariocp.services;

import com.daisan.diariocp.entities.Photo;
import com.daisan.diariocp.errors.ErrorService;
import com.daisan.diariocp.repositories.PhotoRepository;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PhotoServices{

    @Autowired
    private PhotoRepository photoRepository;

    @Transactional
    public Photo save(MultipartFile file) throws ErrorService {
        if (file != null) {
            try {
                Photo photo = new Photo();
                photo.setMime(file.getContentType());
                photo.setName(file.getName());
                photo.setContent(file.getBytes());

                return photoRepository.save(photo);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
        return null;
    }
    
    @Transactional
    public Photo update(String idPhoto, MultipartFile file) throws ErrorService {
        if (file != null) {
            try {
                Photo photo = new Photo();
                if(idPhoto != null){
                    Optional<Photo>reply = photoRepository.findById(idPhoto);
                    if(reply.isPresent()){
                        photo = reply.get();
                    }
                }
                photo.setMime(file.getContentType());
                photo.setName(file.getName());
                photo.setContent(file.getBytes());

                return photoRepository.save(photo);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
        return null;
    }
}
