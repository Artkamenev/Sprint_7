import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.Courier;
import net.datafaker.Faker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class CreateCourierTest {

    Faker faker;
    String login;
    String password;
    String firstName;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
        faker = new Faker();
        login = faker.name().firstName();
        password = String.valueOf(faker.password());
        firstName = String.valueOf(faker.name());
    }

    @After
    public void cleanUp() {
        Courier courier = new Courier(login, password);

        String id = given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier/login").body().asPrettyString().replaceAll("[^0123456789]", "");

        given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .delete("/api/v1/courier/" + id);

    }


    @Test
    @DisplayName("курьера можно создать")
    public void canCreateCourier() {
        Courier courier = new Courier(login, password, firstName);

        Response response = given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
        response
                .then().assertThat().body("ok", is(true))
                .and().statusCode(201);
    }

    @Test
    @DisplayName("нельзя создать двух одинаковых курьеров")
    public void createTwoSameCourier() {
        Courier courier = new Courier(login, password, firstName);

        given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");

        Response response = given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
        response
                .then().assertThat().body("message", is("Этот логин уже используется. Попробуйте другой."))
                .statusCode(409);
    }

    @Test
    @DisplayName("чтобы создать курьера, нужно передать в ручку все обязательные поля")
    public void createCourierWithAllRequiredFields() {
        Courier courier = new Courier(login, password, null);

        Response response = given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
        response
                .then().assertThat().body("ok", is(true))
                .and().statusCode(201);
    }

    @Test
    @DisplayName("запрос возвращает правильный код ответа")
    public void checkCorrectResponseCode() {
        Courier courier = new Courier(login, password, firstName);

        Response response = given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
        response
                .then().statusCode(201);
    }

    @Test
    @DisplayName("успешный запрос возвращает ok: true")
    public void checkCorrectBodyReturnOkAndTrue() {
        Courier courier = new Courier(login, password, firstName);

        Response response = given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
        response
                .then().body("ok", is(true));
    }

    @Test
    @DisplayName("если одного из полей нет, запрос возвращает ошибку")
    public void createWithEmptyRequiredField() {
        Courier courier = new Courier(null, password, firstName);

        Response response = given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
        response
                .then().assertThat().body("message", is("Недостаточно данных для создания учетной записи"))
                .and().statusCode(400);
    }

    @Test
    @DisplayName("если создать пользователя с логином, который уже есть, возвращается ошибка")
    public void createCourierWithSameLogin() {
        Courier courier = new Courier(login, password, firstName);

        String newPassword = String.valueOf(faker.password());
        String newFirstName = String.valueOf(faker.name());
        Courier courierWithSameLogin = new Courier(login, newPassword, newFirstName);

        given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");

        Response response = given()
                .header("Content-type", "application/json")
                .body(courierWithSameLogin)
                .when()
                .post("/api/v1/courier");
        response
                .then().assertThat().body("message", is("Этот логин уже используется. Попробуйте другой."))
                .and().statusCode(409);

    }
}