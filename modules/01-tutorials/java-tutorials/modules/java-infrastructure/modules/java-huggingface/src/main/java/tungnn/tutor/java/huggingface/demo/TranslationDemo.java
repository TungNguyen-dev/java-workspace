package tungnn.tutor.java.huggingface.demo;

import java.nio.file.Files;
import java.nio.file.Path;
import tungnn.tutor.java.huggingface.hub.ModelRef;
import tungnn.tutor.java.huggingface.pipeline.HfPipelines;
import tungnn.tutor.java.huggingface.pipeline.Pipeline;
import tungnn.tutor.java.huggingface.pipeline.Task;

/** Usage: TranslationDemo [hubId|localDir] [text]. Run with --enable-native-access=ALL-UNNAMED. */
public class TranslationDemo {

  static void main(String[] args) {
    var model = args.length > 0 ? args[0] : "Helsinki-NLP/opus-mt-zh-vi";
    var text = args.length > 1 ? args[1] : "你好，世界！";
    var ref = Files.isDirectory(Path.of(model)) ? ModelRef.local(Path.of(model)) : ModelRef.of(model);

    long t0 = System.nanoTime();
    try (Pipeline<String, String> pipeline = HfPipelines.load(Task.TRANSLATION, ref)) {
      System.out.printf("Loaded %s in %d ms%n", pipeline.modelName(), (System.nanoTime() - t0) / 1_000_000);

      long t1 = System.nanoTime();
      var result = pipeline.run(text);
      System.out.printf("Source     : %s%nTranslation: %s%nTime       : %d ms%n",
          text, result, (System.nanoTime() - t1) / 1_000_000);
    }
  }
}
