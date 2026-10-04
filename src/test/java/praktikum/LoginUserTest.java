package praktikum;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class LoginUserTest extends BaseTest {

    private String email;
    private final String password = "password123";
    private final String name = "Test User";

    private UserApi userApi;
    private String accessToken;

    @Before
    public void setUp() {
        userApi = new UserApi();

        email = "login" + System.currentTimeMillis() + "@yandex.ru";

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
    @Description("Вход под существующим пользователем")
    public void loginExistingUserTest() {
        UserModel user = new UserModel(email, password, name);

        userApi.loginUser(user)
                .then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .body("user.email", equalTo(email))
                .body("user.name", equalTo(name));
    }

    @Test
    @Description("Вход с неверным логином")
    public void loginWithIncorrectEmailTest() {
        UserModel user = new UserModel(
                "wrong" + System.currentTimeMillis() + "@yandex.ru",
                password,
                name
        );

        userApi.loginUser(user)
                .then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @Description("Вход с неверным паролем")
    public void loginWithIncorrectPasswordTest() {
        UserModel user = new UserModel(
                email,
                "wrongPassword",
                name
        );

        userApi.loginUser(user)
                .then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}