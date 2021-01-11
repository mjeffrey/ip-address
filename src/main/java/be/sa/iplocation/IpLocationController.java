package be.sa.iplocation;

import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Location;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Slf4j
public class IpLocationController {
    private final GeoliteDatabaseService locationService;

    @SneakyThrows
    @GetMapping(value= "ip")
    public IpLocation getLocation(@RequestParam("address") String addressString,
                                                  @RequestParam(value = "lang", defaultValue = "en") String language) {

        CityResponse cityResponse = locationService.getCity(addressString);

        Country country = cityResponse.getCountry();
        String countryName = country.getNames().get(language);
        if (countryName == null){
            countryName = country.getName();
        }

        City city = cityResponse.getCity();
        String cityName = city.getNames().get(language);
        if (cityName == null){
            cityName = city.getName();
        }
        Location location = cityResponse.getLocation();
        IpLocation ipLocation = IpLocation.builder()
                .isoCountryCode(country.getIsoCode())
                .country( countryName)
                .city(cityName)
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .build();
        return ipLocation;
    }

}
