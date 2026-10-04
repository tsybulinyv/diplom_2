package praktikum;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CreateUserTest extends BaseTest {

    private String email;
    private final String password = "password123";
    private final String name = "Test User";
    private UserApi userApi;
    private String accessToken;

    @Before
    public void setUp() {
        userApi = new UserApi();
        email = "test" + System.currentTimeMillis() + "@yandex.ru";
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userApi.deleteUser(accessToken);
        }
    }

    @Test
    @Description("Создание уникального пользователя")
    public void createUniqueUserTest() {
        UserModel user = new UserModel(email, password, name);

        Response response = userApi.createUser(user);

        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("user.email", equalTo(email))
                .body("user.name", equalTo(name))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());

        accessToken = response.path("accessToken");
    }

    @Test
    @Description("Попытка создания уже зарегистрированного пользователя")
    public void createExistingUserTest() {
        UserModel user = new UserModel(email, password, name);

        Response response = userApi.createUser(user);

        response.then()
                .statusCode(SC_OK);

        accessToken = response.path("accessToken");

        userApi.createUser(user)
                .then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @Description("Попытка создания пользователя без email")
    public void createUserWithoutEmailTest() {
        UserModel user = new UserModel(null, password, name);

        userApi.createUser(user)
                .then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @Description("Попытка создания пользователя без password")
    public void createUserWithoutPasswordTest() {
        UserModel user = new UserModel(email, null, name);

        userApi.createUser(user)
                .then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @Description("Попытка создания пользователя без name")
    public void createUserWithoutNameTest() {
        UserModel user = new UserModel(email, password, null);

        userApi.createUser(user)
                .then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}