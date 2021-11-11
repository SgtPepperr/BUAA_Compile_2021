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

    public int calculate(int k1,int k2) {
        ArraySymbol sym= (ArraySymbol) inttable.get(op.getContent());
        return sym.getValue(k1*sym.getLevel2()+k2);
    }

    public Expr getOneindex() {
        return oneindex;
    }

    public Expr getTwoindex() {
        return twoindex;
    }

    @Override
    public Expr reduce() {
        if(twoindex==null){
            Temp temp=new Temp(op);
            emit(new midCode(midCode.operation.GETARRAY,temp.toString(),op.getContent(),oneindex.reduce().toString()));
            return temp;
        }else{
            Temp temp1=new Temp(op);
            Temp temp2=new Temp(op);
            Temp temp3=new Temp(op);
            int index2=((ArraySymbol)inttable.get(op.getContent())).getLevel2();
            emit(new midCode(midCode.operation.MULTOP,temp1.toString(),oneindex.reduce().toString(),String.valueOf(index2)));
            emit(new midCode(midCode.operation.PLUSOP,temp2.toString(),temp1.toString(),twoindex.reduce().toString()));
            emit(new midCode(midCode.operation.GETARRAY,temp3.toString(),op.getContent(),temp2.reduce().toString()));
            return temp3;
        }
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
