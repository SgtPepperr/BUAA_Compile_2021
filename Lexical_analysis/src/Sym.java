import java.util.ArrayList;
import java.util.HashMap;

public class Sym {
    private String source_string;
    private char[] source_char;
    private char[] prech;
    private String preprocess_string;
    private ArrayList<String> words = new ArrayList<>();
    private ArrayList<Integer> symbolnumber = new ArrayList<>();
    private HashMap<String, Integer> resword;
    private HashMap<Character, Integer> extword;
    private String[] symbol;

    private enum Symbol {
        A, IDENFR, INTCON, STRCON, MAINTK, CONSTTK, INTTK, BREAKTK, CONTINUETK, IFTK, ELSETK,
        NOT, AND, OR, WHILETK, GETINTTK, PRINTFTK, RETURNTK, PLUS, MINU, VOIDTK, MULT, DIV,
        MOD, LSS, LEQ, GRE, GEQ, EQL, NEQ, ASSIGN, SEMICN, COMMA, LPARENT, RPARENT, LBRACK,
        RBRACK, LBRACE, RBRACE;
    }

    public Sym(String s) {
        initi();
        source_string = s;
        source_char = s.toCharArray();
        Preprocess(source_char);
        getsym();

        //词法分析调试模块
        for (int i = 0; i < words.size(); i++) {
            System.out.print(Symbol.values()[symbolnumber.get(i)]);
            System.out.print(' ' + words.get(i) + '\n');
        }
    }


    public void Preprocess(char[] array) {   //预处理部分
        char[] temp = new char[array.length + 1];
        int index = 0;
        for (int i = 0; i < array.length; i++) {
            //单行注释处理
            if (array[i] == '/' && array[i + 1] == '/') {    //可能越界？
                while (array[i] != '\n') {
                    i++;
                }
            }
            //多行注释处理
            if (array[i] == '/' && array[i + 1] == '*') {
                i += 2;
                while (array[i] != '*' || array[i + 1] != '/') {
                    i++;
                }
                i++;
                temp[index++] = ' ';
                continue;
            }
            //标准表达式内特殊处理
            if (array[i] == '"') {
                temp[index++] = array[i++];
                while (array[i] != '"') {
                    temp[index++] = array[i++];
                }
            }

            //跳过空字符
            if (array[i] != '\n' && array[i] != '\r') {
                temp[index++] = array[i];
            }
        }

        //标记结束字符
        temp[index++] = '$';

        //截取处理完可用字符串部分
        char[] overpre = new char[index];
        for (int i = 0; i < index; i++) {
            overpre[i] = temp[i];
        }
        this.prech = overpre;
        this.preprocess_string = new String(prech);

        //调试模块
//        for (char c : overpre) {
//            System.out.print(c);
//        }
    }

    public void initi() {
        resword = new HashMap<>();
        resword.put("main", 4);
        resword.put("const", 5);
        resword.put("int", 6);
        resword.put("break", 7);
        resword.put("continue", 8);
        resword.put("if", 9);
        resword.put("else", 10);
        resword.put("while", 14);
        resword.put("getint", 15);
        resword.put("printf", 16);
        resword.put("return", 17);
        resword.put("void", 20);
        extword = new HashMap<>();
        extword.put('+', 18);
        extword.put('-', 19);
        extword.put('*', 21);
        extword.put('/', 22);
        extword.put('%', 23);
        extword.put(';', 31);
        extword.put(',', 32);
        extword.put('(', 33);
        extword.put(')', 34);
        extword.put('[', 35);
        extword.put(']', 36);
        extword.put('{', 37);
        extword.put('}', 38);
    }

    public void getsym() {
        int index = 0;
        int last = 0;
        while (prech[index] != '$') {

            while (prech[index] == ' ' || prech[index] == '\t') {
                index++;
            }
            //注意字符是否需要转义
            if (Character.isUpperCase(prech[index]) || Character.isLowerCase(prech[index]) || prech[index] == '_') {
                last = index;
                while (Character.isLetterOrDigit(prech[last]) || prech[last] == '_') {
                    last++;
                }
                String s = preprocess_string.substring(index, last);
                words.add(s);
                if (resword.containsKey(s)) {
                    symbolnumber.add(resword.get(s));
                } else {
                    symbolnumber.add(1);
                }
                index = last;
            } else if (Character.isDigit(prech[index])) {      //注意可能会有对前导0的特判
                last = index;
                while (Character.isDigit(prech[last])) {
                    last++;
                }
                words.add(preprocess_string.substring(index, last));
                symbolnumber.add(2);
                index = last;
            } else if (prech[index] == '"') {
                last = index + 1;
                while (prech[last++] != '"') {
                    continue;
                }
                words.add(preprocess_string.substring(index, last));
                symbolnumber.add(3);
                index = last;
            } else if (prech[index] == '&') {
                if (prech[index + 1] == '&') {
                    words.add("&&");
                    symbolnumber.add(12);
                    index += 2;
                } else {
                    System.out.println("&wrong");
                    break;
                }
            } else if (prech[index] == '!') {
                if (prech[index + 1] == '=') {
                    words.add("!=");
                    symbolnumber.add(29);
                    index += 2;
                } else {
                    words.add("!");
                    symbolnumber.add(11);
                    index++;
                }
            } else if (prech[index] == '|') {
                if (prech[index + 1] == '|') {
                    words.add("||");
                    symbolnumber.add(13);
                    index += 2;
                } else {
                    System.out.println("|wrong");
                    break;
                }
            } else if (prech[index] == '<') {
                if (prech[index + 1] == '=') {
                    words.add("<=");
                    symbolnumber.add(25);
                    index += 2;
                } else {
                    words.add("<");
                    symbolnumber.add(24);
                    index++;
                }
            } else if (prech[index] == '>') {
                if (prech[index + 1] == '=') {
                    words.add(">=");
                    symbolnumber.add(27);
                    index += 2;
                } else {
                    words.add(">");
                    symbolnumber.add(26);
                    index++;
                }
            } else if (prech[index] == '=') {
                if (prech[index + 1] == '=') {
                    words.add("==");
                    symbolnumber.add(28);
                    index += 2;
                } else {
                    words.add("=");
                    symbolnumber.add(30);
                    index++;
                }
            } else if (prech[index] == '[' || prech[index] == ']' || prech[index] == '{' ||
                    prech[index] == '}' || prech[index] == '(' || prech[index] == ')' ||
                    prech[index] == ',' || prech[index] == ';' || prech[index] == '%' ||
                    prech[index] == '+' || prech[index] == '*' || prech[index] == '/' || prech[index] == '-'
            ) {
                words.add(String.valueOf(prech[index]));
                symbolnumber.add(extword.get(prech[index]));
                index++;
            } else if (prech[index] == '$') {
                break;
            } else {
                System.out.println("something is wrong");
                break;
            }
        }
    }
}
