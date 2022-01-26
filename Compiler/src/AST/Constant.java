package AST;

import Word.Word;

public class Constant extends Expr {
    public Constant(Word op) {
        super(op);
    }

//    public Constant(ArrayList<midCode> midCodes, int num) {
//        super( new Word(String.valueOf(num)));
//    }

    @Override
    public int calculate() {
        return Integer.parseInt(op.getContent());
    }

    @Override
    public boolean canculculate() {
        isvalue = true;
        value = Integer.parseInt(op.getContent());
        return true;
    }
}
