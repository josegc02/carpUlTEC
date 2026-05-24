package com.dbp.democarpultec.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestPublicationRequestDto {

    private Long publicationId;

    private Long requesterId;

    @NotNull
    private Boolean requesterIsDriver;

    @NotNull
    @Min(1)
    private Integer seats;

    private String message;
    private String pickupPointOrDestine;

    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    private Double externalLatitude;

    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    private Double externalLongitude;
}
