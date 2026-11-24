package tungnn.tutor.java.docker.model;

public record ExecResult(long exitCode, String stdout, String stderr) {
    public boolean success() {
        return exitCode == 0;
    }
}
