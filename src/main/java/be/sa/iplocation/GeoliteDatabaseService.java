package be.sa.iplocation;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
@AllArgsConstructor
public class GeoliteDatabaseService {

    private static final AtomicReference<DatabaseReader> databaseReaderHolder = new AtomicReference<>();
    private GeoliteReaderService geoliteReaderService;

    @SneakyThrows
    @PostConstruct
    void initializeDatabaseReader(){
        geoliteReaderService.downloadIfNotExists();
        databaseReaderHolder.set(geoliteReaderService.getDatabaseReader());
    }

    @SneakyThrows
    @Scheduled( cron = "5 23 1 * * *") // first of each month at 23:05
    public void refresh() {
        databaseReaderHolder.set(geoliteReaderService.refresh());
    }

    public CityResponse getCity(String addressString) throws IOException, GeoIp2Exception {
        InetAddress ipAddress = InetAddress.getByName(addressString);
        final DatabaseReader databaseReader = databaseReaderHolder.get();
        CityResponse cityResponse = databaseReader.city(ipAddress);
        log.debug("Json response: {}", cityResponse.toJson());
        return cityResponse;
    }

 }
