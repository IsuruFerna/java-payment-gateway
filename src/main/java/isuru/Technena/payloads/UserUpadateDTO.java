package isuru.Technena.payloads;

import jakarta.validation.constraints.Size;

public record UserUpadateDTO(
        String username,
        String email,
        @Size(min=6, message = "password must contain at least 6 characters!")
        String password
) {
}
