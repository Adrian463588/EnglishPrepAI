package com.englishprep.ai.domain.models

sealed interface AudioSource {
    data class BundledAsset(val relativeAssetPath: String) : AudioSource
    data class LocalFile(val absolutePath: String) : AudioSource
    data class SafUri(val uriString: String) : AudioSource
}
