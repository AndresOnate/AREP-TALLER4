package edu.escuelaing.arep.app;

import edu.escuelaing.arep.app.annotations.Component;
import edu.escuelaing.arep.app.annotations.GetMapping;

@Component
public class HelloController {

    /**
     * This method returns a greeting message.
     * @return A string containing the greeting message.
     */
    @GetMapping("/hello")
    public static String index() {
        return "Greetings from Spring Boot!";
    }
}
