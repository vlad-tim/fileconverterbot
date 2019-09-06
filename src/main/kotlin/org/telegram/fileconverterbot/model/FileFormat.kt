/**
 * Copyright (c) 2016 Timofeev Vlad
 */
package org.telegram.fileconverterbot.model

const val MP4 = "MP4"

enum class FileType {
    IMAGE,
    AUDIO,
    VIDEO
}

enum class FileFormat(val extension: String,
                      val fileType: FileType,
                      val buttonDescription: String = extension) {
    // image
    JPEG("JPEG", FileType.IMAGE),
    PNG("PNG", FileType.IMAGE),
    GIF("GIF", FileType.IMAGE),
    BMP("BMP", FileType.IMAGE),
    TIFF("TIFF", FileType.IMAGE),
    WEBP("WEBP", FileType.IMAGE),
    // audio
    MP3("MP3", FileType.AUDIO),
    WMA("WMA", FileType.AUDIO),
    WAV("WAV", FileType.AUDIO),
    OGG("OGG", FileType.AUDIO, "OGG-Vorbis"),
    OPUS("OPUS", FileType.AUDIO, "OGG-Opus"),
    FLAC("FLAC", FileType.AUDIO),
    AAC("AAC", FileType.AUDIO),
    AIFF("AIFF", FileType.AUDIO),
    AMR("AMR", FileType.AUDIO),
    // video
    MPEG4(MP4, FileType.VIDEO),
    AVI("AVI", FileType.VIDEO),
    WMV("WMV", FileType.VIDEO),
    MOV("MOV", FileType.VIDEO),
    FLV("FLV", FileType.VIDEO),
    MPEG("MPG", FileType.VIDEO),
    MKV("MKV", FileType.VIDEO);

    val identifiedDescription: String

    init {
        var description = "${buttonDescription}(${fileType.name.toLowerCase()})"
        identifiedDescription = description
    }
}