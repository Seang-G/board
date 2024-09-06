package com.coco.board.controller;

import com.coco.board.application.dto.UserDto;
import com.coco.board.domain.Role;
import com.coco.board.domain.User;
import com.coco.board.infrastructure.persistence.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import javax.transaction.Transactional;
import java.util.Optional;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc//(addFilters = false)  // Security 필터 비활성화
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private UserRepository userRepository;


    @Test
    @DisplayName("Should Successfully Create User When Valid Data Provided")
    public void shouldSuccessfullyCreateUserWhenValidDataProvided() throws Exception {
        // given
        UserDto.Request userRequest = UserDto.Request.builder()
                .email("new@te.st")
                .password("Password123123!")
                .nickname("new")
                .username("new1")
                .build();

        // when
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/auth/joinProc")
                .param("email", userRequest.getEmail())
                .param("password", userRequest.getPassword())
                .param("nickname", userRequest.getNickname())
                .param("username", userRequest.getUsername())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andReturn();

        // then
        Optional<User> foundUser_opt =  userRepository.findByUsername(userRequest.getUsername());
        assertTrue(foundUser_opt.isPresent(), "User should exist");

        User foundUser = foundUser_opt.get();
        User userRequestEntity = userRequest.toEntity();

        assertEquals(302, response.getResponse().getStatus(), "Expected redirection status");
        assertEquals("/auth/login", response.getResponse().getRedirectedUrl(), "Redirect url should match");
        assertEquals(foundUser.getEmail(), userRequestEntity.getEmail(), "Email should match");
        assertEquals(foundUser.getUsername(), userRequestEntity.getUsername(), "Username should match");
        assertEquals(foundUser.getRole(), Role.USER, "Role should match");
        assertEquals(foundUser.getNickname(), userRequestEntity.getNickname(), "Nickname should match");
    }

    @Test
    @DisplayName("Should Successfully Login When Valid Data Provided")
    public void shouldSuccessfullyLoginWhenValidDataProvided() throws Exception {
        // given
        UserDto.Request userRequest = UserDto.Request.builder()
                .username("test123")
                .password("Password123123!")
                .build();

        // when 1
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/auth/login")
                .param("username", userRequest.getUsername())
                .param("password", userRequest.getPassword())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andReturn();

        // then 1
        assertEquals(200, response.getResponse().getStatus(), "Expected ok status");

        // when 2
        MvcResult response2 = mockMvc.perform(MockMvcRequestBuilders.post("/auth/loginProc")
                    .param("username", userRequest.getUsername())
                    .param("password", userRequest.getPassword())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andReturn();

        // then 2
        assertEquals(302, response2.getResponse().getStatus(), "Expected redirection status");
        assertEquals("/", response2.getResponse().getRedirectedUrl(), "Redirect url should match");
    }

    @Test
    @DisplayName("Should Successfully Logout When Valid Data Provided")
    public void shouldSuccessfullyLogoutWhenValidDataProvided() throws Exception {
        // given
        UserDto.Request userRequest = UserDto.Request.builder()
                .username("test123")
                .password("Password123123!")
                .build();

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("test123")
                .password("Password123123!")
                .roles(String.valueOf(Role.USER))
                .build();

        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // when
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/logout")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andReturn();

        // then
        assertEquals(302, response.getResponse().getStatus(), "Expected redirection status");
        assertEquals("/", response.getResponse().getRedirectedUrl(), "Redirect url should match");
        assertNull(SecurityContextHolder.getContext().getAuthentication(), "SecurityContent should null");
    }
}