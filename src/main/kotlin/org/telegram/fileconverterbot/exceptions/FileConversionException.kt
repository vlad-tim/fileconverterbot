/**
 * Copyright (c) 2016 Timofeev Vlad
 */
package org.telegram.fileconverterbot.exceptions

class FileConversionException : Exception {

    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
}