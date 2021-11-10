package Symbol_table.Symbols;

import AST.Expr;

public class ArraySymbol extends NorSymbol {
    private int  level;
    private Expr level1;
    private Expr level2;

    public ArraySymbol(String name, boolean isConst) {
        super(name, isConst);
    }

        public ArraySymbol(String name,boolean isConst,Expr level1) {
        super(name,isConst);
        this.level1=level1;
    }

    public ArraySymbol(String name,boolean isConst,Expr level1,Expr level2) {
        super(name,isConst);
        this.level1=level1;
        this.level2=level2;
    }

    public ArraySymbol(String name, boolean isConst, int level) {
        super(name, isConst, level);
    }

    public Expr getLevel1() {
        return level1;
    }

    public Expr getLevel2() {
        return level2;
    }
}
