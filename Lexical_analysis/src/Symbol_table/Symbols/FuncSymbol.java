package Symbol_table.Symbols;

import java.util.ArrayList;

public class FuncSymbol {
    private String name;
    private ArrayList<NorSymbol> params;
    private int returntype;    //0=void  1=int

    public FuncSymbol(String name, ArrayList<NorSymbol> params, int returntype) {
        this.name = name;
        this.params = params;
        this.returntype = returntype;
    }

    public String getName() {
        return name;
    }

    public ArrayList<NorSymbol> getParams() {
        return params;
    }

    public int getReturntype() {
        return returntype;
    }
}
