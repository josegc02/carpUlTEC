package com.dbp.democarpultec.event;

import com.dbp.democarpultec.model.enums.Status;

public record RequestStatusChangedEvent(
        Long requestId,
        String email,
        String name,
        String publicationTitle,
        Status status
) {
}
