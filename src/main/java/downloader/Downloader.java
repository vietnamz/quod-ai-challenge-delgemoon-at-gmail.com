package downloader;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

public class Downloader {

    private static final Logger LOGGER = LoggerFactory.getLogger(Downloader.class);
    private static final int BUFFER_SIZE = 4096;

    private static final String DEFAUL_DIR = "src/main/resources/githuharchive";


    public static Optional<String> downloadResource(String sourceUrl, String saveDir) {

        HttpURLConnection httpConn = null;
        String saveFileName = null;
        if (saveDir == null) {
            saveDir = DEFAUL_DIR;
        }

        // Create the target directory to save the file.
        try {
            File dir = new File(saveDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        } catch (Exception e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Can not create target folder with name: {}", saveDir);
                LOGGER.error(e.getMessage());
            }
            return Optional.empty();
        }

        try {
            URL url = new URL(sourceUrl);
            httpConn = (HttpURLConnection) url.openConnection();

            // Connect to server
            httpConn.connect();

            // Check HTTP response code
            int responseCode = httpConn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                if (LOGGER.isErrorEnabled()) {
                    LOGGER.error("No file to download. Server replied HTTP code: {} ", responseCode);
                }
                return Optional.empty();
            }

            String fileName = "";
            String disposition = httpConn.getHeaderField("Content-Disposition");
            String etag = httpConn.getHeaderField("ETag");
            String contentType = httpConn.getContentType();
            int contentLength = httpConn.getContentLength();

            // extract file name
            if (disposition != null) {
                // extract file name from header field
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 10, disposition.length() - 1);
                }
            } else {
                // extract file name from URL
                fileName = FilenameUtils.getName(url.getPath());
            }
            saveFileName = saveDir + File.separator + fileName;

            // log to check file information
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Content-Type = {}", contentType);
                LOGGER.info("Content-Disposition = {}", disposition);
                LOGGER.info("Content-Length = {}", contentLength);
                LOGGER.info("File name = {}", fileName);
                LOGGER.info("Save dir = {}", saveFileName);
                LOGGER.info(etag);
            }
            // check MD5 to validate the authenticity of the file
            File file = new File(saveFileName);
            if (file.exists() && checkMd5(file, etag)) {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("{} is downloaded and already exits.", fileName);
                }
                return Optional.of(saveFileName);
            }

            // write file to storage
            try (
                    // open input stream from the HTTP connection
                    InputStream inputStream = httpConn.getInputStream();
                    // open an output stream to save into file
                    FileOutputStream outputStream = new FileOutputStream(saveFileName);) {
                int bytesRead;
                byte[] buffer = new byte[BUFFER_SIZE];
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("{} downloaded.", fileName);
            }

        } catch (Exception e) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.error(e.getMessage());
                LOGGER.error("Failed to download resoure at URL {}", sourceUrl);
            }
            // Server error when downloading
            return Optional.empty();
        } finally {
            if (httpConn != null) {
                httpConn.disconnect();
            }
        }

        return Optional.of(saveFileName);
    }

    private static boolean checkMd5(File file, String etag) throws IOException {
        boolean result = false;
        if (file.exists() && !file.isDirectory()) {
            FileInputStream fis = new FileInputStream(file);
            String fileMD5 = DigestUtils.md5Hex(fis);
            if (fileMD5.equals(etag)) {
                result = true;
            }
        }
        return result;
    }


}
