package tungnn.tutor.java.tool.finance.service;

import java.util.ArrayList;
import java.util.List;

public final class SyncReport {

    private int documentsRead;
    private int extracted;
    private int alreadyWritten;
    private int duplicates;
    private int toWrite;
    private int written;
    private int failed;
    private final List<String> issues = new ArrayList<>();

    void documentRead() {
        documentsRead++;
    }

    void extracted(int count) {
        extracted += count;
    }

    void alreadyWritten(int count) {
        alreadyWritten += count;
    }

    void duplicates(int count) {
        duplicates += count;
    }

    void toWrite(int count) {
        toWrite = count;
    }

    void recordWritten() {
        written++;
    }

    void recordFailed(String reason) {
        failed++;
        issues.add("Write failed: " + reason);
    }

    void issue(String issue) {
        issues.add(issue);
    }

    public int documentsRead() {
        return documentsRead;
    }

    public int extracted() {
        return extracted;
    }

    public int alreadyWritten() {
        return alreadyWritten;
    }

    public int duplicates() {
        return duplicates;
    }

    public int toWrite() {
        return toWrite;
    }

    public int written() {
        return written;
    }

    public int failed() {
        return failed;
    }

    public List<String> issues() {
        return List.copyOf(issues);
    }

    @Override
    public String toString() {
        var sb = new StringBuilder(
                "documents=%d extracted=%d alreadyWritten=%d duplicates=%d toWrite=%d written=%d failed=%d"
                        .formatted(documentsRead, extracted, alreadyWritten, duplicates, toWrite, written, failed));
        issues.forEach(issue -> sb.append(System.lineSeparator()).append(" - ").append(issue));
        return sb.toString();
    }
}
