package AST;

import Midcode.midCode;
import Symbol_table.Symbols.ArraySymbol;
import Symbol_table.Symbols.VarSymbol;

import java.util.ArrayList;

public class ConstDef extends Def {
    ArrayList<Expr> IniVal;
    ArrayList<Integer> IniValue = new ArrayList<>();

    public ConstDef(Lval lval, ArrayList<Expr> iniVal) {
        super(lval);
        IniVal = iniVal;
    }

    @Override
    public void gen() {
//        lval.canculculate();
        for (Expr e : IniVal)
            e.canculculate();

        for (int i = 0; i < IniVal.size(); i++) {
            IniValue.add(IniVal.get(i).calculate());    //计算每一个初始值，并存入另一个数组中
        }
        String name = lval.op.getContent();

        if (lval instanceof Id) {
            int value = IniValue.get(0);
            emit(new midCode(midCode.operation.CONST, name, String.valueOf(value), null));                //常数情况
            inttable.add(name, new VarSymbol(name, true, value));
        } else if (lval instanceof Array) {
            Array arr = (Array) lval;
            Expr expr1 = arr.getOneindex();
            Expr expr2 = arr.getTwoindex();
            int level1 = expr1.calculate();
            if (expr2 == null) {                                                                             //一维数组情况
                emit(new midCode(midCode.operation.ARRAY, name, String.valueOf(level1), null));
                for (int i = 0; i < IniVal.size(); i++) {
                    emit(new midCode(midCode.operation.PUTARRAY, name, String.valueOf(i), String.valueOf(IniValue.get(i))));
                }
                inttable.add(name, new ArraySymbol(name, true, 1, IniValue));
            } else {                                                     //二维数组情况
                int level2 = expr2.calculate();
                emit(new midCode(midCode.operation.ARRAY, name, String.valueOf(level1), String.valueOf(level2)));
                for (int i = 0; i < IniVal.size(); i++) {
                    emit(new midCode(midCode.operation.PUTARRAY, name, String.valueOf(i), String.valueOf(IniValue.get(i))));      //输出中间代码
                }
                inttable.add(name, new ArraySymbol(name, true, 2, level2, IniValue));        //载入符号表
            }
        } else {
            System.out.print("--------------defwrong-----------");
        }
    }
}
