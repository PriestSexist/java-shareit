package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserRequestDto {
    private final String name;
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email can't be blank")
    private final String email;
}
