package com.dbp.democarpultec.controller;

import com.dbp.democarpultec.dto.AuthLoginRequestDto;
import com.dbp.democarpultec.dto.AuthRegisterRequestDto;
import com.dbp.democarpultec.dto.AuthResponseDto;
import com.dbp.democarpultec.dto.UserResponseDto;
import com.dbp.democarpultec.exception.UnauthorizedException;
import com.dbp.democarpultec.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @Test
    void shouldRegisterWhenRequestIsValid() throws Exception {
        AuthRegisterRequestDto request = AuthRegisterRequestDto.builder()
                .name("Juan")
                .lastName("Perez")
                .email("juan@utec.edu.pe")
                .password("Password123")
                .build();

        when(authService.register(any(AuthRegisterRequestDto.class))).thenReturn(buildAuthResponse());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.accessToken").value("jwt-token"))
                .andExpect(jsonPath("$.user.email").value("juan@utec.edu.pe"));

        verify(authService).register(any(AuthRegisterRequestDto.class));
    }

    @Test
    void shouldLoginWhenRequestIsValid() throws Exception {
        AuthLoginRequestDto request = AuthLoginRequestDto.builder()
                .email("juan@utec.edu.pe")
                .password("Password123")
                .build();

        when(authService.login(any(AuthLoginRequestDto.class))).thenReturn(buildAuthResponse());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.accessToken").value("jwt-token"));

        verify(authService).login(any(AuthLoginRequestDto.class));
    }

    @Test
    void shouldReturnUnauthorizedWhenCredentialsAreInvalid() throws Exception {
        AuthLoginRequestDto request = AuthLoginRequestDto.builder()
                .email("juan@utec.edu.pe")
                .password("bad-password")
                .build();

        when(authService.login(any(AuthLoginRequestDto.class))).thenThrow(new UnauthorizedException("Invalid credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    private AuthResponseDto buildAuthResponse() {
        return AuthResponseDto.builder()
                .tokenType("Bearer")
                .accessToken("jwt-token")
                .user(UserResponseDto.builder()
                        .id(1L)
                        .name("Juan")
                        .lastName("Perez")
                        .email("juan@utec.edu.pe")
                        .build())
                .build();
    }
}
