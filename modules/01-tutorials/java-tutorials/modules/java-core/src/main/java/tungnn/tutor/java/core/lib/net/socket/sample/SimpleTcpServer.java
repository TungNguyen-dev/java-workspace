package tungnn.tutor.java.core.lib.net.socket.sample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import tungnn.tutor.java.core.lib.net.socket.utils.SocketUtils;

public class SimpleTcpServer {

  static void main() {
    int port = 8080;
    int backlog = 50;
    ExecutorService threadPool = Executors.newCachedThreadPool();

    System.out.println("Server starting on port " + port + "...");

    // 1. Tạo ServerSocket sử dụng SocketUtils
    ServerSocket serverSocket = null;
    try {
      serverSocket = SocketUtils.createTcpServer(port, backlog);

      // Thread lắng nghe lệnh 'exit' từ console để đóng server
      ServerSocket finalServerSocket = serverSocket;
      Thread consoleListener =
          new Thread(
              () -> {
                try (BufferedReader consoleReader =
                    new BufferedReader(new InputStreamReader(System.in))) {
                  System.out.println("Type 'exit' in server console to stop the server.");
                  while (true) {
                    String input = consoleReader.readLine();
                    if ("exit".equalsIgnoreCase(input)) {
                      System.out.println("Stopping server...");
                      // Đóng ServerSocket an toàn để ngắt vòng lặp accept()
                      SocketUtils.closeQuietly(finalServerSocket);
                      break;
                    }
                  }
                } catch (IOException e) {
                  System.err.println("Console reader error: " + e.getMessage());
                }
              });
      consoleListener.setDaemon(true);
      consoleListener.start();

      // Vòng lặp lắng nghe kết nối từ client
      while (!serverSocket.isClosed()) {
        try {
          Socket clientSocket = serverSocket.accept();
          System.out.println("Client connected: " + clientSocket.getRemoteSocketAddress());

          // Xử lý mỗi Client kết nối trên một Thread độc lập trong ThreadPool
          threadPool.execute(() -> handleClient(clientSocket));
        } catch (SocketException e) {
          if (serverSocket.isClosed()) {
            System.out.println("Server socket closed.");
            break;
          }
          System.err.println("Socket error: " + e.getMessage());
        }
      }
    } catch (Exception e) {
      System.err.println("Server error: " + e.getMessage());
    } finally {
      SocketUtils.closeQuietly(serverSocket);
      threadPool.shutdown();
      System.out.println("Server shut down cleanly.");
    }
  }

  private static void handleClient(Socket clientSocket) {
    try {
      byte[] buffer = new byte[1024];
      int bytesRead;

      // Đọc byte liên tục từ client bằng SocketUtils.receiveTcpBytes
      while ((bytesRead = SocketUtils.receiveTcpBytes(clientSocket, buffer)) != -1) {
        String clientMessage = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8).trim();
        System.out.println("[" + clientSocket.getRemoteSocketAddress() + "]: " + clientMessage);

        if ("exit".equalsIgnoreCase(clientMessage)) {
          byte[] goodbyeMsg = "Goodbye!\n".getBytes(StandardCharsets.UTF_8);
          SocketUtils.sendTcpBytes(clientSocket, goodbyeMsg);
          break;
        }

        // Phản hồi lại client bằng SocketUtils.sendTcpBytes
        String response = "Echo from server: " + clientMessage + "\n";
        SocketUtils.sendTcpBytes(clientSocket, response.getBytes(StandardCharsets.UTF_8));
      }
    } catch (Exception e) {
      System.err.println("Client handler error: " + e.getMessage());
    } finally {
      System.out.println("Client disconnected: " + clientSocket.getRemoteSocketAddress());
      // Đóng client socket an toàn
      SocketUtils.closeQuietly(clientSocket);
    }
  }
}
