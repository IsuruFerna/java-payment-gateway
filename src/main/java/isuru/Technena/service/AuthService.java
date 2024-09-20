package isuru.Technena.service;

import isuru.Technena.entities.user.Role;
import isuru.Technena.entities.user.User;
import isuru.Technena.exceptions.BadRequestException;
import isuru.Technena.exceptions.UnauthorizedException;
import isuru.Technena.payloads.LoginDTO;
import isuru.Technena.payloads.UserRegisterDTO;
import isuru.Technena.repository.UserRepo;
import isuru.Technena.security.JWTTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private PasswordEncoder bcrypt;

    @Autowired
    private JWTTools jwtTools;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepo userRepo;

    // login
    public String authenticateUser(LoginDTO body) {
        User user = userService.findByEmail(body.email());
        if(bcrypt.matches(body.password(), user.getPassword())) {
            return jwtTools.createToken(user);
        } else {
            throw new UnauthorizedException("Not valid credentials!");
        }
    }

    // register
    public User saveUser(UserRegisterDTO body) {
        userRepo.findByEmail(body.email()).ifPresent(user -> {
            throw new BadRequestException("Email " + body.email() + " is already taken!");
        });
        userRepo.findByUsername(body.username()).ifPresent(user -> {
            throw new BadRequestException("Username " + body.username() + " is already taken!");
        });
        if(!body.password().equals(body.passwordConfirm())) {
            throw new BadRequestException("Password and confirmation mismatch!");
        }

        // TO-DO: handle password validation

        User newUser = new User();
        newUser.setEmail(body.email());
        newUser.setUsername(body.username());
        newUser.setPassword(bcrypt.encode(body.password()));
        newUser.setRole(Role.USER);

        return userRepo.save(newUser);
    }
}
