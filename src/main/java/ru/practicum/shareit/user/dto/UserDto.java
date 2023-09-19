package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserDto {
    private final int id;
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email can't be blank")
    private final String email;
    private final String name;
}
