package isuru.Technena.payloads;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public record UserRegisterDTO(
        @NotEmpty(message = "Username is required")
        String username,
        @NotEmpty(message = "Email is required")
        @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Email isn't valid")
        String email,
        @NotEmpty(message = "Password is required")
        String password,
        @NotEmpty(message = "Confirm password is required")
        String passwordConfirm
) {
}
