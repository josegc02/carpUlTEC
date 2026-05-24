package com.dbp.democarpultec.repository;

import com.dbp.democarpultec.PostgresContainerTest;
import com.dbp.democarpultec.model.Publication;
import com.dbp.democarpultec.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class PublicationRepositoryTest extends PostgresContainerTest {

    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSavePublicationWhenValidData() {
        User author = userRepository.save(RepositoryTestFactory.user("publicationAuthor"));
        Publication publication = RepositoryTestFactory.publication(author);

        Publication savedPublication = publicationRepository.save(publication);

        assertNotNull(savedPublication.getId());
        assertEquals(author.getId(), savedPublication.getAuthor().getId());
        assertEquals("Miraflores", savedPublication.getDestinationOrOrigin());
    }

    @Test
    void shouldReturnAllPublicationsWhenPublicationsExist() {
        User author = userRepository.save(RepositoryTestFactory.user("publicationListAuthor"));
        publicationRepository.save(RepositoryTestFactory.publication(author));
        publicationRepository.save(RepositoryTestFactory.publication(author));

        List<Publication> publications = publicationRepository.findAll();

        assertEquals(2, publications.size());
    }

    @Test
    void shouldDeletePublicationWhenPublicationExists() {
        User author = userRepository.save(RepositoryTestFactory.user("publicationDeleteAuthor"));
        Publication savedPublication = publicationRepository.save(RepositoryTestFactory.publication(author));

        publicationRepository.deleteById(savedPublication.getId());

        assertTrue(publicationRepository.findById(savedPublication.getId()).isEmpty());
    }
}
