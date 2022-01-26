import AST.*;
import Midcode.midCode;
import Word.Word;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Parsing_mid {
    private enum Symbol {
        A, IDENFR, INTCON, STRCON, MAINTK, CONSTTK, INTTK, BREAKTK, CONTINUETK, IFTK, ELSETK,
        NOT, AND, OR, WHILETK, GETINTTK, PRINTFTK, RETURNTK, PLUS, MINU, VOIDTK, MULT, DIV,
        MOD, LSS, LEQ, GRE, GEQ, EQL, NEQ, ASSIGN, SEMICN, COMMA, LPARENT, RPARENT, LBRACK,
        RBRACK, LBRACE, RBRACE
    }

    private ArrayList<midCode> midCodes=new ArrayList<>();
    private LinkedList<String> strings=new LinkedList<>();
    private Program root;
    private ArrayList<Word> words;
    private int label = 0;           //区分不同基本块标签
    private int temp = 0;            //区分不同临时变量
    private int index = 0;

    public Parsingtemp(ArrayList<Word> words) {
        this.words = words;
    }

    public ArrayList<midCode> getMidCodes() {
        return midCodes;
    }

    public LinkedList<String> getStrings() {
        return strings;
    }

    public void analyse() {
//        CompUnit();
        root.gen();
        midCodes=root.getMidCodes();
        midCodes.add(new midCode(midCode.operation.EXIT,null));
        strings=root.getStringss();
        outputMidcode();
    }

    public void outputMidcode() {
        for (int i = 0; i < midCodes.size(); i++) {
            System.out.println(midCodes.get(i).toString());
        }
    }

    private Word getWord() {
        if (index < words.size()) {
//            System.out.print(Parsing.Symbol.values()[words.get(index).getSymnumber()]);
//            System.out.print(' ' + words.get(index).getContent() + '\n');
            return words.get(index++);
        } else {
            return new Word();
        }

    }

    private Word showWord() {
        if (index < words.size())
            return words.get(index);
        else
            return new Word();
    }

    private Word showWord(int num) {
        if (num < words.size())
            return words.get(num);
        else
            return new Word();
    }

    private void error() {
        System.out.print("---------------wrong-------------");
    }

    private void CompUnit() {
        LinkedList<Decl> decls = new LinkedList<>();
        LinkedList<Func> funcs = new LinkedList<>();

        while (!showWord(index + 2).getContent().equals("("))
            decls.add(Decl());
        while (!showWord(index + 1).getContent().equals("main"))
            funcs.add(FuncDef());
        funcs.add(MainFuncDef());
        root = new Program(decls, funcs);
//        System.out.print("<CompUnit>\n");
    }

    private Decl Decl() {
        if (showWord().getContent().equals("const")) {
            return ConstDecl();
        } else {
            return VarDecl();
        }
//        System.out.print("<Decl>\n");
    }

    private Decl ConstDecl() {
        LinkedList<Def> defs = new LinkedList<>();
        if (getWord().getContent().equals("const")) {
            BType();
            defs.add(ConstDef());
            while (showWord().getContent().equals(",")) {
                getWord();
                defs.add(ConstDef());
            }
            if (!getWord().getContent().equals(";")) {
                error();
            }
        } else {
            error();
        }
        return new Decl(defs, true);
//        System.out.print("<ConstDecl>\n");
    }

    private void BType() {
        if (!getWord().getContent().equals("int")) {
            error();
        }
//        System.out.print("<BType>\n");
    }

    private Def ConstDef() {
        Word w = getWord();
        Lval lval=null;
        Expr expr1 = null;
        Expr expr2 = null;
        ArrayList<Expr> exprs = new ArrayList<>();
        if (w.getSymnumber() == 1) {
            int count = 0;                       //记录层次信息
            while (showWord().getContent().equals("[")) {
                count++;
                getWord();

                if (count == 1)
                    expr1 = ConstExp();
                else if (count == 2)
                    expr2 = ConstExp();

                if (!getWord().getContent().equals("]")) {
                    error();
                }
            }

            if (count == 0)
                lval = new Id(w);               //产生左值表达式
            else if (count == 1)
                lval = new Array(w, expr1);
            else
                lval = new Array(w, expr1, expr2);

            if (!getWord().getContent().equals("=")) {
                error();
            }
            ConstInitVal(exprs);

        } else {
            error();
        }
        return new ConstDef(lval,exprs);
//        System.out.print("<ConstDef>\n");
    }

    private void ConstInitVal(ArrayList<Expr> exprs) {
        if (showWord().getContent().equals("{")) {
            getWord();
            if (showWord().getContent().equals("}")) {
                getWord();
            } else {
                ConstInitVal(exprs);
                while (showWord().getContent().equals(",")) {
                    getWord();
                    ConstInitVal(exprs);
                }
                if (!getWord().getContent().equals("}")) {
                    error();
                }
            }
        } else {
            exprs.add(ConstExp());
        }
//        System.out.print("<ConstInitVal>\n");
    }

    private Decl VarDecl() {
        LinkedList<Def> defs=new LinkedList<>();
        BType();
        defs.add(VarDef());
        while (showWord().getContent().equals(",")) {
            getWord();
            defs.add(VarDef());
        }
        if (!getWord().getContent().equals(";")) {
            error();
        }
        return new Decl(defs,false);
//        System.out.print("<VarDecl>\n");
    }

    private Def VarDef() {

        Word w = getWord();
        int level=0;    //变量初始化层次
        Lval lval=null;
        Expr expr1 = null;
        Expr expr2 = null;
        ArrayList<ArrayList<Expr>> exprs = new ArrayList<>();
        if (w.getSymnumber() == 1) {
            int count = 0;                       //记录层次信息
            while (showWord().getContent().equals("[")) {
                count++;
                getWord();

                if (count == 1)
                    expr1 = ConstExp();
                else if (count == 2)
                    expr2 = ConstExp();

                if (!getWord().getContent().equals("]")) {
                    error();
                }
            }

            if (count == 0)
                lval = new Id(w);               //产生左值表达式
            else if (count == 1)
                lval = new Array(w, expr1);
            else
                lval = new Array(w, expr1, expr2);

            if (showWord().getContent().equals("=")) {
                getWord();
                InitVal(exprs,level);
            }

        } else {
            error();
        }
        return new VarDef(lval,exprs);
//        System.out.print("<VarDef>\n");
    }

    private void InitVal(ArrayList<ArrayList<Expr>> exprs,int level) {
        if (showWord().getContent().equals("{")) {
            if(level==1)
                exprs.add(new ArrayList<>());
            getWord();
            if (showWord().getContent().equals("}")) {
                getWord();
            } else {
                InitVal(exprs,level+1);
                while (showWord().getContent().equals(",")) {
                    getWord();
                    InitVal(exprs,level+1);
                }
                if (!getWord().getContent().equals("}")) {
                    error();
                }
            }
        } else {
            if(exprs.size()==0){
                exprs.add(new ArrayList<>());
            }
            exprs.get(exprs.size()-1).add(Exp());
        }
//        System.out.print("<InitVal>\n");
    }

    private Func FuncDef() {
        int functype=FuncType();
        Id id=new Id(getWord());
        ArrayList<Fparam> paras=new ArrayList<>();
        Block block;
        if (!getWord().getContent().equals("(")) {
            error();
        }
        if (showWord().getContent().equals(")")) {
            getWord();
            block=Block();
        } else {
            paras=FuncFParams();
            if (!getWord().getContent().equals(")")) {
                error();
            }
            block=Block();
        }
        return new Func(functype,id,paras,block);
//        System.out.print("<FuncDef>\n");
    }

    private Func MainFuncDef() {                         //在预判的时候就会确定符合要求，不进行每个的特殊处理
        getWord();
        getWord();
        getWord();
        getWord();
        Block block=Block();
        return new Func(1,new Id(new Word("main")),new ArrayList<>(),block,true);
//        System.out.print("<MainFuncDef>\n");
    }

    private int FuncType() {
        String s = getWord().getContent();
//        System.out.print("<FuncType>\n");
        if(s.equals("void"))
            return 0;
        else if(s.equals("int"))
            return 1;
        else
            return -1;
    }

    private ArrayList<Fparam> FuncFParams() {
        ArrayList<Fparam> list=new ArrayList<>();
        list.add(FuncFParam());
        while (showWord().getContent().equals(",")) {
            getWord();
            list.add(FuncFParam());
        }
        return list;
//        System.out.print("<FuncFParams>\n");
    }

    private Fparam FuncFParam() {
        BType();
        int count=0;
        Expr expr=null;
        Id id=new Id(getWord());
        if (showWord().getContent().equals("[")) {
            count++;
            getWord();
            if (!getWord().getContent().equals("]"))
                error();
            while (showWord().getContent().equals("[")) {
                count++;
                getWord();
                expr=ConstExp();
                if (!getWord().getContent().equals("]"))
                    error();
            }
        }
        return new Fparam(id,count,expr);
//        System.out.print("<FuncFParam>\n");
    }

    private Block Block() {
        ArrayList<BlockItem> items=new ArrayList<>();
        if (!getWord().getContent().equals("{")) {
            error();
        }
        if (showWord().getContent().equals("}")) {
            getWord();
        } else {
            BlockItem item=BlockItem();
            if(item!=null) {
                items.add(item);
            }
            while (!showWord().getContent().equals("}")) {
                item=BlockItem();
                if(item!=null) {
                    items.add(item);
                }
            }
            getWord();
        }
        return new Block(items);
//        System.out.print("<Block>\n");
    }

    private BlockItem BlockItem() {
        if (showWord().getContent().equals("int") || showWord().getContent().equals("const")) {
            return Decl();
        } else {
            return Stmt();
        }
//        System.out.print("<BlockItem>\n");
    }

    private Stmt Stmt() {
        if (showWord().getContent().equals("if")) {
            Stmt stmt2=null;
            getWord();
            if (!getWord().getContent().equals("("))
                error();
            Or or=Cond();
            if (!getWord().getContent().equals(")"))
                error();
            Stmt stmt1=Stmt();
            if (showWord().getContent().equals("else")) {
                getWord();
                stmt2=Stmt();
            }
                return new If(or,stmt1,stmt2);

        } else if (showWord().getContent().equals("{")) {
            return Block();
        } else if (showWord().getContent().equals("while")) {
            getWord();
            if (!getWord().getContent().equals("("))
                error();
            Or or=Cond();
            if (!getWord().getContent().equals(")"))
                error();
            Stmt stmt=Stmt();
            return new While(or,stmt);
        } else if (showWord().getContent().equals("break") || showWord().getContent().equals("continue")) {
            int flag=0;
            if(getWord().getContent().equals("break")){
                flag=1;
            }
            if (!getWord().getContent().equals(";")) {
                error();
            }
            if(flag==1){
                return new Break();
            }else{
                return new Continue();
            }
        } else if (showWord().getContent().equals("return")) {
            Expr expr=null;
            getWord();
            if (showWord().getContent().equals(";")) {
                getWord();
            } else {
                expr=Exp();
                if (!getWord().getContent().equals(";"))
                    error();
            }
            return new Ret(expr);
        } else if (showWord().getContent().equals("printf")) {
            Word format;
            ArrayList<Expr> exprs=new ArrayList<>();
            getWord();
            if (!getWord().getContent().equals("("))
                error();
            format=getWord();
            while (showWord().getContent().equals(",")) {
                getWord();
                exprs.add(Exp());
            }
            if (!getWord().getContent().equals(")"))
                error();
            if (!getWord().getContent().equals(";"))
                error();
            return new Print(format,exprs);
        } else if (showWord().getContent().equals(";")) {
            getWord();
        } else if (showWord().getSymnumber() == 1) {
            int flag1 = 0;
            if (showWord(index + 1).getContent().equals("=")) {
                flag1 = 1;
            } else if (showWord(index + 1).getContent().equals("(")) {
                flag1 = 2;
            } else if (showWord(index + 1).getContent().equals("[")) {
                int k = index + 1;
                while (showWord(k).getContent().equals("[")) {
                    k++;
                    int level = 1;
                    while (level > 0) {
                        if (showWord(k).getContent().equals("["))
                            level++;
                        else if (showWord(k).getContent().equals("]"))
                            level--;
                        k++;
                    }
                }
                if (showWord(k).getContent().equals("="))
                    flag1 = 1;
                else
                    flag1 = 2;
            } else {
                flag1 = 2;
            }

            if (flag1 == 1) {
                Lval lval= (Lval) LVal();
                getWord();
                if (showWord().getContent().equals("getint")) {
                    getWord();
                    if (!getWord().getContent().equals("("))
                        error();
                    if (!getWord().getContent().equals(")"))
                        error();
                    if (!getWord().getContent().equals(";"))
                        error();
                    return new Scanf(lval);
                } else {
                    Expr expr=Exp();
                    if (!getWord().getContent().equals(";"))
                        error();
                    return new Assign(lval,expr);
                }
            } else {
                Expr expr=Exp();
                if (!getWord().getContent().equals(";"))
                    error();
                return expr;
            }
        } else {
            Expr expr=Exp();
            if (!getWord().getContent().equals(";"))
                error();
            return expr;
        }
        return null;
//        System.out.print("<Stmt>\n");
    }

    private Expr Exp() {
        Expr expr = AddExp();
        return expr;
//        System.out.print("<Exp>\n");
    }

    private Or Cond() {
        return LOrExp();
//        System.out.print("<Cond>\n");
    }

    private Expr LVal() {
        Word id = getWord();
        int flag = 0;
        Expr exp1=null;
        Expr exp2=null;
        while (showWord().getContent().equals("[")) {
            flag++;
            getWord();

            if (flag == 1)
                exp1 = Exp();
            else if (flag == 2)
                exp2 = Exp();

            if (!getWord().getContent().equals("]")) {
                error();
            }
        }
        if (flag == 0) {
            return new Id( id);
        } else if (flag == 1) {
            return new Array(id,exp1);
        } else {
            return new Array(id,exp1,exp2);
        }
//        System.out.print("<LVal>\n");
    }

    private Expr PrimaryExp() {
        if (showWord().getContent().equals("(")) {
            getWord();
            Expr expr=Exp();
            if (!getWord().getContent().equals(")")) {
                error();
            }
            return expr;
        } else if (showWord().getSymnumber() == 1) {
            return LVal();
        } else if (showWord().getSymnumber() == 2) {
            return Number();
        } else {
            error();
        }
        return null;
//        System.out.print("<PrimaryExp>\n");
    }

    private Expr Number() {
        return new Constant(getWord());
//        System.out.print("<Number>\n");
    }

    private Expr UnaryExp() {
        String s = showWord().getContent();
        if (showWord().getSymnumber() == 1 && showWord(index + 1).getContent().equals("(")) {
            Word w=getWord();
            ArrayList<Expr> exprs=new ArrayList<>();
            getWord();
            if (showWord().getContent().equals(")")) {
                getWord();
            } else {
                exprs=FuncRParams();
                if (!getWord().getContent().equals(")"))
                    error();
            }
            return new FuncR(w,exprs);
        } else if (s.equals("+") || s.equals("-") || s.equals("!")) {
            Word w=UnaryOp();
            Expr expr=UnaryExp();
            return new Unary(w,expr);
        } else {
            return PrimaryExp();
        }
//        System.out.print("<UnaryExp>\n");
    }

    private Word UnaryOp() {
        return getWord();
    }

    private ArrayList<Expr> FuncRParams() {
        ArrayList<Expr> exprs=new ArrayList<>();
        exprs.add(Exp());
        while (showWord().getContent().equals(",")) {
            getWord();
            exprs.add(Exp());
        }
        return exprs;
//        System.out.print("<FuncRParams>\n");
    }

    private Expr MulExp() {
        Expr expr1 = UnaryExp();
        while (true) {
            String s = showWord().getContent();
            if (s.equals("*") || s.equals("/") || s.equals("%")) {
//                System.out.print("<MulExp>\n");
                Word w = getWord();
                Expr expr2 = UnaryExp();
                expr1 = new Arith( w, expr1, expr2);
            } else {
                break;
            }
        }
        return expr1;
//        System.out.print("<MulExp>\n");
    }

    private Expr AddExp() {
        Expr expr1 = MulExp();
        while (showWord().getContent().equals("+") || showWord().getContent().equals("-")) {
//            System.out.print("<AddExp>\n");
            Word w = getWord();
            Expr expr2 = MulExp();
            expr1 = new Arith( w, expr1, expr2);
        }
        return expr1;
//        System.out.print("<AddExp>\n");
    }

    private Expr RelExp() {
        Expr expr1=AddExp();
        while (true) {
            String s = showWord().getContent();
            if (s.equals("<") || s.equals(">") || s.equals("<=") || s.equals(">=")) {
//                System.out.print("<RelExp>\n");
                Word w=getWord();
                Expr expr2=AddExp();
                expr1=new Logical(w,expr1,expr2);
            } else {
                break;
            }
        }
        return expr1;
//        System.out.print("<RelExp>\n");
    }

    private Expr EqExp() {
        Expr expr1=RelExp();
        while (showWord().getContent().equals("==") || showWord().getContent().equals("!=")) {
//            System.out.print("<EqExp>\n");
            Word w=getWord();
            Expr expr2=RelExp();
            expr1=new Logical(w,expr1,expr2);
        }
        return expr1;
//        System.out.print("<EqExp>\n");
    }

    private And LAndExp() {
        ArrayList<Expr> exprs=new ArrayList<>();
        exprs.add(EqExp());
        while (showWord().getContent().equals("&&")) {
//            System.out.print("<LAndExp>\n");
            getWord();
            exprs.add(EqExp());
        }
//        System.out.print("<LAndExp>\n");
        return new And(exprs);
    }

    private Or LOrExp() {
        ArrayList<And> ands=new ArrayList<>();
        ands.add(LAndExp());
        while (showWord().getContent().equals("||")) {
//            System.out.print("<LOrExp>\n");
            getWord();
            ands.add(LAndExp());
        }
//        System.out.print("<LOrExp>\n");
        return new Or(ands);
    }

    private Expr ConstExp() {
        Expr e = AddExp();
        return e;
//        System.out.print("<ConstExp>\n");
    }

}
