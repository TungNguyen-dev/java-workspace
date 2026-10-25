package tungnn.tutor.java.huggingface.demo;

import java.nio.file.Files;
import java.nio.file.Path;
import tungnn.tutor.java.huggingface.hub.ModelRef;
import tungnn.tutor.java.huggingface.pipeline.HfPipelines;
import tungnn.tutor.java.huggingface.pipeline.Pipeline;
import tungnn.tutor.java.huggingface.pipeline.Task;

/** Usage: EmbeddingDemo [hubId|localDir]. Example id: sentence-transformers/all-MiniLM-L6-v2. */
public class EmbeddingDemo {

  static void main(String[] args) {
    var model = args.length > 0 ? args[0] : "sentence-transformers/all-MiniLM-L6-v2";
    var ref = Files.isDirectory(Path.of(model)) ? ModelRef.local(Path.of(model)) : ModelRef.of(model);

    try (Pipeline<String, float[]> pipeline = HfPipelines.load(Task.EMBEDDING, ref)) {
      var vectors = pipeline.runBatch(java.util.List.of("I love Java", "Java is great", "The weather is cold"));
      System.out.printf("dim=%d%n", vectors.getFirst().length);
      System.out.printf("cos(0,1)=%.4f  cos(0,2)=%.4f%n",
          dot(vectors.get(0), vectors.get(1)), dot(vectors.get(0), vectors.get(2)));
    }
  }

  private static double dot(float[] a, float[] b) {
    double sum = 0;
    for (int i = 0; i < a.length; i++) {
      sum += a[i] * b[i];
    }
    return sum; // vectors are L2-normalized => dot == cosine
  }
}
