import Mipscode.Mips;
import Optim.Optimize;

import java.io.*;

public class Compiler {

    public static void readToBuffer(StringBuffer buffer, String filePath) throws IOException {
        InputStream is = new FileInputStream(filePath);
        String line; // 用来保存每行读取的内容
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        line = reader.readLine(); // 读取第一行
        while (line != null) { // 如果 line 为空说明读完了
            buffer.append(line); // 将读到的内容添加到 buffer 中
            buffer.append("\n"); // 添加换行符
            line = reader.readLine(); // 读取下一行
        }
        reader.close();
        is.close();
    }


    public static void main(String[] args) {

        String inputpath = "testfile.txt";
        String outputpath = "output.txt";

        StringBuffer sb = new StringBuffer();
        try {
            Compiler.readToBuffer(sb, inputpath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String source = sb.toString();


        PrintStream out = null;
        try {
            out = new PrintStream(outputpath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.setOut(out);

        //1.词法分析程序,生成单词列表
        Sym sym=new Sym(source);
        //2.语法分析程序&错误处理程序,有错误直接结束程序
        Parsing_error parsing=new Parsing_error(sym.getWords());
        //3.语法分析程序，生成AST抽象语法树
        Parsing_mid parsingtemp=new Parsing_mid(sym.getWords());
        parsingtemp.CompUnit();
        //4.解析抽象语法树，生成中间代码
        parsingtemp.analyse();
        //5.目标代码生成程序，输入中间代码，生成目标代码
        Mips mips=new Mips(parsingtemp.getMidCodes(), parsingtemp.getStrings());

        Optimize opt = new Optimize(parsingtemp.getMidCodes());

        Mips mips = new Mips(opt.getNewmidCodes(), parsingtemp.getStrings());
//        try {
//            Scanner in = new Scanner(new FileReader(inputpath));
//            while (in.hasNextLine()) {
//                String line = in.nextLine();
//                System.out.println(line);
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

    }
}
