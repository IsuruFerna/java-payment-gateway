package isuru.Technena.controllers;

import isuru.Technena.entities.user.User;
import isuru.Technena.exceptions.BadRequestException;
import isuru.Technena.payloads.LoginDTO;
import isuru.Technena.payloads.UserRegisterDTO;
import isuru.Technena.responses.GeneralResponse;
import isuru.Technena.responses.TokenResponse;
import isuru.Technena.service.AuthService;
import isuru.Technena.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.CREATED)
    public TokenResponse login(@RequestBody @Validated LoginDTO payload) {
        String accessToken = authService.authenticateUser(payload);
        return new TokenResponse(accessToken);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public GeneralResponse seveUser(@RequestBody(required = false) @Validated UserRegisterDTO body, BindingResult validation) {
        if(body == null) {
            throw new BadRequestException("Request body can't be empty!");
        }
        if(validation.hasErrors()) {
            System.out.println(validation.getAllErrors());
            throw new BadRequestException(validation.getAllErrors());
        } else {
            User newUser = authService.saveUser(body);
            return new GeneralResponse(newUser.getId());
        }
    }

}
