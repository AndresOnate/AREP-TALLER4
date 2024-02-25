package edu.escuelaing.arep.app.controllers;


import edu.escuelaing.arep.app.annotations.Component;
import edu.escuelaing.arep.app.annotations.GetMapping;
import edu.escuelaing.arep.app.annotations.RequestParam;

import java.io.IOException;

@Component
public class MoviesController {


    /**
     * Retrieves movie information from the API based on the provided title.
     * This method performs a GET request to the movie API using the specified title as a query parameter.
     * It then returns the JSON response obtained from the API.
     *
     * @param title The title of the movie for which information is requested.
     * @return The JSON response containing information about the movie.
     * @throws IOException If an error occurs during the API request.
     * */
    @GetMapping(value = "/movies", produces = "application/json")
    public static String getMovieInformation(@RequestParam String title) throws IOException {
        APIController apiMovies = new APIController();
        return  apiMovies.connectToMoviesAPI(title);
    }
}
