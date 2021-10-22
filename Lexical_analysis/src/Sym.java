import Word.Word;
import Word.FormatWord;

import java.util.ArrayList;
import java.util.HashMap;

public class Sym {
    private int line = 1;
    private final String source_string;
    private char[] source_char;
    private char[] prech;
    private String preprocess_string;
    private ArrayList<Word> words = new ArrayList<>();
    private HashMap<String, Integer> resword;
    private HashMap<Character, Integer> extword;
//    private String[] symbol;

//    private enum Symbol {
//        A, IDENFR, INTCON, STRCON, MAINTK, CONSTTK, INTTK, BREAKTK, CONTINUETK, IFTK, ELSETK,
//        NOT, AND, OR, WHILETK, GETINTTK, PRINTFTK, RETURNTK, PLUS, MINU, VOIDTK, MULT, DIV,
//        MOD, LSS, LEQ, GRE, GEQ, EQL, NEQ, ASSIGN, SEMICN, COMMA, LPARENT, RPARENT, LBRACK,
//        RBRACK, LBRACE, RBRACE;
//    }

    public Sym(String s) {
        initi();
        source_string = s;
        source_char = s.toCharArray();
        Preprocess(source_char);
        getsym();

        //词法分析调试模块
//        for (int i = 0; i < words.size(); i++) {
//            System.out.print(Symbol.values()[words.get(i).getSymnumber()]);
//            System.out.print(' ' + words.get(i).getContent()+'\n');
////            System.out.print(" " + words.get(i).getLine());
//        }
    }

    public ArrayList<Word> getWords() {
        return words;
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
            else if (array[i] == '/' && array[i + 1] == '*') {
                i += 2;
                while (array[i] != '*' || array[i + 1] != '/') {
                    if(array[i]=='\n'){
                        temp[index++]='\n';
                    }
                    i++;
                }
                i++;
                temp[index++] = ' ';
                continue;
            }
            //标准表达式内特殊处理
            else if (array[i] == '"') {
                temp[index++] = array[i++];
                while (array[i] != '"') {
                    temp[index++] = array[i++];
                }
            }

            //跳过空字符,同时保留换行符用来记录行数
            if ( array[i] != '\r') {
                temp[index++] = array[i];
            }
        }

        //标记结束字符
        temp[index++] = '$';

        //截取处理完可用字符串部分
        char[] overpre = new char[index];
        if (index >= 0) System.arraycopy(temp, 0, overpre, 0, index);
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
            //跳过空字符
            while (prech[index] == ' ' || prech[index] == '\t'||prech[index]=='\n') {
                if(prech[index]=='\n')
                    line++;
                index++;
            }
            //注意字符是否需要转义
            if (Character.isUpperCase(prech[index]) || Character.isLowerCase(prech[index]) || prech[index] == '_') {
                last = index;
                while (Character.isLetterOrDigit(prech[last]) || prech[last] == '_') {
                    last++;
                }
                String s = preprocess_string.substring(index, last);
//                if (resword.containsKey(s)) {
//                    words.add(new Word.Word(resword.get(s), s, line));
//                } else {
//                    words.add(new Word.Word(1, s, line));
//                }
                words.add(new Word(resword.getOrDefault(s,1),s,line));
                index = last;
            } else if (Character.isDigit(prech[index])) {      //注意可能会有对前导0的特判
                last = index;
                while (Character.isDigit(prech[last])) {
                    last++;
                }
                words.add(new Word(2, preprocess_string.substring(index, last), line));
                index = last;
            } else if (prech[index] == '"') {
                int count=0;
                boolean flag=true;
                last = index + 1;
                while (prech[last] != '"') {
                    if(prech[last]=='\\'){
                        if(prech[last+1]!='n')
                            flag=false;
                    }else if(prech[last]=='%'){
                        if(prech[last+1]=='d'){
                            count++;
                        }else {
                            flag=false;
                        }
                    }else if(prech[last]==32||prech[last]==33||prech[last]>=40&&prech[last]<=126){
                        last++;
                        continue;
                    }else{
                        flag=false;
                    }
                   last++;
                }
                words.add(new FormatWord(3, preprocess_string.substring(index, last), line,count,flag));
                index = last+1;
            } else if (prech[index] == '&') {
                if (prech[index + 1] == '&') {
                    words.add(new Word(12, "&&", line));
                    index += 2;
                } else {
                    System.out.println("&wrong");
                    break;
                }
            } else if (prech[index] == '!') {
                if (prech[index + 1] == '=') {
                    words.add(new Word(29, "!=", line));
                    index += 2;
                } else {
                    words.add(new Word(11, "!", line));
                    index++;
                }
            } else if (prech[index] == '|') {
                if (prech[index + 1] == '|') {
                    words.add(new Word(13, "||", line));
                    index += 2;
                } else {
                    System.out.println("|wrong");
                    break;
                }
            } else if (prech[index] == '<') {
                if (prech[index + 1] == '=') {
                    words.add(new Word(25, "<=", line));
                    index += 2;
                } else {
                    words.add(new Word(24, "<", line));
                    index++;
                }
            } else if (prech[index] == '>') {
                if (prech[index + 1] == '=') {
                    words.add(new Word(27, ">=", line));
                    index += 2;
                } else {
                    words.add(new Word(26, ">", line));
                    index++;
                }
            } else if (prech[index] == '=') {
                if (prech[index + 1] == '=') {
                    words.add(new Word(28, "==", line));
                    index += 2;
                } else {
                    words.add(new Word(30, "=", line));
                    index++;
                }
            } else if (prech[index] == '[' || prech[index] == ']' || prech[index] == '{' ||
                    prech[index] == '}' || prech[index] == '(' || prech[index] == ')' ||
                    prech[index] == ',' || prech[index] == ';' || prech[index] == '%' ||
                    prech[index] == '+' || prech[index] == '*' || prech[index] == '/' || prech[index] == '-'
            ) {
                words.add(new Word(extword.get(prech[index]), String.valueOf(prech[index]), line));
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
