package Symbol_table.Symbols;

public class NorSymbol {
    private String name;
    private boolean isConst;
    private int line=0;

    public NorSymbol() {
    }

    public NorSymbol(String name, boolean isConst) {
        this.name = name;
        this.isConst=isConst;
    }

    public String getName() {
        return name;
    }

    public boolean isConst() {
        return isConst;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getLine() {
        return line;
    }
}
