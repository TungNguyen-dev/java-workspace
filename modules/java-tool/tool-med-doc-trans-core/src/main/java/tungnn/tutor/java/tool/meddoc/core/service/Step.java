package tungnn.tutor.java.tool.meddoc.core.service;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public enum Step {
    OCR("ocr"),
    REFINE_SOURCE("refine-source"),
    TRANSLATE("translate"),
    EDIT("edit"),
    REFINE_TARGET("refine-target"),
    EXPORT("export");

    private final String cliName;

    Step(String cliName) {
        this.cliName = cliName;
    }

    public String cliName() {
        return cliName;
    }

    public boolean usesLlm() {
        return this == OCR || this == TRANSLATE || this == EDIT || this == REFINE_TARGET;
    }

    public String envName() {
        return name();
    }

    public static Step byCliName(String name) {
        var normalized = name == null ? "" : name.strip().toLowerCase(Locale.ROOT);
        return Arrays.stream(values())
                .filter(step -> step.cliName.equals(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown step '" + normalized + "'. Valid steps: " + validNames()));
    }

    public static Set<Step> all() {
        return EnumSet.allOf(Step.class);
    }

    public static String validNames() {
        return Arrays.stream(values()).map(Step::cliName).collect(Collectors.joining(", "));
    }

    public static Set<Step> parse(String csv) {
        var steps = EnumSet.noneOf(Step.class);
        if (csv != null) {
            for (var part : csv.split(",")) {
                var name = part.strip().toLowerCase(Locale.ROOT);
                if (name.isEmpty()) {
                    continue;
                }
                steps.add(Arrays.stream(values())
                        .filter(step -> step.cliName.equals(name))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Unknown step '" + name + "'. Valid steps: " + validNames())));
            }
        }
        if (steps.isEmpty()) {
            throw new IllegalArgumentException("No steps given. Valid steps: " + validNames());
        }
        return steps;
    }
}
