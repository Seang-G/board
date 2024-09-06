package com.coco.board.controller;

import com.coco.board.application.dto.UserDto;
import com.coco.board.domain.Role;
import com.coco.board.domain.User;
import com.coco.board.infrastructure.persistence.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.junit.jupiter.api.Assertions.*;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import javax.transaction.Transactional;
import java.util.Optional;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(addFilters = false)  // Security 필터 비활성화
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("Join Successfully")
    public void join_shouldSuccess() throws Exception {
        // given
        UserDto.Request userRequest = UserDto.Request.builder()
                .id(12L)
                .role(Role.USER)
                .email("test@te.st")
                .password("Password123123!")
                .nickname("test")
                .username("test123")
                .build();

        // when
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/auth/joinProc")
                .with(csrf())
                .param("id", String.valueOf(userRequest.getId()))
                .param("role", String.valueOf(userRequest.getRole()))
                .param("email", userRequest.getEmail())
                .param("password", userRequest.getPassword())
                .param("nickname", userRequest.getNickname())
                .param("username", userRequest.getUsername())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andReturn();

        // then
        Optional<User> foundUser_opt =  userRepository.findByUsername(userRequest.getUsername());
        assertTrue(foundUser_opt.isPresent());

        User foundUser = foundUser_opt.get();
        User userRequestEntity = userRequest.toEntity();

        assertEquals(302, response.getResponse().getStatus());
        assertEquals(foundUser.getEmail(), userRequestEntity.getEmail());
        assertEquals(foundUser.getUsername(), userRequestEntity.getUsername());
        assertEquals(foundUser.getRole(), userRequestEntity.getRole());
        assertEquals(foundUser.getNickname(), userRequestEntity.getNickname());
    }
}