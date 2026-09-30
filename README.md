# VIZARCE Connector

![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)
![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Mule SDK](https://img.shields.io/badge/Mule%20SDK-1.7.0-blue.svg)

A custom Anypoint Connector, built with the **Mule SDK** (not DataWeave/HTTP-connector
composition — a real Java extension), wrapping [VIZARCE](https://vizarce.com)'s
production REST API as native Mule operations.

VIZARCE is an AI prompt engineering studio for Suno/AI Songmaker platforms. This
connector exposes five of its API endpoints so a Mule integration flow can drive
AI music-prompt generation as one step in a larger enterprise orchestration.

See [`exchange-docs/`](exchange-docs/home.md) for the listing page content published
alongside this connector on Anypoint Exchange.

## Operations

| Operation           | VIZARCE endpoint                         | Purpose                                                          |
|---------------------|-------------------------------------------|-------------------------------------------------------------------|
| `composeSong`       | `POST /compose`                          | Full Master Prompt compose (lyrics + style prompt)                |
| `generateLyrics`     | `POST /generate`                         | Standalone lyrics generation                                      |
| `buildArtistDNA`     | `POST /artist-dna`                       | Descriptive (name-free) Vocal/Sound DNA for a custom artist        |
| `regenerateSection`  | `POST /structure` (`action: regenerate-section`) | Regenerate one section of an existing lyrics-prompt                |
| `generateStructure`  | `POST /structure` (`action: from-concept`) | AI-generated section-name sequence from a free-text style description |
| `fillStructureTags`  | `POST /structure` (`action: generate-tags`) | One tag-prompt per section of an already-fixed section list      |
| `refineText`         | `POST /text-tools` (`action: refine`)    | Revise a lyrics-prompt or style-prompt with a free-text instruction |
| `annotateStress`     | `POST /text-tools` (`action: annotate-stress`) | Insert Ukrainian stress-accent marks into a lyrics-prompt     |

## Architecture notes

- **Connection**: `VizarceConnectionProvider` is a `CachedConnectionProvider` — one
  `HttpClient` per configuration, reused across every operation call, not recreated
  per request.
- **Typed errors**: four connector-specific error types
  (`VIZARCE:CONNECTIVITY`, `VIZARCE:RATE_LIMITED`, `VIZARCE:API_ERROR`,
  `VIZARCE:INVALID_RESPONSE`) are declared via `VizarceErrorTypeProvider`, so an
  orchestration flow can branch on them individually — e.g. route `RATE_LIMITED`
  into an **Until Successful** retry scope with backoff, and `CONNECTIVITY` into a
  **Circuit Breaker**, rather than one generic catch-all handler.
- **Typed results**: every operation returns a Jackson-mapped POJO (e.g.
  `ComposeSongResult`), not a raw JSON string — so DataWeave scripts downstream can
  reference `payload.lyricsPrompt` directly.
- **Transport**: raw `java.net.http.HttpClient` (Java 11+ standard library) rather
  than embedding the Mule HTTP Connector inside the extension — keeps the connector's
  own dependency footprint minimal and its HTTP behavior fully test-controllable.

## Prerequisites

- Anypoint Studio 7.19+ (matches the version already used for the EDA Order
  Processing project)
- Java 17
- Maven 3.8+
- A VIZARCE API key (Settings → API Keys in the VIZARCE app)

## Building

```bash
mvn clean package
```

This produces a `.jar` in `target/`, packaged as a `mule-extension` via the
`mule-extensions-maven-plugin` — the plugin generates the extension's XML/JSON
descriptors automatically from the `@Extension`/`@Operations`/`@ConnectionProvider`
annotations; there is no hand-written `mule-artifact.json` in this project (that
file is for Mule **applications**, not connector extensions).

## Importing into Anypoint Studio

1. `mvn clean install` to publish the connector to your local `.m2` repository.
2. In the consuming Mule application's `pom.xml`, add a dependency on
   `com.vizarce:vizarce-connector:1.0.0-SNAPSHOT`.
3. Anypoint Studio's Mule Palette will pick up the VIZARCE connector's operations
   automatically once the dependency resolves.

## Testing

```bash
mvn test
```

Runs the JUnit suite covering `VizarceConnection`'s own logic (base-URL
normalization, exception field mapping) in isolation. Note:
`connectingToAnUnreachableHostFailsFast` makes a real outbound connection attempt to
a non-routable test address (RFC 5737) and requires the build environment to allow
outbound network access — it may need skipping in fully network-isolated CI runners.

Full integration testing against a real VIZARCE instance (sandbox or production)
belongs in a separate **MUnit** suite once this connector is consumed inside an
actual Mule application flow — not in this connector project itself.

## Next steps (not built yet)

- The **AI Orchestration Flow** (Scatter-Gather, Until Successful, Circuit Breaker,
  DataWeave transform to an enterprise schema) that *consumes* this connector — this
  project is the connector only.
- MUnit test suite at the consuming-application level.

## License

MIT — see [LICENSE](LICENSE).
