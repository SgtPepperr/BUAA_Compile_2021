package Symbol_table.Symbols;

import java.util.ArrayList;

public class ArraySymbol extends NorSymbol {
    private int level;
    private int level1 = 0;
    private int level2 = 0;
    private ArrayList<Integer> values;

    public ArraySymbol(String name, boolean isConst) {
        super(name, isConst);
    }

//        public ArraySymbol(String name,boolean isConst,int level,int level1) {
//        super(name,isConst);
//        this.level=level;
//        this.level1=level1;
//    }

    public ArraySymbol(String name, boolean isConst, int level, int level2) {
        super(name, isConst);
        this.level = level;
        this.level2 = level2;
    }

    public ArraySymbol(String name, int offset) {
        super(name, offset);
    }

    public ArraySymbol(String name, int offset, boolean ispointer) {
        super(name, offset, ispointer);
    }

    public ArraySymbol(String name, boolean isConst, int level) {
        super(name, isConst, level);
    }

    public ArraySymbol(String name, boolean isConst, int level, ArrayList<Integer> values) {
        super(name, isConst);
        this.level = level;
        this.values = values;
    }

    public ArraySymbol(String name, boolean isConst, int level, int level2, ArrayList<Integer> values) {
        super(name, isConst);
        this.level = level;
        this.level2 = level2;
        this.values = values;
    }

    @Override
    public int getLevel() {
        return level;
    }


    public int getLevel2() {
        return level2;
    }

    public int getValue(int i) {
        return values.get(i);
    }
}
