package tungnn.tutor.java.tool.meddoc.core.check;

import java.util.List;

public record DoseMismatch(String section, List<String> missing, List<String> extra) {

    public DoseMismatch {
        missing = List.copyOf(missing);
        extra = List.copyOf(extra);
    }
}
