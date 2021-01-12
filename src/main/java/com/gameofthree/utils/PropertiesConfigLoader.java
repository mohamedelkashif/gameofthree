package com.gameofthree.utils;

import com.gameofthree.game.exceptions.ConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public class PropertiesConfigLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesConfigLoader.class);

    private static Properties properties = new Properties();

    private PropertiesConfigLoader() {
    }

    public static Properties getProperties() {
        return properties;
    }

    public static void initialize(String filename) throws FileNotFoundException {
        tryLoadProperties(filename);
    }


    private static void tryLoadProperties(String filename) throws FileNotFoundException {

        try (InputStream input = PropertiesConfigLoader.class.getClassLoader().getResourceAsStream(filename)) {
            if (Objects.isNull(input)) throw new FileNotFoundException();
            properties.load(input);
        } catch (FileNotFoundException ex) {
            throw new FileNotFoundException("Properties file '" + filename + "' not found.");
        } catch (IOException er) {
            throw new ConnectionException("IO error while reading properties file '" + filename + "'.");
        }

        LOGGER.info("Properties file '{}' loaded successfully.", filename);
    }

}
