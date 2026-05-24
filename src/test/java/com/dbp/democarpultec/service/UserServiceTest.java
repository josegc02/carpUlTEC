package com.dbp.democarpultec.service;

import com.dbp.democarpultec.dto.UserRequestDto;
import com.dbp.democarpultec.dto.UserResponseDto;
import com.dbp.democarpultec.model.User;
import com.dbp.democarpultec.model.enums.Carreras;
import com.dbp.democarpultec.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserRequestDto requestDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Juan")
                .lastName("Perez")
                .email("juan.perez@utec.edu.pe")
                .phone("999999999")
                .studentCode("202410032")
                .career(Carreras.Ciencia_de_la_Computacion)
                .cycle(5)
                .rating(4.5)
                .build();

        requestDto = UserRequestDto.builder()
                .name("Juan")
                .lastName("Perez")
                .email("juan.perez@utec.edu.pe")
                .phone("999999999")
                .studentCode("202410032")
                .career(Carreras.Ciencia_de_la_Computacion)
                .cycle(5)
                .rating(4.5)
                .build();
    }

    @Test
    void shouldReturnUserListWhenUsersExist() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserResponseDto> result = userService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Juan", result.get(0).getName());
        assertEquals("Perez", result.get(0).getLastName());
        assertEquals("juan.perez@utec.edu.pe", result.get(0).getEmail());

        verify(userRepository).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoUsersExist() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserResponseDto> result = userService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userRepository).findAll();
    }

    @Test
    void shouldReturnUserWhenIdExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponseDto result = userService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Juan", result.getName());
        assertEquals("Perez", result.getLastName());
        assertEquals("juan.perez@utec.edu.pe", result.getEmail());

        verify(userRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.findById(1L));

        verify(userRepository).findById(1L);
    }

    @Test
    void shouldReturnUserEntityWhenEmailExists() {
        when(userRepository.findByEmail("juan.perez@utec.edu.pe")).thenReturn(Optional.of(user));

        User result = userService.findEntityByEmail("juan.perez@utec.edu.pe");

        assertNotNull(result);
        assertEquals("Juan", result.getName());
        assertEquals("Perez", result.getLastName());
        assertEquals("juan.perez@utec.edu.pe", result.getEmail());

        verify(userRepository).findByEmail("juan.perez@utec.edu.pe");
    }

    @Test
    void shouldThrowExceptionWhenEmailDoesNotExist() {
        when(userRepository.findByEmail("noexiste@utec.edu.pe")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.findEntityByEmail("noexiste@utec.edu.pe"));

        verify(userRepository).findByEmail("noexiste@utec.edu.pe");
    }

    @Test
    void shouldCreateUserWhenValidData() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDto result = userService.create(requestDto);

        assertNotNull(result);
        assertEquals("Juan", result.getName());
        assertEquals("Perez", result.getLastName());
        assertEquals("juan.perez@utec.edu.pe", result.getEmail());
        assertEquals(4.5, result.getRating());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldUpdateUserWhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDto result = userService.update(1L, requestDto);

        assertNotNull(result);
        assertEquals("Juan", result.getName());
        assertEquals("Perez", result.getLastName());
        assertEquals("juan.perez@utec.edu.pe", result.getEmail());

        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.update(1L, requestDto));

        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldDeleteUserWhenUserExists() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.delete(1L);

        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingUser() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> userService.delete(1L));

        verify(userRepository).existsById(1L);
        verify(userRepository, never()).deleteById(anyLong());
    }
}