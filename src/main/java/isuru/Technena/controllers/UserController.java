package isuru.Technena.controllers;

import isuru.Technena.entities.user.User;
import isuru.Technena.exceptions.BadRequestException;
import isuru.Technena.payloads.UserUpadateDTO;
import isuru.Technena.responses.UserResponse;
import isuru.Technena.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile/me")
    public UserResponse getUser(@AuthenticationPrincipal User currentUser) {
        User user = userService.findById(currentUser.getId());
        System.out.println("received user: " + currentUser.getEmail());

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }

    @PatchMapping("/profile/me")
    public UserResponse updateUser(
            @RequestBody
            @Validated UserUpadateDTO body,
            BindingResult validation,
            @AuthenticationPrincipal User currentUser
            ) {
        if(validation.hasErrors()) {
            throw new BadRequestException(validation.getAllErrors());
        } else {
            return userService.updateUser(currentUser, body);
        }
    }
}
