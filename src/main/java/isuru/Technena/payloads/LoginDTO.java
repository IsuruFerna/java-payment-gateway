package isuru.Technena.payloads;

import jakarta.validation.constraints.NotEmpty;

public record LoginDTO(
        @NotEmpty(message = "Email is required!")
        String email,

        @NotEmpty(message = "Password is required!")
        String password
) {
}
