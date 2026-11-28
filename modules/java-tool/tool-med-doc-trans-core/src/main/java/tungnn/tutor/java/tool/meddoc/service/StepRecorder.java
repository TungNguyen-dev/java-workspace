package tungnn.tutor.java.tool.meddoc.service;

import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownElement.MarkdownDocument;
import tungnn.tutor.java.tool.meddoc.core.Step;
import tungnn.tutor.java.tool.meddoc.core.gate.GateResult;
import tungnn.tutor.java.tool.meddoc.llm.StepChats;
import tungnn.tutor.java.tool.meddoc.service.report.StepReport;
import tungnn.tutor.java.tool.meddoc.service.report.StepStatus;

final class StepRecorder {

    static final String LOAD = "load";

    private static final Logger LOGGER = LoggerFactory.getLogger(StepRecorder.class);

    private final StepChats chats;
    private final Map<String, StepReport> reports = new LinkedHashMap<>();
    private String name;
    private Step step;
    private Integer blocksBefore;
    private long startNanos;

    StepRecorder(StepChats chats) {
        this.chats = chats;
    }

    void begin(String name, Step step, MarkdownDocument before) {
        this.name = name;
        this.step = step;
        this.blocksBefore = before == null ? null : before.blocks().size();
        this.startNanos = System.nanoTime();
    }

    GateResult finish(GateResult gate, MarkdownDocument after, Path output, List<String> details) {
        var status = gate == null || gate.passed() ? StepStatus.DONE : StepStatus.FAILED;
        store(
                status,
                output == null ? null : output.getFileName().toString(),
                after == null ? null : after.blocks().size(),
                gate,
                details);
        return gate;
    }

    void fail(Exception error) {
        if (name == null) {
            return;
        }
        store(StepStatus.FAILED, null, null, null, List.of("Error: " + error.getMessage()));
    }

    void skip(Step skipped, String reason) {
        reports.put(skipped.cliName(), StepReport.without(skipped.cliName(), StepStatus.SKIPPED, skipped.usesLlm(), reason));
    }

    List<StepReport> build(Set<Step> selected) {
        var result = new ArrayList<StepReport>();
        if (reports.containsKey(LOAD)) {
            result.add(reports.get(LOAD));
        }
        for (var s : Step.values()) {
            var report = reports.get(s.cliName());
            if (report == null) {
                report = StepReport.without(
                        s.cliName(),
                        selected.contains(s) ? StepStatus.NOT_RUN : StepStatus.NOT_SELECTED,
                        s.usesLlm(),
                        null);
            }
            result.add(report);
        }
        return result;
    }

    private void store(StepStatus status, String output, Integer after, GateResult gate, List<String> details) {
        var usage = step != null && step.usesLlm() ? chats.usageLine(step) : null;
        if (usage != null) {
            LOGGER.info("Usage {}", usage);
        }
        var elapsed = Duration.ofNanos(System.nanoTime() - startNanos);
        reports.put(
                name,
                new StepReport(
                        name,
                        status,
                        output,
                        blocksBefore,
                        after,
                        elapsed,
                        gate,
                        details,
                        step != null && step.usesLlm(),
                        usage));
        name = null;
        step = null;
    }
}
