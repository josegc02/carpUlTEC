package com.dbp.democarpultec.repository;

import com.dbp.democarpultec.PostgresContainerTest;
import com.dbp.democarpultec.model.Publication;
import com.dbp.democarpultec.model.RequestPublication;
import com.dbp.democarpultec.model.User;
import com.dbp.democarpultec.model.enums.Status;
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
class RequestPublicationRepositoryTest extends PostgresContainerTest {

    @Autowired
    private RequestPublicationRepository requestPublicationRepository;

    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindRequestsByPublicationIdWhenRequestsExist() {
        Publication publication = savePublication("requestPublicationAuthor");
        User requester = userRepository.save(RepositoryTestFactory.user("requester"));
        RequestPublication savedRequest = requestPublicationRepository.save(
                RepositoryTestFactory.request(publication, requester, Status.PENDING)
        );

        List<RequestPublication> requests = requestPublicationRepository.findByPublication_Id(publication.getId());

        assertEquals(1, requests.size());
        assertEquals(savedRequest.getId(), requests.get(0).getId());
    }

    @Test
    void shouldFindRequestsByPublicationIdAndStatusWhenStatusesDiffer() {
        Publication publication = savePublication("requestStatusAuthor");
        User pendingRequester = userRepository.save(RepositoryTestFactory.user("pendingRequester"));
        User rejectedRequester = userRepository.save(RepositoryTestFactory.user("rejectedRequester"));
        RequestPublication pendingRequest = requestPublicationRepository.save(
                RepositoryTestFactory.request(publication, pendingRequester, Status.PENDING)
        );
        requestPublicationRepository.save(
                RepositoryTestFactory.request(publication, rejectedRequester, Status.REJECTED)
        );

        List<RequestPublication> requests = requestPublicationRepository.findByPublication_IdAndStatus(
                publication.getId(),
                Status.PENDING
        );

        assertEquals(1, requests.size());
        assertEquals(pendingRequest.getId(), requests.get(0).getId());
    }

    @Test
    void shouldReturnTrueWhenActiveRequestExists() {
        Publication publication = savePublication("activeRequestAuthor");
        User requester = userRepository.save(RepositoryTestFactory.user("activeRequester"));
        requestPublicationRepository.save(RepositoryTestFactory.request(publication, requester, Status.PENDING));

        boolean exists = requestPublicationRepository.existsByPublication_IdAndRequester_IdAndStatusIn(
                publication.getId(),
                requester.getId(),
                List.of(Status.PENDING, Status.ACCEPTED)
        );

        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseWhenRequestStatusIsNotActive() {
        Publication publication = savePublication("inactiveRequestAuthor");
        User requester = userRepository.save(RepositoryTestFactory.user("inactiveRequester"));
        requestPublicationRepository.save(RepositoryTestFactory.request(publication, requester, Status.CANCELLED));

        boolean exists = requestPublicationRepository.existsByPublication_IdAndRequester_IdAndStatusIn(
                publication.getId(),
                requester.getId(),
                List.of(Status.PENDING, Status.ACCEPTED)
        );

        assertFalse(exists);
    }

    private Publication savePublication(String prefix) {
        User author = userRepository.save(RepositoryTestFactory.user(prefix));
        return publicationRepository.save(RepositoryTestFactory.publication(author));
    }
}
