package com.newcen.newcen.question.service;

import com.newcen.newcen.common.config.security.TokenProvider;
import com.newcen.newcen.common.entity.BoardEntity;
import com.newcen.newcen.common.entity.BoardFileEntity;
import com.newcen.newcen.common.entity.UserEntity;
import com.newcen.newcen.question.repository.QuestionsFileRepository;
import com.newcen.newcen.question.repository.QuestionsRepository;
import com.newcen.newcen.question.repository.QuestionsRepositorySupport;
import com.newcen.newcen.question.request.QuestionCreateRequestDTO;
import com.newcen.newcen.question.request.QuestionFileRequestDTO;
import com.newcen.newcen.question.request.QuestionUpdateRequestDTO;
import com.newcen.newcen.question.response.QuestionListResponseDTO;
import com.newcen.newcen.question.response.QuestionResponseDTO;
import com.newcen.newcen.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuestionService {
    private final UserRepository userRepository;
    private final QuestionsRepository questionsRepository;

    private final QuestionsFileRepository questionsFileRepository;
    private final TokenProvider tokenProvider;

    private final QuestionsRepositorySupport questionsRepositorySupport;

    //문의사항 목록조회
    public QuestionListResponseDTO retrieve(){
        List<BoardEntity> entityList = questionsRepository.findAll();
        List<QuestionResponseDTO> responseDTO = entityList.stream()
                .map(QuestionResponseDTO::new)
                .collect(Collectors.toList());
        return QuestionListResponseDTO.builder()
                .data(responseDTO)
                .build();
    }
    //문의사항 상세조회
    public QuestionResponseDTO questionDetail(Long boardId){
        BoardEntity boardGet = questionsRepository.getById(boardId);
        return new QuestionResponseDTO(boardGet);
    }

    //문의사항 등록
    public QuestionResponseDTO create(final QuestionCreateRequestDTO dto, String userId){
        UserEntity user = null;
        user = userRepository.findById(userId).get();
        if (user == null){
            throw new RuntimeException("해당 유저가 없습니다.");
        }

        BoardEntity board = dto.toEntity(user);
        BoardEntity createdBoard = questionsRepository.save(board);
        QuestionResponseDTO createdQuestion = new QuestionResponseDTO(createdBoard);

        return createdQuestion;
    }
    //문의사항 수정
    public QuestionResponseDTO updateQuestion(QuestionUpdateRequestDTO dto, String userId, Long boardId){
        UserEntity user = userRepository.getById(userId);
        BoardEntity boardGet = questionsRepositorySupport.findBoardByUserIdAndBoardId(userId,boardId);
        boardGet.updateQuestion(dto.getBoardTitle(),dto.getBoardContent());
        BoardEntity savedBoard = questionsRepository.save(boardGet);
        return new QuestionResponseDTO(savedBoard);
    }

    //문의사항 삭제
    public boolean deleteQuestion(String userId, Long boardId){
        BoardEntity boardGet = questionsRepositorySupport.findBoardByUserIdAndBoardId(userId,boardId);
        questionsRepository.delete(boardGet);
        return true;
    }

    // 게시물 파일 등록
    public QuestionResponseDTO createFile(String userId, Long boardId, String boardFilePath){
        UserEntity user = null;
        user = userRepository.findById(userId).get();
        BoardEntity board = questionsRepositorySupport.findBoardByUserIdAndBoardId(userId,boardId);
        if (user == null){
            throw new RuntimeException("해당 유저가 없습니다.");
        }
        
        BoardFileEntity boardFileEntity = BoardFileEntity.builder()
                .boardFilePath(boardFilePath)
                .boardId(boardId)
                .build();
        board.getBoardFileEntityList().add(boardFileEntity);
        BoardEntity savedBoard = questionsRepository.save(board);
        return new QuestionResponseDTO(savedBoard);
    }
    //게시물 파일 수정
    public QuestionResponseDTO updateFile(String userId, Long boardId, String boardFilePath, String boardFileId){
        UserEntity user = null;
        BoardEntity board = questionsRepositorySupport.findBoardByUserIdAndBoardId(userId,boardId);
        user = userRepository.findById(userId).get();
        if (user == null){
            throw new RuntimeException("해당 유저가 없습니다.");
        }
        if (board.getUserId()!=userId){
            throw new RuntimeException("본인이 작성한 글이 아닙니다.");
        }

        BoardFileEntity boardFileGetById = questionsFileRepository.getById(boardFileId);
        boardFileGetById.setBoardFilePath(boardFilePath);
        BoardFileEntity savedBoardFile = questionsFileRepository.save(boardFileGetById);

        return new QuestionResponseDTO(board);
    }
    //게시물 파일 삭제
    public QuestionResponseDTO deleteFile(String userId, Long boardId, String boardFileId){
        UserEntity user = null;
        BoardEntity board = questionsRepositorySupport.findBoardByUserIdAndBoardId(userId,boardId);
        user = userRepository.findById(userId).get();
        if (user == null){
            throw new RuntimeException("해당 유저가 없습니다.");
        }
        if (board.getUserId()!=userId){
            throw new RuntimeException("본인이 작성한 글이 아닙니다.");
        }
        questionsFileRepository.deleteById(boardFileId);
        return new QuestionResponseDTO(board);
    }


}
