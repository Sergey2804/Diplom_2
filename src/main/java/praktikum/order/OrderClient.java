package praktikum.order;

import io.restassured.response.ValidatableResponse;
import praktikum.model.BaseURL;
import praktikum.model.Order;

import static io.restassured.RestAssured.given;

public class OrderClient extends BaseURL {


    public ValidatableResponse orderWithoutAuth(Order orderStellar) {
        return given()
                .spec(getBaseURL())
                .body(orderStellar)
                .post("/api/orders")
                .then();

    }

    public ValidatableResponse orderWithAuth(String accessToken, Order orderStellar) {
        return given()
                .spec(getBaseURL())
                .body(orderStellar)
                .auth().oauth2(accessToken)
                .post("/api/orders")
                .then();
    }

    public ValidatableResponse getOrderUserAuth(String accessToken) {
        return given()
                .spec(getBaseURL())
                .auth().oauth2(accessToken)
                .get("/api/orders")
                .then();
    }

    public ValidatableResponse getOrderUserNotAuth() {
        return given()
                .spec(getBaseURL())
                .get("/api/orders")
                .then();
    }
}
