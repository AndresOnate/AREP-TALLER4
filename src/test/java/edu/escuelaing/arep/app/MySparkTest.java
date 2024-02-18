package edu.escuelaing.arep.app;


import edu.escuelaing.arep.app.controller.APIController;
import org.junit.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
public class MySparkTest {


    @Test
    public void testGetMovieInformationValidTitle() throws IOException {
        String uriStr = "/movies?title=Inception";
        APIController movieAPI = new APIController();
        String result = movieAPI.connectToMoviesAPI("Inception");
        assertTrue(result.contains("Inception"));
        assertTrue(result.contains("2010"));
        assertTrue(result.contains("Sci-Fi"));
    }

    @Test
    public void testGetMovieInformationInvalidTitle() throws IOException {
        String uriStr = "/movies?title=NonExistentMovie";
        APIController movieAPI = new APIController();
        String result = movieAPI.connectToMoviesAPI("NonExistentMovie");
        System.out.println(result);
        assertTrue(result.contains("{\"Response\":\"False\",\"Error\":\"Movie not found!\"}"));
    }
}
