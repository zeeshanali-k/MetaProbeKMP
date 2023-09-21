package com.devscion.metaprobekmp

import com.devscion.metaprobekmp.model.ProbedData
import com.devscion.metaprobekmp.utils.MetaDataProber
import com.devscion.metaprobe.utils.OnMetaDataProbed
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout

class MetaProbe constructor(
    private val url: String,
) {

    private var httpClient: HttpClient? = null

    fun setClient(httpClient: HttpClient) {
        this.httpClient = httpClient
    }

    suspend fun probeLink(): Result<ProbedData> {
        return MetaDataProber().fetchMetadataSuspend(
            httpClient ?: HttpClient {
                install(HttpTimeout) {
                    requestTimeoutMillis = 5000
                }
            },
            url
        )
    }

    fun probeLink(onMetaDataProbed: OnMetaDataProbed) {
        MetaDataProber().fetchMetadataCallback(httpClient ?: HttpClient {
            install(HttpTimeout) {
                requestTimeoutMillis = 5000
            }
        }, url, onMetaDataProbed::onMetaDataProbed)
    }

}