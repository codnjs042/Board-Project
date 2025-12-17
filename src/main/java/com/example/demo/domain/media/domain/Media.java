package com.example.demo.domain.media.domain;

import com.example.demo.domain.post.domain.Post;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="medias")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String fileName;

    @Column
    private LocalDateTime uploadAt;

    @Column
    private String fileType;

    @ManyToOne
    @JoinColumn(name="media_id")
    private Post post;

}
