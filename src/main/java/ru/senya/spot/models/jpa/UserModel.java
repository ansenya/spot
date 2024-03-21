package ru.senya.spot.models.jpa;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CurrentTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(unique = true)
    @EqualsAndHashCode.Include
    private String username;

    private String password;

    @EqualsAndHashCode.Include
    private String name, surname;

    private String publicStreamLink;

    private String privateStreamLink;

    @EqualsAndHashCode.Include
    private String sex = "none";

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id")
    private ImageModel pfp;

    @EqualsAndHashCode.Include
    private Integer age;

    @EqualsAndHashCode.Include
    private String role = "user";

    @EqualsAndHashCode.Include
    private String country;

    @OneToMany(mappedBy = "user")
    private Set<ChannelModel> channels = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<ChannelModel> followedChannels = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "subscriptions",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "channel_id"))
    private Set<ChannelModel> subscriptions;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Set<CommentModel> comments;

    @ManyToMany
    @JoinTable(
            name = "videos_liked",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "video_id"))
    @JsonIgnore
    private Set<VideoModel> likedVideos;

    @ManyToMany
    @JoinTable(
            name = "videos_disliked",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "video_id"))
    private Set<VideoModel> dislikedVideos;

    @ManyToMany
    @JoinTable(
            name = "comments_liked",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "comment_id"))
    @JsonIgnore
    private Set<CommentModel> likedComments;

    @ManyToMany
    @JoinTable(
            name = "comments_disliked",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "comment_id"))
    private Set<CommentModel> dislikedComments;

    @ManyToMany
    @JoinTable(
            name = "videos_watched",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "video_id"))
    @JsonIgnore
    private Set<VideoModel> lastViewed;

    @OneToMany(mappedBy = "owner")
    private Set<PlaylistModel> playlists;

    @OneToMany(mappedBy = "user")
    private Set<StreamingTaskModel> tasks;

    @ManyToMany
    @JoinTable(
            name = "recommendations",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "video_id"))
    private Set<VideoModel> recommendedVideos;

    @CurrentTimestamp
    @EqualsAndHashCode.Include
    private Date created;

    @UpdateTimestamp
    @EqualsAndHashCode.Include
    private Date updated;

    @EqualsAndHashCode.Include
    private boolean streaming = false;

    @EqualsAndHashCode.Include
    private boolean deleted = false;

    @EqualsAndHashCode.Include
    private boolean locked = false;
}
