// The REST controller that handles HTTP requests.
// Lives in sub-package controller, marked with the @RestController annotation
// for auto-configuration; the @CrossOrigin annotation enables CORS.
// Handler methods wrapped into ResponseEntity for proper response headers.

package welcome.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import welcome.model.*;
import welcome.service.*;

@RestController
@CrossOrigin
public class WelcomeController {

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

    @GetMapping("/ding/{lang}")
    public ResponseEntity<Welcome> getWelcome(@PathVariable String lang, @RequestParam(required=false) String name) {
        Welcome welcome = ws.getWelcome(lang, name);
        if (welcome == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(welcome, HttpStatus.OK);
    }

    // Updates existing welcome for lang; succeeds only if lang matches the language of updatedWelcome.
    @PutMapping("/ding/{lang}")
    public ResponseEntity<Void> updateWelcome(@RequestBody Welcome updatedWelcome, @PathVariable String lang) {
        if (!ws.hasWelcome(lang)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        if (!lang.equals(updatedWelcome.getLang())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        // update of an existing resource; return status NO CONTENT
        ws.addWelcome(updatedWelcome);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

}
