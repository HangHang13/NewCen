package com.newcen.newcen.question.controller;

import com.newcen.newcen.question.request.QuestionCreateRequestDTO;
import com.newcen.newcen.question.request.QuestionFileRequestDTO;
import com.newcen.newcen.question.response.QuestionListResponseDTO;
import com.newcen.newcen.question.response.QuestionResponseDTO;
import com.newcen.newcen.question.service.QuestionService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final QuestionService questionService;

    //문의 게시글 목록조회
    @GetMapping
    private ResponseEntity<?> getQuestionList(){
        QuestionListResponseDTO getList = questionService.retrieve();
        return ResponseEntity.ok()
                .body(getList);
    }
    //문의 게시글 상세조회
    @GetMapping("/{boardId}")
    private ResponseEntity<?> getQuestionsDetail(@PathVariable Long boardId){
        QuestionResponseDTO questionResponseDTO = questionService.questionDetail(boardId);
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
        userId = "402880c3862a5ba301862a5badf20000";
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
            @AuthenticationPrincipal String userId, @Validated @RequestBody QuestionCreateRequestDTO questionCreateRequestDTO
    ){
        userId = "402880c3862a5ba301862a5badf20000";
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
    //문의사항 삭제
    @DeleteMapping("/{boardId}")
    private ResponseEntity<?> deleteQuestion( @PathVariable Long boardId){

//        @AuthenticationPrincipal String userId,
        String userId = "402880c3862a5ba301862a5badf20000";
        boolean deleted = questionService.deleteQuestion(userId, boardId);
        if (deleted==true){
            return ResponseEntity.ok().body("게시글이 삭제되었습니다.");
        }else {
            return ResponseEntity.internalServerError().body("게시글 삭제에 실패했습니다.");
        }
    }

    //문의사항 파일 등록
    @PostMapping("/{boardId}")
    private ResponseEntity<?> createQuestions(
            @AuthenticationPrincipal String userId, @PathVariable Long boardId, @Validated @RequestBody QuestionFileRequestDTO questionFileRequestDTO
            , BindingResult result
    ){
        userId = "402880c3862ae9ac01862ae9b6610000";
        if (result.hasErrors()){
            log.warn("DTO 검증 에러 발생 : {} ", result.getFieldError());
            return ResponseEntity
                    .badRequest()
                    .body(result.getFieldError());
        }
        try {
            QuestionResponseDTO questionResponseDTO = questionService.createFile(userId,boardId,questionFileRequestDTO.getBoardFilePath());
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
    @PatchMapping("/{boardId}/{boardFileId}")
    private ResponseEntity<?> createQuestions(
            @AuthenticationPrincipal String userId, @PathVariable Long boardId,@PathVariable String boardFileId,
            @Validated @RequestBody QuestionFileRequestDTO questionFileRequestDTO
            , BindingResult result
    ){
        userId = "402880c3862a5ba301862a5badf20000";
        if (result.hasErrors()){
            log.warn("DTO 검증 에러 발생 : {} ", result.getFieldError());
            return ResponseEntity
                    .badRequest()
                    .body(result.getFieldError());
        }
        try {
            QuestionResponseDTO questionResponseDTO = questionService.updateFile(userId,boardId,questionFileRequestDTO.getBoardFilePath(),boardFileId);
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
    @DeleteMapping("/{boardId}/{boardFileId}")
    private ResponseEntity<?> deleteQuestionFile( @PathVariable Long boardId,@PathVariable String boardFileId){

//        @AuthenticationPrincipal String userId,
        String userId = "402880c3862a5ba301862a5badf20000";
        QuestionResponseDTO deleted = questionService.deleteFile(userId, boardId,boardFileId);
        if (deleted==null){
            return ResponseEntity.internalServerError().body("게시글 삭제에 실패했습니다..");
        }else {
            return ResponseEntity.ok().body(deleted);
        }
    }


}
