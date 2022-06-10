package Assignment3Starter.udp.models;

public class QuoteResult {

    private Long id;
    private byte[] imgBytes;

    public QuoteResult(Long id, byte[] imgBytes) {
        this.id = id;
        this.imgBytes = imgBytes;
    }

    public Long getId() {
        return id;
    }

    public byte[] getImgBytes() {
        return imgBytes;
    }
}
