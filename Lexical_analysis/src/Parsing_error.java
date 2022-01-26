import Symbol_table.FuncTable;
import Symbol_table.IntergerTable;
import Symbol_table.Symbols.ArraySymbol;
import Symbol_table.Symbols.FuncSymbol;
import Symbol_table.Symbols.NorSymbol;
import Symbol_table.Symbols.VarSymbol;
import Word.FormatWord;
import Word.Word;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class Parsing_error {
//    private enum Symbol {
//        A, IDENFR, INTCON, STRCON, MAINTK, CONSTTK, INTTK, BREAKTK, CONTINUETK, IFTK, ELSETK,
//        NOT, AND, OR, WHILETK, GETINTTK, PRINTFTK, RETURNTK, PLUS, MINU, VOIDTK, MULT, DIV,
//        MOD, LSS, LEQ, GRE, GEQ, EQL, NEQ, ASSIGN, SEMICN, COMMA, LPARENT, RPARENT, LBRACK,
//        RBRACK, LBRACE, RBRACE
//    }

    private final ArrayList<Word> words;
    private int index = 0;
    private FuncTable functable = new FuncTable();
    private IntergerTable inttable = new IntergerTable();
    private int errorarray[][] = new int[500][2];
    private int errorcount = 0;
    private int CircleLevel = 0;
    private int functype = 0;      //void:0,int:1
    private boolean inCond = false;
    private boolean isLast = false;
    private boolean hasvoid = false;
    private ArrayList<Integer> maxarray = new ArrayList<>();
    private int intofunc = 0;

    public Parsing_error(ArrayList<Word> words) {
        this.words = words;
        maxarray.add(0);
        analyse();
        if (errorcount>0){
            System.out.println("Something is wrong with your code\nBelows are your problems");
            errorout();
            System.exit(1);
        }
    }

    public void analyse() {
        CompUnit();
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

    private boolean NextIsExp(Word w) {
        String s = w.getContent();
        int num = w.getSymnumber();
        if (s.equals("(") || s.equals("+") || s.equals("-") || s.equals("!")) {
            return true;
        } else if (num == 1 || num == 2) {
            return true;
        } else {
            return false;
        }
    }

    private void error() {
        System.out.print("---------------wrong-------------");
    }

    private void errordeal(int type, int line) {
        //      System.out.print(line + " " + (char) ('a' + type) + "\n");
        errorarray[errorcount][0] = line;
        errorarray[errorcount++][1] = type;
    }

    private void errorout() {
        int array[][] = new int[errorcount][2];
        System.arraycopy(errorarray, 0, array, 0, errorcount);
        Arrays.sort(array, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                return o1[0] - o2[0];
            }
        });
        for (int i = 0; i < errorcount; i++) {
            System.out.println(array[i][0] + " " + (char) ('a' + array[i][1]));        //可能需要排序，记得排序
        }
    }

//    private void output() {
//        System.out.print(Parsing.Symbol.values()[words.get(index).getSymnumber()]);
//        System.out.print(' ' + words.get(index).getContent() + '\n');
//    }

    private void CompUnit() {
        while (!showWord(index + 2).getContent().equals("("))
            Decl();
        while (!showWord(index + 1).getContent().equals("main"))
            FuncDef();
        MainFuncDef();
//        System.out.print("<CompUnit>\n");
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
        NorSymbol sym = new NorSymbol();
        if (getWord().getContent().equals("const")) {
            BType();
            sym = ConstDef();
            while (showWord().getContent().equals(",")) {
                getWord();
                sym = ConstDef();
            }
            if (showWord().getContent().equals(";")) {
                getWord();
            } else {
                errordeal(8, sym.getLine());                             //i类错误
            }
        } else {
            error();
        }
//        System.out.print("<ConstDecl>\n");
    }

    private void BType() {
        if (!getWord().getContent().equals("int")) {
            error();
        }
//        System.out.print("<BType>\n");
    }

    private NorSymbol ConstDef() {                                        //暂时不考虑多维数组每一维的个数
        Word w = getWord();
        String name = w.getContent();
        NorSymbol sym = new NorSymbol();
        int line = w.getLine();
        if (w.getSymnumber() == 1) {
            int count = 0;
            while (showWord().getContent().equals("[")) {
                count++;
                Word w1 = getWord();
                ConstExp();
                if (showWord().getContent().equals("]")) {              //使用[作为行号标记
                    getWord();
                } else {
                    errordeal(10, w1.getLine());                   //错误处理k型缺少】
                }
            }
            if (!getWord().getContent().equals("=")) {
                error();
            }
            ConstInitVal();

            if (inttable.contains(name)) {                          //错误处理b型名字重定义
                errordeal(1, line);
            } else {
                if (count == 0) {
                    sym = new VarSymbol(name, true);
                } else {
                    sym = new ArraySymbol(name, true, count);
                }
                sym.setLine(line);
                inttable.add(name, sym);
            }

        } else {
            error();
        }

        return sym;
//        System.out.print("<ConstDef>\n");
    }

    private void ConstInitVal() {
        if (showWord().getContent().equals("{")) {
            getWord();
            if (showWord().getContent().equals("}")) {
                getWord();
            } else {
                ConstInitVal();
                while (showWord().getContent().equals(",")) {
                    getWord();
                    ConstInitVal();
                }
                if (!getWord().getContent().equals("}")) {
                    error();
                }
            }
        } else {
            ConstExp();
        }
//        System.out.print("<ConstInitVal>\n");
    }

    private void VarDecl() {
        NorSymbol sym = new NorSymbol();
        BType();
        sym = VarDef();
        while (showWord().getContent().equals(",")) {
            getWord();
            sym = VarDef();
        }
        if (showWord().getContent().equals(";")) {
            getWord();
        } else {
            errordeal(8, sym.getLine());                                    //i类错误类型
        }
//        System.out.print("<VarDecl>\n");
    }

    private NorSymbol VarDef() {
        NorSymbol sym = new NorSymbol();
        Word w = getWord();
        String name = w.getContent();
        int line = w.getLine();
        if (w.getSymnumber() == 1) {
            int count = 0;
            while (showWord().getContent().equals("[")) {
                count++;
                Word w1 = getWord();
                ConstExp();
                if (showWord().getContent().equals("]")) {              //使用[作为行号标记
                    getWord();
                } else {
                    errordeal(10, w1.getLine());                   //错误处理k型
                }
            }
            if (showWord().getContent().equals("=")) {
                getWord();
                InitVal();
            }

            if (inttable.contains(name)) {                          //错误处理b型
                errordeal(1, line);
            } else {
                if (count == 0) {
                    sym = new VarSymbol(name, false);
                } else {
                    sym = new ArraySymbol(name, false, count);
                }
                sym.setLine(line);
                inttable.add(name, sym);
            }

        } else {
            error();
        }
        return sym;
//        System.out.print("<VarDef>\n");
    }

    private void InitVal() {
        if (showWord().getContent().equals("{")) {
            getWord();
            if (showWord().getContent().equals("}")) {
                getWord();
            } else {
                InitVal();
                while (showWord().getContent().equals(",")) {
                    getWord();
                    InitVal();
                }
                if (!getWord().getContent().equals("}")) {
                    error();
                }
            }
        } else {
            Exp();
        }
//        System.out.print("<InitVal>\n");
    }

    private void FuncDef() {

        IntergerTable newtable = new IntergerTable();               //进入函数创建一个新的作用域
        newtable.setOut(inttable);
        inttable = newtable;

        int type = FuncType();
        functype = type;
        Word w = getWord();
        String name = w.getContent();
        int line = w.getLine();
        ArrayList<NorSymbol> list = new ArrayList<>();
        if (w.getSymnumber() != 1) {
            error();
        }
        Word w1 = getWord();      //"("

        if (showWord().getContent().equals(")")) {
            getWord();
        } else if (showWord().getContent().equals("{")) {
            errordeal(9, w1.getLine());                         //j类错误缺少）
        } else {
            list = FuncFParams();
            if (showWord().getContent().equals(")")) {
                getWord();
            } else {
                errordeal(9, w1.getLine());                     //j类错误缺少）
            }
        }

        if (functable.contains(name)) {                          //错误处理b型未定义
            errordeal(1, line);
        } else {
            FuncSymbol sym = new FuncSymbol(name, list, type);       //函数定义中增加声明
            functable.add(name, sym);
        }

        intofunc = 1;
        Word w2 = Block();
        if (type == 1 && !isLast)
            errordeal(6, w2.getLine());             //g类错误缺少最后return语句


//        System.out.print("<FuncDef>\n");
    }

    private void MainFuncDef() {//在预判的时候就会确定符合要求，不进行每个的特殊处理

        getWord();
        getWord();
        Word w = getWord();
        if (showWord().getContent().equals(")")) {
            getWord();
        } else {
            errordeal(9, w.getLine());
        }
        functype = 1;
        Word w1 = Block();
        if (!isLast)
            errordeal(6, w1.getLine());              //g类错误
//        System.out.print("<MainFuncDef>\n");
    }

    private int FuncType() {
        String s = getWord().getContent();
        if (s.equals("void"))
            return 0;
        else if (s.equals("int"))
            return 1;
        else
            return -1;
//        System.out.print("<FuncType>\n");
    }

    private ArrayList<NorSymbol> FuncFParams() {
        ArrayList<NorSymbol> list = new ArrayList<>();
        list.add(FuncFParam());
        while (showWord().getContent().equals(",")) {
            getWord();
            list.add(FuncFParam());
        }
        return list;
//        System.out.print("<FuncFParams>\n");
    }

    private NorSymbol FuncFParam() {
        BType();
        Word w = getWord();
        String name = w.getContent();
        int line = w.getLine();
        int count = 0;
        if (w.getSymnumber() != 1)
            error();
        if (showWord().getContent().equals("[")) {
            count++;
            Word w1 = getWord();

            if (showWord().getContent().equals("]")) {
                getWord();
            } else {
                errordeal(10, w1.getLine());                //k型错误
            }

            while (showWord().getContent().equals("[")) {
                count++;
                w1 = getWord();
                ConstExp();
                if (showWord().getContent().equals("]")) {
                    getWord();
                } else {
                    errordeal(10, w1.getLine());                //k型错误
                }
            }
        }
        if (inttable.contains(name)) {                          //错误处理b型
            errordeal(1, line);
        }
        if (count == 0) {
            NorSymbol sym = new VarSymbol(name, false);
            inttable.add(name, sym);
            return sym;
        } else {
            NorSymbol sym = new ArraySymbol(name, false, count);
            inttable.add(name, sym);
            return sym;
        }
//        System.out.print("<FuncFParam>\n");
    }

    private Word Block() {
        getWord();
        Word w;
        if (showWord().getContent().equals("}")) {
            isLast = false;
            w = getWord();
            if (intofunc == 1) {
                intofunc = 0;
                inttable = inttable.getOut();
            }
        } else {
            if (intofunc == 1) {
                intofunc = 0;
            } else {
                IntergerTable newtable = new IntergerTable();                //进入一个新的作用域时，创建新的符号表
                newtable.setOut(inttable);
                inttable = newtable;
            }
            isLast = false;
            BlockItem();
            while (!showWord().getContent().equals("}")) {
                isLast = false;
                BlockItem();
            }
            w = getWord();
            inttable = inttable.getOut();
        }
        return w;
//        System.out.print("<Block>\n");
    }

    private void BlockItem() {
        if (showWord().getContent().equals("int") || showWord().getContent().equals("const")) {
            Decl();
        } else {
            Stmt();
        }
//        System.out.print("<BlockItem>\n");
    }

    private void Stmt() {
        if (showWord().getContent().equals("if")) {
            inCond = true;
            Word w = getWord();
            int line = w.getLine();
            if (!getWord().getContent().equals("("))
                error();
            Cond();
            if (showWord().getContent().equals(")")) {
                getWord();
            } else {
                errordeal(9, line);                             //j kind error
            }
            Stmt();
            if (showWord().getContent().equals("else")) {
                getWord();
                Stmt();
            }
            inCond = false;
        } else if (showWord().getContent().equals("{")) {
            inCond = true;
            Block();
            inCond = false;
        } else if (showWord().getContent().equals("while")) {
            inCond = true;
            Word w = getWord();
            int line = w.getLine();
            if (!getWord().getContent().equals("("))
                error();
            Cond();
            if (showWord().getContent().equals(")")) {
                getWord();
            } else {
                errordeal(9, line);                             //j kind error
            }
            CircleLevel++;                                           //循环层次增加
            Stmt();
            CircleLevel--;
            inCond = false;
        } else if (showWord().getContent().equals("break") || showWord().getContent().equals("continue")) {
            Word w = getWord();

            if (CircleLevel == 0)                                       //m型错误
                errordeal(12, w.getLine());

            if (showWord().getContent().equals(";")) {
                getWord();
            } else {
                errordeal(8, w.getLine());                      //i类错误
            }

        } else if (showWord().getContent().equals("return")) {
            if (inCond == false)
                isLast = true;
            Word w = getWord();
            int line = w.getLine();
//            if(showWord().getContent().equals(";")){
//                getWord();
//            }else{
//                Exp();
//                if(!getWord().getContent().equals(";"))
//                    error();
//            }
            if (NextIsExp(showWord())) {
                Exp();
                if (functype == 0)
                    errordeal(5, line);                      //f型错误
                if (showWord().getContent().equals(";")) {
                    getWord();
                } else {
                    errordeal(8, line);                              //i kind error
                }
            } else {
                if (showWord().getContent().equals(";")) {
                    getWord();
                } else {
                    errordeal(8, line);                              //i kind error
                }
            }
        } else if (showWord().getContent().equals("printf")) {
            int symnum = 0;
            Word w1 = getWord();
            int count1 = w1.getLine();

            if (!getWord().getContent().equals("("))
                error();

            FormatWord w2 = (FormatWord) getWord();
            int count2 = w2.getLine();
            boolean correct = w2.isCorrect();
            int num = w2.getNum();

            if (!correct)
                errordeal(0, count2);                            //a类错误

            while (showWord().getContent().equals(",")) {
                symnum++;
                getWord();
                Exp();
            }

            if (showWord().getContent().equals(")")) {
                getWord();
            } else {
                errordeal(9, count2);                             //j kind error
            }
            if (showWord().getContent().equals(";")) {
                getWord();
            } else {
                errordeal(8, count2);                              //i kind error
            }
//            if(!getWord().getContent().equals(")"))
//                error();
//            if(!getWord().getContent().equals(";"))
//                error();

            if (num != symnum)                                           //l类错误
                errordeal(11, count1);

        } else if (showWord().getContent().equals(";")) {
            getWord();
        } else if (showWord().getSymnumber() == 1) {
            int flag1 = 0;
            if (showWord(index + 1).getContent().equals("=")) {
                flag1 = 1;
            } else if (showWord(index + 1).getContent().equals("(")) {
                flag1 = 2;
            } else if (showWord(index + 1).getContent().equals("[")) {
//                int k=index+1;                                                 //有可能会出问题就是
//                while (!showWord(k).getContent().equals(";")){
//                    if(showWord(k).getContent().equals("=")){
//                        break;
//                    }
//                    k++;
//                }
//                if(showWord(k).getContent().equals("="))
//                    flag1=1;
//                else
//                    flag1=2;
                int k = index + 1;
                while (showWord(k).getContent().equals("[")) {
                    k++;
                    int level = 1;
                    while (level > 0) {
                        if (showWord(k).getContent().equals("["))
                            level++;
                        else if (showWord(k).getContent().equals("]"))
                            level--;
                        else if (showWord(k).getContent().equals("=")) {
                            flag1 = 1;
                            break;
                        } else if (showWord(k).getContent().equals(";")) {
                            flag1 = 2;
                            break;
                        }
                        k++;
                    }
                    if (flag1 > 0)
                        break;
                }
                if (flag1 == 0) {
                    if (showWord(k).getContent().equals("="))
                        flag1 = 1;
                    else
                        flag1 = 2;
                }
            } else {
                flag1 = 2;
            }

            if (flag1 == 1) {

                NorSymbol sym = LVal();
                if (sym.isConst()) {                               //h型错误
                    errordeal(7, sym.getLine());
                }

                Word w = getWord();
                int line = w.getLine();

                if (showWord().getContent().equals("getint")) {
                    getWord();
                    if (!getWord().getContent().equals("("))
                        error();
                    if (showWord().getContent().equals(")")) {
                        getWord();
                    } else {
                        errordeal(9, line);                             //j kind error
                    }
                    if (showWord().getContent().equals(";")) {
                        getWord();
                    } else {
                        errordeal(8, line);                              //i kind error
                    }
                } else {
                    Exp();
                    if (showWord().getContent().equals(";")) {
                        getWord();
                    } else {
                        errordeal(8, line);                              //i kind error
                    }
                }
            } else {
                Word w = Exp();
                if (showWord().getContent().equals(";"))
                    getWord();
                else
                    errordeal(8, w.getLine());
            }
        } else {
            Word w = Exp();
            if (showWord().getContent().equals(";")) {
                getWord();
            } else {
                errordeal(8, w.getLine());                              //i kind error缺;
            }
        }
//        System.out.print("<Stmt>\n");
    }

    private Word Exp() {
        Word w = showWord();
        AddExp();
        return w;
//        System.out.print("<Exp>\n");
    }

    private void Cond() {
        LOrExp();
//        System.out.print("<Cond>\n");
    }

    private NorSymbol LVal() {
        NorSymbol sym = new NorSymbol();
        Word w = getWord();
        String name = w.getContent();
        int line = w.getLine();
        int flag = 0;
        if (w.getSymnumber() != 1)
            error();

        IntergerTable table = inttable;
        while (table != null) {                      //未定义名字c类错误
            if (table.contains(name)) {
                flag = 1;
                sym = table.get(name);
                break;
            }
            table = table.getOut();
        }

        if (flag == 0)
            errordeal(2, line);

        int arrayLevel = sym.getLevel();

        while (showWord().getContent().equals("[")) {
            arrayLevel--;
            Word w1 = getWord();
            Exp();
            if (showWord().getContent().equals("]")) {
                getWord();
            } else {
                errordeal(10, w1.getLine());                //k型错误
            }
        }
        maxarray.set(0, Math.max(maxarray.get(0), arrayLevel));
        sym.setLine(line);
        return sym;
//        System.out.print("<LVal>\n");
    }

    private void PrimaryExp() {
        if (showWord().getContent().equals("(")) {
            Word w = getWord();
            Exp();
            if (showWord().getContent().equals(")")) {
                getWord();
            } else {
                errordeal(9, w.getLine());
            }
        } else if (showWord().getSymnumber() == 1) {
            LVal();
        } else if (showWord().getSymnumber() == 2) {
            Number();
        } else {
            error();
        }
//        System.out.print("<PrimaryExp>\n");
    }

    private void Number() {
        if (getWord().getSymnumber() != 2) {
            error();
        }
//        System.out.print("<Number>\n");
    }

    private void UnaryExp() {
        String s = showWord().getContent();
        if (showWord().getSymnumber() == 1 && showWord(index + 1).getContent().equals("(")) {

            Word w = getWord();
            String name = w.getContent();
            int line = w.getLine();
            int flag = 0;
            if (!functable.contains(name)) {
                errordeal(2, line);//错误处理c类
                getWord();                      //(

                if (NextIsExp(showWord())) {
                    maxarray.add(0, 0);
                    FuncRParams();
                    maxarray.remove(0);
                    if (showWord().getContent().equals(")")) {
                        getWord();
                    } else {
                        errordeal(9, line);               //j kind error
                    }
                } else {

                    if (showWord().getContent().equals(")")) {
                        getWord();
                    } else {
                        errordeal(9, line);              //j kind error
                    }
                }

            } else {
                FuncSymbol sym = functable.get(name);
                if (sym.getReturntype() == 0)
                    maxarray.set(0, 100);                             //for return type void maxarray=100

                ArrayList<NorSymbol> Fparas = sym.getParams();
                ArrayList<Integer> Rparas = new ArrayList<>();
                getWord();                       //(
//            if (showWord().getContent().equals(")")) {
//                getWord();
//            }else{
//                FuncRParams();
//                if (!getWord().getContent().equals(")"))
//                    error();
//            }
                if (NextIsExp(showWord())) {
                    maxarray.add(0, 0);
                    Rparas = FuncRParams();
                    maxarray.remove(0);
                    if (Rparas.size() != Fparas.size()) {
                        errordeal(3, line);                          //d类型错误
                    } else {
                        for (int i = 0; i < Rparas.size(); i++) {
                            if (Rparas.get(i) != Fparas.get(i).getLevel()) {
                                errordeal(4, line);
                                break;                                         //e类型错误
                            }
                        }
                    }

                    if (showWord().getContent().equals(")")) {
                        getWord();
                    } else {
                        errordeal(9, line);               //j kind error
                    }
                } else {

                    if (Rparas.size() != Fparas.size()) {
                        errordeal(3, line);
                    }

                    if (showWord().getContent().equals(")")) {
                        getWord();
                    } else {
                        errordeal(9, line);              //j kind error
                    }
                }
            }
        } else if (s.equals("+") || s.equals("-") || s.equals("!")) {
            UnaryOp();
            UnaryExp();
        } else {
            PrimaryExp();
        }
//        System.out.print("<UnaryExp>\n");
    }

    private void UnaryOp() {
        String s = getWord().getContent();
        if (s.equals("+") || s.equals("-") || s.equals("!")) {

        } else {
            error();
        }
//        System.out.print("<UnaryOp>\n");
    }

    private ArrayList<Integer> FuncRParams() {
        ArrayList<Integer> list = new ArrayList<>();
        maxarray.set(0, 0);
        Exp();
        list.add(maxarray.get(0));
        while (showWord().getContent().equals(",")) {
            getWord();
            maxarray.set(0, 0);
            Exp();
            list.add(maxarray.get(0));
        }
        return list;
//        System.out.print("<FuncRParams>\n");
    }

    private void MulExp() {
        UnaryExp();
        while (true) {
            String s = showWord().getContent();
            if (s.equals("*") || s.equals("/") || s.equals("%")) {
//                System.out.print("<MulExp>\n");
                getWord();
                UnaryExp();
            } else {
                break;
            }
        }
//        System.out.print("<MulExp>\n");
    }

    private void AddExp() {
        MulExp();
        while (showWord().getContent().equals("+") || showWord().getContent().equals("-")) {
//            System.out.print("<AddExp>\n");
            getWord();
            MulExp();
        }
//        System.out.print("<AddExp>\n");
    }

    private void RelExp() {
        AddExp();
        while (true) {
            String s = showWord().getContent();
            if (s.equals("<") || s.equals(">") || s.equals("<=") || s.equals(">=")) {
//                System.out.print("<RelExp>\n");
                getWord();
                AddExp();
            } else {
                break;
            }
        }
//        System.out.print("<RelExp>\n");
    }

    private void EqExp() {
        RelExp();
        while (showWord().getContent().equals("==") || showWord().getContent().equals("!=")) {
//            System.out.print("<EqExp>\n");
            getWord();
            RelExp();
        }
//        System.out.print("<EqExp>\n");
    }

    private void LAndExp() {
        EqExp();
        while (showWord().getContent().equals("&&")) {
//            System.out.print("<LAndExp>\n");
            getWord();
            EqExp();
        }
//        System.out.print("<LAndExp>\n");
    }

    private void LOrExp() {
        LAndExp();
        while (showWord().getContent().equals("||")) {
//            System.out.print("<LOrExp>\n");
            getWord();
            LAndExp();
        }
//        System.out.print("<LOrExp>\n");
    }

    private void ConstExp() {
        AddExp();
//        System.out.print("<ConstExp>\n");
    }

}
