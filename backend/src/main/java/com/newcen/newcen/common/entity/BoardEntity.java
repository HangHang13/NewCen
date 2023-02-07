package com.newcen.newcen.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "boardId")
@Table(name="board")
public class BoardEntity {
    @Id
    @Column(name="board_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long boardId;

    @Column(name="board_type")
    @Enumerated(EnumType.STRING)
    private BoardType boardType;

    @Column(name="board_title",nullable = false)
    private String boardTitle;

    @Column(name="board_writer",nullable = false)
    private String boardWriter;

    @Column(name="board_content",nullable = false)
    private String boardContent;

    @Column(name="board_createdate")
    @CreationTimestamp
    private LocalDateTime createDate;

    @Column(name="board_updatedate")
    @LastModifiedDate
    private LocalDateTime boardUpdateDate;

    @Column(name="board_iscomment")
    @Enumerated(EnumType.STRING)
    private BoardCommentIs boardCommentIs;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name="user_id", insertable = false, updatable = false)
    private UserEntity user;

    @Column(name="user_id")
    private String userId;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name="comment_id")
    private final List<CommentEntity> commentEntityList = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name="board_file_id")
    private final List<BoardFileEntity> boardFileEntityList = new ArrayList<>();


}
