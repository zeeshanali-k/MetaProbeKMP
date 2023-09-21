package com.devscion.metaprobekmp.model

sealed class MetaProbeError : Throwable() {
    data object NetworkError : MetaProbeError()
    data object ParsingError : MetaProbeError()
    data object UnknownError : MetaProbeError()

}
