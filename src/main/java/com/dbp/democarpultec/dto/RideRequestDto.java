package com.dbp.democarpultec.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RideRequestDto {

    @NotNull
    @Positive
    private Long publicationId;

    @NotNull
    @Positive
    private Long driverId;

    @NotNull
    @Positive
    private Long vehicleId;

    @NotNull
    private Boolean fromUTEC;

    @NotBlank
    @Size(max = 120)
    private String destinationOrOrigin;

    @NotNull
    private LocalDateTime departureTime;
}
