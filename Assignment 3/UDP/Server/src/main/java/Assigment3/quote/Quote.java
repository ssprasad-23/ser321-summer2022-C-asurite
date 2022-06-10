package Assigment3.quote;

public class Quote {
    private Long id;
    private String answer;
    private String pathToFile;


    public Quote(Long id, String answer, String pathToFile) {
        this.id = id;
        this.answer = answer;
        this.pathToFile = pathToFile;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getPathToFile() {
        return pathToFile;
    }

    public void setPathToFile(String pathToFile) {
        this.pathToFile = pathToFile;
    }
}
