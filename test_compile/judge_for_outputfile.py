import os
import re
import difflib
import filecmp

test_files_root = "./testfiles/"
output_files_root = "./output/"
ans_files_root = "./grammar/"
opt = "C/"

test_files_folder = os.path.join(test_files_root, opt)
output_files_folder = os.path.join(output_files_root, opt)
ans_files_folder = os.path.join(ans_files_root, opt)
#字符串匹配去除
reg = re.compile(r"[a-z.]")

flag = True

# 文件复制
def copy_function(src_path, target_path):
    with open(src_path, 'rb') as rstream:
        container = rstream.read()
        with open(target_path, 'wb') as wstream:
            wstream.write(container)


for file in os.listdir(test_files_folder):
    if "testfile" in file:
        print("judging %s%s" % (opt, file))
        output_file = "output" + reg.sub("", file) + ".txt"
        print(output_file)
        input_file = "input" + reg.sub("", file) + ".txt"
        print(input_file)

        file_path = os.path.join(test_files_folder, file)
        input_path = os.path.join(test_files_folder, input_file)
        answer_path = os.path.join(test_files_folder, output_file)
        output_path = os.path.join(output_files_root, output_file)

        copy_function(file_path, "./testfile.txt")
        copy_function(input_path, "./input.txt")
        # 执行编译器，编译汇编码输出到mips.txt文件中
        os.system("java -jar compile.jar")
        # 执行mars文件，文件输入重定向为input_path,用<,文件输出重定向为output.txt,用>
        os.system("java -jar mars.jar mips.txt >output.txt <%s" % input_path)
        copy_function("./output.txt", output_path)
        # 头文件内容复制到answer文件中
        copy_function("./header.txt", "./answer.txt")
        # 正确结果临时文件复制
        copy_function(answer_path, "./answer_temp.txt")
        # 正确结果附加重定向输出到answer文件中
        os.system("type answer_temp.txt >>  answer.txt")
        # 文件比较
        os.system("fc output.txt answer.txt > res.txt")
        flag = 0

        with open("res.txt", "r") as f:
            res = f.read()
            if res.find("找不到差异") != -1:
                flag = 1

        if flag == 0:
            print("fail!")
            print(res)
            # exit(0)
        else:
            print("success!")
