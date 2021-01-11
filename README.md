# Used to lookup the location of an IP address for checking security


# Getting Started
Obtain a free licence key for the data from: https://www.maxmind.com/en/accounts/current/license-key
Note it is up to you to comply with max mind licencing and privacy conditions when using this data.

Build and then run the application providing the key.
e.g:
```
./mvnw spring-boot:run -Dspring-boot.run.arguments="--licence-key=bf8D....."
```

Try some IP lookups
```
curl -s 'http://localhost:8080/ip?address=81.244.15.207&lang=nl' | jq .
```

(DNS name also works but is less useful)
```
curl -s 'http://localhost:8080/ip?address=unifiedpost.com' | jq .
```


