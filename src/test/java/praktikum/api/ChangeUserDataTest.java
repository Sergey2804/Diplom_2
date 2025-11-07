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

import static praktikum.testValue.TestValue.*;


public class ChangeUserDataTest {
    private UserClient userClient;
    private User user;
    private ValidatableResponse response;

    @Before
    @Step("Создание пользователя")
    public void setUp() {
        userClient = new UserClient();
        user = new User(TEST_LOGIN_ONE, TEST_PASSWORD_ONE, TEST_NAME_ONE);
        response = userClient.createUser(user);
    }

    @Test
    @DisplayName("Изменение информации о пользователе с авторизацией. Ответ 200")
    @Description("Patch запрос на ручку /api/auth/user")
    @Step("Изменение информации")
    public void updateUserWithAuth() {
        User user = new User(TEST_LOGIN_ONE, TEST_PASSWORD_ONE, TEST_NAME_ONE);
        ValidatableResponse responseLogin = userClient.loginUser(user);
        String accessToken = extractToken(responseLogin).replace("Bearer ", "");
        User userTwo = new User(TEST_LOGIN_TWO, TEST_PASSWORD_TWO, TEST_NAME_TWO);
        userClient
                .updateUser(accessToken, userTwo)
                .assertThat()
                .statusCode(200);
    }

    @Test
    @DisplayName("Изменение информации о пользователе без авторизации. Ответ 401")
    @Description("Patch запрос на ручку /api/auth/user")
    @Step("Изменение информации")
    public void updateUserWithoutAuth() {
        User user = new User(TEST_LOGIN_ONE, TEST_PASSWORD_ONE, TEST_NAME_ONE);
        ValidatableResponse responseLogin = userClient.loginUser(user);
        String accessToken = extractToken(responseLogin).replace("Bearer ", "");
        User userTwo = new User(TEST_LOGIN_TWO, TEST_PASSWORD_TWO, TEST_NAME_TWO);
        userClient
                .updateUserNotAuth(userTwo)
                .assertThat()
                .statusCode(401);

        userClient.deleteUser(accessToken);
    }

    private String extractToken(ValidatableResponse responseLogin){
        return responseLogin.extract().path("accessToken");
    }

    @After
    @Step("Удаление пользователя")
    public void clearData() {
        try {
            User userTwo = new User(TEST_LOGIN_TWO, TEST_PASSWORD_TWO, TEST_NAME_TWO);
            ValidatableResponse responseLogin = userClient.loginUser(userTwo);
            String accessToken = extractToken(responseLogin).replace("Bearer ", "");
            userClient.deleteUser(accessToken);
        } catch (Exception e) {
            System.out.println("Пользователь не удалился");
        }
    }
}
