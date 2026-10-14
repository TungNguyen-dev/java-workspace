package tungnn.tutor.java.sample.s1_sharedmemory;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.charset.StandardCharsets;

public class SharedMemorySample {

  private static final String SHM_NAME = "/java-shared-memory-demo";
  private static final long SHM_SIZE = 4096;

  private static final long LENGTH_OFFSET = 0;
  private static final long DATA_OFFSET = Integer.BYTES;

  public static void main(String[] args) throws Throwable {
    if (args.length != 1) {
      System.out.println("Usage: java SharedMemorySample <writer|reader>");
      return;
    }

    switch (args[0]) {
      case "writer" -> write();
      case "reader" -> read();
      default -> System.out.println("Unknown mode: " + args[0]);
    }
  }

  private static void write() throws Throwable {
    try (PosixSharedMemory sharedMemory = PosixSharedMemory.create(SHM_NAME, SHM_SIZE)) {

      MemorySegment memory = sharedMemory.memory();

      byte[] data = "Hello from Process A".getBytes(StandardCharsets.UTF_8);

      memory.set(ValueLayout.JAVA_INT, LENGTH_OFFSET, data.length);

      MemorySegment.copy(data, 0, memory, ValueLayout.JAVA_BYTE, DATA_OFFSET, data.length);

      System.out.println("Written: " + new String(data, StandardCharsets.UTF_8));

      System.out.println("Press ENTER to exit...");
      System.in.read();

      sharedMemory.unlink();
    }
  }

  private static void read() throws Throwable {
    try (PosixSharedMemory sharedMemory = PosixSharedMemory.open(SHM_NAME, SHM_SIZE)) {

      MemorySegment memory = sharedMemory.memory();

      int length = memory.get(ValueLayout.JAVA_INT, LENGTH_OFFSET);

      byte[] data = new byte[length];

      MemorySegment.copy(memory, ValueLayout.JAVA_BYTE, DATA_OFFSET, data, 0, length);

      System.out.println("Read: " + new String(data, StandardCharsets.UTF_8));
    }
  }
}
