package praktikum;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CreateOrderTest extends BaseTest {

    private final String email =
            "order" + System.currentTimeMillis() + "@yandex.ru";

    private final String password = "password123";
    private final String name = "Order User";

    private final String bunId =
            "61c0c5a71d1f82001bdaaa6d";

    private final String ingredientId =
            "61c0c5a71d1f82001bdaaa6f";

    private final String invalidIngredientId =
            "000000000000000000000000";

    private UserApi userApi;
    private OrderApi orderApi;
    private String accessToken;

    @Before
    public void setUp() {
        userApi = new UserApi();
        orderApi = new OrderApi();

        UserModel user = new UserModel(email, password, name);

        Response response = userApi.createUser(user);

        response.then()
                .statusCode(SC_OK);

        accessToken = response.path("accessToken");
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userApi.deleteUser(accessToken);
        }
    }

    @Test
    @Description("Создание заказа с авторизацией")
    public void createOrderWithAuthorizationTest() {
        OrderModel order = new OrderModel(
                Arrays.asList(bunId, ingredientId)
        );

        orderApi.createOrder(order, accessToken)
                .then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @Description("Создание заказа без авторизации")
    public void createOrderWithoutAuthorizationTest() {
        OrderModel order = new OrderModel(
                Arrays.asList(bunId, ingredientId)
        );

        orderApi.createOrderWithoutAuthorization(order)
                .then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @Description("Создание заказа с ингредиентами")
    public void createOrderWithIngredientsTest() {
        OrderModel order = new OrderModel(
                Arrays.asList(ingredientId)
        );

        orderApi.createOrder(order, accessToken)
                .then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @Description("Создание заказа без ингредиентов")
    public void createOrderWithoutIngredientsTest() {
        OrderModel order = new OrderModel();

        orderApi.createOrder(order, accessToken)
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @Description("Создание заказа с неверным хешем ингредиента")
    public void createOrderWithInvalidIngredientHashTest() {
        OrderModel order = new OrderModel(
                Arrays.asList(invalidIngredientId)
        );

        orderApi.createOrder(order, accessToken)
                .then()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }
}