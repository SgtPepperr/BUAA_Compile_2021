package Symbol_table.Symbols;

public class NorSymbol {
    private String name;
    private boolean isConst;
    private int level=0;
    private int line=0;
    private int offset=0;

    public NorSymbol() {
    }

    public NorSymbol(String name, boolean isConst) {
        this.name = name;
        this.isConst=isConst;
    }
    public NorSymbol(String name, boolean isConst,int level) {
        this.name = name;
        this.isConst=isConst;
        this.level=level;
    }

    public NorSymbol(String name, int offset) {
        this.name = name;
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
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

    public int getLevel() {
        return level;
    }
}
