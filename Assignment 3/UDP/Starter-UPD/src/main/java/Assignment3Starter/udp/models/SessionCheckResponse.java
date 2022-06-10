package Assignment3Starter.udp.models;


public class SessionCheckResponse {
    boolean isExpired;
    private byte[] image;
    private long secondsRemaining;

    public SessionCheckResponse(boolean isExpired, byte[] image, long secondsRemaining) {
        this.isExpired = isExpired;
        this.image = image;
        this.secondsRemaining = secondsRemaining;
    }

    public long getSecondsRemaining() {
        return secondsRemaining;
    }

    public byte[] getImage() {
        return image;
    }

    public boolean isExpired() {
        return isExpired;
    }
}
