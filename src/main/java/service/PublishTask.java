package main.java.service;

import main.java.dao.FinancialDataDaoImpl;
import main.java.model.FinancialData;
import main.java.model.Payload;
import main.java.model.Status;
import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This class is used as a thread class .
 * Multiple instances are submitted in thread pool
 */
public class PublishTask implements Runnable {

    private static final Logger logger = Logger.getLogger(PublishTask.class);

    private AtomicReference<Status> status = new AtomicReference<Status>();

    public AtomicReference<Status> getStatus() {
        return status;
    }

    public void setStatus(AtomicReference<Status> status) {
        this.status = status;
    }

    private List<String> list=new ArrayList<>();

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public PublishTask(Status status,List<String> list){
        this.status.set(status);
        this.list=list;
    }

    /**
     * Reads the list of data published by publish service and saves it in temporary db until the producer has completed
     */
    @Override
    public void run(){
        FinancialDataDaoImpl financialDataDao=FinancialDataDaoImpl.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        try{
            if (((Status.RUN).name()).equals(status.get().name())){
            list.forEach((l)->{

                    try{
                        String s[]=l.split(",");
                        FinancialData f=new FinancialData();
                        f.setId(s[0]);
                        Date date = new Date();
                        f.setAsOf(dateFormat.format(date));
                        Payload p=new Payload();
                        if(s[1]!=null && s[1]!=""){
                            p.setPrice(Double.valueOf(s[1]));
                        }

                        f.setPayload(p);
                        financialDataDao.addValue(s[0],f);
                    }
                    catch (Exception e){
                       logger.error("Error in the entry:"+l,e);
                    }


            });
            }
        }
        catch (Exception e){
            logger.error("Error while saving the uploading the batch",e);
        }


    }
}
