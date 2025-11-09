package praktikum.api;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import praktikum.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.user.UserClient;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static praktikum.testValue.TestValue.*;


public class LoginUserTest {
    private UserClient userClient;
    private ValidatableResponse responseLogin;
    private User userStellar;


    @Before
    public void setUp() {
        userClient = new UserClient();
        userStellar = new User(TEST_LOGIN_ONE, TEST_PASSWORD_ONE, TEST_NAME_ONE);
        responseLogin = userClient.createUser(userStellar);
    }

    @Test
    @DisplayName("Логин существующего пользователя.")
    @Description("Post запрос на ручку /api/auth/login")
    public void loginWithUserTrueAndBody() {
        responseLogin.assertThat().statusCode(HTTP_OK);
        responseLogin.assertThat().body("success", equalTo(true));
        responseLogin.assertThat().body("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjY5MDcyMTFiOTg0MjcwMDAxYmRlYzc3YiIsImlhdCI6MTc2MjA3NDkxNCwiZXhwIjoxNzYyMDc2MTE0fQ.dio6EEwQ-DiPCKqwddvOBn4WERCg5QtkFz3zqyGGH3Y", startsWith("Bearer "))
                .and()
                .body("refreshToken", notNullValue());
        responseLogin.assertThat().body("user.email", equalTo(TEST_LOGIN_ONE))
                .and()
                .body("user.name", equalTo(TEST_NAME_ONE));
    }

    @Test
    @DisplayName("Логин с неверным адресом почты.")
    @Description("Post запрос на ручку /api/auth/login")
    public void loginWithUserFalseAndBody() {
        userStellar.setEmail(TEST_LOGIN_TWO);
        userClient.loginUser(userStellar).assertThat().statusCode(HTTP_UNAUTHORIZED);
        userClient.loginUser(userStellar)
                .assertThat().body("success", equalTo(false))
                .and()
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Логин под неверным паролем.")
    @Description("Post запрос на ручку /api/auth/login")
    public void loginWithUserFalsePasswordAndBody() {
        userStellar.setPassword(TEST_PASSWORD_TWO);
        ValidatableResponse loginResponse = userClient.loginUser(userStellar);

        loginResponse.assertThat().statusCode(HTTP_UNAUTHORIZED);
        loginResponse.assertThat().body("success", equalTo(false))
                .and()
                .body("message", equalTo("email or password are incorrect"));
    }

    @After
    public void clearData() {
        try {
            String accessTokenWithBearer = responseLogin.extract().path("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjY5MDcyMTFiOTg0MjcwMDAxYmRlYzc3YiIsImlhdCI6MTc2MjA3NDkxNCwiZXhwIjoxNzYyMDc2MTE0fQ.dio6EEwQ-DiPCKqwddvOBn4WERCg5QtkFz3zqyGGH3Y");
            String accessToken = accessTokenWithBearer.replace("Bearer ", "");
            userClient.deleteUser(accessToken);
            System.out.println("удален");
        } catch (Exception e) {
            System.out.println("Пользователь не удалился. Возможно ошибка при создании");
        }
    }
}
