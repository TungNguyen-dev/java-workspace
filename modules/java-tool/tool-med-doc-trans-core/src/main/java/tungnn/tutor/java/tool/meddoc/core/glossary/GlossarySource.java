package tungnn.tutor.java.tool.meddoc.core.glossary;

import java.io.IOException;

public interface GlossarySource {

    String describe();

    Glossary load() throws IOException;
}
