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
import static org.hamcrest.Matchers.notNullValue;

public class LoginCourierTest {

    Faker faker;
    Courier courier;
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

        courier = new Courier(login, password, firstName);

        given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");

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
    @DisplayName("курьер может авторизоваться")
    public void canLogIn() {

        Response response = given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier/login");
        response
                .then().statusCode(200);

    }

    @Test
    @DisplayName("для авторизации нужно передать все обязательные поля")
    public void LogInWithAllRequiredFields () {

        Response response = given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier/login");
        response
                .then().statusCode(200);

    }

    @Test
    @DisplayName("система вернёт ошибку, если неправильно указать логин или пароль")
    public void logInWithWrongField() {

        courier.setPassword(password + password);

        Response responseLogIn = given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier/login");
        responseLogIn
                .then().assertThat().body("message", is("Учетная запись не найдена"))
                .and().statusCode(404);

    }

    @Test
    @DisplayName("если какого-то поля нет, запрос возвращает ошибку")
    public void logInWithoutField() {

        courier.setPassword("");

        Response responseLogIn = given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier/login");
        responseLogIn
                .then().assertThat().body("message", is("Недостаточно данных для входа"))
                .and().statusCode(400);

    }

    @Test
    @DisplayName("если авторизоваться под несуществующим пользователем, запрос возвращает ошибку")
    public void logInNotExist() {

        courier.setLogin(login + login);

        Response responseLogIn = given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier/login");
        responseLogIn
                .then().assertThat().body("message", is("Учетная запись не найдена"))
                .and().statusCode(404);

    }

    @Test
    @DisplayName("успешный запрос возвращает id")
    public void checkSuccessfulRequest() {

        Response responseLogIn = given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier/login");
        responseLogIn
                .then().assertThat().body("id", notNullValue())
                .and().statusCode(200);

    }

}