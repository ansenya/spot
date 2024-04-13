package ru.senya.spot.controllers.search;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.senya.spot.models.dto.VideoDto;
import ru.senya.spot.models.es.EsVideoModel;
import ru.senya.spot.models.jpa.UserModel;
import ru.senya.spot.models.jpa.VideoModel;
import ru.senya.spot.repos.es.ElasticVideoRepository;
import ru.senya.spot.repos.jpa.UserRepository;
import ru.senya.spot.repos.jpa.VideoRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

@RestController
@RequestMapping("videos")
public class VideoSearchController {

    private final ElasticVideoRepository elasticVideoRepository;
    private final VideoRepository videoRepository;
    private final ModelMapper modelMapper;
    private final Logger logger = LoggerFactory.getLogger(VideoSearchController.class);
    private final HashMap<UserModel, ArrayList<String>> searchHistory = new HashMap<>();
    private final UserRepository userRepository;

    @Autowired
    public VideoSearchController(ElasticVideoRepository elasticVideoRepository, VideoRepository videoRepository, UserRepository userRepository) {
        this.elasticVideoRepository = elasticVideoRepository;
        this.videoRepository = videoRepository;
        this.userRepository = userRepository;
        modelMapper = new ModelMapper();
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam(value = "q") String query,
                                    @RequestParam(value = "page", defaultValue = "0") int page,
                                    @RequestParam(value = "size", defaultValue = "10") int size,
                                    Authentication authentication) {
        PageRequest pageRequest = PageRequest.of(page, size);

        try {
            if (query.isEmpty()) {
                return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), pageRequest, 0));
            }
            var searchResultPage = elasticVideoRepository.find(query, pageRequest);

            List<Long> videoIds = searchResultPage.stream()
                    .map(esVideoModel -> Long.valueOf(esVideoModel.getId()))
                    .collect(Collectors.toList());

            List<VideoModel> foundVideos = videoRepository.findAllById(videoIds);

            List<VideoDto> videoDtos = foundVideos.stream()
                    .map(videoModel -> modelMapper.map(videoModel, VideoDto.class))
                    .collect(Collectors.toList());

            int fromIndex = (int) pageRequest.getOffset();
            int toIndex = Math.min((fromIndex + pageRequest.getPageSize()), videoDtos.size());

            PageImpl<VideoDto> resultPage = new PageImpl<>(videoDtos.subList(fromIndex, toIndex), pageRequest, searchResultPage.size());
            if (authentication != null) {
                if (userRepository.existsByUsername(authentication.getName())) {
                    var user = userRepository.findByUsername(authentication.getName());

                    var history = searchHistory.get(user);
                    if (history != null) {
                        if (history.size() > 4) {
                            history.remove(0);
                        }
                    } else {
                        history = new ArrayList<>();
                    }
                    history.add(query);

                    searchHistory.put(user, history);
                }
            }

            return ResponseEntity.ok(resultPage);
        } catch (Exception e) {
            logger.error("search error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred during search.");
        }
    }

    @GetMapping("/searchHistory")
    public ResponseEntity<?> getSearchHistory(Authentication authentication) {
        if (authentication != null && userRepository.existsByUsername(authentication.getName())) {
            return ResponseEntity.ok(searchHistory.get(userRepository.findByUsername(authentication.getName())));
        }
        return ResponseEntity.status(401).build();
    }

}
