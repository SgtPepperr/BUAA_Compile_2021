package Symbol_table;

import Symbol_table.Symbols.FuncSymbol;
import Symbol_table.Symbols.NorSymbol;

import java.util.HashMap;

public class IntergerTable {
    private HashMap<String, NorSymbol> maps=new HashMap<>();
    private IntergerTable out=null;

    public IntergerTable() {
    }

    public HashMap<String, NorSymbol> getMaps() {
        return maps;
    }

    public IntergerTable getOut() {
        return out;
    }

    public void add(String s,NorSymbol symbol){
        maps.put(s,symbol);
    }

    public boolean contains(String s){
        return maps.containsKey(s);
    }

    public NorSymbol get(String s){
        return maps.get(s);
    }

    public void setOut(IntergerTable out) {
        this.out = out;
    }
}
