package praktikum.api;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import praktikum.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import praktikum.user.UserClient;

import static org.hamcrest.CoreMatchers.*;
import static praktikum.testValue.TestValue.*;

public class CreateUserTest {
    private UserClient userClient;
    private ValidatableResponse responseLogin;
    private User userStellar;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setUp() {
        userClient = new UserClient();
        userStellar = new User(TEST_LOGIN_ONE, TEST_PASSWORD_ONE, TEST_NAME_ONE);
        responseLogin = userClient.createUser(userStellar);
    }

    @Test
    @DisplayName("Создать уникального пользователя.")
    @Description("Post запрос на ручку /api/v1/courier")
    public void createUniqueUserAndBodyTest() {
        responseLogin
                .statusCode(200)
                .body("user.email", equalTo(TEST_LOGIN_ONE))
                .body("user.name", equalTo(TEST_NAME_ONE))
                .body("accessToken", startsWith("Bearer "))
                .body("refreshToken", notNullValue())
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Создать пользователя, который уже зарегистрирован.")
    @Description("Post запрос на ручку /api/v1/courier")
    public void createRegisteredUserAndBodyTest() {
        ValidatableResponse response = userClient.createUser(userStellar);
        response
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создать пользователя и не заполнить пароль.")
    @Description("Post запрос на ручку /api/v1/courier")
    public void createUserWithoutPasswordTest() {
        ValidatableResponse response = userClient.createUser(new User(TEST_LOGIN_ONE, null, TEST_NAME_ONE));
        response
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создать пользователя без заполнения поля почты.")
    @Description("Post запрос на ручку /api/v1/courier")
    public void createUserWithoutEmailTest() {
        ValidatableResponse response = userClient.createUser(new User(null, TEST_PASSWORD_ONE, TEST_NAME_ONE));
        response
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создать пользователя без заполнения поля имя пользователя.")
    @Description("Post запрос на ручку /api/v1/courier")
    public void createUserWithoutNameTest() {
        ValidatableResponse response = userClient.createUser(new User(TEST_LOGIN_ONE, TEST_PASSWORD_ONE, null));
        response
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @After
    public void clearData() {
        try {
            String accessTokenWithBearer = responseLogin.extract().path("accessToken");
            if (accessTokenWithBearer != null) {
                String accessToken = accessTokenWithBearer.replace("Bearer ", "");
                userClient.deleteUser(accessToken);
                System.out.println("Пользователь успешно удален.");
            } else {
                System.out.println("Токен отсутствует. Пользователь не был удален.");
            }
        } catch (Exception e) {
            System.out.println("Пользователь не удалился из-за ошибки при создании: " + e.getMessage());
        }
    }
}
