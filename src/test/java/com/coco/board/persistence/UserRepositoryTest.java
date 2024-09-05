package com.coco.board.persistence;


import com.coco.board.application.dto.UserDto;
import com.coco.board.domain.User;
import com.coco.board.infrastructure.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    public void init() {
        UserDto.Request userDto = UserDto.Request.builder()
                .id(1L)
                .username("user")
                .email("user@us.er")
                .password("pass")
                .build();

        userRepository.save(userDto.toEntity());
    }

    @Test
    @DisplayName("username으로 User 찾기")
    public void findByUsernameTest() {
        // given
        Optional<User> member = userRepository.findByUsername("user");

        // then
        assertTrue(member.isPresent());
    }
}
