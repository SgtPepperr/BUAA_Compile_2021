package AST;

import Midcode.midCode;
import Symbol_table.Symbols.ArraySymbol;
import Word.Word;

import java.util.ArrayList;

public class Array extends Lval {
    private int onelevel=0;
    private int twolevel=0;
    private Expr oneindex=null;
    private Expr twoindex=null;
    private Expr temp;

    public Array( Word op, Expr oneindex, Expr twoindex) {
        super( op);
        this.twoindex = twoindex;
        this.oneindex = oneindex;
    }

    public Array(Word op, Expr oneindex) {
        super(op);
        this.oneindex = oneindex;
    }
//    public Array( Word op,  Expr oneindex,Expr onelevel, Expr twolevel, Expr twoindex) {
//        super( op);
//        this.oneindex = oneindex;
//        this.twoindex = twoindex;
//        this.onelevel= onelevel;
//        this.twolevel= twolevel;
//    }
//
//    @Override
//    public Expr gen() {
//        if (twolevel == null)
//            return super.gen();
//        else{
//////            Expr t1=new Arith(midCodes,new Word("*"),oneindex,twolevel);
//////            Expr t2=new Arith(midCodes,new Word("+"),twoindex,t1);
////            temp=t2.gen();
//            return super.gen();
//        }
//    }
//
//    @Override
//    public String toString() {
//        if (twolevel ==null) {
//            return op.getContent() + "[" + oneindex.gen().toString() + "]";
//        } else {
//            return op.getContent() + "[" + temp.toString() + "]";
//        }
//    }

}
