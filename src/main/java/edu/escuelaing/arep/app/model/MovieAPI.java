package edu.escuelaing.arep.app.model;

import java.io.IOException;

public interface MovieAPI {
    String connectToMoviesAPI(String title) throws IOException;
}
