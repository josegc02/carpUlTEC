package com.dbp.democarpultec.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RidePassengerRequestDto {

    @NotNull
    @Positive
    private Long passengerId;

    @NotNull
    @Positive
    private Long rideId;

    @NotNull
    @Min(1)
    private Integer seatsReserved;

    @Size(max = 120)
    private String pickupPoint;
}
