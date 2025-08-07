package it.myhouse.switchbot;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class AppStartupLogger {

    private static final Logger logger = LoggerFactory.getLogger(AppStartupLogger.class);

    @Autowired
    private Environment env;

    @PostConstruct
    public void logConfiguration() {
        logger.info("=== CONFIGURATION SUMMARY ===");

        if (env instanceof ConfigurableEnvironment configurableEnv) {
            for (var propertySource : configurableEnv.getPropertySources()) {
                if (propertySource.getSource() instanceof java.util.Map map) {
                    map.forEach((key, value) -> {
                        logger.info("{} = {}", key, value);
                    });
                }
            }
        } else {
            logger.warn("Environment is not configurable, can't list properties");
        }

        logger.info("==============================");
    }

    private void log(String key, String value) {
        logger.info("{} = {}", key, value != null ? value : "(null)");
    }

    private void logMasked(String key, String value) {
        String masked = (value != null && value.length() > 4)
                ? "****" + value.substring(value.length() - 4)
                : "****";
        logger.info("{} = {}", key, masked);
    }
}
