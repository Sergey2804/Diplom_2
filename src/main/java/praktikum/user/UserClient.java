package praktikum.user;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import praktikum.model.BaseURL;
import praktikum.model.User;

import static io.restassured.RestAssured.given;

public class UserClient extends BaseURL {


    @Step("Создание нового пользователя")
    public ValidatableResponse createUser(User userStellar) {
        return given()
                .spec(getBaseURL())
                .body(userStellar)
                .when()
                .post("/api/auth/register")
                .then();
    }

    @Step("Вход по логину уже существующего пользователя")
    public ValidatableResponse loginUser(User userStellar) {
        return given()
                .spec(getBaseURL())
                .body(userStellar)
                .when()
                .post("/api/auth/login")
                .then();
    }

    @Step("Удаление пользователя")
    public void deleteUser(String accessToken) {
        given()
                .spec(getBaseURL())
                .auth().oauth2(accessToken)
                .delete("/api/auth/user")
                .then();
    }

    @Step("Изменение данных авторизованного пользователя")
    public ValidatableResponse updateUser(String accessToken, User userStellar) {
        return given()
                .spec(getBaseURL())
                .body(userStellar)
                .auth().oauth2(accessToken)
                .patch("/api/auth/user")
                .then();
    }

    @Step("Изменение данных неавторизованного пользователя")
    public ValidatableResponse updateUserNotAuth(User userStellar) {
        return given()
                .spec(getBaseURL())
                .body(userStellar)
                .patch("/api/auth/user")
                .then();
    }
}