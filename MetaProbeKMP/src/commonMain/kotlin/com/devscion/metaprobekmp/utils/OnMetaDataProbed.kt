package com.devscion.metaprobe.utils

import com.devscion.metaprobekmp.model.ProbedData


interface OnMetaDataProbed {

    fun onMetaDataProbed(probedData: Result<ProbedData>)

}