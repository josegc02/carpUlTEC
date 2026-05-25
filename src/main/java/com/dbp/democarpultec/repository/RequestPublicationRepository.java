package com.dbp.democarpultec.repository;

import com.dbp.democarpultec.model.RequestPublication;
import com.dbp.democarpultec.model.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface RequestPublicationRepository extends JpaRepository<RequestPublication, Long> {
    List<RequestPublication> findByPublication_Id(Long publicationId);

    List<RequestPublication> findByPublication_IdAndStatus(Long publicationId, Status status);

    boolean existsByPublication_IdAndRequester_IdAndStatusIn(
            Long publicationId,
            Long requesterId,
            Collection<Status> statuses
    );
}
