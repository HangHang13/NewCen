package com.newcen.newcen.users.service;

import com.newcen.newcen.common.entity.UserEntity;
import com.newcen.newcen.users.dto.request.UserSignUpRequestDTO;
import com.newcen.newcen.users.dto.response.UserSignUpResponseDTO;
import com.newcen.newcen.users.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
// JUint 테스트 순서 지정 각 테스트 항목에 @Order() 로 순서지정
class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Test
    @Order(1)
    @DisplayName("존재하지 않는 회원정보로 가입을 시도하면 RuntimeException이 발생해야 한다.")
    void nonexistentUserInfoTest() {
        // given
        UserSignUpRequestDTO dto = UserSignUpRequestDTO.builder()
                .userEmail("alsongdalsong@gmail.com")
                .userPassword("abc1234")
                .userName("강감찬")
                .validCode("QWERZX")
                .build();

        // when
//        UserService.create(dto);

        // then
        assertThrows(RuntimeException.class, () -> {
            userService.create(dto);    // when, then을 같이 실행 - create를 실행하면 error 발생 단언
        });

    }

    @Test
    @Order(2)
    @DisplayName("검증된 회원정보로 가입하면 회원가입에 성공해야 한다.")
    void createTest() {
        // given
        UserSignUpRequestDTO dto = UserSignUpRequestDTO.builder()
                .userEmail("postman@naver.com")
                .userPassword("abc1234")
                .userName("암호맨")
                .validCode("XY2baJQ")
                .build();

        // when
        UserSignUpResponseDTO responseDTO = userService.create(dto);

        // then
        assertEquals("암호맨", responseDTO.getUserName());

    }

    @Test
    @Order(3)
    @DisplayName("회원 가입된 회원을 조회해야 한다.")
    @Transactional
    void selectUserEmailTest() {
        // given
        String email = "postman@naver.com";

        // when
        UserEntity selectUser = userRepository.findByUserEmail(email);

        // then
        assertEquals("암호맨", selectUser.getUserName());

        System.out.println("selectUser = " + selectUser);

    }

}

