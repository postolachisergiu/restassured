package com.mytests.fake;


import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import com.mytests.fake.responses.PostResponse;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.junit.MatcherAssert.assertThat;

public class Tests {

    @Test
    public void getPosts() {
        Response response = RestAssured.given().contentType(ContentType.JSON)
                .get("https://jsonplaceholder.typicode.com/posts/1");

        String bodyResponse = response.body().prettyPrint();
        System.out.println(bodyResponse);
        System.out.println(response.header("Server"));
    }

    @Test
    public void testDeserialization() {
        Response response = RestAssured.given().contentType(ContentType.JSON)
                .get("https://jsonplaceholder.typicode.com/posts/1");

        PostResponse postResponse = response.body().as(PostResponse.class);

        assertThat(postResponse.getUserId(), is(1));
    }

    @Test
    public void testArrayDeserialization() {
        Response response = RestAssured.given().contentType(ContentType.JSON)
                .get("https://jsonplaceholder.typicode.com/posts");

        List<PostResponse> postResponse = Arrays.asList(response.body().as(PostResponse[].class));

        assertThat(postResponse.size(), is(100));
    }

    @Test
    public void testSimplePath() {
        Response response = RestAssured.given().contentType(ContentType.JSON)
                .get("https://jsonplaceholder.typicode.com/users");

        response.then().statusCode(200).body("[0].id", is("1"));
    }

    @Test
    public void testLessSimplePath() {
        Response response = RestAssured.given().contentType(ContentType.JSON)
                .get("https://jsonplaceholder.typicode.com/users");

        response.then().statusCode(200).body("[0].address.street", is("Kulas Light"));
    }

    @Test
    public void testSimplePathFunction() {
        Response response = RestAssured.given().contentType(ContentType.JSON)
                .get("https://jsonplaceholder.typicode.com/users");

        response.then().statusCode(200).body("size()", is(10));
    }

    @Test
    public void testComplexPathFunction() {
        Response response = RestAssured.given().contentType(ContentType.JSON)
                .get("https://jsonplaceholder.typicode.com/users");

        String search  =  "findAll {it.%s == '%s'}.name";

        Integer id  = response.then().extract().path("[0].id");


        response.then().statusCode(200).body("findAll {it.id > 0}.id.sum()", is(55));

        //response.then().statusCode(200).body(String.format(search, "username", "Bret"), is(1));
    }

    @Test
    public void testExtractionParameters() {
        Response response = RestAssured.given().contentType(ContentType.JSON)
                .get("https://jsonplaceholder.typicode.com/users");

        response.then().body("findAll {it.id==1}.username[0]", is("Bret"));

        response.then().body("findAll {it.address.city == 'South Elvis'}.size()", is(1));

        ArrayList<String> cities = response.then().extract().path("address.city");
        Integer number = cities.stream().filter(city -> Arrays.asList(city.split(" ")).size() == 2).collect(Collectors.toList()).size();
        assertThat(number%2, is(0));

    }

    @Test
    public void testPost() {
        PostPayload payload = new PostPayload();
        payload.setUserId(6);
        Response response =
                RestAssured.given()
                        .contentType(ContentType.JSON)
                        .body(payload)
                        .post("https://jsonplaceholder.typicode.com/posts/1");

        String bodyResponse = response.body().prettyPrint();
        System.out.println(bodyResponse);
        System.out.println(response.header("Server"));
    }



}
