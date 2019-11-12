package fileutil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

public class FileUtil {

    private final static Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

    /**
     * GunZip it
     */
    public static Optional<String> gunzipIt(String inputFile) {

        byte[] buffer = new byte[1024];

        String outputFile = inputFile.substring(0, inputFile.indexOf(".gz"));

        try {

            GZIPInputStream gzis =
                    new GZIPInputStream(new FileInputStream(inputFile));

            FileOutputStream out =
                    new FileOutputStream(outputFile);

            int len;
            while ((len = gzis.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }

            gzis.close();
            out.close();
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Done to unzip the file {}", inputFile);
            }
            return Optional.of(outputFile);

        } catch (IOException ex) {
            LOGGER.error("Failed to unzip the file {}", inputFile);
            return Optional.empty();
        }
    }
}
