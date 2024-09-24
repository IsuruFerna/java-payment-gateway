package isuru.Technena.controllers;

import isuru.Technena.responses.TokenResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/token")
    public TokenResponse response() {
        return new TokenResponse("this is the new token!");
    }
}
