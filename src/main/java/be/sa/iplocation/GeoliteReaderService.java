package be.sa.iplocation;

import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.file.Files;

@Service
@AllArgsConstructor
@Slf4j
public class GeoliteReaderService {

    private GeoliteProperties properties;

    @SneakyThrows
    public DatabaseReader refresh() {
        downloadTarBall();
        return getDatabaseReader();
    }

    public void downloadTarBall() throws IOException {
        RestTemplate restTemplate = new RestTemplate();

        File tarballTempFile = getTempFile();
        log.info("Attempting download to {}", tarballTempFile.getAbsolutePath() );
        try (FileOutputStream outputStream = new FileOutputStream(tarballTempFile)) {
            restTemplate.execute(properties.getRemoteUrl(), HttpMethod.GET, null, clientHttpResponse -> {
                final InputStream responseBody = clientHttpResponse.getBody();
                StreamUtils.copy(responseBody, outputStream);
                return tarballTempFile;
            });
        }
        FileUtils.moveFile(tarballTempFile, properties.getTarballFile());
        log.info("Downloaded to {}", properties.getTarballFile().getAbsolutePath() );
    }

    public DatabaseReader getDatabaseReader() throws IOException {
        File tarball = properties.getTarballFile();
        log.info("Reading from {}, size {} bytes", tarball.getAbsolutePath(),  Files.size(tarball.toPath()));
        try (TarArchiveInputStream tarInput = new TarArchiveInputStream(new GzipCompressorInputStream(new FileInputStream(tarball)))) {
            TarArchiveEntry currentEntry = tarInput.getNextTarEntry();
            while (currentEntry != null) {
                if ( currentEntry.getName().endsWith(".mmdb") ){
                    log.info("File in tar {}", currentEntry.getName());
                    return new DatabaseReader.Builder(tarInput).withCache(new CHMCache()).build();
                }
                else {
                    currentEntry = tarInput.getNextTarEntry();
                }
            }
        }
        throw new IllegalStateException("Cannot read any *.mmdb file from tarball: " + tarball.getAbsolutePath());
    }

    void downloadIfNotExists() throws IOException {
        File tarball = properties.getTarballFile();
        if (!tarball.exists()){
            downloadTarBall();
        }
    }

    @SneakyThrows
    private File getTempFile() {
        File tarball = properties.getTarballFile();
        File tarballTempFile = new File( tarball.getParentFile(), tarball.getName() + ".tmp" );
        FileUtils.forceMkdirParent(tarballTempFile);
        return tarballTempFile;
    }

}
