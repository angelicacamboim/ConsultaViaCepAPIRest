package Tests;

import io.restassured.builder.*;
import org.junit.BeforeClass;
import org.junit.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class ConsultaCEP {

    @BeforeClass
    public static void setup(){
        baseURI = "https://viacep.com.br/";
        //RestAssured.port = APP_PORT;
        basePath = "ws";

        //tempo de resposta aceitável
        ResponseSpecBuilder resBuilder =  new ResponseSpecBuilder();
        resBuilder.expectResponseTime(lessThan(8000L));
        responseSpecification = resBuilder.build();

        enableLoggingOfRequestAndResponseIfValidationFails();

    }
    @Test
    public void consultaCepComSucesso(){

       given()
                .pathParam("cep", "01001000")
               .when()
                    .get("/{cep}/json")
                .then()
                    .statusCode(200)
                    .body("cep", is("01001-000"))
                    .body("logradouro", is("Praça da Sé"))
                    .body("complemento", is("lado ímpar"))
                    .body("bairro", is("Sé"))
                    .body("localidade", is("São Paulo"))
                    .body("uf", is("SP"))
        ;
    }
    @Test
    public void consultaCepPorEnderecoComSucesso(){

        given()
                    .pathParam("uf", "RS")
                    .pathParam("cidade", "Canoas")
                    .pathParam("logradouro", "Venancio")
                .when()
                    .get("/{uf}/{cidade}/{logradouro}/json")
                .then()
                    .statusCode(200)
                    .body("$", hasSize(2))//array Raiz $
                    .body("cep", hasItems("92110-000", "92110-001"))
                    .body("uf", hasItems("RS"))
                    .body("localidade", hasItems("Canoas"))
                    .body("logradouro", hasItems("Rua Venâncio Aires"))
                    .body("complemento", hasItems("até 2039/2040", "de 2041/2042 ao fim"))
                    .body("bairro", hasItems("Niterói"))

        ;
    }
    @Test
    public void consultaCepValidoEInexistente(){

        given()
                    .pathParam("cep", "99999999")
                .when()
                    .get("/{cep}/json")
                .then()
                    .statusCode(200)
                    .body("erro", is(true))
        ;
    }
    @Test
    public void consultaCepFormatoInvalidoComLetras(){

        given()
                    .pathParam("cep", "95010A10")
                .when()
                    .get("/{cep}/json")
                .then()
                    .statusCode(400)
        ;
    }
    @Test
    public void consultaCepFormatoInvalidoComMaisDe8Digitos(){

        given()
                    .pathParam("cep", "921100011")
                .when()
                    .get("/{cep}/json")
                .then()
                    .statusCode(400)
        ;
    }
    @Test
    public void consultaCepPorEnderecoComMenosDeTresCaracteres(){//https://viacep.com.br/ws/RS/Ca/Ve/json/

        given()
                    .pathParam("uf", "RS")
                    .pathParam("cidade", "Ca")
                    .pathParam("logradouro", "Ve")
                .when()
                    .get("/{uf}/{cidade}/{logradouro}/json")
                .then()
                    .statusCode(400)
        ;
    }
    @Test
    public void consultaCepValidarCamposObrigatorios(){

        given()
                    .pathParam("cep", "")
                .when()
                    .get("/{cep}/json")
                .then()
                    .statusCode(400)
        ;
    }

}
