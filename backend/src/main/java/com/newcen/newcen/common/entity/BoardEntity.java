package com.newcen.newcen.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@ToString(exclude = {"user"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "boardId")
@Table(name="board")
@EntityListeners(AuditingEntityListener.class)
public class BoardEntity {
    @Id
    @Column(name="board_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

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

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="board_id")
    @JsonIgnore
    private List<CommentEntity> commentEntityList = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="board_id")
    private List<BoardFileEntity> boardFileEntityList = new ArrayList<>();
    public void updateBoard(String boardTitle, String boardContent){
        this.boardContent = boardContent;
        this.boardTitle = boardTitle;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

}
