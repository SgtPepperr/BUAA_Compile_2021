package Symbol_table.Symbols;

import AST.Expr;

import java.util.ArrayList;

public class ArraySymbol extends NorSymbol {
    private int  level;
    private int level1=0;
    private int level2=0;
    private ArrayList<Integer> values;

    public ArraySymbol(String name, boolean isConst) {
        super(name, isConst);
    }

        public ArraySymbol(String name,boolean isConst,int level,int level1) {
        super(name,isConst);
        this.level=level;
        this.level1=level1;
    }

    public ArraySymbol(String name,boolean isConst,int level,int level1,int level2) {
        super(name,isConst);
        this.level=level;
        this.level1=level1;
        this.level2=level2;
    }

    public ArraySymbol(String name, boolean isConst, int level) {
        super(name, isConst, level);
    }

    public ArraySymbol(String name, boolean isConst, int level, int level1, ArrayList<Integer> values) {
        super(name, isConst);
        this.level = level;
        this.level1 = level1;
        this.values = values;
    }

    public ArraySymbol(String name, boolean isConst, int level, int level1, int level2, ArrayList<Integer> values) {
        super(name, isConst);
        this.level = level;
        this.level1 = level1;
        this.level2 = level2;
        this.values = values;
    }
    @Override
    public int getLevel() {
        return level;
    }

    public int getLevel1() {
        return level1;
    }

    public int getLevel2() {
        return level2;
    }

    public int getValue(int i){
        return values.get(i);
    }
}
