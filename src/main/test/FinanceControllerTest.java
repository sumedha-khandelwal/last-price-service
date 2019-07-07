package main.test;


import io.restassured.RestAssured;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


import static io.restassured.RestAssured.given;

public class FinanceControllerTest {

    @BeforeClass
    public static void setup() {
        String port = System.getProperty("server.port");
        if (port == null) {
            RestAssured.port = Integer.valueOf(8080);
        }
        else{
            RestAssured.port = Integer.valueOf(port);
        }


        String basePath = System.getProperty("server.base");
        if(basePath==null){
            basePath = "/finance/";
        }
        RestAssured.basePath = basePath;

        String baseHost = System.getProperty("server.host");
        if(baseHost==null){
            baseHost = "http://localhost";
        }
        RestAssured.baseURI = baseHost;

    }


    /**
     * first step for application start
     * It would give 200 for first call , otherwise would return 400 in case we are trying to call again
     * As at 1 time , producer can be started only once
     */
    @Test
    public void testStartProducer(){
        given().when().get("/start-producer").then().body(CoreMatchers.containsString("START")).statusCode(200);
    }

    /**
     * 2nd step for batch uploading
     */
    @Test
    public void testRunProducer(){
        given().when().get("/run-producer").then().body(CoreMatchers.containsString("RUN")).statusCode(200);
    }

    /**
     * 3rd step for batch completion
     */
    @Test
    public void testStopProducer(){
        given().when().get("/stop-producer").then().body(CoreMatchers.containsString("STOP")).statusCode(200);
    }

    /**
     * Step to discard a batch
     */
    @Test
    public void testInterruptProducer(){
        given().when().get("/interrupt-producer").then().body(CoreMatchers.containsString("INTERRUPT")).statusCode(200);
    }

    /**
     * xyz is not present in db as we haven't uploaded any id with xyz name
     * hence it will return status code as 404
     */
    @Test
    public void testGetPrice(){
        given().when().get("/get-price/xyz")
                .then().statusCode(404);
    }

    /**
     * After running the complete set of batch , we have an entry of Adobe in our Db
     * hence status code is 200
     */
    @Test
    public void  testIfDataPresent(){
        given().when().get("/get-price/Adobe")
                .then().body(CoreMatchers.containsString("Adobe")).statusCode(200);
    }



}
