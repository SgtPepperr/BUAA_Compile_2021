package AST;

import java.util.ArrayList;
import java.util.HashMap;

public class VarDef extends Def{
    HashMap<Integer,ArrayList<Expr>> InitVal;

    public VarDef(Lval lval, HashMap<Integer, ArrayList<Expr>> initVal) {
        super(lval);
        InitVal = initVal;
    }
}
