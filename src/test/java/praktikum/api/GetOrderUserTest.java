package praktikum.api;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import praktikum.model.User;
import praktikum.model.Order;
import praktikum.order.OrderClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.testValue.TestValue;
import praktikum.user.UserClient;

import java.util.ArrayList;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.empty;


public class GetOrderUserTest {

    private UserClient userClient;
    private OrderClient orderClient;
    private String accessToken;
    private User testUser;
    private Order testOrder;

    @Before
    public void setUp() {
        userClient = new UserClient();
        orderClient = new OrderClient();
        testUser = new User(
                TestValue.TEST_LOGIN_ONE,
                TestValue.TEST_PASSWORD_ONE,
                TestValue.TEST_NAME_ONE
        );
        testOrder = new Order(
                (ArrayList<String>) List.of(
                        TestValue.TEST_BUN,
                        TestValue.TEST_FILLING_ONE,
                        TestValue.TEST_FILLING_TWO
                )
        );
    }

    @Test
    @DisplayName("Получение заказов авторизованного пользователя")
    @Description("Get запрос на ручку api/orders")
    public void getOrderAuthUser() {
        String accessTokenWithBearer = createUserAndGetToken(testUser);
        accessToken = extractAccessToken(accessTokenWithBearer);

        orderClient.orderWithAuth(accessToken, testOrder).statusCode(200);

        orderClient.getOrderUserAuth(accessToken)
                .statusCode(200)
                .body("success", equalTo(true))
                .body("orders", not(empty()));
    }

    @Test
    @DisplayName("Получение заказов не авторизованного пользователя.")
    @Description("Get запрос на ручку api/orders")
    public void getOrderNotAuthUser() {
        String accessTokenWithBearer = createUserAndGetToken(testUser);
        accessToken = extractAccessToken(accessTokenWithBearer);

        orderClient.orderWithAuth(accessToken, testOrder).statusCode(200);

        orderClient.getOrderUserNotAuth()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @After
    public void clearData() {
        try {
            String accessTokenWithBearer = loginUserAndGetToken(testUser);
            accessToken = extractAccessToken(accessTokenWithBearer);
            userClient.deleteUser(accessToken);
        } catch (Exception e) {
            System.out.println("Завершилось без удаления");
        }
    }

    private String createUserAndGetToken(User user) {
        return userClient.createUser(user)
                .statusCode(HTTP_OK)
                .extract().path("accessToken");
    }

    private String loginUserAndGetToken(User user) {
        return userClient.loginUser(user)
                .statusCode(HTTP_OK)
                .extract().path("accessToken");
    }

    private String extractAccessToken(String tokenWithBearer) {
        return tokenWithBearer.replace("Bearer ", "");
    }
}

