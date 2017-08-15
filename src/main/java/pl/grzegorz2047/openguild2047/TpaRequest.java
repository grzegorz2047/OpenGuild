package pl.grzegorz2047.openguild2047;

public class TpaRequest {

    private String source;
    private String destination;
    private long requestTime;

    public TpaRequest(String source, String destination, long requestTime) {
        this.source = source;
        this.destination = destination;
        this.requestTime = requestTime;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public long getRequestTime() {
        return requestTime;
    }
}
