package com.devscion.metaprobekmp.utils

import com.devscion.metaprobekmp.model.MetaProbeError
import com.devscion.metaprobekmp.model.ProbedData
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlHandler
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlParser
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MetaDataProber {

    suspend fun fetchMetadataSuspend(httpClient: HttpClient, url: String): Result<ProbedData> {
        return try {
            withContext(Dispatchers.IO) {
                val httpResponse: HttpResponse = httpClient.get(url)
                if (httpResponse.status.value == 200) {
                    val html = httpResponse.bodyAsText()
                    var icon: String? = null
                    var ogImage: String? = null
                    var description: String? = null
                    var title: String? = null
                    val handler = KsoupHtmlHandler.Builder()
                        .onOpenTag { name, attributes, isImplied ->
                            if (attributes.containsKey("rel")
                                && attributes.containsKey("class")
                                && attributes.containsKey("href") &&
                                attributes["rel"] == "icon" &&
                                attributes["class"] == "js-site-favicon"
                            ) {
                                icon = attributes["href"]
                            } else if (attributes.containsKey("property")
                                && attributes.containsKey("content")
                                && attributes["property"] == "og:image"
                                && attributes["content"] != null
                            ) {
                                ogImage = attributes["content"]
                            } else if (attributes.containsKey("property")
                                && attributes.containsKey("content")
                                && attributes["property"] == "og:description"
                                && attributes["content"] != null
                            ) {
                                description = attributes["content"]
                            } else if (attributes.containsKey("property")
                                && attributes.containsKey("content")
                                && attributes["property"] == "og:title"
                                && attributes["content"] != null
                            ) {
                                title = attributes["content"]
                            }
                        }
                        .build()

                    // Create a parser
                    val ksoupHtmlParser = KsoupHtmlParser(
                        handler = handler,
                    )
                    ksoupHtmlParser.write(html)
                    ksoupHtmlParser.end()
                    Result.success(
                        ProbedData(
                            title,
                            description, icon ?: parseIcon(html,url), ogImage
                        )
                    )
                } else {
                    Result.failure(Exception(MetaProbeError.ParsingError))
                }
            }
        } catch (e: TimeoutCancellationException) {
            Result.failure(Exception(MetaProbeError.NetworkError))
        } catch (e: Throwable) {
            Result.failure(Exception(MetaProbeError.UnknownError))
        }
    }

    private fun parseIcon(html: String, url: String): String? {
        val lines = html.lineSequence()

        var icon: String? = null
        for (line in lines) {

            if (icon == null) {
                // Try parsing different types of icon links
                icon = Regex(
                    "<link rel=\"icon\" href=\"(.*?)\"",
                    RegexOption.IGNORE_CASE
                ).find(line)?.groups?.get(1)?.value
                    ?: Regex(
                        "<link rel=\"shortcut icon\" href=\"(.*?)\"",
                        RegexOption.IGNORE_CASE
                    ).find(line)?.groups?.get(1)?.value
                            ?: Regex(
                        "<link rel=\"apple-touch-icon\" href=\"(.*?)\"",
                        RegexOption.IGNORE_CASE
                    ).find(line)?.groups?.get(1)?.value

                if (icon != null && !icon.startsWith("http")) {
                    // Handle relative URLs
                    icon = url + icon
                }
            }

            if (icon != null) {
                return icon
            }
        }
        return null
    }



    fun fetchMetadataCallback(
        httpClient: HttpClient,
        url: String,
        callback: (Result<ProbedData>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            fetchMetadataSuspend(httpClient, url).let(callback)
        }
    }
}


