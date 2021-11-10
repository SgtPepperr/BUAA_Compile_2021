package AST;

import java.util.LinkedList;

public class Decl extends BlockItem {
    LinkedList<Def> defs;
    boolean isConst;

    public Decl(LinkedList<Def> defs, boolean isConst) {
        this.defs = defs;
        this.isConst = isConst;
    }

    @Override
    public void gen() {
       for(Def d:defs)
           d.gen();
    }
}
