package edu.escuelaing.arep.app;


import edu.escuelaing.arep.app.controller.APIController;
import org.junit.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
public class APIControllerTest {

    @Test
    public void testConnectToMoviesAPI() throws IOException {
        String expectedResponse = "{\"Title\":\"Inception\",";
        APIController ApiController = new APIController();
        String actualResponse = ApiController.connectToMoviesAPI("Inception");
        assertTrue(actualResponse.contains(expectedResponse));
    }

    @Test
    public void testConnectToMoviesAPIMovieNotExist() throws IOException {
        APIController ApiController = new APIController();
        String actualResponse = ApiController.connectToMoviesAPI("NonExistentMovie");
        assertEquals(actualResponse, "{\"Response\":\"False\",\"Error\":\"Movie not found!\"}");
    }

    @Test
    public void testCacheBehavior() throws IOException {
        APIController apiController = new APIController();
        assertEquals(0, APIController.getCache().size());
        String response = apiController.connectToMoviesAPI("Wish");
        assertEquals(1, APIController.getCache().size());
    }

    @Test
    public void testCacheBehaviorNotMovieFound() throws IOException {
        APIController apiController = new APIController();
        String response = apiController.connectToMoviesAPI("Wish");
        assertEquals(1, APIController.getCache().size());
        String actualResponse = apiController.connectToMoviesAPI("NonExistentMovie");
        assertEquals(actualResponse, "{\"Response\":\"False\",\"Error\":\"Movie not found!\"}");
        assertEquals(1, APIController.getCache().size());
    }



}
