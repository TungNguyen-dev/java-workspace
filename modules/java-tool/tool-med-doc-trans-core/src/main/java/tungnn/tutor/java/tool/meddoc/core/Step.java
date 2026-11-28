package tungnn.tutor.java.tool.meddoc.core;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public enum Step {
    LOAD("load", false),
    FIX_OCR("fix-ocr", true),
    SCAN_SOURCE("scan-source", false),
    STRUCTURE_SOURCE("structure-source", true),
    GLOSSARY("glossary", false),
    TRANSLATE("translate", true),
    POLISH_TARGET("polish-target", true),
    FORMAT_TARGET("format-target", true),
    SCAN_TARGET("scan-target", false),
    CHECK_DOSES("check-doses", true),
    EXPORT("export", true);

    private final String cliName;
    private final boolean selectable;

    Step(String cliName, boolean selectable) {
        this.cliName = cliName;
        this.selectable = selectable;
    }

    public String cliName() {
        return cliName;
    }

    public boolean selectable() {
        return selectable;
    }

    public boolean usesLlm() {
        return this == FIX_OCR || this == TRANSLATE || this == POLISH_TARGET || this == FORMAT_TARGET;
    }

    public String envName() {
        return name();
    }

    public String filePrefix() {
        return "%02d_".formatted(ordinal() + 1);
    }

    public String outputName(String extension) {
        return filePrefix() + cliName.replace('-', '_') + "." + extension;
    }

    public static Step byCliName(String name) {
        var normalized = name == null ? "" : name.strip().toLowerCase(Locale.ROOT);
        return Arrays.stream(values())
                .filter(step -> step.selectable && step.cliName.equals(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown step '" + normalized + "'. Valid steps: " + validNames()));
    }

    public static Set<Step> all() {
        return selectableSteps();
    }

    public static String validNames() {
        return Arrays.stream(values())
                .filter(Step::selectable)
                .map(Step::cliName)
                .collect(Collectors.joining(", "));
    }

    public static Set<Step> parse(String csv) {
        var steps = EnumSet.noneOf(Step.class);
        if (csv != null) {
            for (var part : csv.split(",")) {
                var name = part.strip().toLowerCase(Locale.ROOT);
                if (name.isEmpty()) {
                    continue;
                }
                steps.add(byCliName(name));
            }
        }
        if (steps.isEmpty()) {
            throw new IllegalArgumentException("No steps given. Valid steps: " + validNames());
        }
        return steps;
    }

    private static Set<Step> selectableSteps() {
        var steps = EnumSet.noneOf(Step.class);
        for (var step : values()) {
            if (step.selectable) {
                steps.add(step);
            }
        }
        return steps;
    }
}
