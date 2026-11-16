# YoutubeDownnBot

YoutubeDownnBot is a Telegram bot. It downloads YouTube videos to a server.

## How it works

1. Send `/start` to the bot.
2. The bot replies: "Send a YouTube URL to download."
3. Send a YouTube URL in a message.
4. The bot replies: "Getting \<URL\>."
5. The bot downloads the video with `yt-dlp`.
6. The bot replies "Done!" when the download succeeds.
7. The bot replies "Failed to download the video." when the download fails.

The bot saves the video file on the server. It does not send the video back to the chat. The file name is the video title plus the file extension.

The bot replies "I expect URLs only" to a message with no URL.

The bot accepts messages from any Telegram user. It has no allow-list.

## Requirements

- JDK 21.
- `yt-dlp` on the server PATH.
- A Telegram bot token. Create a bot with [@BotFather](https://t.me/BotFather) and copy the token.

## Configuration

Set these environment variables before you run the bot.

- `TELEGRAM_BOT_TOKEN` — Required. The Telegram bot token.
- `OUTPUT_DIR` — Optional. The download folder. Default: `downloads`.

## Build

Run this command in the project root:

```sh
./gradlew build
```

## Run

1. Set `TELEGRAM_BOT_TOKEN`.
2. Set `OUTPUT_DIR` if you need a different folder.
3. Run the main class `YtDownKt` with the project runtime classpath.

The project has no Gradle run task. The entry point is `main()` in `src/main/kotlin/YtDown.kt`.

## Technology

- Kotlin on JVM 21.
- Gradle 8.10.
- Telegram Bot API library `tgbotapi` 23.1.0.
- `yt-dlp` as an external program.
