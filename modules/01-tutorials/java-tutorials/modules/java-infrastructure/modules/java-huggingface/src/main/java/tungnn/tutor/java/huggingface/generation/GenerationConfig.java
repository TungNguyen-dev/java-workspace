package tungnn.tutor.java.huggingface.generation;

import com.fasterxml.jackson.databind.JsonNode;
import java.nio.file.Path;
import java.util.BitSet;
import tungnn.tutor.java.huggingface.config.HfConfig;
import tungnn.tutor.java.huggingface.hub.ModelFiles;

/**
 * Generation parameters read from {@code generation_config.json} (preferred) and {@code
 * config.json}.
 *
 * @param bannedTokenIds single-token entries of {@code bad_words_ids} (Marian bans the pad token)
 */
public record GenerationConfig(
    int maxNewTokens,
    long decoderStartTokenId,
    long eosTokenId,
    long padTokenId,
    BitSet bannedTokenIds) {

  public static GenerationConfig load(Path modelDir, HfConfig hfConfig) {
    JsonNode model = ModelFiles.readJson(modelDir, "config.json");
    JsonNode gen = ModelFiles.readJson(modelDir, "generation_config.json");

    long pad = firstLong(0, "pad_token_id", gen, model);
    long start = firstLong(pad, "decoder_start_token_id", gen, model);
    long eos = firstLong(1, "eos_token_id", gen, model); // may be an array -> first element
    long maxLength = firstLong(hfConfig.maxNewTokens(), "max_length", gen, model);

    var banned = new BitSet();
    for (JsonNode node : new JsonNode[] {gen, model}) {
      for (JsonNode sequence : node.path("bad_words_ids")) {
        if (sequence.isArray() && sequence.size() == 1) {
          banned.set(sequence.get(0).asInt());
        }
      }
    }

    int maxNew = (int) Math.max(1, Math.min(hfConfig.maxNewTokens(), maxLength));
    return new GenerationConfig(maxNew, start, eos, pad, banned);
  }

  private static long firstLong(long defaultValue, String field, JsonNode... nodes) {
    for (JsonNode node : nodes) {
      JsonNode value = node.get(field);
      if (value == null || value.isNull()) {
        continue;
      }
      if (value.isArray() && !value.isEmpty()) {
        return value.get(0).asLong();
      }
      if (value.isNumber()) {
        return value.asLong();
      }
    }
    return defaultValue;
  }
}
