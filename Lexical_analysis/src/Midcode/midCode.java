package Midcode;

public class midCode {
    public enum operation{
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
    }

    operation op;       //操作符
    String z;           //结果
    String x;           //左操作符
    String y;           //右操作符

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
                return z+"="+x+"+"+y;
            case MINUOP:
                return z+"="+x+"-"+y;
            case MULTOP:
                return z+"="+x+"*"+y;
            case DIVOP:
                return z+"="+x+"/"+y;
            case MODOP:
                return z+"="+x+"%"+y;
            default:
                return "";
//            case LSSOP:
//                midCodefile << mc.z << " = (" << mc.x << " < " << mc.y << ")\n";
//                break;
//            case LEQOP:
//                midCodefile << mc.z << " = (" << mc.x << " <= " << mc.y << ")\n";
//                break;
//            case GREOP:
//                midCodefile << mc.z << " = (" << mc.x << " > " << mc.y << ")\n";
//                break;
//            case GEQOP:
//                midCodefile << mc.z << " = (" << mc.x << " >= " << mc.y << ")\n";
//                break;
//            case EQLOP:
//                midCodefile << mc.z << " = (" << mc.x << " == " << mc.y << ")\n";
//                break;
//            case NEQOP:
//                midCodefile << mc.z << " = (" << mc.x << " != " << mc.y << ")\n";
//                break;
//            case ASSIGNOP:
//                midCodefile << mc.z << " = " << mc.x << "\n";
//                break;
//            case GOTO:
//                midCodefile << "GOTO " << mc.z << "\n";
//                break;
//            case BZ:
//                midCodefile << "BZ " << mc.z << "(" << mc.x << "=0)" << "\n";
//                break;
//            case BNZ:
//                midCodefile << "BNZ " << mc.z << "(" << mc.x << "=1)" << "\n";
//                break;
//            case PUSH:
//                midCodefile << "PUSH " << mc.z << "\n";
//                break;
//            case CALL:
//                midCodefile << "CALL " << mc.z << "\n";
//                break;
//            case RET:
//                midCodefile << "RET " << mc.z << "\n";
//                break;
//            case RETVALUE:
//                midCodefile << "RETVALUE " << mc.z << " = " << mc.x << "\n";
//                break;
//            case SCAN:
//                midCodefile << "SCAN " << mc.z << "\n";
//                break;
//            case PRINT:
//                midCodefile << "PRINT " << mc.z << " " << mc.x << "\n";
//                break;
//            case LABEL:
//                midCodefile << mc.z << ": \n";
//                break;
//			/*case CONST:
//				midCodefile << "CONST " << mc.z << " " << mc.x << " = " << mc.y << endl;
//				break;
//			case ARRAY:
//				midCodefile << "ARRAY " << mc.z << " " << mc.x << "[" << mc.y << "]" << endl;
//				break;
//			case VAR:
//				midCodefile << "VAR " << mc.z << " " << mc.x << endl;
//				break;*/
//            case FUNC:
//                midCodefile << "FUNC " << mc.z << " " << mc.x << "()" << endl;
//                break;
//            case PARAM:
//                midCodefile << "PARA " << mc.z << " " << mc.x << endl;
//                break;
//            case GETARRAY:
//                midCodefile << mc.z << " = " << mc.x << "[" << mc.y << "]\n";
//                break;
//            case PUTARRAY:
//                midCodefile << mc.z << "[" << mc.x << "]" << " = " << mc.y << "\n";
//                break;
//            case EXIT:
//                midCodefile << "EXIT\n";
//                break;
//            default:
//                break;
        }
    }
}
