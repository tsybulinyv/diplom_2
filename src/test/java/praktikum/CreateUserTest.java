package praktikum;

import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CreateUserTest {

    private String email;
    private final String password = "password123";
    private final String name = "Test User";

    private UserApi userApi;
    private String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.education-services.ru";
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
                .statusCode(200)
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
                .statusCode(200);

        accessToken = response.path("accessToken");

        userApi.createUser(user)
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @Description("Попытка создания пользователя без обязательного поля")
    public void createUserWithoutRequiredFieldTest() {
        UserModel user = new UserModel(email, password, null);

        userApi.createUser(user)
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}