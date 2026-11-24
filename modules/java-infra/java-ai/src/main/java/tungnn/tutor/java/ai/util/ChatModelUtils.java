package tungnn.tutor.java.ai.util;

import org.springframework.ai.chat.model.ChatModel;
import tungnn.tutor.java.ai.config.LlmClientProperties;
import tungnn.tutor.java.ai.config.LlmProvider;
import tungnn.tutor.java.ai.factory.AnthropicChatModelFactory;
import tungnn.tutor.java.ai.factory.BedrockConverseChatModelFactory;
import tungnn.tutor.java.ai.factory.ChatModelFactory;
import tungnn.tutor.java.ai.factory.DeepSeekChatModelFactory;
import tungnn.tutor.java.ai.factory.GoogleGenAiChatModelFactory;
import tungnn.tutor.java.ai.factory.MistralAiChatModelFactory;
import tungnn.tutor.java.ai.factory.OllamaChatModelFactory;
import tungnn.tutor.java.ai.factory.OpenAiCompatibleChatModelFactory;

public final class ChatModelUtils {
  private static final ChatModelFactory OPENAI_COMPATIBLE = new OpenAiCompatibleChatModelFactory();
  private static final ChatModelFactory ANTHROPIC = new AnthropicChatModelFactory();
  private static final ChatModelFactory GOOGLE_GENAI = new GoogleGenAiChatModelFactory();
  private static final ChatModelFactory OLLAMA = new OllamaChatModelFactory();
  private static final ChatModelFactory DEEPSEEK = new DeepSeekChatModelFactory();
  private static final ChatModelFactory MISTRAL_AI = new MistralAiChatModelFactory();
  private static final ChatModelFactory BEDROCK_CONVERSE = new BedrockConverseChatModelFactory();

  private ChatModelUtils() {
    throw new UnsupportedOperationException("cannot instantiate utility class");
  }

  public static ChatModel create(LlmClientProperties properties) {
    return factoryFor(properties.provider()).create(properties);
  }

  public static ChatModelFactory factoryFor(LlmProvider provider) {
    return switch (provider) {
      case ANTHROPIC -> ANTHROPIC;
      case GOOGLE_GENAI -> GOOGLE_GENAI;
      case OLLAMA -> OLLAMA;
      case DEEPSEEK -> DEEPSEEK;
      case MISTRAL_AI -> MISTRAL_AI;
      case BEDROCK_CONVERSE -> BEDROCK_CONVERSE;
      case OPENAI,
          HUGGING_FACE,
          OLLAMA_OPENAI,
          GROQ,
          OPENROUTER,
          TOGETHER_AI,
          NVIDIA_NIM,
          PERPLEXITY,
          XAI_GROK,
          LM_STUDIO,
          VLLM,
          CUSTOM_OPENAI ->
          OPENAI_COMPATIBLE;
    };
  }
}
