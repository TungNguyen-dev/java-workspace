package tungnn.tutor.java.core.lib.net.socket.sample;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import tungnn.tutor.java.core.lib.net.socket.utils.SocketUtils;

public class SimpleUdpServer {

  static void main() {
    int port = 9876;
    int bufferSize = 1024;

    System.out.println("UDP Server starting on port " + port + "...");

    // Tạo UDP Socket sử dụng SocketUtils
    try (DatagramSocket serverSocket = SocketUtils.createUdpSocket(port)) {
      System.out.println("Server is running. Waiting for incoming packets...");

      while (!serverSocket.isClosed()) {
        // Prepare receive packet using SocketUtils helper
        DatagramPacket receivePacket = SocketUtils.createUdpReceivePacket(bufferSize);

        // Blocking call to receive packet
        SocketUtils.receiveUdpBytes(serverSocket, receivePacket);

        // Process received payload
        String message =
            new String(
                receivePacket.getData(), 0, receivePacket.getLength(), StandardCharsets.UTF_8);

        System.out.println(
            "["
                + receivePacket.getAddress().getHostAddress()
                + ":"
                + receivePacket.getPort()
                + "]: "
                + message);

        // Stop condition if client sends 'exit'
        if ("exit".equalsIgnoreCase(message.trim())) {
          System.out.println("Received exit signal from client. Shutting down server...");
          break;
        }

        // Prepare response payload (Echo response)
        String responseMessage = "Echo from UDP server: " + message;
        byte[] sendData = responseMessage.getBytes(StandardCharsets.UTF_8);

        // Send payload back to sender address & port
        SocketUtils.sendUdpBytes(
            serverSocket, sendData, receivePacket.getAddress(), receivePacket.getPort());
      }
    } catch (Exception e) {
      System.err.println("UDP Server error: " + e.getMessage());
    } finally {
      System.out.println("UDP Server stopped.");
    }
  }
}
