package edu.escuelaing.arep.app.services;

import edu.escuelaing.arep.app.model.Request;
import edu.escuelaing.arep.app.model.ResponseBuilder;

import java.io.IOException;

public interface Function {

    public String handle (Request request, ResponseBuilder response) throws IOException;
}
