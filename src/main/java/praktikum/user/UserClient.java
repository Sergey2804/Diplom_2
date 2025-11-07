package praktikum.user;

import io.restassured.response.ValidatableResponse;
import praktikum.model.BaseURL;
import praktikum.model.User;

import static io.restassured.RestAssured.given;

public class UserClient extends BaseURL {


    public ValidatableResponse createUser(User userStellar) {
        return given()
                .spec(getBaseURL())
                .body(userStellar)
                .when()
                .post("/api/auth/register")
                .then();
    }

    public ValidatableResponse loginUser(User userStellar) {
        return given()
                .spec(getBaseURL())
                .body(userStellar)
                .when()
                .post("/api/auth/login")
                .then();
    }

    public void deleteUser(String accessToken) {
        given()
                .spec(getBaseURL())
                .auth().oauth2(accessToken)
                .delete("/api/auth/user")
                .then();
    }

    public ValidatableResponse updateUser(String accessToken, User userStellar) {
        return given()
                .spec(getBaseURL())
                .body(userStellar)
                .auth().oauth2(accessToken)
                .patch("/api/auth/user")
                .then();
    }

    public ValidatableResponse updateUserNotAuth(User userStellar) {
        return given()
                .spec(getBaseURL())
                .body(userStellar)
                .patch("/api/auth/user")
                .then();
    }
}