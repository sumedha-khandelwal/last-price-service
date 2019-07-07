package main.java.service;

import main.java.controller.FinanceController;
import main.java.dao.FinancialDataDaoImpl;
import main.java.model.FinancialData;
import org.apache.log4j.Logger;

/**
 * This is the service layer between controller and dao
 * Data is consumed through this class
 * Singleton class
 */
public class DataConsumeService {

    private static final Logger logger = Logger.getLogger(DataConsumeService.class);

    private final static DataConsumeService dcs=new DataConsumeService();

    private DataConsumeService(){

    }

    public static DataConsumeService getInstance(){
        return DataConsumeService.dcs;
    }

    /**
     * fetched the data from master table
     * @param id
     * @return
     */
    public FinancialData getValue(String id){
        return FinancialDataDaoImpl.getInstance().getValue(id);
    }
}
