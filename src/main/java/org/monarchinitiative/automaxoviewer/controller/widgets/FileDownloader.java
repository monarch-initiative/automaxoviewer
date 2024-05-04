package org.monarchinitiative.automaxoviewer.controller.widgets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Set;


/**
 * Helper class for downloading files over HTTP and FTP.
 * <p>
 * The implementation of FTP downloads is more complex since we need passive FTP transfer through firewalls. This is not
 * possible when just opening a stream through an {@link URL} object with Java's builtin features.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public class FileDownloader {
    private static final Logger logger = LoggerFactory.getLogger(FileDownloader.class);

    static class ProxyOptions {
        String host = null;
        int port = -1;
        String user = null;
        String password = null;
    }

    /**
     * Configuration for the {@link FileDownloader}.
     */
    static class Options {
        boolean printProgressBar = false;
        ProxyOptions http = new ProxyOptions();
        ProxyOptions https = new ProxyOptions();
        ProxyOptions ftp = new ProxyOptions();
    }

    /**
     * configuration for the downloader
     */
    private final Options options;

    /**
     * Downloader with default options
     */
    public FileDownloader() {
        this(new Options());
    }

    /**
     * Initializer FileDownloader with the given options string
     */
    FileDownloader(Options options) {
        this.options = options;
    }


    /**
     * This method downloads a file to the specified local file path. If the file already exists, it will
     * overwrite it and emit a warning.
     *
     * @param src  {@link URL} with file to download
     * @param dest {@link File} with destination path
     * @return File file downloaded
     */
    public File copyURLToFile(URL src, File dest)  {
        if (dest.exists()) {
            logger.warn("Overwriting file at {}", dest);
        }
        logger.trace("copyURLToFile dest={}", dest);
        logger.trace("dest.getParentFile()={}", dest.getParentFile());
        if (!dest.getParentFile().exists()) {
            logger.info("Creating directory {}", dest.getParentFile().getAbsolutePath());
            dest.getParentFile().mkdirs();
        }

        return copyURLToFileThroughURL(src, dest);
    }

    /**
     * Copy contents of a URL to a file using the {@link URL} class.
     * <p>
     * This works for the HTTP and the HTTPS protocol and for FTP through a proxy. For plain FTP, we need to use the
     * passive mode.
     */
    private File copyURLToFileThroughURL(URL src, File dest)  {
        setProxyProperties();

        // actually copy the file
        BufferedInputStream in;
        FileOutputStream out;
        try {
            int connectionTimeout = 5000; // 5 seconds should be more than enough to connect to a server
            final String TEXTPLAIN_REQUEST_TYPE = ", text/plain; q=0.1";
            URLConnection connection = connect(src.openConnection(), connectionTimeout, TEXTPLAIN_REQUEST_TYPE, new HashSet<>());
            final int fileSize = connection.getContentLength();
            in = new BufferedInputStream(connection.getInputStream());
            out = new FileOutputStream(dest);

            ProgressBar pb = null;
            if (fileSize != -1)
                pb = new ProgressBar(0, fileSize, options.printProgressBar);
            else
                logger.info("(server did not tell us the file size, no progress bar)");

            // Download file.
            byte[] buffer = new byte[128 * 1024];
            int readCount;
            long pos = 0;
            if (pb != null)
                pb.print(pos);

            while ((readCount = in.read(buffer)) > 0) {
                out.write(buffer, 0, readCount);
                pos += readCount;
                if (pb != null)
                    pb.print(pos);
            }
            in.close();
            out.close();
            if (pb != null && pos != pb.getMax())
                pb.print(fileSize);
            // return file
            return dest;
        } catch (IOException | IllegalStateException e) {
            logger.error(String.format("Failed to downloaded file from %s", src.getHost()), e);
            throw new RuntimeException("ERROR: Problem downloading file: " + e.getMessage());
        }
    }


    protected static URLConnection connect(URLConnection conn, int connectionTimeout, String acceptHeaders, Set<String> visited)
            throws IOException {
        if (conn instanceof HttpURLConnection) {
            // follow redirects to HTTPS
            HttpURLConnection con = (HttpURLConnection) conn;
            // we set the Accept encoding property to empty to force the download of files unzipped
            conn.setRequestProperty("Accept-Encoding", "");
            con.connect();
            int responseCode = con.getResponseCode();
            // redirect
            if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP
                    || responseCode == HttpURLConnection.HTTP_MOVED_PERM
                    || responseCode == HttpURLConnection.HTTP_SEE_OTHER
                    // no constants for temporary and permanent redirect in HttpURLConnection
                    || responseCode == 307 || responseCode == 308) {
                String location = con.getHeaderField("Location");
                if (visited.add(location)) {
                    URL newURL = new URL(location);
                    return connect(rebuildConnection(connectionTimeout, newURL, acceptHeaders),
                            connectionTimeout, acceptHeaders, visited);
                } else {
                    throw new IllegalStateException(
                            "Infinite loop: redirect cycle detected. " + visited);
                }
            }
        }
        return conn;
    }

    protected static URLConnection rebuildConnection(int connectionTimeout, URL newURL, String acceptHeaders) throws IOException {
        URLConnection conn;
        conn = newURL.openConnection();
        final String ACCEPTABLE_CONTENT_ENCODING = "xz,gzip,deflate";
        conn.addRequestProperty("Accept", acceptHeaders);
        conn.setRequestProperty("Accept-Encoding", ACCEPTABLE_CONTENT_ENCODING);
        conn.setConnectTimeout(connectionTimeout);
        return conn;
    }


    /**
     * Set system properties from {@link #options}.
     */
    private void setProxyProperties() {
        if (options.ftp.host != null)
            System.setProperty("ftp.proxyHost", options.ftp.host);
        if (options.ftp.port != -1)
            System.setProperty("ftp.proxyPort", Integer.toString(options.ftp.port));
        if (options.ftp.user != null)
            System.setProperty("ftp.proxyUser", options.ftp.user);
        if (options.ftp.password != null)
            System.setProperty("ftp.proxyPassword", options.ftp.password);

        if (options.http.host != null)
            System.setProperty("http.proxyHost", options.http.host);
        if (options.http.port != -1)
            System.setProperty("http.proxyPort", Integer.toString(options.http.port));
        if (options.http.user != null)
            System.setProperty("http.proxyUser", options.http.user);
        if (options.http.password != null)
            System.setProperty("http.proxyPassword", options.http.password);

        if (options.https.host != null)
            System.setProperty("https.proxyHost", options.https.host);
        if (options.https.port != -1)
            System.setProperty("https.proxyPort", Integer.toString(options.https.port));
        if (options.https.user != null)
            System.setProperty("https.proxyUser", options.https.user);
        if (options.https.password != null)
            System.setProperty("https.proxyPassword", options.https.password);
    }

}
