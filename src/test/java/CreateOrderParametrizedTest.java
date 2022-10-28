import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)

public class CreateOrderParametrizedTest {
    public String[] color;

    public CreateOrderParametrizedTest(String[] color) {
        this.color = color;
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru";

    }

    @Parameterized.Parameters
    public static Object[][] getColor() {
        return new Object[][]{
                {new String[]{"Black"}},
                {new String[]{"Grey"}},
                {new String[]{"Black", "Grey"}},
                {new String[]{}},
        };
    }

    @Test
    @DisplayName("можно указать один из цветов — BLACK или GREY/можно указать оба цвета/можно совсем не указывать цвет")
    public void createOrderWithColor() {

        Response response =
                 given()
                .header("Content-type", "application/json")
                .body(color)
                .post("/api/v1/orders");
        response
                .then().body("track", notNullValue());

    }

}