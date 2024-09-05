package com.coco.board.controller;

import com.coco.board.application.dto.UserDto;
import com.coco.board.domain.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import javax.transaction.Transactional;

@SpringBootTest
//@Transactional
//@AutoConfigureMockMvc
@AutoConfigureMockMvc(addFilters = false)  // Security 필터 비활성화
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    ObjectMapper objectMapper;

//    @Autowired
//    WebApplicationContext webApplicationContext;

//    @BeforeEach
//    public void setup() {
//        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();  // MockMvc 객체 초기화
//    }

    @Test
    @DisplayName("정상 회원가입 통합 테스트")
    public void join_shouldSuccess() throws Exception {
        // given
        UserDto.Request userRequest = UserDto.Request.builder()
                .id(93L)
                .role(Role.USER)
                .email("test@te.st")
                .password("password")
                .nickname("testNickName")
                .username("test123")
                .build();

        String aa = objectMapper.writeValueAsString(userRequest);
        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/joinProc")
                .with(csrf())
                .content(objectMapper.writeValueAsString(userRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        // then
    }
}