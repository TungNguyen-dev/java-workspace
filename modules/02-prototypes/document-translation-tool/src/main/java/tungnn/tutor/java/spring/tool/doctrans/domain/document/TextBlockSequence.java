package tungnn.tutor.java.spring.tool.doctrans.domain.document;

import java.util.List;
import java.util.Objects;

public record TextBlockSequence(SequenceType sequenceType, List<TextBlock> textBlocks) {

  public TextBlockSequence {
    Objects.requireNonNull(sequenceType, "sequenceType must not be null");
    Objects.requireNonNull(textBlocks, "textNodes must not be null");
    textBlocks = List.copyOf(textBlocks); // Bảo vệ immutability cho list
  }

  public enum SequenceType {
    BODY,
    HEADER,
    FOOTER,
    FOOTNOTE,
    ENDNOTE,
    COMMENT,
    OTHER
  }
}
