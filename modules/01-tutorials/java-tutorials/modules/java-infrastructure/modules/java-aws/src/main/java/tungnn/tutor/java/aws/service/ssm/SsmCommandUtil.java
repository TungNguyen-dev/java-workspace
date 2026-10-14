package tungnn.tutor.java.aws.service.ssm;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.*;

public final class SsmCommandUtil {

  private SsmCommandUtil() {}

  public static CancelCommandResponse cancelCommand(
      SsmClient client, CancelCommandRequest request) {
    return client.cancelCommand(request);
  }

  public static GetCommandInvocationResponse getCommandInvocation(
      SsmClient client, GetCommandInvocationRequest request) {
    return client.getCommandInvocation(request);
  }

  public static List<CommandInvocation> listCommandInvocations(
      SsmClient client, ListCommandInvocationsRequest request) {
    return client.listCommandInvocationsPaginator(request).stream()
        .flatMap(r -> r.commandInvocations().stream())
        .collect(Collectors.toList());
  }

  public static List<Command> listCommands(SsmClient client, ListCommandsRequest request) {
    return client.listCommandsPaginator(request).stream()
        .flatMap(r -> r.commands().stream())
        .collect(Collectors.toList());
  }

  public static SendCommandResponse sendCommand(SsmClient client, SendCommandRequest request) {
    return client.sendCommand(request);
  }
}
