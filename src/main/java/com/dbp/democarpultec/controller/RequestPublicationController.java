package com.dbp.democarpultec.controller;

import com.dbp.democarpultec.dto.RequestPublicationAcceptRequestDto;
import com.dbp.democarpultec.dto.RequestPublicationRequestDto;
import com.dbp.democarpultec.dto.RequestPublicationResponseDto;
import com.dbp.democarpultec.dto.UserResponseDto;
import com.dbp.democarpultec.service.AuthService;
import com.dbp.democarpultec.service.RequestPublicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/request-publications")
@RequiredArgsConstructor
public class RequestPublicationController {

    private final RequestPublicationService requestPublicationService;
    private final AuthService authService;

    @GetMapping
    public List<RequestPublicationResponseDto> findAll() {
        return requestPublicationService.findAll();
    }

    @GetMapping("/{id}")
    public RequestPublicationResponseDto findById(@PathVariable Long id) {
        return requestPublicationService.findById(id);
    }

    @PatchMapping("/{id}/cancel")
    public RequestPublicationResponseDto cancel(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable Long id
    ) {
        UserResponseDto currentUser = authService.getCurrentUser(authorization);
        return requestPublicationService.cancel(id, currentUser.getId());
    }

    @PatchMapping("/{id}/reject")
    public RequestPublicationResponseDto reject(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable Long id
    ) {
        UserResponseDto currentUser = authService.getCurrentUser(authorization);
        return requestPublicationService.reject(id, currentUser.getId());
    }

    @PatchMapping("/{id}/accept")
    public RequestPublicationResponseDto accept(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable Long id,
            @Valid @RequestBody RequestPublicationAcceptRequestDto acceptRequest
    ) {
        UserResponseDto currentUser = authService.getCurrentUser(authorization);
        return requestPublicationService.accept(id, currentUser.getId(), acceptRequest.getVehicleId());
    }

    @PostMapping
    public ResponseEntity<RequestPublicationResponseDto> create(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody RequestPublicationRequestDto requestPublication
    ) {
        UserResponseDto currentUser = authService.getCurrentUser(authorization);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(requestPublicationService.createAuthenticated(currentUser.getId(), requestPublication));
    }

    @PutMapping("/{id}")
    public RequestPublicationResponseDto update(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable Long id,
            @Valid @RequestBody RequestPublicationRequestDto requestPublication
    ) {
        UserResponseDto currentUser = authService.getCurrentUser(authorization);
        return requestPublicationService.updateAuthenticated(id, currentUser.getId(), requestPublication);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable Long id
    ) {
        UserResponseDto currentUser = authService.getCurrentUser(authorization);
        requestPublicationService.deleteAuthenticated(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}
