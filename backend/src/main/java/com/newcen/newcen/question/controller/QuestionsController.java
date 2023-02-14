package com.newcen.newcen.question.controller;

import com.newcen.newcen.comment.dto.request.CommentCreateRequest;
import com.newcen.newcen.comment.dto.request.CommentUpdateRequest;
import com.newcen.newcen.comment.dto.response.CommentListResponseDTO;
import com.newcen.newcen.comment.dto.response.CommentResponseDTO;
import com.newcen.newcen.comment.repository.CommentRepository;
import com.newcen.newcen.comment.service.CommentService;
import com.newcen.newcen.commentFile.dto.request.CommentFileCreateRequest;
import com.newcen.newcen.commentFile.dto.request.CommentFileUpdateRequest;
import com.newcen.newcen.commentFile.dto.response.CommentFileListResponseDTO;
import com.newcen.newcen.commentFile.service.CommentFileService;
import com.newcen.newcen.common.dto.request.SearchCondition;
import com.newcen.newcen.common.entity.BoardEntity;
import com.newcen.newcen.common.entity.UserEntity;
import com.newcen.newcen.notice.dto.response.NoticeDetailResponseDTO;
import com.newcen.newcen.question.repository.QuestionsRepository;
import com.newcen.newcen.question.request.QuestionCreateRequestDTO;
import com.newcen.newcen.question.request.QuestionFileRequestDTO;
import com.newcen.newcen.question.request.QuestionUpdateRequestDTO;
import com.newcen.newcen.question.response.QuestionListResponseDTO;
import com.newcen.newcen.question.response.QuestionResponseDTO;
import com.newcen.newcen.question.response.QuestionsOneResponseDTO;
import com.newcen.newcen.question.service.QuestionService;
import com.newcen.newcen.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/questions")
public class QuestionsController {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final QuestionsRepository questionsRepository;

    private final QuestionService questionService;
    private final CommentService commentService;

    private final CommentFileService commentFileService;
    //문의 게시글 목록조회
    @GetMapping
    private ResponseEntity<?> getQuestionList(Pageable pageable){
//        QuestionListResponseDTO getList = questionService.retrieve();
        PageImpl<QuestionResponseDTO> responseDTO = questionService.getPageList(pageable);
        return ResponseEntity.ok()
                .body(responseDTO);
    }

    @PostMapping("/search")
    public ResponseEntity<?> getPageListNotice(@RequestBody SearchCondition searchCondition, Pageable pageable) {
        log.info("/api/notices GET request");

        PageImpl<QuestionResponseDTO> responseDTO = questionService.getPageListWithSearch(searchCondition, pageable);

        return ResponseEntity
                .ok()
                .body(responseDTO);
    }

    //문의 게시글 상세조회
    @GetMapping("/{boardId}")
    private ResponseEntity<?> getQuestionsDetail(@PathVariable Long boardId){
        QuestionsOneResponseDTO questionResponseDTO = questionService.questionDetail(boardId);
        if (questionResponseDTO == null){
            return ResponseEntity.badRequest().body("해당 게시글은 존재하지 않습니다.");
        }
        return ResponseEntity.ok().body(questionResponseDTO);
    }

    //문의 사항 등록
    @PostMapping
    private ResponseEntity<?> createQuestions(
            @AuthenticationPrincipal String userId, @Validated @RequestBody QuestionCreateRequestDTO questionCreateRequestDTO
            , BindingResult result
    ){

        if (result.hasErrors()){
            log.warn("DTO 검증 에러 발생 : {} ", result.getFieldError());
            return ResponseEntity
                    .badRequest()
                    .body(result.getFieldError());
        }
        try {
            QuestionResponseDTO questionResponseDTO = questionService.create(questionCreateRequestDTO, userId);

            return ResponseEntity
                    .ok()
                    .body(questionResponseDTO);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity
                    .internalServerError()
                    .body("서버에러입니다.");
        }
    }

    //문의 사항 수정
    @PatchMapping("/{boardId}")
    private ResponseEntity<?> createQuestions(
            @AuthenticationPrincipal String userId, @Validated @RequestBody QuestionUpdateRequestDTO questionCreateRequestDTO,@PathVariable Long boardId
    ){
        try {
            QuestionResponseDTO questionResponseDTO = questionService.updateQuestion(questionCreateRequestDTO, userId, boardId);

            return ResponseEntity
                    .ok()
                    .body(questionResponseDTO);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity
                    .internalServerError()
                    .body("서버에러입니다.");
        }
    }
    //문의사항 삭제
    @DeleteMapping("/{boardId}")
    private ResponseEntity<?> deleteQuestion(@AuthenticationPrincipal String userId, @PathVariable Long boardId){

        boolean deleted = questionService.deleteQuestion(userId, boardId);
        if (deleted==true){
            return ResponseEntity.ok().body("게시글이 삭제되었습니다.");
        }else {
            return ResponseEntity.internalServerError().body("게시글 삭제에 실패했습니다.");
        }
    }

    //문의사항 파일 등록
    @PostMapping("/{boardId}/files")
    private ResponseEntity<?> createQuestionsFile(
            @AuthenticationPrincipal String userId, @PathVariable Long boardId, @Validated @RequestBody QuestionFileRequestDTO questionFileRequestDTO
            , BindingResult result
    ){

        if (result.hasErrors()){
            log.warn("DTO 검증 에러 발생 : {} ", result.getFieldError());
            return ResponseEntity
                    .badRequest()
                    .body(result.getFieldError());
        }
        try {
            QuestionsOneResponseDTO questionResponseDTO = questionService.createFile(userId,boardId,questionFileRequestDTO.getBoardFilePath());
            return ResponseEntity
                    .ok()
                    .body(questionResponseDTO);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity
                    .internalServerError()
                    .body("서버에러입니다.");
        }
    }
    //문의사항 파일 수정
    @PatchMapping("/{boardId}/files/{boardFileId}")
    private ResponseEntity<?> updateQuestionsFile(
            @AuthenticationPrincipal String userId, @PathVariable("boardId") Long boardId,@PathVariable("boardFileId") String boardFileId,
            @Validated @RequestBody QuestionFileRequestDTO questionFileRequestDTO
            , BindingResult result
    ){
        if (result.hasErrors()){
            log.warn("DTO 검증 에러 발생 : {} ", result.getFieldError());
            return ResponseEntity
                    .badRequest()
                    .body(result.getFieldError());
        }
        try {
            QuestionsOneResponseDTO questionResponseDTO = questionService.updateFile(userId,boardId,questionFileRequestDTO.getBoardFilePath(),boardFileId);
            return ResponseEntity
                    .ok()
                    .body(questionResponseDTO);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity
                    .internalServerError()
                    .body("서버에러입니다.");
        }
    }
    //문의사항 파일 삭제
    @DeleteMapping("/{boardId}/files/{boardFileId}")
    private ResponseEntity<?> deleteQuestionFile(@AuthenticationPrincipal String userId, @PathVariable("boardId") Long boardId,@PathVariable("boardFileId") String boardFileId){
        QuestionResponseDTO deleted = questionService.deleteFile(userId, boardId,boardFileId);
        if (deleted==null){
            return ResponseEntity.internalServerError().body("파일 삭제에 실패했습니다..");
        }else {
            return ResponseEntity.ok().body(deleted);
        }
    }
    //문의사항 댓글 조회
    @GetMapping("/{boardId}/comments")
    private  ResponseEntity<?> getCommentList(Pageable pageable, @PathVariable Long boardId){
//        CommentListResponseDTO retrived = commentService.retrive(boardId);
        PageImpl<CommentResponseDTO> retrived = commentService.getCommentListPage(pageable,boardId);
        return ResponseEntity.ok()
                .body(retrived);
    }

    //문의사항 댓글 생성
    @PostMapping("/{boardId}/comments")
    private ResponseEntity<?> createComment(@AuthenticationPrincipal String userId, @Validated @RequestBody CommentCreateRequest dto, @PathVariable("boardId") Long boardId, BindingResult result){
        if (result.hasErrors()){
            log.warn("DTO 검증 에러 발생 : {} ", result.getFieldError());
            return ResponseEntity
                    .badRequest()
                    .body(result.getFieldError());
        }
        BoardEntity board = questionsRepository.findById(boardId).get();
        if (board == null){
            log.warn("해당 글이 없습니다.");
            return ResponseEntity
                    .badRequest()
                    .body("해당 글이 없습니다.");
        }
        UserEntity getUser = userRepository.findByUserId(userId).get();
        if (getUser ==null){
            log.warn("해당 유저가 없습니다.");
            return ResponseEntity
                    .badRequest()
                    .body("해당 유저가 없습니다.");
        }
        CommentListResponseDTO retrived = commentService.createComment(dto, userId, boardId);


        return ResponseEntity.ok()
                .body(retrived);
    }
    //문의사항 댓글 수정
    @PatchMapping("/{boardId}/comments/{commentId}")
    private ResponseEntity<?> updateComment(@AuthenticationPrincipal String userId,@Validated @RequestBody CommentUpdateRequest dto,

                                            @PathVariable("boardId") Long boardId, @PathVariable("commentId") Long commentId){


        try {
            commentService.updateComment(dto, userId,boardId,commentId);
            CommentListResponseDTO retried = commentService.retrive(boardId);
            return ResponseEntity.ok()
                    .body(retried);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    //문의사항 댓글 삭제
    @DeleteMapping("/{boardId}/comments/{commentId}")
    private ResponseEntity<?> deleteComment(@AuthenticationPrincipal String userId, @PathVariable("boardId") Long boardId, @PathVariable("commentId") Long commentId){
        try {
            commentService.deleteComment(userId,commentId);
            CommentListResponseDTO retried = commentService.retrive(boardId);
            return ResponseEntity.ok()
                    .body(retried);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    //문의사항 댓글 파일목록 조회
    @GetMapping("/{boardId}/comments/{commentId}/files")
    private ResponseEntity<?> getCommentFileList(@PathVariable("boardId") Long boardId, @PathVariable("commentId") Long commentId){
        CommentFileListResponseDTO commentFileList = commentFileService.retrive(commentId);
        return ResponseEntity.ok().body(commentFileList);
    }
    //문의사항 댓글 파일 생성
    @PostMapping("/{boardId}/comments/{commentId}/files")
    private ResponseEntity<?> createCommentFile(@AuthenticationPrincipal String userId,@PathVariable("boardId") Long boardId, @Validated @RequestBody CommentFileCreateRequest dto, @PathVariable("commentId") Long commentId
    ,BindingResult result){
        if (result.hasErrors()){
            log.warn("DTO 검증 에러 발생 : {} ", result.getFieldError());
            return ResponseEntity
                    .badRequest()
                    .body(result.getFieldError());
        }
        try {
            CommentFileListResponseDTO commentFileList = commentFileService.createCommentFile(dto,userId,commentId);
            return ResponseEntity
                    .ok()
                    .body(commentFileList);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity
                    .internalServerError()
                    .body("서버에러입니다.");
        }
    }
    //문의사항 댓글 파일 수정
    @PatchMapping("/{boardId}/comments/{commentId}/files/{commentFileId}")
    public ResponseEntity<?> updateCommentFile(@AuthenticationPrincipal String userId, @PathVariable("boardId") Long boardId, @Validated @RequestBody CommentFileUpdateRequest dto, @PathVariable("commentId") Long commentId
            , @PathVariable("commentFileId") String commentFileId
            , BindingResult result){
        if (result.hasErrors()){
            log.warn("DTO 검증 에러 발생 : {} ", result.getFieldError());
            return ResponseEntity
                    .badRequest()
                    .body(result.getFieldError());
        }
        try {
            CommentFileListResponseDTO commentFileListResponseDTO = commentFileService.updateCommentFile(dto, userId, commentId, commentFileId);
            return ResponseEntity
                    .ok()
                    .body(commentFileListResponseDTO);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity
                    .internalServerError()
                    .body("서버에러입니다.");
        }
    }
    //문의사항 댓글 파일 삭제
    @DeleteMapping("/{boardId}/comments/{commentId}/files/{commentFileId}")
    public ResponseEntity<?> deleteCommentFile(@AuthenticationPrincipal String userId, @PathVariable("boardId") Long boardId,  @PathVariable("commentId") Long commentId
            , @PathVariable("commentFileId") String commentFileId
            ){
        try {
            boolean deleteCommentFile = commentFileService.deleteCommentFile(userId, commentId, commentFileId);
            CommentFileListResponseDTO commentFileList = commentFileService.retrive(commentId);
            if (deleteCommentFile==true){
                return ResponseEntity
                        .ok()
                        .body(commentFileList);
            }else {
                return ResponseEntity
                        .badRequest()
                        .body("삭제에 실패했습니다.");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity
                    .internalServerError()
                    .body("서버에러입니다.");
        }
    }
}


