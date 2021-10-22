package Symbol_table.Symbols;

public class VarSymbol extends NorSymbol {
    private int value;

    public VarSymbol(String name,boolean isConst, int value) {
        super(name,isConst);
        this.value = value;
    }
    public VarSymbol(String name,boolean isConst) {
        super(name,isConst);
    }


    public int getValue() {
        return value;
    }
}
