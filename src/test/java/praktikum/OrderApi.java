package praktikum;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class OrderApi {

    private static final String ORDERS_ENDPOINT = "/api/orders";

    @Step("Создать заказ с авторизацией")
    public Response createOrder(OrderModel order, String accessToken) {
        return given()
                .filter(new AllureRestAssured())
                .contentType("application/json")
                .header("Authorization", accessToken)
                .body(order)
                .when()
                .post(ORDERS_ENDPOINT);
    }

    @Step("Создать заказ без авторизации")
    public Response createOrderWithoutAuthorization(OrderModel order) {
        return given()
                .filter(new AllureRestAssured())
                .contentType("application/json")
                .body(order)
                .when()
                .post(ORDERS_ENDPOINT);
    }
}