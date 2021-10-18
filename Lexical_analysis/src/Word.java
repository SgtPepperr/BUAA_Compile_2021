public class Word {
    private final int symnumber;
    private final String content;
    private int line;

    public Word(int symnumber, String content, int line) {
        this.symnumber = symnumber;
        this.content = content;
        this.line = line;
    }

    public Word(){
        this.symnumber=0;
        this.content="";
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
