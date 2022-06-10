package Assignment3Starter.udp.models;

public class QuoteSubmitResult {
    private boolean isCorrect;
    private int totalScore;

    private boolean isWon;
    private byte[] image;

    public QuoteSubmitResult(boolean isCorrect, int totalScore, boolean isWon, byte[] image) {
        this.isCorrect = isCorrect;
        this.totalScore = totalScore;
        this.isWon = isWon;
        this.image = image;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public boolean isWon() {
        return isWon;
    }

    public byte[] getImage() {
        return image;
    }
}
