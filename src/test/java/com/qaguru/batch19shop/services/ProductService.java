package com.qaguru.batch19shop.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qaguru.batch19shop.models.Product;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.testng.Assert;

import java.io.IOException;
import java.net.URL;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

public class ProductService {
    private ObjectMapper objectMapper = new ObjectMapper();
    private String baseUri = "http://localhost:8090";
    private String basePath = "/api/v1/products";

    public Product readProductDetails(String file) {
        URL url = getClass().getClassLoader().getResource(file);
        Product product = null;
        try {
            product = objectMapper.readValue(url,Product.class);

        } catch (IOException e) {
            System.out.println("File read error");
            e.printStackTrace();
        }
        return product;
    }

    public String saveANewProduct(Product product){
        ValidatableResponse response = given().baseUri(baseUri)
                .basePath(basePath)
                .contentType(ContentType.JSON)
                .body(product)
//                .with().auth().basic("maria","maria123")
                .log().all()
                .when()
                .post("/")
                .then()
                .log().all()
                .assertThat().statusCode(HttpStatus.SC_CREATED)
                .assertThat().header("Location",containsString("/api/v1/products/"));
        String location = response.extract().header("Location");
        String id = location.substring(basePath.length()+1);
        System.out.println("Product id - " +id);
        return id;
    }

    public void updateAProduct(int productId, Product product) {
    }

    public void findAProduct(String productId, Product product) {
        ValidatableResponse getResponse = given().baseUri(baseUri)
                .basePath(basePath)
                .log().all()
                .when()
                .get("/"+ productId)
                .then()
                .log().all()
                .assertThat().statusCode(HttpStatus.SC_OK);

        Product resProduct = getResponse.extract().body().as(Product.class);
        product.setId(resProduct.getId());
        Assert.assertEquals(resProduct,product,"Incorrect product details");
    }
}
