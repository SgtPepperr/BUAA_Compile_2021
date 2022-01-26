package AST;

import Midcode.midCode;
import Symbol_table.IntergerTable;
import Symbol_table.Symbols.ArraySymbol;
import Word.Word;

public class Array extends Lval {
    public Expr temp;
    public String num2;
    private int onelevel = 0;
    private int twolevel = 0;
    private int fact = 0;
    private Expr oneindex = null;
    private Expr twoindex = null;
    private boolean ispointer = false;

    public Array(Word op, Expr oneindex, Expr twoindex) {
        super(op);
        this.twoindex = twoindex;
        this.oneindex = oneindex;
    }

    public Array(Word op, Expr oneindex) {
        super(op);
        this.oneindex = oneindex;
    }


    public int calculate() {
        IntergerTable table = inttable;
        ArraySymbol sym = null;
        while (table != null) {
            if (table.contains(op.getContent())) {
                sym = (ArraySymbol) table.get(op.getContent());
                break;
            }
            table = table.getOut();
        }
        int k1 = 0, k2 = 0;
        if (twoindex == null) {
            k2 = oneindex.calculate();
        } else {
            k1 = oneindex.calculate();
            k2 = twoindex.calculate();
        }
        return sym.getValue(k1 * sym.getLevel2() + k2);
    }

    @Override
    public boolean canculculate() {
        boolean ok1 = oneindex.canculculate();

        IntergerTable table = inttable;
        ArraySymbol sym = null;
        while (table != null) {
            if (table.contains(op.getContent())) {
                sym = (ArraySymbol) table.get(op.getContent());
                break;
            }
            table = table.getOut();
        }
        if (sym.isConst()) {
            if (twoindex == null) {
                if (sym.getLevel2() != 0) {
                    return false;
                } else {
                    if (ok1) {
                        int k1 = oneindex.value;
                        isvalue = true;
                        value = sym.getValue(k1);
                        return true;
                    } else {
                        return false;
                    }
                }
            } else {
                boolean ok2 = twoindex.canculculate();
                if (ok1 && ok2) {
                    int k1 = oneindex.value;
                    int k2 = twoindex.value;
                    isvalue = true;
                    value = sym.getValue(k1 * sym.getLevel2() + k2);
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    public Expr getOneindex() {
        return oneindex;
    }

    public Expr getTwoindex() {
        return twoindex;
    }

    @Override
    public Expr reduce() {
        if (twoindex == null) {

            IntergerTable table = inttable;
            ArraySymbol sym = null;
            while (table != null) {
                if (table.contains(op.getContent())) {
                    sym = (ArraySymbol) table.get(op.getContent());
                    break;
                }
                table = table.getOut();
            }

            if (sym.getLevel2() != 0) {                                    //判断是否为指针
                ispointer = true;
                temp = oneindex.reduce();
                num2 = String.valueOf(sym.getLevel2());
                return this;
            } else {

                if (isvalue) {
                    return new Constant(new Word(String.valueOf(value)));
                }

                Temp temp = new Temp(op);
                emit(new midCode(midCode.operation.GETARRAY, temp.toString(), op.getContent(), oneindex.reduce().toString()));
                return temp;
            }
        } else {

            if (isvalue) {
                return new Constant(new Word(String.valueOf(value)));
            }

            Temp temp1 = new Temp(op);
            Temp temp2 = new Temp(op);
            Temp temp3 = new Temp(op);

            IntergerTable table = inttable;
            ArraySymbol sym = null;
            while (table != null) {
                if (table.contains(op.getContent())) {
                    sym = (ArraySymbol) table.get(op.getContent());
                    break;
                }
                table = table.getOut();
            }

            int index2 = sym.getLevel2();
            emit(new midCode(midCode.operation.MULTOP, temp1.toString(), oneindex.reduce().toString(), String.valueOf(index2)));
            emit(new midCode(midCode.operation.PLUSOP, temp2.toString(), temp1.toString(), twoindex.reduce().toString()));
            emit(new midCode(midCode.operation.GETARRAY, temp3.toString(), op.getContent(), temp2.reduce().toString()));
            return temp3;
        }
    }

    @Override
    public String toString() {                      //数组被赋值等情况时，返回实际的Index
        if (twoindex == null) {
            return oneindex.reduce().toString();
        } else {
            Temp temp1 = new Temp(op);
            Temp temp2 = new Temp(op);

            IntergerTable table = inttable;
            ArraySymbol sym = null;
            while (table != null) {
                if (table.contains(op.getContent())) {
                    sym = (ArraySymbol) table.get(op.getContent());
                    break;
                }
                table = table.getOut();
            }

            int index2 = sym.getLevel2();

            emit(new midCode(midCode.operation.MULTOP, temp1.toString(), oneindex.reduce().toString(), String.valueOf(index2)));
            emit(new midCode(midCode.operation.PLUSOP, temp2.toString(), temp1.toString(), twoindex.reduce().toString()));
            return temp2.toString();
        }
    }


}
