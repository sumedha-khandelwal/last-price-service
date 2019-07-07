package main.java.model;

/**
 * Model Object in java whose corresponding values are saved in db
 */
public class FinancialData {


    private String id;
    private String asOf;
    private Payload payload;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAsOf() {
        return asOf;
    }

    public void setAsOf(String asOf) {
        this.asOf = asOf;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "FinancialData{" +
                "id='" + id + '\'' +
                ", asOf='" + asOf + '\'' +
                ", payload=" + payload +
                '}';
    }
}
