// The REST controller that handles HTTP requests.
// Lives in sub-package controller, marked with the @RestController annotation
// for auto-configuration; the @CrossOrigin annotation enables CORS.

package welcome.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import welcome.model.*;
import welcome.service.*;

import java.util.List;

@RestController
@CrossOrigin
public class WelcomeController {

    private final String ROOT_PATH = "/ding";

    // The WelcomeController depends on the WelcomeService, so it needs to keep a reference to it.
    private WelcomeService ws;

    // The fact that the constructor for the WelcomeController requires a
    // WelcomService argument tells Spring to auto-configure a WelcomeService
    // and pass it to the constructor. This is called "Dependency Injection",
    // and it (a) saves boilerplate code, and (b) makes it easy to swap
    // components. (We can change the WelcomeService implementation without
    // changing any code in the rest of the system.)
    public WelcomeController(WelcomeService ws) {
        this.ws = ws;
    }

    @GetMapping(ROOT_PATH)
    public List<Welcome> getAllWelcomes() {
        return ws.getAllWelcomes();
    }

    @PostMapping(ROOT_PATH)
    public ResponseEntity<Void> addWelcome(@RequestBody Welcome newWelcome) {
        if (ws.hasWelcome(newWelcome.getLang())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        ws.addWelcome(newWelcome);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(ROOT_PATH + "/{lang}")
    public Welcome getWelcome(@PathVariable String lang, @RequestParam(required=false) String name) {
        Welcome welcome = ws.getWelcome(lang, name);
        if (welcome == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return welcome;
    }

}
