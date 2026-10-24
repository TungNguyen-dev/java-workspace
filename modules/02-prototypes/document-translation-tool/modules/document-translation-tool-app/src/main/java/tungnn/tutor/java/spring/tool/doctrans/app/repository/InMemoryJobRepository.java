package tungnn.tutor.java.spring.tool.doctrans.app.repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.UnaryOperator;
import org.springframework.stereotype.Component;
import tungnn.tutor.java.spring.tool.doctrans.app.model.TranslationJob;

@Component
public class InMemoryJobRepository {

  private final Map<String, TranslationJob> jobs = new ConcurrentHashMap<>();

  public void save(TranslationJob job) {
    jobs.put(job.id(), job);
  }

  public Optional<TranslationJob> find(String id) {
    return Optional.ofNullable(jobs.get(id));
  }

  public void update(String id, UnaryOperator<TranslationJob> fn) {
    jobs.computeIfPresent(id, (k, v) -> fn.apply(v));
  }
}
