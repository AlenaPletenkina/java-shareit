package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.hibernate.sql.Update;
import ru.practicum.shareit.validation.Create;

import java.util.Objects;

@Data
@NonNull
@AllArgsConstructor
@Builder
public class UserDtoRequest {
    private final Long id;
    @NotBlank(groups = {Create.class})
    @Pattern(regexp = "[a-zA-Zа-яА-Я\\s]*", groups = {Create.class, Update.class})
    private final String name;
    @NotEmpty(groups = {Create.class})
    @Email(groups = {Create.class, Update.class})
    private final String email;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDtoRequest userDtoRequest = (UserDtoRequest) o;
        return Objects.equals(name, userDtoRequest.name) && Objects.equals(email, userDtoRequest.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email);
    }
}
