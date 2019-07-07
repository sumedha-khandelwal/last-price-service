package main.java.controller;

import main.java.model.FinancialData;
import main.java.model.Status;
import main.java.service.DataConsumeService;
import main.java.service.DataPublishService;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * This is the starting point of the application, all the api's are exposed through this
 * controller/api
 */
@Path("/finance")
public class FinanceController {

    private static final Logger logger = Logger.getLogger(FinanceController.class);

    /**
     * This api returns the last updated value from master copy of in memory db
     * In case the value is not found, it returns no value found
     * This is basically the consumer api
     * @param id
     * @return
     * @throws JSONException
     */
    @Path("/get-price/{id}")
    @GET
    @Produces("application/json")
    public Response getPrice(@PathParam("id") String id) throws JSONException {

        try{
            JSONObject jsonObject = new JSONObject();
            DataConsumeService dcs=DataConsumeService.getInstance();
            if(id!=null && !id.equals("")){
                FinancialData fd=dcs.getValue(id);
                if(fd!=null){
                    return Response.status(200).entity(fd.toString()).build();
                }
                else {
                    return Response.status(404).entity("No value found").build();
                }

            }

        }
        catch (Exception e){
          logger.error("Error while getting price for :"+id , e);

        }
        return Response.status(404).entity("No value found").build();

    }

    /**
     * This api has to be called before producing/uploading any batch of data
     * This is the entry point for producing the data
     * @return
     */
    @Path("/start-producer")
    @GET
    @Produces("application/json")
    public Response startProducer(){
        try{
            DataPublishService dps=DataPublishService.getInstance();
            if(((Status.IDLE).name()).equals(dps.getStatus().get().name())){
                dps.getStatus().set(Status.START);
                return Response.status(200).entity((Status.START).name()).build();
            }

            else{
                return Response.status(400).entity("Producer not idle").build();
            }

        }
        catch (Exception e){
            logger.error("Error while starting the thread",e);
            return Response.status(500).entity("Error while starting").build();
        }
    }

    /**
     * Once the producer has called the start api , this is the second step for uploading the
     * batch of data, it will not work until the producer has started
     * @return
     */
    @Path("/run-producer")
    @GET
    @Produces("application/json")
    public Response runProducer(){
        try{
            DataPublishService dps=DataPublishService.getInstance();
            if(((Status.START).name()).equals(dps.getStatus().get().name()) || ((Status.RUN).name()).equals(dps.getStatus().get().name())){
                dps.getStatus().set(Status.RUN);
                Thread t1=new Thread(()-> {
                    dps.runPublisher();
                });

                t1.start();
                return Response.status(200).entity((Status.RUN).name()).build();
            }

            else {
                return Response.status(400).entity("Producer not started").build();
            }


        }
        catch (Exception e){
            logger.error("Error while running producer :" , e);
            return Response.status(500).entity("Error while running").build();
        }

    }

    /**
     * This api is the last point for producer , once all the data has been uploaded.
     * It can be used to notify producer that batch has been completed.
     * @return
     */
    @Path("/stop-producer")
    @GET
    @Produces("application/json")
    public Response stopProducer(){

        try{
            DataPublishService dps=DataPublishService.getInstance();
            if(((Status.RUN).name()).equals(dps.getStatus().get().name())){
               dps.stopPublishing();
                return Response.status(200).entity((Status.STOP).name()).build();
            }

            else{
                return Response.status(400).entity("Producer not running").build();
            }

        }
        catch (Exception e){
          logger.error("Error while stopping the thread",e);
          return Response.status(500).entity("Error while stopping").build();
        }

    }

    /**
     * This api can be called while a batch is running , it is used to notify the producer to cancel/discard this
     * batch of records
     * @return
     */
    @Path("/interrupt-producer")
    @GET
    @Produces("application/json")
    public Response interruptProducer(){
        try{
            DataPublishService dps=DataPublishService.getInstance();
            if(((Status.RUN).name()).equals(dps.getStatus().get().name())){
               dps.interruptPublish();
                return Response.status(200).entity((Status.INTERRUPT).name()).build();
            }

            else{
                return Response.status(400).entity("Producer not running").build();
            }

        }
        catch (Exception e){
            logger.error("Error while interrupting the thread",e);
            return Response.status(500).entity("Error while interrupting the thread").build();
        }

    }
}
