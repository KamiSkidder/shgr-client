package com.kamiskidder.shgr.util.client;

import com.kamiskidder.shgr.SHGR;
import org.apache.logging.log4j.Logger;

public class LogUtil {
    private static final Logger logger = SHGR.logger;

    public static void info(Object obj) {
        logger.info(obj);
    }

    public static void warn(Object obj) {
        logger.warn(obj);
    }

    public static void error(Object obj) {
        logger.error(obj);
    }
}
