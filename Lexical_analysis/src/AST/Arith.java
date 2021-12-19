package AST;

import Midcode.midCode;
import Word.Word;

public class Arith extends Expr {
    public Expr exp1, exp2;

    public Arith(Word op, Expr exp1, Expr exp2) {
        super(op);
        this.exp1 = exp1;
        this.exp2 = exp2;
    }

    @Override
    public boolean canculculate() {
        boolean a1 = exp1.canculculate();
        boolean a2 = exp2.canculculate();
        if (a1 && a2) {
            this.isvalue = true;
            String midop = op.getContent();
            if (midop.equals("+")) {
                value = exp1.value + exp2.value;
            } else if (midop.equals("-")) {
                value = exp1.value - exp2.value;
            } else if (midop.equals("*")) {
                value = exp1.value * exp2.value;
            } else if (midop.equals("/")) {
                value = exp1.value / exp2.value;
            } else if (midop.equals("%")) {
                value = exp1.value % exp2.value;
            }
            return true;
        }
        return false;
    }

    public Expr reduce() {

        if (isvalue) {
            return new Constant(new Word(String.valueOf(value)));
        }

        Temp t = new Temp(op);
        String midop = op.getContent();
        midCode.operation ope = midCode.operation.DEBUG;
        if (midop.equals("+")) {
            ope = midCode.operation.PLUSOP;
        } else if (midop.equals("-")) {
            ope = midCode.operation.MINUOP;
        } else if (midop.equals("*")) {
            ope = midCode.operation.MULTOP;
        } else if (midop.equals("/")) {
            ope = midCode.operation.DIVOP;
        } else if (midop.equals("%")) {
            ope = midCode.operation.MODOP;
        }

        emit(new midCode(ope, t.toString(), exp1.reduce().toString(), exp2.reduce().toString()));
        return t;
    }

    @Override
    public int calculate() {
        int value = 0;
        int left1 = exp1.calculate();
        int left2 = exp2.calculate();
        String midop = op.getContent();
        if (midop.equals("+")) {
            value = left1 + left2;
        } else if (midop.equals("-")) {
            value = left1 - left2;
        } else if (midop.equals("*")) {
            value = left1 * left2;
        } else if (midop.equals("/")) {
            value = left1 / left2;
        } else if (midop.equals("%")) {
            value = left1 % left2;
        }
        return value;
    }

    //    public Expr reduce() {
//        Expr x = gen();
//        Temp t = new Temp(midCodes,op);
//        emit(new midCode(midCode.operation.ASSIGNOP,t,)t.toString() + " = " + x.toString());
//        return t;
//    }


}
