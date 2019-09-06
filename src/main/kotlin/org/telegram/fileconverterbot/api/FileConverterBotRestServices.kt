/**
 * Copyright (c) 2016 Timofeev Vlad
 */
package org.telegram.fileconverterbot.api

import org.telegram.fileconverterbot.utils.Constants
import javax.ws.rs.ApplicationPath
import javax.ws.rs.core.Application

// This class is required to enable REST services
@ApplicationPath("${Constants.API_PATH}")
class FileConverterBotRestServices : Application()