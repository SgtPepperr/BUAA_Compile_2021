public class Word {
    private int symnumber;
    private String content;
    private int line;

    public Word(int symnumber, String content, int line) {
        this.symnumber = symnumber;
        this.content = content;
        this.line = line;
    }

    public int getSymnumber() {
        return symnumber;
    }

    public String getContent() {
        return content;
    }

    public int getLine() {
        return line;
    }
}
