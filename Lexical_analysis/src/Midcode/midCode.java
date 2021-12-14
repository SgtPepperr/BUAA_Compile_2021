package Midcode;

public class midCode {
    public enum operation {
        MAIN,   //主函数进入入口
        PLUSOP, //+
        MINUOP, //-
        MULTOP, //*
        DIVOP,  // /
        MODOP,  // %
        LSSOP,  //<
        LEQOP,  //<=
        GREOP,  //>
        GEQOP,  //>=
        EQLOP,  //==
        NEQOP,  //!=
        ASSIGNOP,  //=
        GOTO,  //无条件跳转
        Jump,  //跳转标记
        BZ,    //不满足条件跳转
        BNZ,   //满足条件跳转
        PUSH,  //函数调用时参数传递
        CALL,  //函数调用
        RET,   //函数返回语句
        RETVALUE, //有返回值函数返回的结果
        SCAN,  //读
        PRINT, //写
        LABEL, //标号,区分不同作用域和标号
        CONST, //常量
        ARRAY, //数组
        VAR,   //变量
        FUNC,  //函数定义
        PARAM, //函数参数
        GETARRAY,  //取数组的值  t = a[]
        PUTARRAY,  //给数组赋值  a[] = t
        EXIT,  //退出 main最后
        DEBUG,
        SLL,   //左移
        SRL,   //右移
    }

    public operation op;       //操作符
    public String z = null;           //结果
    public String x = null;           //左操作符
    public String y = null;           //右操作符

    public midCode(operation op, String z, String x, String y) {
        this.op = op;
        this.z = z;
        this.x = x;
        this.y = y;
    }

    public midCode(operation op, String z) {
        this.op = op;
        this.z = z;
    }

    public midCode(operation op, String z, String x) {
        this.op = op;
        this.z = z;
        this.x = x;
    }

    @Override
    public String toString() {
        switch (op) {
            case PLUSOP:
                return z + " = " + x + " + " + y;
            case MINUOP:
                return z + " = " + x + " - " + y;
            case MULTOP:
                return z + " = " + x + " * " + y;
            case DIVOP:
                return z + " = " + x + " / " + y;
            case MODOP:
                return z + " = " + x + " % " + y;
            case LSSOP:
                return z+" = "+x+" < "+y;
            case LEQOP:
                return z+" = "+x+" <= "+y;
            case GREOP:
                return z+" = "+x+" > "+y;
            case GEQOP:
                return z+" = "+x+" >= "+y;
            case EQLOP:
                return z+" = "+x+" == "+y;
            case NEQOP:
                return z+" = "+x+" != "+y;
            case ASSIGNOP:
                return z + " = " + x;
            case GOTO:
//                if(x==null){
                    return "GOTO "+z;
//                }else{
//                    return "GOTO loop"+z+"---"+x;
//                }
            case BZ:
               return "if "+x+" == 0 then goto "+z;
            case BNZ:
//                midCodefile << "BNZ " << mc.z << "(" << mc.x << "=1)" << "\n";
                break;
            case Jump:
                if(x==null){
                    return "        <JUMPDST JUMP"+z+">";
                }else{
                    return "        <JUMPDST LOOP"+z+"---"+x+">";
                }
            case PUSH:
                if (x == null)
                    return "push " + z;
                else
                    return "push " + z + "[" + x + "]"+"["+y+"]";
            case CALL:
                return "call " + z;
            case RET:
                if (z != null)
                    return "RET  " + z;
                else
                    return "RET null";
            case RETVALUE:
                return "retvalue " + z;
            case SCAN:
                return "scan " + z;
            case PRINT:
                if (x.equals("string"))
                    return "print \"" + z + "\"";
                else
                    return "print " + z;
            case LABEL:
                return "    <LABEL " + z + " " + x + ">";
            case CONST:
                return "const int " + z + " = " + x;
            case ARRAY:
                if (y == null) {
                    return "array int " + z + "[" + x + "]";
                } else {
                    return "array int " + z + "[" + x + "]" + "[" + y + "]";
                }
            case VAR:
                if (x == null) {
                    return "var int " + z;
                } else {
                    return "var int " + z + " = " + x;
                }
            case FUNC:
                return x + " " + z + "( )";
            case PARAM:
                if (x.equals("0")) {
                    return "para int " + z;
                } else if (x.equals("1")) {
                    return "para int " + z + "[]";
                } else {
                    return "para int " + z + "[][" + y + "]";
                }
            case MAIN:
                return "\nMAIN\n";
            case GETARRAY:
                return z + " = " + x + "[" + y + "]";
            case PUTARRAY:
                return z + "[" + x + "] = " + y;
            case EXIT:
                return "\n-----------------EXIT--------------\n";
            case SLL:
                return z+" = "+x+" << "+y;
            case SRL:
                return z+" = "+x+" >> "+y;
            default:
                return null;
        }
        return null;
    }
}
