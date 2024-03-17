package com.api.component;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CustomLogger {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CustomLogger.class);
    public CustomLogger(){
         logger.info("logger is create");
    }
    public void logError(String text){
        logger.error(text);
    }
    public void logCachePut(String text){
        logger.info("Cache put : {}", text);
    }

    public void logInfo(String text){
        logger.info("{}", text);
    }

    public void logCacheRemove(String text){
        logger.info("Cache remove : {}", text);
    }


}
