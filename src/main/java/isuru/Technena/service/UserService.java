package isuru.Technena.service;

import isuru.Technena.entities.user.User;
import isuru.Technena.exceptions.NotFoundException;
import isuru.Technena.payloads.UserUpadateDTO;
import isuru.Technena.repository.UserRepo;
import isuru.Technena.responses.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    public User findById(UUID id) throws NotFoundException {
        return userRepo.findById(id).orElseThrow(() -> new NotFoundException(id));
    }

    public User findByUsername(String username) throws NotFoundException {
        return userRepo.findByUsername(username).orElseThrow(() -> new NotFoundException("User with username: " + username + " doesn't found!"));
    }

    public User findByEmail(String email) throws NotFoundException {
        return userRepo.findByEmail(email).orElseThrow(() -> new NotFoundException("User with email: " + email + " doesn't found!"));
    }

    public void findByIdAndDelete(UUID id) {
        User user = this.findById(id);
        userRepo.delete(user);
    }

    // update user details
    public UserResponse updateUser(User currentUser, UserUpadateDTO payload) {
        User user = this.findById(currentUser.getId());

        // !!! bcript password
        if(payload.password() != null && !payload.password().isEmpty()) {
            user.setPassword(payload.password());
        }

        if(payload.username() != null && !payload.username().isEmpty()) {
            user.setUsername(payload.username());
        }

        if(payload.email() != null && !payload.email().isEmpty()) {
            user.setEmail(payload.email());
        }

        userRepo.save(user);
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getRole()
        );
    }

}
