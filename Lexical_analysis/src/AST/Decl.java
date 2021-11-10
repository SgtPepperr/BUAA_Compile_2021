package AST;

import java.util.LinkedList;

public class Decl extends BlockItem {
    LinkedList<Def> defs;
    boolean isConst;
}
