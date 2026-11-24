package tungnn.tutor.java.core.lib.net.socket.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public final class SocketUtils {

    private SocketUtils() {
        // Prevent instantiation
    }

    // ==========================================
    // FACTORY UTILS (TCP & UDP)
    // ==========================================

    public static Socket createConnectedTcpSocket(String host, int port, int connectTimeoutMs) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), connectTimeoutMs);
            return socket;
        } catch (IOException e) {
            throw new UncheckedIOException(String.format("Failed to connect TCP socket to %s:%d", host, port), e);
        }
    }

    public static ServerSocket createTcpServer(int port, int backlog) {
        try {
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(port), backlog);
            return serverSocket;
        } catch (IOException e) {
            throw new UncheckedIOException(String.format("Failed to bind TCP ServerSocket on port %d", port), e);
        }
    }

    public static ServerSocket createTcpServerOnFreePort() {
        return createTcpServer(0, 50);
    }

    public static DatagramSocket createUdpSocket(int port) {
        try {
            DatagramSocket socket = new DatagramSocket(null);
            socket.setReuseAddress(true);
            socket.bind(new InetSocketAddress(port));
            return socket;
        } catch (SocketException e) {
            throw new UncheckedIOException(String.format("Failed to create UDP socket on port %d", port), e);
        }
    }

    public static DatagramSocket createUdpSocketOnEphemeralPort() {
        // Port 0 tells the OS to automatically assign an available ephemeral port.
        return createUdpSocket(0);
    }

    public static DatagramPacket createUdpReceivePacket(int bufferSize) {
        byte[] buffer = new byte[bufferSize];
        return new DatagramPacket(buffer, buffer.length);
    }

    public static DatagramPacket createUdpSendPacket(byte[] data, String host, int port) {
        try {
            InetAddress address = InetAddress.getByName(host);
            return new DatagramPacket(data, data.length, address, port);
        } catch (UnknownHostException e) {
            throw new UncheckedIOException(String.format("Failed to resolve host address for %s", host), e);
        }
    }

    // ==========================================
    // TCP UTILS
    // ==========================================

    public static void sendTcpBytes(Socket socket, byte[] data) {
        try {
            OutputStream out = socket.getOutputStream();
            out.write(data);
            out.flush();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to write data to TCP socket", e);
        }
    }

    public static int receiveTcpBytes(Socket socket, byte[] buffer) {
        try {
            InputStream in = socket.getInputStream();
            return in.read(buffer);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read data from TCP socket", e);
        }
    }

    public static int receiveTcpBytesFully(Socket socket, byte[] buffer) {
        try {
            InputStream in = socket.getInputStream();
            return in.readNBytes(buffer, 0, buffer.length);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read complete data frame from TCP socket", e);
        }
    }

    public static boolean isTcpPortOpen(String host, int port, int timeoutMs) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeoutMs);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static int findFreeTcpPort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to find an available TCP port", e);
        }
    }

    // ==========================================
    // UDP UTILS
    // ==========================================

    public static void sendUdpBytes(DatagramSocket socket, byte[] data, InetAddress address, int port) {
        try {
            DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
            socket.send(packet);
        } catch (IOException e) {
            throw new UncheckedIOException(
                    String.format("Failed to send UDP packet to %s:%d", address.getHostAddress(), port), e);
        }
    }

    public static int receiveUdpBytes(DatagramSocket socket, DatagramPacket packet) {
        try {
            socket.receive(packet);
            return packet.getLength();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to receive UDP packet", e);
        }
    }

    // ==========================================
    // SOCKET MANAGEMENT UTILS
    // ==========================================

    public static void closeQuietly(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ignored) {
                // Ignore exception during cleanup
            }
        }
    }
}
