package com.dbp.democarpultec.event;

public record UserRegisteredEvent(
        Long userId,
        String email,
        String name
) {
}
