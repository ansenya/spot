package ru.senya.storage.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import ru.senya.storage.models.UserModel;
import ru.senya.storage.service.UserService;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

import static ru.senya.storage.controllers.Utils.save;

@RestController
public class VideoUploadController {
    private final Logger log = LoggerFactory.getLogger(VideoUploadController.class);
    private final UserService userService;

    @Autowired
    public VideoUploadController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("vid/allow")
    public ResponseEntity<?> allowUpload(@RequestParam("uuid") String uuid,
                                         @RequestParam("user") String username) {
        UserModel user;
        if (userService.existsByUsername(username)) {
            user = userService.findByUsername(username).get();
            user.setUuids(user.getUuids() + " " + uuid);
        } else {
            user = UserModel.builder()
                    .username(username)
                    .uuids(uuid)
                    .build();
        }
        user = userService.save(user);
        return ResponseEntity.ok(user);
    }

    @PostMapping("vid/upload")
    public ResponseEntity<?> uploadVideo(Authentication authentication,
                                         @RequestParam("file") MultipartFile video,
                                         @RequestParam("uuid") String uuid) {
        if (video.isEmpty()) {
            return ResponseEntity.status(124).body("видео пусто");
        }
        var optUser = userService.findByUsername(authentication.getName());
        if (optUser.isEmpty()) {
            log.error("user is empty");
            return ResponseEntity.status(401).body("you are not allowed to upload any data");
        }
        var user = optUser.get();
        System.out.println(user.getUuids());
        if (!user.getUuids().contains(uuid)) {
            ResponseEntity.status(401).body("you are not allowed to upload any data");
        }
        try {
            saveVideo(video, uuid);
        } catch (IOException exception) {
            sendDoneProcessing("-1", uuid);
            return ResponseEntity.status(123).body("не сохранилось");
        }
        return ResponseEntity.ok().build();
    }

    private void saveVideo(MultipartFile multipartFile, String uuid) throws IOException {
        String path = "vids/" + uuid + "." + Objects.requireNonNull(multipartFile.getContentType()).split("/")[1];
        File file = new File(path);
        save(multipartFile, file);
        new Thread(() -> {
            encode(path, uuid);
            if (!new File(path).delete()) {
                log.error("could not delete file: " + path);
            }
        }).start();
    }


    private void encode(String filename, String uuid) {
        try {
            ProcessBuilder pb = new ProcessBuilder("sh", "scripts/encode.sh", filename);
            pb.redirectErrorStream(true);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.contains("finished")) {
                    var q = line.split(" ")[0];
                    try {
                        sendQuality(q, uuid);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                } else if (line.equals("all")) {
                    sendDoneProcessing("1", uuid);
                }
            }

            process.waitFor();
        } catch (IOException | InterruptedException e) {
            log.error("encode error", e);
        }
    }

    private void sendQuality(String q, String uuid) {
        String url = "http://main:1984/api/videos/upload/" + uuid + "/setQuality?q=" + q;
        new RestTemplate().exchange(url, HttpMethod.POST, null, String.class);
    }

    private void sendDoneProcessing(String status, String uuid) {
        String url = "http://main:1984/api/videos/upload/" + uuid + "/setDoneQualities?status=" + status;
        new RestTemplate().exchange(url, HttpMethod.POST, null, String.class);
    }
}

