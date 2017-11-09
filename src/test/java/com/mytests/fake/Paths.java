package com.mytests.fake;

import com.jayway.restassured.config.EncoderConfig;
import com.jayway.restassured.internal.http.URIBuilder;
import org.junit.Assert;

import java.net.URI;
import java.net.URISyntaxException;

public class Paths {

    private URIBuilder uriBuilder;

    private String postsByIdPath = "posts/%s";

    private static Paths ourInstance = new Paths();

    public static Paths getInstance() {
        return ourInstance;
    }

    private Paths() {
        final String basePath = "https://jsonplaceholder.typicode.com";
        try {
            uriBuilder = new URIBuilder(URIBuilder.convertToURI(basePath), true, EncoderConfig.encoderConfig());
        } catch (URISyntaxException e) {
            Assert.fail(String.format("Thrown %s because %s could not be converted to URI", e, basePath));
        }
    }

    public URI getPostsUri() throws URISyntaxException {
        return uriBuilder.setPath("posts").toURI();
    }

    public URI getPostsByIdURI(Integer postId) throws URISyntaxException {
        return uriBuilder.setPath(String.format(postsByIdPath, postId)).toURI();
    }


}
