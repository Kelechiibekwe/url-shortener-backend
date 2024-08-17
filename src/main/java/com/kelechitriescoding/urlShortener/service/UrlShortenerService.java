package com.kelechitriescoding.urlShortener.service;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlShortenerService {

    @Value("${url-shortener.base-url}")
    private String baseUrl;

    private static final int SHORT_URL_LENGTH = 7;
    private static final BloomFilter<String> bloomFilter = BloomFilter.create(Funnels.stringFunnel(StandardCharsets.UTF_8), 1000000);
    private static final ConcurrentHashMap<String, String> urlDatabase = new ConcurrentHashMap<>();
    private static final String HASH_ALGORITHM = "SHA-1";

    private final RedisTemplate<String, String> redisTemplate;

    public String getLongUrl(String shortUrl) {
        return redisTemplate.opsForValue().get(shortUrl);
    }

    public String deleteShortUrl(String shortUrl){
        Boolean removed = redisTemplate.delete(shortUrl);
        return removed != null && removed ? "Url: " + shortUrl + " was found and removed" : "Url: " + shortUrl + " does not exist in the db";
    }

    public String generateUniqueShortUrl(String longUrl) {
        String shortUrl = createShortUrl(longUrl);

        if (urlDatabase.containsValue(longUrl)){
            return baseUrl + shortUrl;
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
        return bloomFilter.mightContain(shortUrl) && redisTemplate.opsForValue().get(shortUrl) != null;
    }

    private void saveShortUrl(String shortUrl, String longUrl) {
        bloomFilter.put(shortUrl);
        redisTemplate.opsForValue().set(shortUrl, longUrl, 1, TimeUnit.DAYS); // TTL can be adjusted as per requirements
    }

    private String generateRandomSuffix() {
        return Long.toHexString(Double.doubleToLongBits(Math.random()));
    }
}
