package fit.ome.io;

import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @version 0.0.1-SNAPSHOT
 * @auther Zero
 * @date 2021/4/22
 **/
public class Code01_OSFileIO {
    static byte[] data = "1234567890\n".getBytes();
    static String path = "/tmp/testfileio/out.txt";

    public static void main(String[] args) {
        try {
            testRandomFileWrite();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
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
    public static void testRandomFileWrite() throws IOException, InterruptedException {
        RandomAccessFile raf = new RandomAccessFile(path, "rw");
        raf.write("hello word!\n".getBytes());
        raf.write("hello zero!\n".getBytes());
        System.out.println("------write-------");
        System.in.read();
        raf.seek(10); // 调整文件指针位置

        raf.write("mr.\n".getBytes());// 在第10个位置插入
        System.out.println("---------seek------------");
        System.in.read();
        FileChannel rafChannel = raf.getChannel();
        // mmap 对外创建 和文件映射 byte not object
        // 是在对外创建了一个文件的映射空间
        MappedByteBuffer map = rafChannel.map(FileChannel.MapMode.READ_WRITE, 0, 4096);
        map.put("@@@".getBytes());// 不是系统调用，但是数据会到达内核的pagecache
        // 之前的用法需要out.write() ,这种操作是系统调用，才能让程序的data进入到pagecache
        // 之前的用户需要用户态和内核态进行切换
        // mmap 的内存映射，依然是内核pagecache体系锁约束的
        // 也就是说！！！！丢数据
        // github 上有c程序写的jni扩展库，使用linux内核的direct io
        // direct io(直接io) 是忽略linux的pagecache的
        // 是把 pagecache 交给了程序自己开辟一个字节数组当做pagecache，动用代码逻辑维护一致性 dirty... 等复杂问题
        System.out.println("-------------map put------------");
        System.in.read();// 至此，@@@ 字符串还未刷盘，此时宕机，会丢失数据

//        map.force();// 会强制mmap flush

        raf.seek(0);
        // 读取内容到buffer中
        ByteBuffer buffer = ByteBuffer.allocate(8192);// 其实这也会直接申请在堆外
//        ByteBuffer.allocateDirect(8192);// 申请对外内存，直接内存
        int read = rafChannel.read(buffer);
        System.out.println(buffer);// 打印
        // 会将limit 设置position
        // 失效mark
        // 然后position 设置为0
        buffer.flip();// 读写交替
        for (int i = 0; i < buffer.limit(); i++) {
            Thread.sleep(200);
            System.out.print((char) buffer.get(i));
        }

    }

    @Test
    public void whatByteBuffer() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
//        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
        System.out.println("position:"+buffer.position());
        System.out.println("limit:"+buffer.limit());
        System.out.println("mark:"+buffer);
        System.out.println("capacity:"+buffer.capacity());
        buffer.put("aaa".getBytes());
        System.out.println("-----put:123--------");
        System.out.println("mark:"+buffer);


        buffer.flip(); //读写交替
        System.out.println("---------flip------------");
        System.out.println("mark:"+buffer);

        buffer.get();
        System.out.println("----------get-------------");
        System.out.println("mark:"+buffer);

        buffer.compact();
        System.out.println("---------compact------------");
        System.out.println("mark:"+buffer);

        buffer.clear();
        System.out.println("----------clear-------------");
        System.out.println("mark:"+buffer);

    }

}
