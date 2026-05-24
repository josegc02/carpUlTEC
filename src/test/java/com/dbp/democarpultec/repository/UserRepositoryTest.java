package com.dbp.democarpultec.repository;

import com.dbp.democarpultec.model.User;
import com.dbp.democarpultec.model.enums.Carreras;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest extends BaseRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        user = User.builder()
                .name("Juan")
                .lastName("Perez")
                .email("juan.perez@utec.edu.pe")
                .phone("111111111")
                .studentCode("202410001")
                .career(Carreras.Ciencia_de_la_Computacion)
                .cycle(5)
                .rating(4.5)
                .build();
    }

    @Test
    void shouldSaveUserWhenValidData() {
        User saved = userRepository.save(user);

        assertNotNull(saved.getId());
        assertEquals("Juan", saved.getName());
        assertEquals("Perez", saved.getLastName());
        assertEquals("juan.perez@utec.edu.pe", saved.getEmail());
    }

    @Test
    void shouldGenerateIdAutomaticallyWhenUserIsSaved() {
        User saved = userRepository.save(user);

        assertNotNull(saved.getId());
        assertTrue(saved.getId() > 0);
    }

    @Test
    void shouldReturnUserWhenIdExists() {
        User saved = userRepository.save(user);

        Optional<User> result = userRepository.findById(saved.getId());

        assertTrue(result.isPresent());
        assertEquals("Juan", result.get().getName());
        assertEquals("Perez", result.get().getLastName());
        assertEquals("juan.perez@utec.edu.pe", result.get().getEmail());
    }

    @Test
    void shouldReturnEmptyWhenIdDoesNotExist() {
        Optional<User> result = userRepository.findById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void shouldReturnUserWhenEmailExists() {
        userRepository.save(user);

        Optional<User> result = userRepository.findByEmail("juan.perez@utec.edu.pe");

        assertTrue(result.isPresent());
        assertEquals("Juan", result.get().getName());
        assertEquals("Perez", result.get().getLastName());
        assertEquals("juan.perez@utec.edu.pe", result.get().getEmail());
    }

    @Test
    void shouldReturnEmptyWhenEmailDoesNotExist() {
        Optional<User> result = userRepository.findByEmail("noexiste@utec.edu.pe");

        assertFalse(result.isPresent());
    }

    @Test
    void shouldReturnCorrectUserWhenMultipleUsersExistAndEmailMatches() {
        userRepository.save(user);
        userRepository.save(User.builder()
                .name("Ana")
                .lastName("Lopez")
                .email("ana.lopez@utec.edu.pe")
                .phone("222222222")
                .studentCode("202410002")
                .career(Carreras.Ciencia_de_Datos)
                .cycle(4)
                .rating(4.0)
                .build());

        Optional<User> result = userRepository.findByEmail("ana.lopez@utec.edu.pe");

        assertTrue(result.isPresent());
        assertEquals("Ana", result.get().getName());
    }

    @Test
    void shouldReturnAllUsersWhenMultipleUsersExist() {
        userRepository.save(user);
        userRepository.save(User.builder()
                .name("Ana")
                .lastName("Lopez")
                .email("ana.lopez@utec.edu.pe")
                .phone("222222222")
                .studentCode("202410002")
                .career(Carreras.Ciencia_de_Datos)
                .cycle(4)
                .rating(4.0)
                .build());

        List<User> result = userRepository.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void shouldReturnEmptyListWhenNoUsersExist() {
        List<User> result = userRepository.findAll();

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldUpdateUserWhenUserExists() {
        User saved = userRepository.save(user);

        saved.setName("Pedro");
        saved.setLastName("Garcia");
        saved.setEmail("pedro.garcia@utec.edu.pe");
        User updated = userRepository.save(saved);

        assertEquals("Pedro", updated.getName());
        assertEquals("Garcia", updated.getLastName());
        assertEquals("pedro.garcia@utec.edu.pe", updated.getEmail());
        assertEquals(saved.getId(), updated.getId());
    }

    @Test
    void shouldDeleteUserWhenUserExists() {
        User saved = userRepository.save(user);
        Long id = saved.getId();

        userRepository.deleteById(id);

        assertFalse(userRepository.findById(id).isPresent());
    }

    @Test
    void shouldNotFailWhenDeletingAllUsers() {
        userRepository.save(user);
        userRepository.save(User.builder()
                .name("Ana")
                .lastName("Lopez")
                .email("ana.lopez@utec.edu.pe")
                .phone("222222222")
                .studentCode("202410002")
                .career(Carreras.Ciencia_de_Datos)
                .cycle(4)
                .rating(4.0)
                .build());

        userRepository.deleteAll();

        assertEquals(0, userRepository.count());
    }

    @Test
    void shouldReturnTrueWhenUserExists() {
        User saved = userRepository.save(user);

        assertTrue(userRepository.existsById(saved.getId()));
    }

    @Test
    void shouldReturnFalseWhenUserDoesNotExist() {
        assertFalse(userRepository.existsById(999L));
    }

    @Test
    void shouldThrowExceptionWhenEmailIsDuplicated() {
        userRepository.save(user);
        User duplicate = User.builder()
                .name("Pedro")
                .lastName("Garcia")
                .email("juan.perez@utec.edu.pe")
                .phone("333333333")
                .studentCode("202410003")
                .build();

        assertThrows(Exception.class, () -> userRepository.saveAndFlush(duplicate));
    }

    @Test
    void shouldThrowExceptionWhenPhoneIsDuplicated() {
        userRepository.save(user);
        User duplicate = User.builder()
                .name("Pedro")
                .lastName("Garcia")
                .email("pedro.garcia@utec.edu.pe")
                .phone("111111111")
                .studentCode("202410003")
                .build();

        assertThrows(Exception.class, () -> userRepository.saveAndFlush(duplicate));
    }

    @Test
    void shouldThrowExceptionWhenStudentCodeIsDuplicated() {
        userRepository.save(user);
        User duplicate = User.builder()
                .name("Pedro")
                .lastName("Garcia")
                .email("pedro.garcia@utec.edu.pe")
                .phone("333333333")
                .studentCode("202410001")
                .build();

        assertThrows(Exception.class, () -> userRepository.saveAndFlush(duplicate));
    }

    @Test
    void shouldReturnCorrectCountWhenUsersAreSaved() {
        userRepository.save(user);
        userRepository.save(User.builder()
                .name("Ana")
                .lastName("Lopez")
                .email("ana.lopez@utec.edu.pe")
                .phone("222222222")
                .studentCode("202410002")
                .career(Carreras.Ciencia_de_Datos)
                .cycle(4).rating(4.0)
                .build());

        assertEquals(2, userRepository.count());
    }
}