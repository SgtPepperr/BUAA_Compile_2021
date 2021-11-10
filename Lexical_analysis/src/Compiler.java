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
//        String inputpath = "D:\\compile\\Lexical_analysis\\src\\testfile.txt";
//        String outputpath = "D:\\compile\\Lexical_analysis\\src\\output.txt";
        String inputpath = "testfile.txt";
        String outputpath = "output.txt";

        StringBuffer sb=new StringBuffer();
        try {
            Compiler.readToBuffer(sb,inputpath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String source=sb.toString();

//                String source=null;
//        try {
//            source = Files.readString(Paths.get(inputpath));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        PrintStream out = null;
        try {
            out = new PrintStream(outputpath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.setOut(out);

        Sym sym=new Sym(source);
        Parsing parsing=new Parsing(sym.getWords());
        Parsingtemp parsingtemp=new Parsingtemp(sym.getWords());
        parsingtemp.analyse();
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
