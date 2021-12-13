package AST;

import Midcode.midCode;
import Symbol_table.IntergerTable;
import Symbol_table.Symbols.ArraySymbol;
import Word.Word;

import java.util.ArrayList;

public class Array extends Lval {
    private int onelevel = 0;
    private int twolevel = 0;
    private int fact = 0;
    private Expr oneindex = null;
    private Expr twoindex = null;
    public Expr temp;
    private boolean ispointer=false;
    public String num2;

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
        IntergerTable table=inttable;
        ArraySymbol sym=null;
        while(table!=null){
            if(table.contains(op.getContent())){
                sym = (ArraySymbol) table.get(op.getContent());
                break;
            }
            table=table.getOut();
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

    public Expr getOneindex() {
        return oneindex;
    }

    public Expr getTwoindex() {
        return twoindex;
    }

    @Override
    public Expr reduce() {
        if (twoindex == null) {

            IntergerTable table=inttable;
            ArraySymbol sym=null;
            while(table!=null){
                if(table.contains(op.getContent())){
                    sym = (ArraySymbol) table.get(op.getContent());
                    break;
                }
                table=table.getOut();
            }

            if(sym.getLevel2()!=0){                                    //判断是否为指针
                ispointer=true;
                temp=oneindex.reduce();
                num2=String.valueOf(sym.getLevel2());
                return this;
            }else {
                Temp temp = new Temp(op);
                emit(new midCode(midCode.operation.GETARRAY, temp.toString(), op.getContent(), oneindex.reduce().toString()));
                return temp;
            }
        } else {
            Temp temp1 = new Temp(op);
            Temp temp2 = new Temp(op);
            Temp temp3 = new Temp(op);

            IntergerTable table=inttable;
            ArraySymbol sym=null;
            while(table!=null){
                if(table.contains(op.getContent())){
                    sym = (ArraySymbol) table.get(op.getContent());
                    break;
                }
                table=table.getOut();
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

            IntergerTable table=inttable;
            ArraySymbol sym=null;
            while(table!=null){
                if(table.contains(op.getContent())){
                    sym = (ArraySymbol) table.get(op.getContent());
                    break;
                }
                table=table.getOut();
            }

            int index2 = sym.getLevel2();

            emit(new midCode(midCode.operation.MULTOP, temp1.toString(), oneindex.reduce().toString(), String.valueOf(index2)));
            emit(new midCode(midCode.operation.PLUSOP, temp2.toString(), temp1.toString(), twoindex.reduce().toString()));
            return temp2.toString();
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
