package praktikum;

import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class LoginUserTest {

    private String email;
    private final String password = "password123";
    private final String name = "Test User";

    private UserApi userApi;
    private String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.education-services.ru";
        userApi = new UserApi();

        email = "login" + System.currentTimeMillis() + "@yandex.ru";

        UserModel user = new UserModel(email, password, name);

        Response response = userApi.createUser(user);

        response.then()
                .statusCode(200);

        accessToken = response.path("accessToken");
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userApi.deleteUser(accessToken);
        }
    }

    @Test
    @Description("Вход под существующим пользователем")
    public void loginExistingUserTest() {
        UserModel user = new UserModel(email, password, name);

        userApi.loginUser(user)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .body("user.email", equalTo(email))
                .body("user.name", equalTo(name));
    }

    @Test
    @Description("Вход с неверным логином и паролем")
    public void loginWithIncorrectCredentialsTest() {
        UserModel user = new UserModel(
                "wrong" + System.currentTimeMillis() + "@yandex.ru",
                "wrongPassword",
                name
        );

        userApi.loginUser(user)
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}