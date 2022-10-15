import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class ListOrderTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";

    }

    @Test
    @DisplayName("в тело ответа возвращается список заказов")
    public void checkBodyListOrderIsNotEmpty() {

                given()
                        .header("Content-type", "application/json")
                        .get("/api/v1/orders")
                        .then().assertThat().body("orders", notNullValue())
                        .and().statusCode(200);

    }

}