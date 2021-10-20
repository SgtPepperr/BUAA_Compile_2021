package Symbol_table.Symbols;

public class ArraySymbol extends NorSymbol {
    private int level;
    private int level1;
    private int level2;

    public ArraySymbol(String name,boolean isConst,int level) {
        super(name,isConst);
        this.level = level;
    }
//    public ArraySymbol(String name,boolean isConst,int level,int level1) {
//        super(name,isConst);
//        this.level = level;
//        this.level1=level1;
//    }
//    public ArraySymbol(String name,boolean isConst,int level,int level1,int level2) {
//        super(name,isConst);
//        this.level = level;
//        this.level1=level1;
//        this.level2=level2;
//    }

    public int getLevel() {
        return level;
    }

    public int getLevel1() {
        return level1;
    }

    public int getLevel2() {
        return level2;
    }
}
