package tungnn.tutor.java.aws.service.ssm;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.*;

public final class InvokeCommandUtil {

  private static final String DOCUMENT_SHELL = "AWS-RunShellScript";

  // Backoff config
  private static final long INITIAL_WAIT_MS = 250;
  private static final long MAX_WAIT_MS = 5000;
  private static final double BACKOFF_MULTIPLIER = 1.5;

  private InvokeCommandUtil() {}

  // =========================
  // Public API
  // =========================

  public static Map<String, SsmCommandResult> invokeCommand(
      SsmClient client, List<String> instanceIds, String command, Duration timeout) {

    validate(instanceIds, command, timeout);

    var commandId = sendCommand(client, instanceIds, command, timeout);

    return pollCommandMultiThread(client, instanceIds, commandId, timeout);
  }

  // =========================
  // Core logic
  // =========================

  private static String sendCommand(
      SsmClient client, List<String> instanceIds, String command, Duration timeout) {

    var request =
        SendCommandRequest.builder()
            .instanceIds(instanceIds)
            .documentName(DOCUMENT_SHELL)
            .parameters(Map.of("commands", List.of(command)))
            .timeoutSeconds(Math.max(1, Long.valueOf(timeout.toSeconds()).intValue()))
            .build();

    var response = client.sendCommand(request);

    return response.command().commandId();
  }

  private static Map<String, SsmCommandResult> pollCommandMultiThread(
      SsmClient client, List<String> instanceIds, String commandId, Duration timeout) {

    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
      var futures =
          instanceIds.stream()
              .map(
                  instanceId ->
                      executor.submit(() -> pollCommand(client, instanceId, commandId, timeout)))
              .toList();

      var result = new HashMap<String, SsmCommandResult>();

      for (int i = 0; i < instanceIds.size(); i++) {
        var instanceId = instanceIds.get(i);
        try {
          var res = futures.get(i).get();
          result.put(instanceId, res);
        } catch (Exception e) {
          throw new RuntimeException("Failed to get result for instance: " + instanceId, e);
        }
      }

      return result;
    }
  }

  private static SsmCommandResult pollCommand(
      SsmClient client, String instanceId, String commandId, Duration timeout) {

    var deadline = System.currentTimeMillis() + timeout.toMillis();
    var waitTime = INITIAL_WAIT_MS;

    var request =
        GetCommandInvocationRequest.builder().commandId(commandId).instanceId(instanceId).build();

    SsmCommandResult lastResult = null;

    while (System.currentTimeMillis() < deadline) {
      try {
        var res = client.getCommandInvocation(request);

        lastResult = toResult(res);
        var status = res.status();

        switch (status) {
          case SUCCESS:
            return lastResult;
          case FAILED:
          case CANCELLED:
          case TIMED_OUT:
            throw new SsmCommandException("SSM command failed [" + status + "]", lastResult);
          default:
            // PENDING, IN_PROGRESS, DELAYED, future states
            break;
        }

      } catch (InvocationDoesNotExistException e) {
        // eventual consistency → retry
      } catch (SsmException e) {
        if (!isRetryable(e)) {
          throw e;
        }
      }

      sleep(waitTime);

      waitTime = Math.min((long) (waitTime * BACKOFF_MULTIPLIER), MAX_WAIT_MS);
    }

    throw new SsmCommandTimeoutException(
        "SSM command timeout after " + timeout.toMillis() + " ms", lastResult);
  }

  // =========================
  // Helpers
  // =========================

  private static boolean isRetryable(SsmException e) {
    return e.isThrottlingException() || e.statusCode() >= 500;
  }

  private static void sleep(long ms) {
    try {
      Thread.sleep(ms);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Interrupted while waiting", e);
    }
  }

  private static SsmCommandResult toResult(GetCommandInvocationResponse res) {
    return new SsmCommandResult(
        res.standardOutputContent(), res.standardErrorContent(), res.responseCode(), res.status());
  }

  private static void validate(List<String> instanceIds, String command, Duration timeout) {

    if (instanceIds == null || instanceIds.isEmpty()) {
      throw new IllegalArgumentException("instanceIds must not be null/empty");
    }

    for (var id : instanceIds) {
      if (id == null || id.isBlank()) {
        throw new IllegalArgumentException("instanceId must not be null/blank");
      }
    }

    if (command == null || command.isBlank()) {
      throw new IllegalArgumentException("command must not be null/blank");
    }

    if (timeout == null || timeout.isZero() || timeout.isNegative()) {
      throw new IllegalArgumentException("timeout must be positive");
    }
  }

  // =========================
  // Result + Exceptions
  // =========================

  public record SsmCommandResult(
      String stdout, String stderr, Integer responseCode, CommandInvocationStatus status) {}

  public static class SsmCommandException extends RuntimeException {
    private final SsmCommandResult result;

    public SsmCommandException(String message, SsmCommandResult result) {
      super(message);
      this.result = result;
    }

    public SsmCommandResult getResult() {
      return result;
    }
  }

  public static class SsmCommandTimeoutException extends RuntimeException {
    private final SsmCommandResult lastResult;

    public SsmCommandTimeoutException(String message, SsmCommandResult lastResult) {
      super(message);
      this.lastResult = lastResult;
    }

    public SsmCommandResult getLastResult() {
      return lastResult;
    }
  }
}
