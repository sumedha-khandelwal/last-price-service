package main.java.dao;

import main.java.model.FinancialData;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Dao layer which saves the data in memory
 * It's a singleton class as we would require same instance for every request
 */
public class FinancialDataDaoImpl {

    private static final FinancialDataDaoImpl financialDao=new FinancialDataDaoImpl();

    public ConcurrentHashMap<String, FinancialData> tempMap=new ConcurrentHashMap<>();
    public ConcurrentHashMap<String,FinancialData> masterMap=new ConcurrentHashMap<>();

    private FinancialDataDaoImpl(){

    }

    public static FinancialDataDaoImpl getInstance(){
        return FinancialDataDaoImpl.financialDao;
    }

    /**
     * returns the value updated in master copy
     * @param id
     * @return
     */
    public FinancialData getValue(String id){
        return masterMap.get(id);
    }

    /**
     * adds the value in temporary db
     * @param id
     * @param value
     */
    public void addValue(String id, FinancialData value){
        tempMap.put(id,value);
    }

    /**
     * Copies the value from temprorary db to master db
     */
    public void updateMaster(){
        masterMap.putAll(tempMap);
        tempMap.clear();
    }

    /**
     * removes all the entries from temp db
     */
    public void clearData(){
        tempMap.clear();
    }
}
