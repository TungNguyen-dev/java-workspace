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

/**
 * Utility class providing factory methods and helper operations for managing and interacting with
 * TCP and UDP sockets.
 *
 * <p>Provides simplified wrappers around common socket operations such as factory creation, byte
 * transmission, connection testing, free port discovery, and silent resource cleanup.
 *
 * <p>This class cannot be instantiated.
 */
public final class SocketUtils {

  private SocketUtils() {
    // Prevent instantiation
  }

  // ==========================================
  // FACTORY UTILS (TCP & UDP)
  // ==========================================

  /**
   * Creates and connects a new TCP {@link Socket} to the specified host and port with a timeout.
   *
   * @param host the target host name or IP address
   * @param port the target port number
   * @param connectTimeoutMs connection timeout in milliseconds
   * @return a connected {@link Socket}
   * @throws UncheckedIOException if an I/O error occurs or connection times out
   */
  public static Socket createConnectedTcpSocket(String host, int port, int connectTimeoutMs) {
    try {
      Socket socket = new Socket();
      socket.connect(new InetSocketAddress(host, port), connectTimeoutMs);
      return socket;
    } catch (IOException e) {
      throw new UncheckedIOException(
          String.format("Failed to connect TCP socket to %s:%d", host, port), e);
    }
  }

  /**
   * Creates a bound TCP {@link ServerSocket} on the specified port.
   *
   * @param port the port to bind to (use {@code 0} to bind to an automatically allocated free port)
   * @param backlog the maximum queue length for incoming connection requests
   * @return a bound {@link ServerSocket}
   * @throws UncheckedIOException if an I/O error occurs while binding
   */
  public static ServerSocket createTcpServer(int port, int backlog) {
    try {
      ServerSocket serverSocket = new ServerSocket();
      serverSocket.setReuseAddress(true);
      serverSocket.bind(new InetSocketAddress(port), backlog);
      return serverSocket;
    } catch (IOException e) {
      throw new UncheckedIOException(
          String.format("Failed to bind TCP ServerSocket on port %d", port), e);
    }
  }

  /**
   * Creates a bound TCP {@link ServerSocket} on an automatically allocated free port.
   *
   * @return a bound {@link ServerSocket} listening on an available local port
   * @throws UncheckedIOException if an I/O error occurs while binding
   */
  public static ServerSocket createTcpServerOnFreePort() {
    return createTcpServer(0, 50);
  }

  /**
   * Creates a bound {@link DatagramSocket} for UDP communications.
   *
   * @param port the local port to bind to (use {@code 0} for an automatically allocated free port)
   * @return a bound {@link DatagramSocket}
   * @throws UncheckedIOException if an I/O error occurs while creating or binding the socket
   */
  public static DatagramSocket createUdpSocket(int port) {
    try {
      DatagramSocket socket = new DatagramSocket(null);
      socket.setReuseAddress(true);
      socket.bind(new InetSocketAddress(port));
      return socket;
    } catch (SocketException e) {
      throw new UncheckedIOException(
          String.format("Failed to create UDP socket on port %d", port), e);
    }
  }

  /**
   * Creates an unbound {@link DatagramSocket} assigned to an available local port.
   *
   * @return a bound {@link DatagramSocket}
   * @throws UncheckedIOException if an I/O error occurs
   */
  public static DatagramSocket createUdpSocketOnEphemeralPort() {
    // Port 0 tells the OS to automatically assign an available ephemeral port.
    return createUdpSocket(0);
  }

  /**
   * Creates a pre-allocated {@link DatagramPacket} buffer suitable for receiving incoming UDP
   * packets.
   *
   * @param bufferSize the size of the internal byte array buffer
   * @return a configured {@link DatagramPacket} for receiving data
   */
  public static DatagramPacket createUdpReceivePacket(int bufferSize) {
    byte[] buffer = new byte[bufferSize];
    return new DatagramPacket(buffer, buffer.length);
  }

  /**
   * Creates a {@link DatagramPacket} configured for sending raw data to a remote address.
   *
   * @param data the byte array payload
   * @param host the remote target host name or IP address
   * @param port the remote target port number
   * @return a configured {@link DatagramPacket} ready for transmission
   * @throws UncheckedIOException if the host address resolution fails
   */
  public static DatagramPacket createUdpSendPacket(byte[] data, String host, int port) {
    try {
      InetAddress address = InetAddress.getByName(host);
      return new DatagramPacket(data, data.length, address, port);
    } catch (UnknownHostException e) {
      throw new UncheckedIOException(
          String.format("Failed to resolve host address for %s", host), e);
    }
  }

  // ==========================================
  // TCP UTILS
  // ==========================================

  /**
   * Writes a byte array payload to a TCP socket's output stream and flushes the stream.
   *
   * @param socket the target {@link Socket} to send data through; must be connected
   * @param data the byte array containing the payload to write
   * @throws NullPointerException if {@code socket} or {@code data} is {@code null}
   * @throws UncheckedIOException if an I/O error occurs while writing to the stream
   */
  public static void sendTcpBytes(Socket socket, byte[] data) {
    try {
      OutputStream out = socket.getOutputStream();
      out.write(data);
      out.flush();
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to write data to TCP socket", e);
    }
  }

  /**
   * Reads up to {@code buffer.length} bytes from a TCP socket's input stream into the provided
   * buffer.
   *
   * <p><b>Note:</b> TCP is a stream-oriented protocol. This method performs a single read operation
   * and is not guaranteed to fill the buffer completely, even if more data is on the wire.
   *
   * @param socket the source {@link Socket} to read from; must be connected
   * @param buffer the byte array into which the received data is stored
   * @return the total number of bytes read into the buffer, or {@code -1} if the end of the stream
   *     has been reached
   * @throws NullPointerException if {@code socket} or {@code buffer} is {@code null}
   * @throws UncheckedIOException if an I/O error occurs while reading from the stream
   * @see #receiveTcpBytesFully(Socket, byte[])
   */
  public static int receiveTcpBytes(Socket socket, byte[] buffer) {
    try {
      InputStream in = socket.getInputStream();
      return in.read(buffer);
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to read data from TCP socket", e);
    }
  }

  /**
   * Reads exactly {@code buffer.length} bytes from a TCP socket's input stream, blocking until the
   * requested number of bytes are read or end-of-stream is reached.
   *
   * @param socket the source {@link Socket} to read from; must be connected
   * @param buffer the byte array into which the received data is stored
   * @return the total number of bytes read into the buffer, which may be less than {@code
   *     buffer.length} only if the end of the stream is reached before reading the full count
   * @throws NullPointerException if {@code socket} or {@code buffer} is {@code null}
   * @throws UncheckedIOException if an I/O error occurs while reading from the stream
   */
  public static int receiveTcpBytesFully(Socket socket, byte[] buffer) {
    try {
      InputStream in = socket.getInputStream();
      return in.readNBytes(buffer, 0, buffer.length);
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to read complete data frame from TCP socket", e);
    }
  }

  /**
   * Checks whether a specific TCP port is open and listening on the designated host.
   *
   * @param host the hostname or IP address of the target server
   * @param port the target TCP port number (1-65535)
   * @param timeoutMs the maximum connection timeout in milliseconds
   * @return {@code true} if a TCP connection was successfully established within the timeout;
   *     {@code false} otherwise
   */
  public static boolean isTcpPortOpen(String host, int port, int timeoutMs) {
    try (Socket socket = new Socket()) {
      socket.connect(new InetSocketAddress(host, port), timeoutMs);
      return true;
    } catch (IOException e) {
      return false;
    }
  }

  /**
   * Finds an ephemeral TCP port currently available on the local system by temporarily binding a
   * {@link ServerSocket} to port 0.
   *
   * <p><b>Warning:</b> This method closes the socket before returning. A race condition (TOCTOU)
   * exists where another process or thread could claim the port before the caller binds to it.
   *
   * @return an available TCP port number
   * @throws UncheckedIOException if an error occurs while opening or closing the ephemeral socket
   */
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

  /**
   * Sends raw byte data over a UDP socket to the specified target address and port.
   *
   * @param socket the bound {@link DatagramSocket} used to transmit the datagram
   * @param data the byte array payload to transmit
   * @param address the target destination's resolved {@link InetAddress}
   * @param port the target destination port number (1-65535)
   * @throws NullPointerException if {@code socket}, {@code data}, or {@code address} is {@code
   *     null}
   * @throws UncheckedIOException if an I/O error occurs while sending the packet
   */
  public static void sendUdpBytes(
      DatagramSocket socket, byte[] data, InetAddress address, int port) {
    try {
      DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
      socket.send(packet);
    } catch (IOException e) {
      throw new UncheckedIOException(
          String.format("Failed to send UDP packet to %s:%d", address.getHostAddress(), port), e);
    }
  }

  /**
   * Receives a UDP datagram packet into a pre-allocated {@link DatagramPacket} buffer.
   *
   * @param socket the bound {@link DatagramSocket} listening for incoming data
   * @param packet the target {@link DatagramPacket} buffer to populate with received data
   * @return the actual length in bytes of the payload received
   * @throws NullPointerException if {@code socket} or {@code packet} is {@code null}
   * @throws UncheckedIOException if an I/O error occurs while receiving the packet
   */
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

  /**
   * Quietly closes any {@link AutoCloseable} resource (such as a {@link Socket}, {@link
   * ServerSocket}, or {@link DatagramSocket}) without throwing exceptions.
   *
   * <p>If the passed resource is {@code null}, no action is taken. Any checked or unchecked
   * exceptions raised during closing are caught and ignored.
   *
   * @param closeable the target resource to close; may be {@code null}
   */
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
