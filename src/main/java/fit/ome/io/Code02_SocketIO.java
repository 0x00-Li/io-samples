package fit.ome.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 网络IO
 *
 * @version 0.0.1-SNAPSHOT
 * @auther Zero
 * @date 2021/4/26
 **/
public class Code02_SocketIO {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9090, 20);
        System.out.println("step1:new ServerSocket(9090)");
        while (true) {
            Socket client = serverSocket.accept();// 等待获取连接的时候，会阻塞
            System.out.println("step2:client \t" + client.getPort());
            new Thread(() -> {
                InputStream in = null;
                try {
                    in = client.getInputStream();

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                    while (true) {
                        String line = bufferedReader.readLine();
                        if (null != line) {
                            System.out.println(line);
                        } else {
                            client.close();
                            break;
                        }
                    }
                    System.out.println("客户端断开");
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }).start();
        }
    }
}
