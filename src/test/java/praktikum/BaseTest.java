package praktikum;

import io.restassured.RestAssured;
import org.junit.BeforeClass;

public class BaseTest {

    @BeforeClass
    public static void setUpBaseUrl() {
        RestAssured.baseURI = "https://stellarburgers.education-services.ru";
    }
}