package ru.senya.mytybe.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.senya.mytybe.dto.ChannelDto;
import ru.senya.mytybe.models.ChannelModel;
import ru.senya.mytybe.repos.ChannelRepository;
import ru.senya.mytybe.repos.UserRepository;

import java.util.Objects;

@RestController
@RequestMapping("c")
public class ChannelController {
    final
    ChannelRepository channelRepository;
    final
    UserRepository userRepository;
    ModelMapper modelMapper = new ModelMapper();


    public ChannelController(ChannelRepository channelRepository, UserRepository userRepository) {
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("channel")
    public ResponseEntity<?> getOne(@RequestParam(value = "id", required = false) Long id) {
        if (id == null) {
            return ResponseEntity.badRequest().body("id is empty");
        }
        ChannelModel channel = channelRepository.findById(id).orElse(null);

        if (channel == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(channel);
    }

    @PostMapping("create")
    public ResponseEntity<?> create() {
        ChannelModel channel = ChannelModel.builder()
                .name("piska")
                .user(userRepository.findById(3L).get())
                .build();

        channel = channelRepository.save(channel);

        return ResponseEntity.ok(new Object[]{userRepository.findById(3L).get(), modelMapper.map(channel, ChannelDto.class)});
    }

    @GetMapping("all")
    public ResponseEntity<?> getALl(@RequestParam(value = "page", required = false) Integer pageNum,
                                    @RequestParam(value = "size", required = false, defaultValue = "10") int pageSize,
                                    @RequestParam(value = "sort", required = false, defaultValue = "asc") String sort) {
        if (pageNum == null) {
            return ResponseEntity.badRequest().body("page is null");
        }

        Sort.Direction direction;

        if (Objects.equals(sort, "desc")) {
            direction = Sort.Direction.ASC;
        } else {
            direction = Sort.Direction.DESC;
        }

        PageRequest page = PageRequest.of(pageNum, pageSize, Sort.by(direction, "created"));
        Page<ChannelModel> userPage = channelRepository.findAll(page);

        Page<ChannelDto> channelDtoPage = userPage.map(user -> modelMapper.map(user, ChannelDto.class));

        return ResponseEntity.ok(channelDtoPage);
    }

}
