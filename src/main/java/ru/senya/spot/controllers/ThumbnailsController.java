package ru.senya.spot.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.senya.spot.models.dto.VideoDto;
import ru.senya.spot.models.jpa.ImageModel;
import ru.senya.spot.repos.jpa.ImagesRepository;
import ru.senya.spot.repos.jpa.UserRepository;
import ru.senya.spot.services.VideoService;


@RestController
@RequestMapping("videos")
public class ThumbnailsController {

    final VideoService videoService;
    final UserRepository userRepository;
    final ImagesRepository imagesRepository;
    final ModelMapper modelMapper = new ModelMapper();

    public ThumbnailsController(VideoService videoService, UserRepository userRepository, ImagesRepository imagesRepository) {
        this.videoService = videoService;
        this.userRepository = userRepository;
        this.imagesRepository = imagesRepository;
    }

//    @PutMapping("{id}/th")
//    public ResponseEntity<?> th(@PathVariable Long id,
//                                @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
//                                Authentication authentication) {
//        var user = userRepository.findByUsername(authentication.getName());
//        var video = videoService.findById(id);
//
//        if (imageFile.isEmpty()) {
//            return ResponseEntity.badRequest().body("imageFile is null");
//        }
//
//        if (video != null && video.getChannel().getUser().equals(user)) {
//            sendToStorage(video.getPath(), Objects.requireNonNull(imageFile.getContentType()).split("/")[1], "img", imageFile);
//        }
//
//        video.setThumbnail(ImageModel.builder()
//                .type("th")
//                .path(video.getPath() + "." + Objects.requireNonNull(imageFile.getContentType()).split("/")[1])
//                .build());
//
//        video = videoService.save(video);
//        return ResponseEntity.ok(modelMapper.map(video, VideoDto.class));
//    }
}
