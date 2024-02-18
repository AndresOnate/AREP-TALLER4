package edu.escuelaing.arep.app.model;

import java.net.URI;

public class Request {
    private URI uri = null;

    private String HTTPVerb = "GET";
    private String body = null;

    public Request(URI uri, String body) {
        this.uri = uri;
        this.body = body;
    }

    public Request() {}

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getHTTPVerb() {
        return HTTPVerb;
    }

    public void setHTTPVerb(String HTTPVerb) {
        this.HTTPVerb = HTTPVerb;
    }
}