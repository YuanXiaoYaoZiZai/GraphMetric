package com.fsc.common.config.loader;

import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonLoader {
    private static final Logger logger = LoggerFactory.getLogger(JsonLoader.class);
    private static final String FILE_CODING = "utf-8";

    public JsonLoader() {
    }

    public static String readToString(File file) throws IOException {
        logger.info("Load File:[{}]", file.getAbsoluteFile());
        String readToString = (String)Files.readLines(file, Charset.forName("utf-8"), new LineProcessor<String>() {
            StringBuilder sb = new StringBuilder();

            public boolean processLine(String line) throws IOException {
                if (!Strings.isNullOrEmpty(line)) {
                    String trimLine = line.trim();
                    if (!trimLine.startsWith("#") && !trimLine.startsWith("//")) {
                        this.sb.append(line).append(System.lineSeparator());
                    } else {
                        this.sb.append("").append(System.lineSeparator());
                    }
                } else {
                    this.sb.append("").append(System.lineSeparator());
                }

                return true;
            }

            public String getResult() {
                return this.sb.toString();
            }
        });
        return readToString;
    }
}
