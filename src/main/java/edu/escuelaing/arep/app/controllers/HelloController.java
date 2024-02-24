package edu.escuelaing.arep.app.controllers;

import edu.escuelaing.arep.app.annotations.Component;
import edu.escuelaing.arep.app.annotations.GetMapping;

@Component
public class HelloController {
    @GetMapping("/hello")
    public static String index() {
        return "Greetings from Spring Boot!";
    }
}
