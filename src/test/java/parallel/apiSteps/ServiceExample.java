package parallel.apiSteps;

import apiHandlers.ApiUtils;
import apiPOJO.exampleOnePojo.secondLayerExample.ExampleResource;
import apiPOJO.exampleOnePojo.secondLayerExample.apiThirdLayerExamplePojo.Root;
import io.restassured.response.Response;

import java.io.IOException;


public class ServiceExample extends ApiUtils {


    Response response;

    Root[] exampleObjectResponse;

    String strOne;

    /**
     * @param resource not mandatory but in some API calls necessary parameter which determines specific target.
     *                 For example if you wish to delete some user you would call DELETE API and specify his ID which is resource.
     */
    private void searchMerchantDataExtraction(String resource) throws IOException {
        response = GET(requestSpecificationForGETWithHeader(), ExampleResource.exampleResourceTwo.getResource(resource), 200);
        exampleObjectResponse = getObjectMapper().readValue(response.asString(), Root[].class);
        for (Root root : exampleObjectResponse) {
            strOne = root.getResult().get(0).getReference();
        }
    }



}
