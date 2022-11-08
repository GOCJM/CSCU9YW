// The REST controller that handles HTTP requests.
// Lives in sub-package controller, marked with the @RestController annotation
// for auto-configuration; the @CrossOrigin annotation enables CORS.
// Handler methods wrapped into ResponseEntity for proper response headers.

package welcome.controller;

import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import welcome.model.*;
import welcome.service.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@CrossOrigin
public class WelcomeController {

    // The WelcomeController depends on the WelcomeService, so it needs to keep a reference to it.
    private final WelcomeService ws;

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
    public EntityModel<Welcome> getWelcome(@PathVariable String lang, @RequestParam(required=false) String name) {
        Welcome welcome = ws.getWelcome(lang, name);
        if (welcome == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return EntityModel.of(welcome,
                linkTo(methodOn(WelcomeController.class).getWelcome(lang, name)).withSelfRel(),
                linkTo(methodOn(WelcomeController.class).getAllWelcomes()).withRel("welcomes")
        );
    }

    @GetMapping("/ding")
    public CollectionModel<Welcome> getAllWelcomes() {
        List<Welcome> allWelcomes = ws.getAllWelcomes();

        for (Welcome welcome : allWelcomes) {
            Link selfLink = linkTo(methodOn(WelcomeController.class).getWelcome(welcome.getLang(), null)).withSelfRel();
            Link welcomes = linkTo(methodOn(WelcomeController.class).getAllWelcomes()).withRel("welcomes");
            welcome.add(selfLink,welcomes);
        }

        return CollectionModel.of(
                allWelcomes,
                linkTo(methodOn(WelcomeController.class).getAllWelcomes()).withSelfRel());
    }

}
