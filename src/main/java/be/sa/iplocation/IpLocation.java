package be.sa.iplocation;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class IpLocation {
    String city;
    String isoCountryCode;
    String country;
    double latitude;
    double longitude;
}
