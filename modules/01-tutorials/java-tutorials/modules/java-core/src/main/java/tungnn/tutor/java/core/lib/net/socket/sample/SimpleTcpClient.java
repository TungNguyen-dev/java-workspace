package tungnn.tutor.java.core.lib.net.socket.sample;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import tungnn.tutor.java.core.lib.net.socket.utils.SocketUtils;

public class SimpleTcpClient {

  static void main() {
    String host = "localhost";
    int port = 8080;
    int timeoutMs = 5000;

    // 1. Kiểm tra Port trước khi kết nối bằng SocketUtils.isTcpPortOpen
    if (!SocketUtils.isTcpPortOpen(host, port, 2000)) {
      System.err.println("Server is not reachable on " + host + ":" + port);
      return;
    }

    System.out.println("Connecting to server " + host + ":" + port + "...");

    Socket socket = null;
    try (BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in))) {

      // 2. Tạo TCP Socket đã kết nối bằng SocketUtils
      socket = SocketUtils.createConnectedTcpSocket(host, port, timeoutMs);
      System.out.println("Connected! Type messages to send to server. Type 'exit' to quit.");

      byte[] receiveBuffer = new byte[1024];

      while (true) {
        System.out.print("> ");
        String userInput = consoleInput.readLine();

        if (userInput == null) {
          break;
        }

        // 3. Gửi chuỗi dạng Byte qua SocketUtils.sendTcpBytes
        byte[] dataToSend = (userInput + "\n").getBytes(StandardCharsets.UTF_8);
        SocketUtils.sendTcpBytes(socket, dataToSend);

        // 4. Nhận byte phản hồi qua SocketUtils.receiveTcpBytes
        int bytesRead = SocketUtils.receiveTcpBytes(socket, receiveBuffer);
        if (bytesRead != -1) {
          String response = new String(receiveBuffer, 0, bytesRead, StandardCharsets.UTF_8);
          System.out.println("Server response: " + response.trim());
        }

        // Ngắt kết nối nếu gửi tín hiệu exit
        if ("exit".equalsIgnoreCase(userInput.trim())) {
          System.out.println("Closing client connection...");
          break;
        }
      }
    } catch (Exception e) {
      System.err.println("Client error: " + e.getMessage());
    } finally {
      // 5. Đóng Socket yên lặng
      SocketUtils.closeQuietly(socket);
    }
  }
}
