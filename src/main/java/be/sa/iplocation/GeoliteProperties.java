package be.sa.iplocation;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@Data
@ConfigurationProperties(prefix = "application.geolite2")
public class GeoliteProperties {
    private String licenceKey;
    private String remoteUrl;
    private File tarballFile;
}
