package Symbol_table;

import Symbol_table.Symbols.FuncSymbol;
import Symbol_table.Symbols.NorSymbol;

import java.util.HashMap;

public class FuncTable {
    private HashMap<String, FuncSymbol> maps=new HashMap<>();
    private FuncTable out;

    public FuncTable() {
    }

    public HashMap<String, FuncSymbol> getMaps() {
        return maps;
    }

    public FuncTable getOut() {
        return out;
    }

    public void add(String s, FuncSymbol symbol){
        maps.put(s,symbol);
    }

    public boolean contains(String s){
        return maps.containsKey(s);
    }
}
