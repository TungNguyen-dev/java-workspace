package tungnn.tutor.java.ai.util;

import java.util.EnumMap;
import java.util.Map;
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
  private static final Map<LlmProvider, ChatModelFactory> FACTORIES = createFactories();

  private ChatModelUtils() {
    throw new UnsupportedOperationException("cannot instantiate utility class");
  }

  public static ChatModel create(LlmClientProperties properties) {
    return factoryFor(properties.provider()).create(properties);
  }

  public static ChatModelFactory factoryFor(LlmProvider provider) {
    var factory = FACTORIES.get(provider);
    if (factory == null) {
      throw new IllegalStateException("No ChatModelFactory registered for " + provider);
    }
    return factory;
  }

  private static Map<LlmProvider, ChatModelFactory> createFactories() {
    var factories = new EnumMap<LlmProvider, ChatModelFactory>(LlmProvider.class);
    var openAi = new OpenAiCompatibleChatModelFactory();
    for (var provider : LlmProvider.values()) {
      if (provider.isOpenAiCompatible()) {
        factories.put(provider, openAi);
      }
    }
    factories.put(LlmProvider.OPENAI, openAi);
    factories.put(LlmProvider.ANTHROPIC, new AnthropicChatModelFactory());
    factories.put(LlmProvider.GOOGLE_GENAI, new GoogleGenAiChatModelFactory());
    factories.put(LlmProvider.OLLAMA, new OllamaChatModelFactory());
    factories.put(LlmProvider.DEEPSEEK, new DeepSeekChatModelFactory());
    factories.put(LlmProvider.MISTRAL_AI, new MistralAiChatModelFactory());
    factories.put(LlmProvider.BEDROCK_CONVERSE, new BedrockConverseChatModelFactory());
    return Map.copyOf(factories);
  }
}
