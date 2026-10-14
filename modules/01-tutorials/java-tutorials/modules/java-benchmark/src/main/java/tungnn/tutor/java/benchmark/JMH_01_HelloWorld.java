package tungnn.tutor.java.benchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class JMH_01_HelloWorld {

  static void main() throws RunnerException {
    Options opt =
        new OptionsBuilder().include(JMH_01_HelloWorld.class.getSimpleName()).forks(1).build();

    new Runner(opt).run();
  }

  @Benchmark
  public void helloWorld() {}
}
