package AST;

import Midcode.midCode;

public class If extends Stmt{
    Or cond;
    Stmt stmt1;
    Stmt stmt2=null;
    int jump1;
    int jump2;


    public If(Or cond, Stmt stmt1, Stmt stmt2) {
        this.cond = cond;
        this.stmt1 = stmt1;
        this.stmt2 = stmt2;
    }

    @Override
    public void gen() {
       if(stmt2==null){
           jump1=++jumps;
           cond.gen(jump1);
           stmt1.gen();
           emit(new midCode(midCode.operation.Jump,String.valueOf(jump1)));
       }else{
           jump1=++jumps;
           jump2=++jumps;
           cond.gen(jump1);
           stmt1.gen();
           emit(new midCode(midCode.operation.GOTO,String.valueOf(jump2)));
           emit(new midCode(midCode.operation.Jump,String.valueOf(jump1)));
           stmt2.gen();
           emit(new midCode(midCode.operation.Jump,String.valueOf(jump2)));
       }
    }
}
