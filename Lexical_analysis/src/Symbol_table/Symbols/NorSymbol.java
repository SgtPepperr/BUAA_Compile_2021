package Symbol_table.Symbols;

public class NorSymbol {
    private String name;
    private boolean isConst;

    public NorSymbol(String name,boolean isConst) {
        this.name = name;
        this.isConst=isConst;
    }

    public String getName() {
        return name;
    }

    public boolean isConst() {
        return isConst;
    }
}
