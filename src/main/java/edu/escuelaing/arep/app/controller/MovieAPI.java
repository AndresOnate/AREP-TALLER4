package edu.escuelaing.arep.app.controller;

import java.io.IOException;

public interface MovieAPI {
    String connectToMoviesAPI(String title) throws IOException;
}
