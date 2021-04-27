package fit.ome.io;

import java.io.*;
import java.net.Socket;

/**
 * @version 0.0.1-SNAPSHOT
 * @auther Zero
 * @date 2021/4/27
 **/
public class Code02_SocketClient {
    public static void main(String[] args) {
        try {
            Socket client = new Socket("192.168.1.7", 9090);
            client.setSendBufferSize(20);
            client.setTcpNoDelay(true);// 不延迟直接发送，不关注数据包大小
            OutputStream os = client.getOutputStream();
            InputStream in = System.in;
            BufferedReader buffer = new BufferedReader(new InputStreamReader(in));
            while (true) {
                String line = buffer.readLine();
                if (line != null) {
                    byte[] bytes = line.getBytes();
                    for (byte b : bytes) {
                        os.write(b);
                    }
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
