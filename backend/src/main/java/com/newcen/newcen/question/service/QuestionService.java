package com.newcen.newcen.question.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.newcen.newcen.comment.repository.CommentRepository;
import com.newcen.newcen.comment.repository.CommentRepositorySupport;
import com.newcen.newcen.commentFile.repository.CommentFileRepository;
import com.newcen.newcen.common.dto.request.SearchCondition;
import com.newcen.newcen.common.entity.*;
import com.newcen.newcen.common.repository.BoardFileRepository;
import com.newcen.newcen.question.repository.QuestionsFileRepository;
import com.newcen.newcen.question.repository.QuestionsRepository;
import com.newcen.newcen.question.repository.QuestionsRepositorySupport;
import com.newcen.newcen.question.request.QuestionCreateRequestDTO;
import com.newcen.newcen.question.request.QuestionUpdateRequestDTO;
import com.newcen.newcen.question.response.QuestionListResponseDTO;
import com.newcen.newcen.question.response.QuestionResponseDTO;
import com.newcen.newcen.question.response.QuestionsOneResponseDTO;
import com.newcen.newcen.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuestionService {
    private final CommentRepository commentRepository;

    private final CommentRepositorySupport commentRepositorySupport;

    private final CommentFileRepository commentFileRepository;

    private final UserRepository userRepository;
    private final QuestionsRepository questionsRepository;

    private final QuestionsFileRepository questionsFileRepository;
    private final BoardFileRepository boardFileRepository;

    private final QuestionsRepositorySupport questionsRepositorySupport;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;


    //???????????? ????????????
    public QuestionListResponseDTO retrieve(){
        List<BoardEntity> entityList = questionsRepositorySupport.getQuestionList();
        List<QuestionResponseDTO> responseDTO = entityList.stream()
                .map(QuestionResponseDTO::new)
                .collect(Collectors.toList());
        return QuestionListResponseDTO.builder()
                .data(responseDTO)
                .build();
    }
    //???????????? ????????????
    public PageImpl<QuestionResponseDTO> getPageList(Pageable pageable){
        PageImpl<QuestionResponseDTO> result = questionsRepositorySupport.getQuestionListPage(pageable);
        return result;
    }
    //???????????? ?????? ??? ????????? ????????????
    public PageImpl<QuestionResponseDTO> getPageListWithSearch(SearchCondition searchCondition, Pageable pageable){
        PageImpl<QuestionResponseDTO> result = questionsRepositorySupport.getQuestionListPageWithSearch(searchCondition, pageable);
        return result;
    }

    //???????????? ????????????
    public QuestionsOneResponseDTO questionDetail(Long boardId){
        BoardEntity boardGet = questionsRepository.getById(boardId);
//        BoardEntity boardGet = questionsRepository.getById(boardId);
        return new QuestionsOneResponseDTO(boardGet);
    }

    //???????????? ??????
    public QuestionResponseDTO create(final QuestionCreateRequestDTO dto, String userId){
        UserEntity user = null;
        user = userRepository.findById(userId).get();
        if (user == null){
            throw new RuntimeException("?????? ????????? ????????????.");
        }

        BoardEntity board = dto.toEntity(user);
        BoardEntity createdBoard = questionsRepository.save(board);
        QuestionResponseDTO createdQuestion = new QuestionResponseDTO(createdBoard);

        return createdQuestion;
    }
    //???????????? ??????
    public QuestionResponseDTO updateQuestion(QuestionUpdateRequestDTO dto, String userId, Long boardId){
        UserEntity user = userRepository.getById(userId);
        BoardEntity boardGet = questionsRepositorySupport.findBoardByUserIdAndBoardId(userId,boardId);
        String content =null;
        String title = null;
        if (dto.getBoardContent().isEmpty()|| dto.getBoardContent().equals("")){
            content = boardGet.getBoardContent();
        }else {
            content = dto.getBoardContent();
        }
        if (dto.getBoardTitle().isEmpty()|| dto.getBoardTitle().equals("")){
            title = boardGet.getBoardContent();
        }else {
            title = dto.getBoardTitle();
        }

        boardGet.updateBoard(title,content);
        BoardEntity savedBoard = questionsRepository.save(boardGet);
        return new QuestionResponseDTO(savedBoard);
    }

    //???????????? ??????, ????????? ?????? ?????? ??????
    public boolean deleteQuestion(String userId, Long boardId){

        UserEntity user = userRepository.findById(userId).get();
        if (user.getUserRole() == UserRole.ADMIN){
            Optional<BoardEntity> boardGetByAdmin = questionsRepository.findById(boardId);
            if(boardGetByAdmin.isPresent()){
                List<BoardFileEntity> boardFileEntityList = boardFileRepository.findByBoardId(boardId);
                List<CommentEntity> commentList = commentRepositorySupport.findAllByBoardId(boardId);
                if (boardFileEntityList.size() !=0 && !boardFileEntityList.isEmpty()){
                    boardFileEntityList.forEach(t-> amazonS3.deleteObject(new DeleteObjectRequest(bucket, t.getBoardFilePath())));
                }
                if (commentList.size() !=0 && !commentList.isEmpty()){
                    commentList.forEach(t->t.getCommentFileList().forEach(yy->amazonS3.deleteObject(new DeleteObjectRequest(bucket, yy.getCommentFilePath()))));
                }

                questionsRepository.delete(boardGetByAdmin.get());
                return true;
            }else
                return false;
        }
        BoardEntity boardGet = questionsRepositorySupport.findBoardByUserIdAndBoardId(userId,boardId);

        if (!Objects.equals(boardGet.getUserId(), user.getUserId())){
            throw new RuntimeException("????????? ????????? ?????? ????????????.");
        }

        List<BoardFileEntity> boardFileEntityList = boardFileRepository.findByBoardId(boardId);
        List<CommentEntity> commentList = commentRepositorySupport.findAllByBoardId(boardId);
        if (boardFileEntityList.size() !=0 && !boardFileEntityList.isEmpty()){
            boardFileEntityList.forEach(t-> amazonS3.deleteObject(new DeleteObjectRequest(bucket, t.getBoardFilePath())));
        }
        if (commentList.size() !=0 && !commentList.isEmpty()){
            commentList.forEach(t->t.getCommentFileList().forEach(yy->amazonS3.deleteObject(new DeleteObjectRequest(bucket, yy.getCommentFilePath()))));
        }

        questionsRepository.delete(boardGet);
        return true;
    }

    // ????????? ?????? ??????
    public QuestionsOneResponseDTO createFile(String filename ,String userId, Long boardId, String boardFilePath){
        UserEntity user = null;
        user = userRepository.findById(userId).get();
        BoardEntity board = questionsRepositorySupport.findBoardByUserIdAndBoardId(userId,boardId);
        if (!Objects.equals(board.getUserId(), user.getUserId())){
            throw new RuntimeException("????????? ????????? ?????? ????????????.");
        }
        if (!board.getUserId().equals(userId)){
            throw new RuntimeException("?????? ???????????? ????????????.");
        }

        BoardFileEntity boardFileEntity = BoardFileEntity.builder()
                .boardFilePath(boardFilePath)
                .boardId(boardId)
                .boardFileName(filename)
                .build();
        BoardFileEntity savedFileEntity = questionsFileRepository.save(boardFileEntity);

        board.getBoardFileEntityList().add(savedFileEntity);
        BoardEntity savedBoard = questionsRepository.save(board);
        return new QuestionsOneResponseDTO(savedBoard);
    }
    //????????? ?????? ??????
    public QuestionsOneResponseDTO updateFile(String userId, Long boardId, String boardFilePath, String boardFileId){
        UserEntity user = null;
        BoardEntity board = questionsRepositorySupport.findBoardByUserIdAndBoardId(userId,boardId);
        user = userRepository.findById(userId).get();
        if (!Objects.equals(board.getUserId(), user.getUserId())){
            throw new RuntimeException("????????? ????????? ?????? ????????????.");
        }

        BoardFileEntity boardFileGetById = questionsFileRepository.getById(boardFileId);
        boardFileGetById.setBoardFilePath(boardFilePath);
        questionsFileRepository.save(boardFileGetById);

        BoardEntity savedBoard = questionsRepository.save(board);
        return new QuestionsOneResponseDTO(savedBoard);
    }
    //????????????, ???????????? ?????? ??????
    public QuestionsOneResponseDTO deleteFile(String userId, Long boardId, String boardFileId){
        BoardEntity board = questionsRepositorySupport.findBoardByUserIdAndBoardId(userId,boardId);
        UserEntity user = userRepository.findById(userId).get();
        BoardFileEntity boardFile = questionsFileRepository.findById(boardFileId).get();
        if (!Objects.equals(board.getUserId(), user.getUserId())){
            throw new RuntimeException("????????? ????????? ?????? ????????????.");
        }
        try {
            questionsFileRepository.selfDeleteById(boardFileId);
            amazonS3.deleteObject(new DeleteObjectRequest(bucket, boardFile.getBoardFilePath()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        List<BoardFileEntity> boardFileEntityList = boardFileRepository.findByBoardId(boardId);
        BoardEntity res = BoardEntity.builder()
                .boardTitle(board.getBoardTitle())
                .boardContent(board.getBoardContent())
                .boardType(board.getBoardType())
                .boardWriter(board.getBoardWriter())
                .boardCommentIs(board.getBoardCommentIs())
                .boardId(board.getBoardId())
                .boardFileEntityList(boardFileEntityList)
                .boardTitle(board.getBoardTitle())
                .boardUpdateDate(board.getBoardUpdateDate())
                .userId(userId)
                .user(user)
                .build();
        return new QuestionsOneResponseDTO(res);
    }


}
