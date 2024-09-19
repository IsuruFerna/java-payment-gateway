package isuru.Technena.responses;

import isuru.Technena.entities.user.Role;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String email,
        Role role
) {
}
