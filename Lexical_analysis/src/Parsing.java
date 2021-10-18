import java.util.ArrayList;

public class Parsing {
    private enum Symbol {
        A, IDENFR, INTCON, STRCON, MAINTK, CONSTTK, INTTK, BREAKTK, CONTINUETK, IFTK, ELSETK,
        NOT, AND, OR, WHILETK, GETINTTK, PRINTFTK, RETURNTK, PLUS, MINU, VOIDTK, MULT, DIV,
        MOD, LSS, LEQ, GRE, GEQ, EQL, NEQ, ASSIGN, SEMICN, COMMA, LPARENT, RPARENT, LBRACK,
        RBRACK, LBRACE, RBRACE
    }

    private final ArrayList<Word> words;
    private int index = 0;

    public Parsing(ArrayList<Word> words) {
        this.words = words;
        analyse();
    }

    public void analyse() {
        CompUnit();
    }

    private Word getWord() {
        if(index<words.size()) {
            System.out.print(Parsing.Symbol.values()[words.get(index).getSymnumber()]);
            System.out.print(' ' + words.get(index).getContent() + '\n');
            return words.get(index++);
        }else {
            return new Word();
        }

    }

    private Word showWord() {
        if(index<words.size())
            return words.get(index);
        else
            return new Word();
    }

    private Word showWord(int num) {
        if(num<words.size())
            return words.get(num);
        else
            return new Word();
    }

    private void error() {
        System.out.print("---------------wrong-------------");
    }

//    private void output() {
//        System.out.print(Parsing.Symbol.values()[words.get(index).getSymnumber()]);
//        System.out.print(' ' + words.get(index).getContent() + '\n');
//    }

    private void CompUnit() {
        while (!showWord(index+2).getContent().equals("("))
            Decl();
        while (!showWord(index+1).getContent().equals("main"))
            FuncDef();
        MainFuncDef();
        System.out.print("<CompUnit>\n");
    }

    private void Decl() {
        if (showWord().getContent().equals("const")) {
            ConstDecl();
        } else {
            VarDecl();
        }
//        System.out.print("<Decl>\n");
    }

    private void ConstDecl() {
        if (getWord().getContent().equals("const")) {
            BType();
            ConstDef();
            while (showWord().getContent().equals(",")) {
                getWord();
                ConstDef();
            }
            if (!getWord().getContent().equals(";")) {
                error();
            }
        } else {
            error();
        }
        System.out.print("<ConstDecl>\n");
    }

    private void BType() {
        if (!getWord().getContent().equals("int")) {
            error();
        }
//        System.out.print("<BType>\n");
    }

    private void ConstDef() {
        if (getWord().getSymnumber() == 1) {
            while (showWord().getContent().equals("[")) {
                getWord();
                ConstExp();
                if (!getWord().getContent().equals("]")) {
                    error();
                }
            }
            if (!getWord().getContent().equals( "=")) {
                error();
            }
            ConstInitVal();
        } else {
            error();
        }

        System.out.print("<ConstDef>\n");
    }

    private void ConstInitVal() {
        if (showWord().getContent().equals("{")) {
            getWord();
            if (showWord().getContent().equals("}")) {
                getWord();
            } else{
                ConstInitVal();
                while (showWord().getContent().equals(",")) {
                    getWord();
                    ConstInitVal();
                }
                if (!getWord().getContent().equals("}")) {
                    error();
                }
            }
        } else{
            ConstExp();
        }
        System.out.print("<ConstInitVal>\n");
    }

    private void VarDecl() {
        BType();
        VarDef();
        while (showWord().getContent().equals(",")) {
            getWord();
            VarDef();
        }
        if (!getWord().getContent().equals(";")) {
            error();
        }
        System.out.print("<VarDecl>\n");
    }

    private void VarDef() {
        if (getWord().getSymnumber() == 1) {
            while (showWord().getContent().equals("[")) {
                getWord();
                ConstExp();
                if (!getWord().getContent().equals("]")) {
                    error();
                }
            }
            if (showWord().getContent().equals("=")) {
                getWord();
                InitVal();
            }
        } else {
            error();
        }
        System.out.print("<VarDef>\n");
    }

    private void InitVal() {
        if (showWord().getContent().equals("{")) {
            getWord();
            if (showWord().getContent().equals("}")) {
                getWord();
            }else{
                InitVal();
                while (showWord().getContent().equals(",")) {
                    getWord();
                    InitVal();
                }
                if (!getWord().getContent().equals("}")) {
                    error();
                }
            }
        }else{
            Exp();
        }
        System.out.print("<InitVal>\n");
    }

    private void FuncDef() {
        FuncType();
        if (getWord().getSymnumber() != 1) {
            error();
        }
        if (!getWord().getContent().equals( "(")) {
            error();
        }
        if (showWord().getContent().equals(")")) {
            getWord();
            Block();
        }else{
            FuncFParams();
            if (!getWord().getContent().equals( ")")) {
                error();
            }
            Block();
        }
        System.out.print("<FuncDef>\n");
    }

    private void MainFuncDef() {                         //在预判的时候就会确定符合要求，不进行每个的特殊处理
        getWord();
        getWord();
        getWord();
        getWord();
        Block();
        System.out.print("<MainFuncDef>\n");
    }

    private void FuncType() {
        String s = getWord().getContent();
        if (!s.equals( "void") && !s.equals( "int"))
            error();
        System.out.print("<FuncType>\n");
    }

    private void FuncFParams() {
        FuncFParam();
        while (showWord().getContent().equals(",")) {
            getWord();
            FuncFParam();
        }
        System.out.print("<FuncFParams>\n");
    }

    private void FuncFParam() {
        BType();
        if (getWord().getSymnumber() != 1)
            error();
        if (showWord().getContent().equals("[")) {
            getWord();
            if (!getWord().getContent().equals( "]"))
                error();
            while (showWord().getContent().equals("[")) {
                getWord();
                ConstExp();
                if (!getWord().getContent().equals("]"))
                    error();
            }
        }
        System.out.print("<FuncFParam>\n");
    }

    private void Block() {
        if(!getWord().getContent().equals("{")){
            error();
        }
        if(showWord().getContent().equals("}")){
            getWord();
        }else{
            BlockItem();
            while(!showWord().getContent().equals("}")){
                BlockItem();
            }
            getWord();
        }
        System.out.print("<Block>\n");
    }

    private void BlockItem() {
        if(showWord().getContent().equals("int")||showWord().getContent().equals("const")){
            Decl();
        }else{
            Stmt();
        }
//        System.out.print("<BlockItem>\n");
    }

    private void Stmt() {
        if(showWord().getContent().equals("if")){
            getWord();
            if(!getWord().getContent().equals("("))
                error();
            Cond();
            if(!getWord().getContent().equals(")"))
                error();
            Stmt();
            if(showWord().getContent().equals("else")){
                getWord();
                Stmt();
            }
        }else if(showWord().getContent().equals("{")){
            Block();
        }else if(showWord().getContent().equals("while")){
            getWord();
            if(!getWord().getContent().equals("("))
                error();
            Cond();
            if(!getWord().getContent().equals(")"))
                error();
            Stmt();
        }else if(showWord().getContent().equals("break")||showWord().getContent().equals("continue")){
            getWord();
            if(!getWord().getContent().equals(";")){
                error();
            }
        }else if(showWord().getContent().equals("return")){
            getWord();
            if(showWord().getContent().equals(";")){
                getWord();
            }else{
                Exp();
                if(!getWord().getContent().equals(";"))
                    error();
            }
        }else if(showWord().getContent().equals("printf")){
            getWord();
            if(!getWord().getContent().equals("("))
                error();
            if(getWord().getSymnumber()!=3)
                error();
            while (showWord().getContent().equals(",")){
                getWord();
                Exp();
            }
            if(!getWord().getContent().equals(")"))
                error();
            if(!getWord().getContent().equals(";"))
                error();
        }else if(showWord().getContent().equals(";")){
            getWord();
        }else if(showWord().getSymnumber()==1){
            int flag1=0;
            if (showWord(index + 1).getContent().equals("=")) {
                flag1=1;
            }else if(showWord(index+1).getContent().equals("(")){
                flag1=2;
            }else if(showWord(index+1).getContent().equals("[")){
                int k=index+1;
                while(showWord(k).getContent().equals("[")){
                    k++;
                    int level=1;
                    while(level>0){
                        if(showWord(k).getContent().equals("["))
                            level++;
                        else if(showWord(k).getContent().equals("]"))
                            level--;
                        k++;
                    }
                }
                if(showWord(k).getContent().equals("="))
                    flag1=1;
                else
                    flag1=2;
            }else{
                flag1=2;
            }

            if(flag1==1){
                LVal();
                getWord();
                if(showWord().getContent().equals("getint")){
                    getWord();
                    if(!getWord().getContent().equals("("))
                        error();
                    if(!getWord().getContent().equals(")"))
                        error();
                    if(!getWord().getContent().equals(";"))
                        error();
                }else{
                    Exp();
                    if(!getWord().getContent().equals(";"))
                        error();
                }
            }else{
                Exp();
                if(!getWord().getContent().equals(";"))
                    error();
            }
        }else{
            Exp();
            if(!getWord().getContent().equals(";"))
                error();
        }
        System.out.print("<Stmt>\n");
    }

    private void Exp() {
        AddExp();
        System.out.print("<Exp>\n");
    }

    private void Cond() {
        LOrExp();
        System.out.print("<Cond>\n");
    }

    private void LVal() {
        if (getWord().getSymnumber() != 1)
            error();
        while (showWord().getContent().equals("[")) {
            getWord();
            Exp();
            if (!getWord().getContent().equals("]")) {
                error();
            }
        }
        System.out.print("<LVal>\n");
    }

    private void PrimaryExp() {
        if (showWord().getContent().equals("(")) {
            getWord();
            Exp();
            if (!getWord().getContent().equals(")")) {
                error();
            }
        }else if (showWord().getSymnumber() == 1) {
            LVal();
        } else if (showWord().getSymnumber() == 2) {
            Number();
        } else {
            error();
        }
        System.out.print("<PrimaryExp>\n");
    }

    private void Number() {
        if (getWord().getSymnumber() != 2) {
            error();
        }
        System.out.print("<Number>\n");
    }

    private void UnaryExp() {
        String s = showWord().getContent();
        if (showWord().getSymnumber() == 1 && showWord(index + 1).getContent().equals("(")) {
            getWord();
            getWord();
            if (showWord().getContent().equals(")")) {
                getWord();
            }else{
                FuncRParams();
                if (!getWord().getContent().equals(")"))
                    error();
            }
        }else if (s.equals("+") || s.equals("-") || s.equals("!")) {
            UnaryOp();
            UnaryExp();
        }else{
            PrimaryExp();
        }
        System.out.print("<UnaryExp>\n");
    }

    private void UnaryOp() {
        String s = getWord().getContent();
        if (s.equals("+") || s.equals( "-") || s.equals( "!")) {

        }else{
            error();
        }
        System.out.print("<UnaryOp>\n");
    }

    private void FuncRParams() {
        Exp();
        while (showWord().getContent().equals(",")) {
            getWord();
            Exp();
        }
        System.out.print("<FuncRParams>\n");
    }

    private void MulExp() {
        UnaryExp();
        while (true) {
            String s = showWord().getContent();
            if (s.equals("*") || s.equals("/") || s.equals("%")) {
                System.out.print("<MulExp>\n");
                getWord();
                UnaryExp();
            }else{
                break;
            }
        }
        System.out.print("<MulExp>\n");
    }

    private void AddExp() {
        MulExp();
        while (showWord().getContent().equals("+") || showWord().getContent().equals("-")) {
            System.out.print("<AddExp>\n");
            getWord();
            MulExp();
        }
        System.out.print("<AddExp>\n");
    }

    private void RelExp() {
        AddExp();
        while (true) {
            String s = showWord().getContent();
            if (s.equals("<") || s.equals(">") || s.equals("<=") || s.equals(">=")) {
                System.out.print("<RelExp>\n");
                getWord();
                AddExp();
            }else{
                break;
            }
        }
        System.out.print("<RelExp>\n");
    }

    private void EqExp() {
        RelExp();
        while (showWord().getContent().equals("==") || showWord().getContent().equals("!=")) {
            System.out.print("<EqExp>\n");
            getWord();
            RelExp();
        }
        System.out.print("<EqExp>\n");
    }

    private void LAndExp() {
        EqExp();
        while (showWord().getContent().equals("&&")) {
            System.out.print("<LAndExp>\n");
            getWord();
            EqExp();
        }
        System.out.print("<LAndExp>\n");
    }

    private void LOrExp() {
        LAndExp();
        while (showWord().getContent().equals("||")) {
            System.out.print("<LOrExp>\n");
            getWord();
            LAndExp();
        }
        System.out.print("<LOrExp>\n");
    }

    private void ConstExp() {
        AddExp();
        System.out.print("<ConstExp>\n");
    }

}
