package com.kelechitriescoding.urlShortener.service;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class UrlShortenerService {

    @Value("${url-shortener.base-url}")
    private String baseUrl;

    private static final int SHORT_URL_LENGTH = 7;
    private static final BloomFilter<String> bloomFilter = BloomFilter.create(Funnels.stringFunnel(StandardCharsets.UTF_8), 1000000);
    private static final ConcurrentHashMap<String, String> urlDatabase = new ConcurrentHashMap<>();
    private static final String HASH_ALGORITHM = "SHA-1";

    public String getLongUrl(String shortUrl) {
        return urlDatabase.get(shortUrl);
    }

    public String deleteShortUrl(String shortUrl){
        if(urlDatabase.containsKey(shortUrl)){
            urlDatabase.remove(shortUrl);
            return "Url: " + shortUrl + " was found and removed";
        }
        return "Url: " + shortUrl + " does not exist in the db";
    }

    public String generateUniqueShortUrl(String longUrl) {
        String shortUrl = createShortUrl(longUrl);

        if (urlDatabase.containsValue(longUrl)){
            return baseUrl + getKeyForValue(urlDatabase,longUrl);
        }

        while (isCollision(shortUrl)) {
            longUrl += generateRandomSuffix();
            shortUrl = createShortUrl(longUrl);
        }

        saveShortUrl(shortUrl, longUrl);
        return baseUrl + shortUrl;
    }

    private String createShortUrl(String longUrl) {
        String hash = hashUrl(longUrl);
        return truncateHashToLength(hash, SHORT_URL_LENGTH);
    }

    private String hashUrl(String url) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hashedBytes = digest.digest(url.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            log.error("Hashing algorithm not found", e);
            throw new RuntimeException("Hashing algorithm not found", e);
        }
    }

    private String truncateHashToLength(String hash, int length) {
        return hash.substring(0, Math.min(length, hash.length()));
    }

    private boolean isCollision(String shortUrl) {
        return bloomFilter.mightContain(shortUrl) && urlDatabase.containsKey(shortUrl);
    }

    private void saveShortUrl(String shortUrl, String longUrl) {
        bloomFilter.put(shortUrl);
        urlDatabase.put(shortUrl, longUrl);
    }

    private String generateRandomSuffix() {
        return Long.toHexString(Double.doubleToLongBits(Math.random()));
    }

    public <K, V> K getKeyForValue(ConcurrentHashMap<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null; // Return null if value is not found
    }

}
