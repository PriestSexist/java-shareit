package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

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
