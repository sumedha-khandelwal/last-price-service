package main.java.service;

import main.java.controller.FinanceController;
import main.java.dao.FinancialDataDaoImpl;
import main.java.model.Status;
import main.java.util.PropertyReader;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This class basically seperates the api layer from dao layer
 * It acts as a service between dao and api layer, all the request from the api service are
 * handled here
 * Singleton class
 */
public class DataPublishService {

    private static final Logger logger = Logger.getLogger(DataPublishService.class);

    private final static DataPublishService dps=new DataPublishService();

    private AtomicReference<Status> status = new AtomicReference<Status>();

    private ExecutorService pool=null;

    private DataPublishService(){

    }

    {
        status.set(Status.IDLE);
    }

    public AtomicReference<Status> getStatus() {
        return status;
    }

    public void setStatus(AtomicReference<Status> status) {
        this.status = status;
    }


    public static DataPublishService getInstance(){
        return DataPublishService.dps;
    }

    /**
     * This function is basically reading the file and saving the details in a temporary in memory db
     */
    public void  runPublisher(){
       try {
           pool = Executors.newFixedThreadPool(10);
           FileInputStream inputStream = null;
           Scanner sc = null;

           List<String> list=new ArrayList<>();
           PropertyReader reader=PropertyReader.getInstance();
           String file=reader.getPropValue("filename");
           if(file!=null && !file.equals("")){
               inputStream = new FileInputStream(file);
               sc = new Scanner(inputStream, "UTF-8");
               if (status.get().equals(Status.RUN)) {
                   while (sc.hasNextLine() && status.get().equals(Status.RUN)) {
                       String line = sc.nextLine();
                       if(line!=null && !line.equalsIgnoreCase("")){
                           list.add(line);
                           if(list.size()==1000){
                               Runnable publishTask =new PublishTask(Status.RUN,list);
                               pool.execute(publishTask);
                               list=new ArrayList<>();
                           }
                       }
                   }

                   if (sc.ioException() != null) {
                       throw sc.ioException();
                   }

                   if(list.size()>0 && status.get().equals(Status.RUN)){
                       Runnable publishTask =new PublishTask(Status.RUN,list);
                       pool.execute(publishTask);
                       list=new ArrayList<>();
                   }
               }
           }

       }
       catch (Exception e){
        logger.error("Error while running publisher",e);
       }
    }

    /**
     * This function is notifying that the batch has completed, now we can move our data to
     * the master copy
     */
    public void stopPublishing(){
        try{
            status.set(Status.STOP);
            pool.shutdown();
            pool.awaitTermination(1000, TimeUnit.MILLISECONDS);

            FinancialDataDaoImpl fd=FinancialDataDaoImpl.getInstance();
            fd.updateMaster();

            status.set(Status.IDLE);
        }
        catch (Exception e){
            logger.error("Error while stopping publisher",e);
        }
    }

    /**
     * This function is interrupting the producer and discarding the values from temporary db
     */
    public void interruptPublish(){
        try{
            status.set(Status.INTERRUPT);
            pool.shutdownNow();
            if (pool.isTerminated()){
                FinancialDataDaoImpl fd=FinancialDataDaoImpl.getInstance();
                fd.clearData();
            }
            status.set(Status.IDLE);
        }
        catch (Exception e){
            logger.error("Error while interrupting publisher",e);
        }
    }


}
