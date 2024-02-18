package edu.escuelaing.arep.app;


import edu.escuelaing.arep.app.model.ResponseBuilder;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;
public class ResponseBuilderTest {

    @Test
    public void testBuildHttpMovieData() {
        String movieJSON = "{\"title\":\"Inception\",\"year\":2010,\"genre\":\"Sci-Fi\"}";
        String result = ResponseBuilder.httpMovieData(movieJSON);
        assertTrue(result.contains("Inception"));
        assertTrue(result.contains("2010"));
        assertTrue(result.contains("Sci-Fi"));
    }

    @Test
    public void testBuildHttpError() {
        String title = "NonExistentMovie";
        String result = ResponseBuilder.httpMovieError(title);
        assertTrue(result.contains("Not Found"));
        assertTrue(result.contains(title));
    }

    @Test
    public void testGetContentTypeForJS() {
        String contentType = ResponseBuilder.getContentType("js");
        assertEquals("text/javascript\r\n", contentType);
    }

    @Test
    public void testGetContentTypeForHTML() {
        String contentType = ResponseBuilder.getContentType("html");
        assertEquals("text/html\r\n", contentType);
    }

    @Test
    public void testGetContentTypeForCSS() {
        String contentType = ResponseBuilder.getContentType("css");
        assertEquals("text/css\r\n", contentType);
    }

    @Test
    public void testGetContentTypeForPNG() {
        String contentType = ResponseBuilder.getContentType("png");
        assertEquals("image/png\r\n", contentType);
    }

    @Test
    public void testGetContentTypeForJPG() {
        String contentType = ResponseBuilder.getContentType("jpg");
        assertEquals("image/jpg\r\n", contentType);
    }

    @Test
    public void testBuildHttpOkHeader() {
        String contentType = "text/html\r\n";
        String httpOkHeader = ResponseBuilder.httpOkHeader(contentType);
        assertTrue(httpOkHeader.contains("200 OK"));
        assertTrue(httpOkHeader.contains(contentType));
    }

    @Test
    public void testBuildHttpNotFoundError() throws URISyntaxException {
        URI requestURI = new URI("/hi");
        String httpError = ResponseBuilder.httpError(requestURI);
        assertTrue(httpError.contains("400 Not Found"));
    }


}
