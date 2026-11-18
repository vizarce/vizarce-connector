# VIZARCE Connector

![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)
![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Mule SDK](https://img.shields.io/badge/Mule%20SDK-1.7.0-blue.svg)

A custom Anypoint Connector, built with the **Mule SDK** (a real Java extension, not DataWeave/HTTP-connector composition), wrapping [VIZARCE](https://vizarce.com)'s production REST API as native Mule operations.

VIZARCE is an AI prompt engineering studio for Suno and other AI music platforms. This connector exposes **eight** of its API operations so a Mule flow can drive AI music-prompt generation as one step of a larger enterprise orchestration.

Exchange listing content: [`exchange-docs/home.md`](exchange-docs/home.md). Reference application: [vizarce-orchestration-flow](https://github.com/vizarce/vizarce-orchestration-flow).

## Operations

| Operation           | VIZARCE endpoint                         | Purpose                                                      |
| ------------------- | ---------------------------------------- | ------------------------------------------------------------ |
| `composeSong`       | `POST /compose`                          | Full Master Prompt compose (lyrics + style prompt)           |
| `generateLyrics`    | `POST /generate`                         | Standalone lyrics generation                                 |
| `buildArtistDNA`    | `POST /artist-dna`                       | Descriptive (name-free) Vocal/Sound DNA for a custom artist  |
| `regenerateSection` | `POST /structure` (`regenerate-section`) | Regenerate one section of an existing lyrics prompt          |
| `generateStructure` | `POST /structure` (`from-concept`)       | AI-generated section sequence from a free-text description   |
| `fillStructureTags` | `POST /structure` (`generate-tags`)      | One tag prompt per section of a fixed section list           |
| `refineText`        | `POST /text-tools` (`refine`)            | Revise a lyrics or style prompt with a free-text instruction |
| `annotateStress`    | `POST /text-tools` (`annotate-stress`)   | Insert Ukrainian stress-accent marks into a lyrics prompt    |

## Architecture notes

- **Connection**: `VizarceConnectionProvider` is a `CachedConnectionProvider`: one `HttpClient` per configuration, reused across calls.
- **Typed errors**: `VIZARCE:CONNECTIVITY`, `VIZARCE:RATE_LIMITED`, `VIZARCE:API_ERROR`, `VIZARCE:INVALID_RESPONSE`, declared through `VizarceErrorTypeProvider`, so flows can branch on each (Until Successful for rate limits, Circuit Breaker for connectivity).
- **Typed results**: every operation returns a Jackson-mapped POJO, so DataWeave can reference fields directly.
- **Transport**: `java.net.http.HttpClient` from the standard library, keeping the dependency footprint minimal.

## Prerequisites

Anypoint Studio 7.19+, Java 17, Maven 3.8+, Mule runtime 4.11.0+, a VIZARCE API key (Settings → API Keys in the VIZARCE app).

## Build and use

```bash
mvn clean package      # build
mvn clean install      # install to local .m2
```

Dependency for a consuming Mule application:

```xml
<dependency>
    <groupId>com.vizarce</groupId>
    <artifactId>vizarce-connector</artifactId>
    <version>1.1.1</version>
    <classifier>mule-plugin</classifier>
</dependency>
```

## Testing

```bash
mvn test
```

Runs the JUnit suite for `VizarceConnection` (base-URL normalization, exception field mapping). `connectingToAnUnreachableHostFailsFast` makes a real outbound attempt to a non-routable address (RFC 5737), so skip it in network-isolated CI.

Flow-level tests with MUnit live in the reference application, [vizarce-orchestration-flow](https://github.com/vizarce/vizarce-orchestration-flow).

## License

MIT, see [LICENSE](LICENSE).
