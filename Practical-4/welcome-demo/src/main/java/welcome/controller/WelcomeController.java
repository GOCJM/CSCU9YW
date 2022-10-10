// The REST controller that handles HTTP requests.
// Lives in sub-package controller, marked with the @RestController annotation
// for auto-configuration; the @CrossOrigin annotation enables CORS.
// Handler methods wrapped into ResponseEntity for proper response headers.

package welcome.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import welcome.model.*;
import welcome.service.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

@RestController
@CrossOrigin
public class WelcomeController {

    private final String API_KEY_CLIENT;

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
        this.API_KEY_CLIENT = createApiKey();
    }

    @GetMapping("/ding/{lang}")
    public ResponseEntity<Welcome> getWelcome(@PathVariable String lang, @RequestParam(required=false) String name) {
        if (Objects.equals(lang, "api")) {
            return new ResponseEntity<>(new Welcome("en",API_KEY_CLIENT), HttpStatus.UNAUTHORIZED);
        }
        Welcome welcome = ws.getWelcome(lang, name);
        if (welcome == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(welcome, HttpStatus.OK);
    }

    // Updates existing welcome for lang; succeeds only if lang matches the language of updatedWelcome.
    @PutMapping("/ding/{lang}")
    public ResponseEntity<Void> updateWelcome(@RequestBody Welcome updatedWelcome, @PathVariable String lang, @RequestHeader(HttpHeaders.AUTHORIZATION) String auth) {
        if (!Objects.equals(auth, API_KEY_CLIENT)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        if (!ws.hasWelcome(lang)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        if (!lang.equals(updatedWelcome.getLang())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        // update of an existing resource; return status NO CONTENT
        ws.addWelcome(updatedWelcome);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private String createApiKey() {
        String uuid = UUID.randomUUID().toString();
        String salt = String.valueOf(System.currentTimeMillis() / 1000L);
        String key = uuid + salt;
        return Base64.getEncoder().encodeToString(key.getBytes(StandardCharsets.UTF_8));
    }

}
