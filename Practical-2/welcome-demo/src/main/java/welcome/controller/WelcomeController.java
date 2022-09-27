// The REST controller that handles HTTP requests.
// Lives in sub-package controller, marked with the @RestController annotation.

package welcome.controller;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import welcome.model.*;

@RestController
public class WelcomeController {

    // The @GetMapping annotation tells the REST controller that the welcome()
    // method handles GET requests for route "/ding".
    @GetMapping("/ding")
    public Welcome welcome(@RequestParam(required = false) String lang, @RequestParam(required = false) String name) {
        return new Welcome(lang, name);
    }

}
