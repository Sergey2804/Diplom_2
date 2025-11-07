package praktikum.api;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import praktikum.model.Order;
import praktikum.model.User;
import praktikum.order.OrderClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.user.UserClient;

import java.util.ArrayList;
import java.util.List;

import static java.net.HttpURLConnection.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static praktikum.testValue.TestValue.*;


public class CreateOrderTest {
    private UserClient userClient;
    private OrderClient orderClient;

    @Before
    public void setUp() {
        userClient = new UserClient();
        orderClient = new OrderClient();
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Post запрос на ручку /api/orders")
    @Step("Создание заказа")
    public void createOrderWithoutAuth() {
        ArrayList<String> ingredients = new ArrayList<>();
        ingredients.add(TEST_BUN);
        ingredients.add(TEST_FILLING_ONE);
        ingredients.add(TEST_FILLING_TWO);
        Order orderStellar = new Order(ingredients);

        orderClient
                .orderWithoutAuth(orderStellar)
                .assertThat()
                .statusCode(HTTP_OK);
    }

    @Test
    @DisplayName("Создание заказа без авторизации, c неверным хешем")
    @Description("Post запрос на ручку /api/orders")
    @Step("Создание заказа")
    public void createOrderWithoutAuthErrorHash() {
        ArrayList<String> ingredients = new ArrayList<>();
        ingredients.add(TEST_BAD_BUN);
        ingredients.add(TEST_FILLING_ONE);
        Order orderStellar = new Order(ingredients);
        orderClient
                .orderWithoutAuth(orderStellar)
                .assertThat()
                .statusCode(HTTP_INTERNAL_ERROR);
    }

    @Test
    @DisplayName("Создание заказа без авторизации, без ингредиентов")
    @Description("Post запрос на ручку /api/orders")
    @Step("Создание заказа")
    public void createOrderWithoutAuthNoIngredient() {
        Order orderStellar = new Order(null);
        orderClient
                .orderWithoutAuth(orderStellar)
                .assertThat()
                .statusCode(HTTP_BAD_REQUEST)
                .assertThat()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    @Description("Post запрос на ручку /api/orders")
    @Step("Создание заказа")
    public void createOrderWithAuth() {
        User userStellar = new User(TEST_LOGIN_ONE, TEST_PASSWORD_ONE, TEST_NAME_ONE);
        ValidatableResponse responseCreate = userClient.createUser(userStellar).assertThat().statusCode(HTTP_OK);
        String accessTokenWithBearer = responseCreate.extract().path("accessToken");
        String accessToken = accessTokenWithBearer.replace("Bearer ", "");
        ArrayList<String> ingredients = new ArrayList<>();
        ingredients.add(TEST_BUN);
        ingredients.add(TEST_FILLING_ONE);
        ingredients.add(TEST_FILLING_TWO);
        Order orderStellar = new Order(ingredients);

        orderClient
                .orderWithAuth(accessToken, orderStellar)
                .assertThat().statusCode(HTTP_OK)
                .assertThat()
                .body("order.owner.name", equalTo(TEST_NAME_ONE))
                .and()
                .body("order.owner.email", equalTo(TEST_LOGIN_ONE));
    }

    @Test
    @DisplayName("Создание заказа с авторизацией, без ингредиентов")
    @Description("Post запрос на ручку /api/orders")
    @Step("Создание заказа")
    public void createOrderWithAuthNoIngredient() {
        User userStellar = new User(TEST_LOGIN_ONE, TEST_PASSWORD_ONE, TEST_NAME_ONE);
        ValidatableResponse responseCreate = userClient.createUser(userStellar).assertThat().statusCode(HTTP_OK);
        String accessTokenWithBearer = responseCreate.extract().path("accessToken");
        String accessToken = accessTokenWithBearer.replace("Bearer ", "");
        Order orderStellar = new Order(null);
        orderClient
                .orderWithAuth(accessToken, orderStellar)
                .assertThat().statusCode(HTTP_BAD_REQUEST)
                .assertThat()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с авторизацией с неверным хешем")
    @Description("Post запрос на ручку /api/orders")
    @Step("Создание заказа")
    public void createOrderWithAuthErrorHash() {
        User userStellar = new User(TEST_LOGIN_ONE, TEST_PASSWORD_ONE, TEST_NAME_ONE);
        ValidatableResponse responseCreate = userClient.createUser(userStellar).assertThat().statusCode(HTTP_OK);
        String accessTokenWithBearer = responseCreate.extract().path("accessToken");
        String accessToken = accessTokenWithBearer.replace("Bearer ", "");
        ArrayList<String> ingredients = new ArrayList<>(List.of(TEST_BAD_BUN, TEST_FILLING_TWO));
        Order orderStellar = new Order(ingredients);
        orderClient
                .orderWithAuth(accessToken, orderStellar)
                .assertThat()
                .statusCode(500);
    }

    @After
    public void clearData() {
        try {
            User userStellar = new User(TEST_LOGIN_ONE, TEST_PASSWORD_ONE, TEST_NAME_ONE);
            ValidatableResponse responseLogin = userClient.loginUser(userStellar);
            String accessTokenWithBearer = responseLogin.extract().path("accessToken");
            String accessToken = accessTokenWithBearer.replace("Bearer ", "");
            userClient.deleteUser(accessToken);
        } catch (Exception e) {
            System.out.println("Завершилось без удаления");
        }
    }
}
