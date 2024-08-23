package com.coco.board.service;

import com.coco.board.application.UserService;
import com.coco.board.application.dto.UserDto;
import com.coco.board.domain.User;
import com.coco.board.infrastructure.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder encoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void initMock() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Join OK")
    public void whenJoin_shouldJoin() {
        // given
        UserDto.Request userDto = UserDto.Request.builder()
                .username("user")
                .email("user@us.er")
                .password("pass")
                .build();

        Mockito.when(userRepository.save(userDto.toEntity())).thenReturn(userDto.toEntity());
        Mockito.when(encoder.encode(userDto.getPassword())).thenReturn("encoded");

        // when
        userService.userJoin(userDto);

        // then

        userDto.setPassword(encoder.encode(userDto.getPassword()));

        // verify
        Mockito.verify(userRepository, Mockito.times(1)).save(userDto.toEntity());
    }
}
