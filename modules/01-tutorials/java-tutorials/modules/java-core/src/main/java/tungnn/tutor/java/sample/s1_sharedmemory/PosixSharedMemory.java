package tungnn.tutor.java.sample.s1_sharedmemory;

import static java.lang.foreign.ValueLayout.*;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public final class PosixSharedMemory implements AutoCloseable {

  private static final Linker LINKER = Linker.nativeLinker();
  private static final SymbolLookup LIBC = LINKER.defaultLookup();

  // Linux constants
  private static final int O_CREAT = 0x40;
  private static final int O_RDWR = 0x02;

  private static final int PROT_READ = 0x01;
  private static final int PROT_WRITE = 0x02;

  private static final int MAP_SHARED = 0x01;

  private static final MethodHandle SHM_OPEN =
      downcall("shm_open", FunctionDescriptor.of(JAVA_INT, ADDRESS, JAVA_INT, JAVA_INT));

  private static final MethodHandle SHM_UNLINK =
      downcall("shm_unlink", FunctionDescriptor.of(JAVA_INT, ADDRESS));

  private static final MethodHandle FTRUNCATE =
      downcall("ftruncate", FunctionDescriptor.of(JAVA_INT, JAVA_INT, JAVA_LONG));

  private static final MethodHandle MMAP =
      downcall(
          "mmap",
          FunctionDescriptor.of(
              ADDRESS, ADDRESS, JAVA_LONG, JAVA_INT, JAVA_INT, JAVA_INT, JAVA_LONG));

  private static final MethodHandle MUNMAP =
      downcall("munmap", FunctionDescriptor.of(JAVA_INT, ADDRESS, JAVA_LONG));

  private static final MethodHandle CLOSE =
      downcall("close", FunctionDescriptor.of(JAVA_INT, JAVA_INT));

  private final String name;
  private final long size;
  private final int fd;
  private final MemorySegment memory;

  private PosixSharedMemory(String name, long size, int fd, MemorySegment memory) {
    this.name = name;
    this.size = size;
    this.fd = fd;
    this.memory = memory;
  }

  public static PosixSharedMemory create(String name, long size) throws Throwable {

    try (Arena arena = Arena.ofConfined()) {
      MemorySegment nativeName = arena.allocateFrom(name);

      int fd = (int) SHM_OPEN.invokeExact(nativeName, O_CREAT | O_RDWR, 0600);

      if (fd == -1) {
        throw new IllegalStateException("shm_open failed");
      }

      int result = (int) FTRUNCATE.invokeExact(fd, size);

      if (result == -1) {
        CLOSE.invokeExact(fd);
        throw new IllegalStateException("ftruncate failed");
      }

      MemorySegment address =
          (MemorySegment)
              MMAP.invokeExact(
                  MemorySegment.NULL, size, PROT_READ | PROT_WRITE, MAP_SHARED, fd, 0L);

      // mmap returns an unbounded address segment.
      MemorySegment memory = address.reinterpret(size);

      return new PosixSharedMemory(name, size, fd, memory);
    }
  }

  public static PosixSharedMemory open(String name, long size) throws Throwable {

    try (Arena arena = Arena.ofConfined()) {
      MemorySegment nativeName = arena.allocateFrom(name);

      int fd = (int) SHM_OPEN.invokeExact(nativeName, O_RDWR, 0);

      if (fd == -1) {
        throw new IllegalStateException("shm_open failed");
      }

      MemorySegment address =
          (MemorySegment)
              MMAP.invokeExact(
                  MemorySegment.NULL, size, PROT_READ | PROT_WRITE, MAP_SHARED, fd, 0L);

      MemorySegment memory = address.reinterpret(size);

      return new PosixSharedMemory(name, size, fd, memory);
    }
  }

  private static MethodHandle downcall(String name, FunctionDescriptor descriptor) {
    MemorySegment symbol =
        LIBC.find(name)
            .orElseThrow(() -> new IllegalStateException("Native function not found: " + name));

    return LINKER.downcallHandle(symbol, descriptor);
  }

  public MemorySegment memory() {
    return memory;
  }

  public void unlink() throws Throwable {
    try (Arena arena = Arena.ofConfined()) {
      MemorySegment nativeName = arena.allocateFrom(name);

      int result = (int) SHM_UNLINK.invokeExact(nativeName);

      if (result == -1) {
        throw new IllegalStateException("shm_unlink failed");
      }
    }
  }

  @Override
  public void close() throws Exception {
    try {
      MUNMAP.invokeExact(memory, size);
      CLOSE.invokeExact(fd);
    } catch (Throwable e) {
      throw new Exception(e);
    }
  }
}
