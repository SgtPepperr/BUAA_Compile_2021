package Word;

public class FormatWord extends Word {
    private int num;
    private boolean correct;

    public FormatWord(int symnumber, String content, int line, int num, boolean correct) {
        super(symnumber, content, line);
        this.num = num;
        this.correct = correct;
    }

    public int getNum() {
        return num;
    }

    public boolean isCorrect() {
        return correct;
    }
}
