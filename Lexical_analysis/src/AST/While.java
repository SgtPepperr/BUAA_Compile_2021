package AST;

import Midcode.midCode;

public class While extends Stmt{
    Or Cond;
    Stmt stmt;
    int jump;

    public While(Or cond, Stmt stmt) {
        Cond = cond;
        this.stmt = stmt;
    }

    @Override
    public void gen() {
       jump=++jumps;
       loopstack.push(jump);
       emit(new midCode(midCode.operation.Jump,String.valueOf(jump),"begin"));
       Cond.gen(jump,true);
       stmt.gen();
       emit(new midCode(midCode.operation.GOTO,"Loop"+String.valueOf(jump)+"begin"));
       emit(new midCode(midCode.operation.Jump,String.valueOf(jump),"end"));
       loopstack.pop();
    }
}
