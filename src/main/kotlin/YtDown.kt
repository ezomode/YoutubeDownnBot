import dev.inmo.tgbotapi.bot.ktor.telegramBot
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onContentMessage
import dev.inmo.tgbotapi.extensions.utils.textLinkTextSourceOrNull
import dev.inmo.tgbotapi.extensions.utils.uRLTextSourceOrNull
import dev.inmo.tgbotapi.extensions.utils.withContentOrNull
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.LinkPreviewOptions
import dev.inmo.tgbotapi.types.message.content.TextedContent
import dev.inmo.tgbotapi.utils.regular
import kotlinx.coroutines.runBlocking

fun main() =
    runBlocking {
        val botToken = System.getenv("TELEGRAM_BOT_TOKEN") ?: error("Bot token not set")
        val outputDir = System.getenv("OUTPUT_DIR") ?: "downloads"

        telegramBot(botToken)
            .buildBehaviourWithLongPolling {
                onCommand("start") { message ->
                    sendMessage(chatId = ChatId(message.chat.id.chatId), text = "Send a YouTube URL to download.")
                }

                onContentMessage { msg ->
                    val url =
                        msg.withContentOrNull<TextedContent>()?.let { message ->
                            message.content.textSources.firstNotNullOfOrNull {
                                it.textLinkTextSourceOrNull()?.url ?: it.uRLTextSourceOrNull()?.source
                            }
                        } ?: null.apply {
                            reply(msg) {
                                regular("I expect URLs only")
                            }
                        } ?: error("no video URL received")

                    sendMessage(
                        chatId = ChatId(msg.chat.id.chatId),
                        text = "Getting $url",
                        linkPreviewOptions = LinkPreviewOptions.Disabled,
                    )

                    if (url.isEmpty()) {
                        sendMessage(chatId = ChatId(msg.chat.id.chatId), text = "Please provide a valid YouTube URL.")
                    } else {
                        val result = downloadYouTubeVideo(url, outputDir)
                        if (result) {
                            sendMessage(chatId = ChatId(msg.chat.id.chatId), text = "Done!")
                        } else {
                            sendMessage(chatId = ChatId(msg.chat.id.chatId), text = "Failed to download the video.")
                        }
                    }
                }
            }.join()
    }

fun downloadYouTubeVideo(
    url: String,
    outputDir: String,
): Boolean =
    try {
        val process = ProcessBuilder("yt-dlp", "-o", "$outputDir/%(title)s.%(ext)s", url).start()
        process.waitFor()
        process.exitValue() == 0
    } catch (e: Exception) {
        println("Error downloading video: ${e.message}")
        false
    }
