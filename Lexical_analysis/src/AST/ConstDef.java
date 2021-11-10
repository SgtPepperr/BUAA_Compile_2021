package AST;

import java.util.ArrayList;

public class ConstDef extends Def{
    ArrayList<Expr> IniVal;

    public ConstDef(Lval lval, ArrayList<Expr> iniVal) {
        super(lval);
        IniVal = iniVal;
    }
}
