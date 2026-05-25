package com.dbp.democarpultec.dto;

import com.dbp.democarpultec.model.enums.Carreras;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {

    @NotBlank
    @Size(max = 80)
    private String name;

    @NotBlank
    @Size(max = 80)
    private String lastName;

    @NotBlank
    @Email
    private String email;

    @Pattern(regexp = "^\\d{9}$", message = "Phone must contain 9 digits")
    private String phone;

    @Pattern(regexp = "^U\\d{9}$", message = "Student code must use format U#########")
    private String studentCode;

    private Carreras career;

    @Min(1)
    @Max(12)
    private Integer cycle;

}
