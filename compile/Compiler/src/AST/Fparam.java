package AST;

import Midcode.midCode;
import Symbol_table.Symbols.ArraySymbol;
import Symbol_table.Symbols.VarSymbol;

public class Fparam extends Node {
    Id id;
    int level;
    Expr expr;

    public Fparam(Id id, int level, Expr expr) {
        this.id = id;
        this.level = level;
        this.expr = expr;
    }

    @Override
    public void gen() {
//        id.canculculate();
//        expr.canculculate();

        String s = id.getcontent();
        if (level == 0) {
            emit(new midCode(midCode.operation.PARAM, s, String.valueOf("0")));
            inttable.add(s, new VarSymbol(s, false, 0));
        } else if (level == 1) {
            emit(new midCode(midCode.operation.PARAM, s, String.valueOf("1")));
            inttable.add(s, new ArraySymbol(s, false, 1));
        } else {
            int level2 = expr.calculate();
            emit(new midCode(midCode.operation.PARAM, s, String.valueOf("2"), String.valueOf(level2)));
            inttable.add(s, new ArraySymbol(s, false, 2, level2));
        }
    }
}
