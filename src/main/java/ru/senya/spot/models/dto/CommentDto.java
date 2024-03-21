package ru.senya.spot.models.dto;

import lombok.Data;

import java.util.*;

@Data
public class CommentDto {
    private Long id;

    private String text;

    private boolean deleted = false;


    private Date created;

    private Date updated;

    private Set<CommentDto> nextComments;

    private UserDtoWithoutChannels user;

    public List<Long> getNextComments() {
        try {
            return nextComments.stream().sorted(Comparator.comparing(o -> o.id)).map(CommentDto::getId).toList();
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }
}
