package org.telegram.fileconverterbot.localization

/**
 * Copyright (c) 2016 Timofeev Vlad
 */
object Localization {

    const val START_COMMAND_RESPONSE = "Hi! File Converter is at your service.\r\n\r\n" +
            "With me you can convert files from one format to another. Images\uD83D\uDCF7, audio\uD83D\uDD0A and " +
            "video\uD83D\uDCF9 files are supported. Send me a file to convert or type /help for more information."

    const val HELP_COMMAND_RESPONSE = "File Converter is a tool that can convert a file from one format to another. " +
            "For example, it can convert images from PNG to JPEG or audio from OGG to MP3. Send the file as an attachment to start conversion. " +
            "Supported attachment types: <b>photo</b>, <b>video</b>, <b>audio</b>, <b>voice</b>, <b>document</b>, and <b>sticker</b>.\r\n\r\n" +
            "Supported image formats: <b>JPEG</b>, <b>PNG</b>, <b>GIF</b>, <b>BMP</b>, <b>TIFF</b>, <b>WEBP</b> (not animated). \r\n" +
            "Supported audio formats: <b>MP3</b>, <b>WMA</b>, <b>WAV</b>, <b>OGG</b> (Opus and Vorbis), <b>FLAC</b>, <b>AAC</b>, <b>AIFF</b>, <b>AMR</b>.\r\n" +
            "Supported video formats: <b>MPEG4</b>, <b>MPEG</b>, <b>AVI</b>, <b>WMV</b>, <b>MOV</b>, <b>FLV</b>, <b>MKV</b>.\r\n\r\n" +
            "Max input file size is <b>20MB</b>, max output file size is <b>50MB</b>.\r\n\r\n" +
            "<b>Note 1:</b> Telegram may perform automatic file conversions to uploaded files (e.g. converting GIF to MP4, or cropping video resolution). " +
            "Consider sending your files as \"files\" instead of \"media\" to avoid it.\r\n\r\n" +
            "<b>Note 2:</b> The conversion process may take a while, so please be patient."
    const val PROMO_COMMAND_RESPONSE = "<b>Why does one need to convert files from one format to another?</b>\r\n" +
            "1. <i>Efficient data storage.</i> Converting from uncompressed format (such as PNG or WAV) to lossy format (such as JPEG or MP3) can " +
            "significantly shrink the file size.\r\n" +
            "2. <i>Overcoming software limitations.</i> Users may face files unsupported by their software environment (e.g. iOS and OS X " +
            "<a href=\"https://developer.apple.com/library/content/documentation/MusicAudio/Conceptual/CoreAudioOverview/SupportedAudioFormatsMacOSX/SupportedAudioFormatsMacOSX.html\">" +
            "cannot play</a> OGG audio out-of-the-box)\r\n\r\n" +
            "<b>Why using this File Converter to convert files?</b>\r\n" +
            "1. <i>Cloud-Based.</i> No need to install anything.\r\n" +
            "2. <i>Cross-Platform.</i> Works on any platform where Telegram does, including web browser.\r\n" +
            "3. <i>Free.</i> Completely free. No ads.\r\n" +
            "4. <i>User-friendly.</i> Interface that's familiar to any Telegram user.\r\n"
    const val TERMS_COMMAND_RESPONSE =
            "1. <b>GENERAL</b>\r\nTHE PROGRAM IS DISTRIBUTED IN THE HOPE THAT IT WILL BE USEFUL, BUT WITHOUT WARRANTY OF ANY KIND, " +
                    "EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF FITNESS FOR A " +
                    "PARTICULAR PURPOSE. THE ENTIRE RISK AS TO THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU.\r\n\r\n" +
                    "2. <b>PRIVACY</b>\r\nTHE PROGRAM USES EXTERNAL SERVER(S) TO OPERATE. ALL THE FILES UPLOADED BY AN INDIVIDUAL WHO USES THE PROGRAM " +
                    "GET DELETED FROM THE SERVERS WITHIN 10 MINUTES SINCE UPLOADING."
    const val CHANGELOG_COMMAND_RESPONSE = "" +
            "<b>2017/06/18</b>\r\n" +
            "Add support for Video Messages (Telegram 4.0+)\r\n" +
            "<b>2017/06/18</b>\r\n" +
            "Add the command to donate\r\n" +
            "<b>2017/01/08</b>\r\n" +
            "Add support for AMR\r\n" +
            "<b>2016/12/10</b>\r\n" +
            "Improve performance and responsiveness\r\n" +
            "<b>2016/11/21</b>\r\n" +
            "Add the ability to extract audio from video\r\n" +
            "<b>2016/11/07</b>\r\n" +
            "Add the command to leave feedback\r\n" +
            "<b>2016/10/07</b>\r\n" +
            "Launch the bot\r\n"
    const val FEEDBACK_COMMAND_RESPONSE = "To leave feedback type:\r\n<b>/feedback &lt;your message&gt;</b>\r\nUse " +
            "English. Your message will be delivered to the developers."
    const val DONATION_COMMAND_RESPONSE = "Donations are always appreciated\r\n" +
            "Bitcoin wallet: <b>MY_BITCOIN_ADDRESS</b>\r\n" +
            "Donation examples:\r\n" +
            "<i>0.001 BTC</i> - Get a cup of coffee\r\n" +
            "<i>0.015 BTC</i> - Cover monthly hosting expenses\r\n" +
            "<i>0.080 BTC</i> - Sponsor one day full-time FileConverterBot's development\r\n"

    const val TEXT_RESPONSE = "Please send a file or command"
    const val FEEDBACK_SUBJECT = "Feedback"
    const val FEEDBACK_SENT_RESPONSE = "Your message has been sent"
    const val UNKNOWN_COMMAND_RESPONSE = "Unknown command"

    const val ATTACHMENT_NOT_SUPPORTED = "Attachment type is not supported. Supported attachment types: photo, video, audio, voice, document, sticker."
    const val INCOMING_FILE_TOO_BIG = "This file is too large. The maximum file size is %d MB"
    // changing below string requires adjusting offset
    const val FILE_FORMAT_NOT_SUPPORTED = "This type of files is not supported. Type /help to learn about supported formats."
    const val FILE_CONVERSION_INFO = "The type of the file is %s. It can be converted to:"
    const val SUPPORTED_AUDIO_FORMATS = "Supported audio formats:"

    const val CONVERTING_NOTIFICATION = "Converting to %s..."
    const val OUTCOMING_FILE_TOO_BIG = "Converted file size is %d MB. Cannot send files larger than %d MB!"
    const val UNABLE_TO_CONVERT = "Ouch! An error occurred when converting the file \uD83D\uDE31"
    const val FILE_NOT_FOUND = "The file to convert was not found. Reupload and try again."
    const val INVALID_CALLBACK_DATA = "Invalid request"
    const val CONCURRENT_CONVERSION = "Please wait for the previous conversion to finish"

    const val EXTRACT_AUDIO = "Extract audio"
}
