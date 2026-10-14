package tungnn.tutor.java.core.lib.net.socket.sample;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import tungnn.tutor.java.core.lib.net.socket.utils.SocketUtils;

public class SimpleUdpClient {

  static void main() {
    String serverHost = "localhost";
    int serverPort = 9876;
    int bufferSize = 1024;

    System.out.println("Starting UDP Client targeting " + serverHost + ":" + serverPort);

    // Tạo UDP socket với local ephemeral port do OS tự động cấp
    try (DatagramSocket clientSocket = SocketUtils.createUdpSocketOnEphemeralPort();
        BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in))) {

      System.out.println("Ready! Type messages to send. Type 'exit' to quit.");

      while (true) {
        System.out.print("> ");
        String userInput = consoleInput.readLine();

        if (userInput == null) {
          break;
        }

        byte[] sendData = userInput.getBytes(StandardCharsets.UTF_8);

        // 1. Gửi gói tin UDP tới Server
        DatagramPacket sendPacket =
            SocketUtils.createUdpSendPacket(sendData, serverHost, serverPort);
        clientSocket.send(sendPacket);

        // 2. Nếu người dùng nhập 'exit', ngắt client mà không chờ phản hồi thêm
        if ("exit".equalsIgnoreCase(userInput.trim())) {
          System.out.println("Closing client connection...");
          break;
        }

        // 3. Nhận phản hồi từ Server
        DatagramPacket receivePacket = SocketUtils.createUdpReceivePacket(bufferSize);
        SocketUtils.receiveUdpBytes(clientSocket, receivePacket);

        String response =
            new String(
                receivePacket.getData(), 0, receivePacket.getLength(), StandardCharsets.UTF_8);

        System.out.println("Server response: " + response);
      }
    } catch (Exception e) {
      System.err.println("UDP Client error: " + e.getMessage());
    }
  }
}
