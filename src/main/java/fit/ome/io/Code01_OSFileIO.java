package fit.ome.io;

import java.io.*;

/**
 * @version 0.0.1-SNAPSHOT
 * @auther Zero
 * @date 2021/4/22
 **/
public class Code01_OSFileIO {
    static byte[] data = "1234567890\n".getBytes();
    static String path = "/tmp/testfileio/out.txt";

    public static void main(String[] args) {

    }

    // ==
//    测试直接写入速度
    // 会每次调用系统io进行文件刷盘
    public static void testBasicFileIO() throws IOException, InterruptedException {
        File f = new File(path);
        FileOutputStream os = new FileOutputStream(f);
        while (true) {
            Thread.sleep(1000);
            os.write(data);
        }

    }

    // 测试buffer的文件IO
    // jvm 8kB syscall write(8kBbyte[])
    // 会根据buffer中的大小没8kB刷一次盘，减少系统调用
    public static void testBufferFileIO() throws IOException, InterruptedException {
        File f = new File(path);
        BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(f));
        while (true) {
            Thread.sleep(1000);
            os.write(data);
        }
    }

    // 测试文件的NIO

}
