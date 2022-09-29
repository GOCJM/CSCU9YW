// The REST controller that handles HTTP requests.
// Lives in sub-package controller, marked with the @RestController annotation.

package welcome.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import welcome.model.*;

@RestController
public class WelcomeController {

    private final String ROOT_PATH = "/ding";

    // The @GetMapping annotation tells the REST controller that the welcome()
    // method handles GET requests for route "/ding".
    @GetMapping(ROOT_PATH)
    public Welcome welcome(@RequestParam(required = false) String lang, @RequestParam(required = false) String name) {
        return new Welcome(lang, name);
    }

    @GetMapping(ROOT_PATH + "/{lang}")
    public Welcome welcomeLang(@PathVariable String lang, @RequestParam(required = false) String name) {
        return new Welcome(lang,name);
    }
}
