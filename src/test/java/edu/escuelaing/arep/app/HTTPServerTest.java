package edu.escuelaing.arep.app;


import edu.escuelaing.arep.app.controllers.APIController;
import org.junit.Test;

import java.io.*;
import java.net.Socket;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class HTTPServerTest {

    @Test
    public void testGetClasses() throws IOException, ClassNotFoundException {
        String directory = "target\\classes\\edu\\escuelaing\\arep\\app\\";
        List<Class<?>> classes = HTTPServer.getClasses(directory);
        assertFalse(classes.isEmpty());
    }

    @Test
    public void testLoadClass() throws ClassNotFoundException {
        String className = "edu.escuelaing.arep.app.controllers.APIController";
        String directory = "target/classes/edu/escuelaing/arep/app/controllers/";
        Class<?> loadedClass = HTTPServer.loadClass(className, directory);
        assertNotNull(loadedClass);
    }

}
