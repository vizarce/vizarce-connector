# VIZARCE Connector

**Українською, коротко:** конектор Anypoint для [VIZARCE](https://vizarce.com/), студії промпт-інжинірингу для Suno та інших AI-платформ створення музики. Він надає 8 нативних операцій Mule над публічним REST API VIZARCE: генерація промпту пісні, лірики, структури, Artist DNA, повторна генерація секції, редагування тексту за інструкцією і розстановка українських наголосів. Типізовані помилки дають змогу будувати повторні спроби й circuit breaker. Безкоштовний, ліцензія MIT.

---

A custom Anypoint Connector built with the **Mule SDK** (a real Java extension, not an HTTP-connector composition) that exposes VIZARCE's production REST API as native Mule operations. Use it to drive AI music-prompt generation as one step in a larger enterprise orchestration, for example fanning results out to Salesforce, a DAM or a notification channel.

## Highlights

- **8 operations**, each returning a typed result object, so DataWeave can reference fields such as `payload.lyricsPrompt` directly.
- **Typed errors** (`VIZARCE:CONNECTIVITY`, `VIZARCE:RATE_LIMITED`, `VIZARCE:API_ERROR`, `VIZARCE:INVALID_RESPONSE`), so flows can route each one separately: Until Successful with backoff for rate limits, Circuit Breaker for connectivity.
- **Cached connection**: one HTTP client per configuration, reused across calls.
- **Configurable timeouts** per operation, with defaults sized to each call (up to 300 s for a full compose).
- Reference application: [vizarce-orchestration-flow](https://github.com/vizarce/vizarce-orchestration-flow) (Scatter-Gather, Until Successful, Circuit Breaker, DataWeave, Salesforce write, MUnit tests).

## Operations

| Operation           | VIZARCE endpoint                         | Purpose                                                                      |
| ------------------- | ---------------------------------------- | ---------------------------------------------------------------------------- |
| `composeSong`       | `POST /compose`                          | Full Master Prompt compose: lyrics prompt and style prompt                   |
| `generateLyrics`    | `POST /generate`                         | Standalone lyrics generation                                                 |
| `buildArtistDNA`    | `POST /artist-dna`                       | Descriptive, name-free Vocal DNA and Sound DNA for a custom artist reference |
| `regenerateSection` | `POST /structure` (`regenerate-section`) | Regenerate one section of an existing lyrics prompt                          |
| `generateStructure` | `POST /structure` (`from-concept`)       | AI-chosen ordered section list from a free-text style description            |
| `fillStructureTags` | `POST /structure` (`generate-tags`)      | One tag prompt per section of an already-fixed section list                  |
| `refineText`        | `POST /text-tools` (`refine`)            | Revise a lyrics or style prompt with a free-text instruction                 |
| `annotateStress`    | `POST /text-tools` (`annotate-stress`)   | Insert Ukrainian stress-accent marks into a lyrics prompt                    |

## Configuration

| Field     | Required | Description                                                                         |
| --------- | -------- | ----------------------------------------------------------------------------------- |
| `apiKey`  | yes      | Scoped API key from **Settings → API Keys** in the VIZARCE app                      |
| `baseUrl` | no       | Defaults to `https://www.vizarce.com/api`. Override only for local or staging tests |

## Quick start

1. Add the dependency to your Mule application:

```xml
<dependency>
    <groupId>43c5563d-f5fb-4a13-93dc-c496b9324c8a</groupId>
    <artifactId>vizarce-connector</artifactId>
    <version>1.1.2</version>
    <classifier>mule-plugin</classifier>
</dependency>
```

2. Configure the connection and call an operation:

```xml
<vizarce:config name="Vizarce_Config">
    <vizarce:connection apiKey="${vizarce.apiKey}" />
</vizarce:config>

<flow name="compose-flow">
    <http:listener config-ref="HTTP_Listener" path="/compose"/>
    <vizarce:compose-song config-ref="Vizarce_Config"
        concept="#[payload.concept]" genreTag="#[payload.genreTag]"
        bpm="#[payload.bpm]" musicalKey="#[payload.musicalKey]"/>
    <set-payload value="#[payload.lyricsPrompt]"/>
    <error-handler>
        <on-error-continue type="VIZARCE:RATE_LIMITED">
            <logger level="WARN" message="Rate limited, retry with backoff"/>
        </on-error-continue>
    </error-handler>
</flow>
```

## Requirements

Mule runtime 4.11.0 or later, Java 17, a VIZARCE API key.

## Notes

- Operations call VIZARCE's AI backend, so they consume the quota of the account that owns the API key.
- `refineText` with `kind="lyrics"` can return `VIZARCE:API_ERROR` (HTTP 422) when the edit would change the song structure. This is intended: the backend discards structure-breaking edits.
- VIZARCE is an independent product. It is not affiliated with or endorsed by Suno or MuleSoft. All trademarks belong to their respective owners.

## Source and license

[github.com/vizarce/vizarce-connector](https://github.com/vizarce/vizarce-connector), MIT License.
