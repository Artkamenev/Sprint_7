import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.Order;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class CreateOrderTest {

    Order order;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
        order = new Order();
    }

    @Test
    @DisplayName("тело ответа содержит track")
    public void createWithBlackColor() {

       Response response =
        given()
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post("/api/v1/orders");
        response
                .then().assertThat().body("track", notNullValue());
    }

}