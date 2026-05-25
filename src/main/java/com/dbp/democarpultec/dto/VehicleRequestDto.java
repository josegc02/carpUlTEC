package com.dbp.democarpultec.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRequestDto {

    @NotBlank
    @Size(max = 12)
    private String plate;

    @NotBlank
    @Size(max = 40)
    private String brand;

    @NotBlank
    @Size(max = 40)
    private String model;

    @Size(max = 30)
    private String color;

    @NotNull
    @Min(1)
    private Integer seats;
}
