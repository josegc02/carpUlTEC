package com.dbp.democarpultec.service;

import com.dbp.democarpultec.dto.AuthLoginRequestDto;
import com.dbp.democarpultec.dto.AuthRegisterRequestDto;
import com.dbp.democarpultec.dto.AuthResponseDto;
import com.dbp.democarpultec.dto.UserResponseDto;
import com.dbp.democarpultec.event.UserRegisteredEvent;
import com.dbp.democarpultec.exception.BusinessRuleException;
import com.dbp.democarpultec.exception.DuplicateResourceException;
import com.dbp.democarpultec.exception.UnauthorizedException;
import com.dbp.democarpultec.model.User;
import com.dbp.democarpultec.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldRegisterWhenDataIsValid() {
        AuthRegisterRequestDto request = AuthRegisterRequestDto.builder()
                .name("Juan")
                .lastName("Perez")
                .email("juan@utec.edu.pe")
                .password("Password123")
                .build();

        when(userRepository.findByEmail("juan@utec.edu.pe")).thenReturn(Optional.empty());
        when(jwtService.generateToken("juan@utec.edu.pe")).thenReturn("token");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        AuthResponseDto response = authService.register(request);

        assertNotNull(response);
        assertEquals("Bearer", response.getTokenType());
        assertEquals("token", response.getAccessToken());
        assertEquals("juan@utec.edu.pe", response.getUser().getEmail());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User savedUser = captor.getValue();
        assertNotNull(savedUser.getPasswordHash());
        assertNotEquals("Password123", savedUser.getPasswordHash());
        assertTrue(savedUser.getPasswordHash().startsWith("$2"));

        ArgumentCaptor<UserRegisteredEvent> eventCaptor = ArgumentCaptor.forClass(UserRegisteredEvent.class);
        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
        UserRegisteredEvent event = eventCaptor.getValue();
        assertEquals(1L, event.userId());
        assertEquals("juan@utec.edu.pe", event.email());
    }

    @Test
    void shouldRejectRegisterWhenEmailDomainIsNotUtec() {
        AuthRegisterRequestDto request = AuthRegisterRequestDto.builder()
                .name("Juan")
                .lastName("Perez")
                .email("juan@gmail.com")
                .password("Password123")
                .build();

        assertThrows(BusinessRuleException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldRejectRegisterWhenEmailAlreadyExists() {
        AuthRegisterRequestDto request = AuthRegisterRequestDto.builder()
                .name("Juan")
                .lastName("Perez")
                .email("juan@utec.edu.pe")
                .password("Password123")
                .build();

        when(userRepository.findByEmail("juan@utec.edu.pe")).thenReturn(Optional.of(new User()));

        assertThrows(DuplicateResourceException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldLoginWhenCredentialsAreValid() {
        AuthLoginRequestDto request = AuthLoginRequestDto.builder()
                .email("juan@utec.edu.pe")
                .password("Password123")
                .build();

        User user = new User();
        user.setId(1L);
        user.setName("Juan");
        user.setLastName("Perez");
        user.setEmail("juan@utec.edu.pe");
        user.setPasswordHash(PASSWORD_ENCODER.encode("Password123"));

        when(userRepository.findByEmail("juan@utec.edu.pe")).thenReturn(Optional.of(user));
        when(jwtService.generateToken("juan@utec.edu.pe")).thenReturn("token");

        AuthResponseDto response = authService.login(request);

        assertNotNull(response);
        assertEquals("token", response.getAccessToken());
        assertEquals("juan@utec.edu.pe", response.getUser().getEmail());
    }

    @Test
    void shouldRejectLoginWhenPasswordDoesNotMatch() {
        AuthLoginRequestDto request = AuthLoginRequestDto.builder()
                .email("juan@utec.edu.pe")
                .password("wrong")
                .build();

        User user = new User();
        user.setEmail("juan@utec.edu.pe");
        user.setPasswordHash(PASSWORD_ENCODER.encode("Password123"));

        when(userRepository.findByEmail("juan@utec.edu.pe")).thenReturn(Optional.of(user));

        assertThrows(UnauthorizedException.class, () -> authService.login(request));
    }

    @Test
    void shouldReturnCurrentUserWhenTokenIsValid() {
        User user = new User();
        user.setId(1L);
        user.setName("Juan");
        user.setLastName("Perez");
        user.setEmail("juan@utec.edu.pe");

        when(jwtService.isTokenValid("valid-token")).thenReturn(true);
        when(jwtService.extractEmail("valid-token")).thenReturn("juan@utec.edu.pe");
        when(userRepository.findByEmail("juan@utec.edu.pe")).thenReturn(Optional.of(user));

        UserResponseDto response = authService.getCurrentUser("Bearer valid-token");

        assertEquals(1L, response.getId());
        assertEquals("juan@utec.edu.pe", response.getEmail());
    }

    @Test
    void shouldRejectCurrentUserWhenAuthorizationHeaderIsMissing() {
        assertThrows(UnauthorizedException.class, () -> authService.getCurrentUser(null));
    }
}
