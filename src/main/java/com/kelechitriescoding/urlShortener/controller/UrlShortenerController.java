package com.kelechitriescoding.urlShortener.controller;

import com.kelechitriescoding.urlShortener.request.LongUrlRequest;
import com.kelechitriescoding.urlShortener.service.UrlShortenerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@RestController
@AllArgsConstructor
public class UrlShortenerController {

    private final UrlShortenerService urlShortenerService;

    @GetMapping("/{shortUrl}")
    public RedirectView redirectToLongUrl(@PathVariable String shortUrl) {
        String longUrl = urlShortenerService.getLongUrl(shortUrl);

        if (longUrl == null) {
            // Handle case where the short URL does not exist
            return new RedirectView("/error"); // Redirect to an error page or handle it appropriately
        }

        return new RedirectView(longUrl);
    }

    @PostMapping(path = "/api/v1/data/shorten")
    public ResponseEntity<?> shortenUrl(@RequestBody LongUrlRequest longUrl) {
        String longUrlString = longUrl.longUrlString();
        String shortUrl = urlShortenerService.generateUniqueShortUrl(longUrlString);
        return ResponseEntity.ok(shortUrl);
    }

    @DeleteMapping("/{shortUrl}")
    public ResponseEntity<?> deleteShortUrl(@PathVariable ("shortUrl") String shortUrlString){
        String response = urlShortenerService.deleteShortUrl(shortUrlString);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }



}
