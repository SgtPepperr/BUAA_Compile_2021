package AST;

import Midcode.midCode;
import Symbol_table.IntergerTable;
import Symbol_table.Symbols.ArraySymbol;
import Symbol_table.Symbols.VarSymbol;
import Word.Word;

import java.util.ArrayList;

public class Id extends Lval{

    public Id(Word op) {
        super(op);
    }

    @Override
    public int calculate() {

        IntergerTable table=inttable;
        VarSymbol sym=null;
        while(table!=null){
            if(table.contains(op.getContent())){
                sym = (VarSymbol) table.get(op.getContent());
                break;
            }
            table=table.getOut();
        }

       return sym.getValue();
    }

    @Override
    public Expr reduce() {
        if(isvalue){
            return new Constant(new Word(String.valueOf(value)));
        }
        return this;
    }

    @Override
    public boolean canculculate() {
        IntergerTable table=inttable;
        VarSymbol sym=null;
        while(table!=null){
            if(table.contains(op.getContent())){
                if(table.get(op.getContent()) instanceof ArraySymbol)
                    return false;
                sym = (VarSymbol) table.get(op.getContent());
                break;
            }
            table=table.getOut();
        }

        if(sym!=null&& sym.isConst()){
            isvalue=true;
            value=sym.getValue();
            return true;
        }
        return false;
    }
}
