package service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {

    @GetMapping("/ding")
    public Welcome welcome() {
        return new Welcome();
    }
}
