package org.telegram.fileconverterbot.utils

import java.io.File

/**
 * Copyright (c) 2016 Timofeev Vlad
 */
object ProcessUtils {

    /**
     * Executes process described by [processBuilder] and blocks until this process is finished.
     * All output of this process is redirected to [outputFile], existing content of [outputFile] is discarded(!)
     * @param [processBuilder] process
     * @param [outputFile] file to store logs
     */
    fun execProcess(processBuilder: ProcessBuilder, outputFile: File): Int {
        processBuilder.directory(Constants.TEMP_DIRECTORY)
        processBuilder.redirectErrorStream(true)
        processBuilder.redirectOutput(ProcessBuilder.Redirect.to(outputFile));
        val process = processBuilder.start()
        return process.waitFor()
    }
}