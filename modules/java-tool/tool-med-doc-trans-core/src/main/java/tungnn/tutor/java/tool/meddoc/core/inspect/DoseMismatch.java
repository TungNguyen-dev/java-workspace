package tungnn.tutor.java.tool.meddoc.core.inspect;

import java.util.List;

public record DoseMismatch(String section, List<String> missing, List<String> extra) {

    public DoseMismatch {
        missing = List.copyOf(missing);
        extra = List.copyOf(extra);
    }
}
