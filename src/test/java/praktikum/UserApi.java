package praktikum;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class UserApi {

    private static final String REGISTER_ENDPOINT = "/api/auth/register";
    private static final String LOGIN_ENDPOINT = "/api/auth/login";
    private static final String USER_ENDPOINT = "/api/auth/user";

    @Step("Создать пользователя")
    public Response createUser(UserModel user) {
        return given()
                .filter(new AllureRestAssured())
                .contentType("application/json")
                .body(user)
                .when()
                .post(REGISTER_ENDPOINT);
    }

    @Step("Войти под пользователем")
    public Response loginUser(UserModel user) {
        return given()
                .filter(new AllureRestAssured())
                .contentType("application/json")
                .body(user)
                .when()
                .post(LOGIN_ENDPOINT);
    }

    @Step("Удалить пользователя")
    public Response deleteUser(String accessToken) {
        return given()
                .filter(new AllureRestAssured())
                .header("Authorization", accessToken)
                .when()
                .delete(USER_ENDPOINT);
    }
}