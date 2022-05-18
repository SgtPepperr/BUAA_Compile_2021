package AST;

import Midcode.midCode;
import Symbol_table.Symbols.ArraySymbol;
import Symbol_table.Symbols.VarSymbol;

import java.util.ArrayList;
import java.util.HashMap;

public class VarDef extends Def {
    ArrayList<ArrayList<Expr>> InitVal = null;

    public VarDef(Lval lval, ArrayList< ArrayList<Expr>> initVal) {
        super(lval);
        InitVal = initVal;
    }

    @Override
    public void gen() {
        String name = lval.op.getContent();

        if (lval instanceof Id) {
            if (InitVal.size() == 0) {
                emit(new midCode(midCode.operation.VAR, name, null, null));                //常数情况
            } else {
                Expr expr = InitVal.get(0).get(0);
                emit(new midCode(midCode.operation.VAR, name, expr.reduce().toString(),null));                //常数情况
            }
        } else if (lval instanceof Array) {
            Array arr = (Array) lval;
            Expr expr1 = arr.getOneindex();
            Expr expr2 = arr.getTwoindex();
            int level1 = expr1.calculate();
            int level2=0;
            if (expr2 == null) {                                                                             //一维数组情况
                emit(new midCode(midCode.operation.ARRAY, name, String.valueOf(level1), null));
                inttable.add(name, new ArraySymbol(name, true, 1));
            } else {                                                     //二维数组情况
                level2 = expr2.calculate();
                emit(new midCode(midCode.operation.ARRAY, name, String.valueOf(level1), String.valueOf(level2)));
                inttable.add(name, new ArraySymbol(name, true, 2, level2));        //载入符号表
            }
                for (int i=0;i<InitVal.size();i++) {
                    for (int j = 0; j < InitVal.get(i).size(); j++) {
                        emit(new midCode(midCode.operation.PUTARRAY, name, String.valueOf(i*level2+j),InitVal.get(i).get(j).reduce().toString() ));
                    }
            }
        } else {
            System.out.print("--------------defwrong-----------");
        }
    }
}
