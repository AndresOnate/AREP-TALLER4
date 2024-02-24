package edu.escuelaing.arep.app.controllers;


import edu.escuelaing.arep.app.annotations.Component;
import edu.escuelaing.arep.app.annotations.GetMapping;
import edu.escuelaing.arep.app.annotations.RequestParam;

import java.io.IOException;

@Component
public class MoviesController {

    @GetMapping(value = "/movies", produces = "application/json")
    public static String getMovieInformation(@RequestParam String title) throws IOException {
        APIController apiMovies = new APIController();
        return  apiMovies.connectToMoviesAPI(title);
    }
}
