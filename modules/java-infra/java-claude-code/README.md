# java-claude-code

Static utilities for calling Claude from Java through the official [Anthropic Java SDK](https://github.com/anthropics/anthropic-sdk-java) (`com.anthropic:anthropic-java`).

`java-ai` wraps many providers through Spring AI. This module is Claude-only and exposes SDK features directly: effort control, prompt caching, typed structured output, token counting, streaming and the tool runner.

## Setup

Requires Java 25. Add the module as a dependency:

```xml
<dependency>
    <groupId>tungnn.tutor.java.infra</groupId>
    <artifactId>java-claude-code</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

Configuration is read from the environment (see `.env.example` at the repo root):

| Variable | Purpose |
| --- | --- |
| `ANTHROPIC_API_KEY` | API key used by `ClaudeClientFactory.create()` |
| `ANTHROPIC_BASE_URL` | Optional endpoint override |

## Classes

Package root: `tungnn.tutor.java.claude`.

| Class | Purpose |
| --- | --- |
| `client.ClaudeClientFactory` | Builds an `AnthropicClient` from the environment, an API key or `ClaudeClientOptions` |
| `config.ClaudeModels` | Model id constants: `OPUS` (default), `SONNET`, `HAIKU`, `FABLE` |
| `config.ChatOptions` | Model, max tokens, system prompt, effort, system-prompt caching |
| `message.ClaudeMessageUtils` | `ask`, `create`, `buildParams`, `extractText`, `requireUsable` |
| `message.ClaudeConversation` | Multi-turn conversation that replays the full assistant content |
| `stream.ClaudeStreamUtils` | Streams text deltas to a callback and returns the full text |
| `structured.ClaudeStructuredUtils` | Maps the response onto a record or POJO |
| `tool.ClaudeToolUtils` | Runs a tool loop over `Supplier<String>` tool classes |
| `token.ClaudeTokenUtils` | Counts input tokens before sending |
| `support.ClaudeException` | Unchecked error for refusals and truncated responses |

## Usage

```java
var client = ClaudeClientFactory.create();

String answer = ClaudeMessageUtils.ask(client, "What is the capital of France?");

var options = ChatOptions.of(ClaudeModels.HAIKU)
        .withMaxTokens(2_000)
        .withEffort(OutputConfig.Effort.LOW)
        .withSystem("Answer in one sentence.", true);
String short = ClaudeMessageUtils.ask(client, "Explain TCP.", options);
```

Streaming:

```java
ClaudeStreamUtils.stream(client, "Write a haiku", ChatOptions.defaults(), System.out::print);
```

Structured output:

```java
record Capital(String country, String city) {}

Capital capital = ClaudeStructuredUtils.ask(client, "Capital of France?", Capital.class, ChatOptions.defaults());
```

Tools:

```java
public static class GetTemperature implements Supplier<String> {
    @JsonPropertyDescription("City name")
    public String city;

    public String get() {
        return "21 degrees Celsius in " + city;
    }
}

String reply = ClaudeToolUtils.runTools(client, "Temperature in Hanoi?", ChatOptions.defaults(), GetTemperature.class);
```

Token counting and multi-turn:

```java
long tokens = ClaudeTokenUtils.countTokens(client, "Hello", ChatOptions.defaults());

var conversation = new ClaudeConversation(client, ChatOptions.defaults());
conversation.send("My favourite number is 42.");
conversation.send("What is my favourite number?");
```

## Defaults and behaviour

- Default model is `claude-opus-5-5`, max tokens 16000, effort `HIGH`. Effort is always sent explicitly because the server-side default differs between Claude releases.
- `ClaudeMessageUtils.create` throws `ClaudeException` when the stop reason is `refusal` or `max_tokens`.
- Streaming callers should raise `maxTokens` (`ChatOptions.STREAM_MAX_TOKENS` is 64000) for long outputs.

## Tests

```bash
cd modules/java-infra
mvn -pl java-claude-code test
```

`ClaudeLiveIT` calls the real API (billed, Haiku model) and runs only when `ANTHROPIC_API_KEY` is set. Surefire does not pick up `*IT` classes by default, so name it explicitly:

```bash
ANTHROPIC_API_KEY=... mvn -pl java-claude-code -Dtest=ClaudeLiveIT -Dsurefire.failIfNoSpecifiedTests=false test
```

## Not covered yet

Message batches, the Files API, Managed Agents and server-side refusal fallbacks.
