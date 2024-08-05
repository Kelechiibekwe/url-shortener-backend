URL Shortener Service
Overview
This is a URL shortener service built with Spring Boot. It provides functionality for shortening long URLs, retrieving the original URLs from short ones, and deleting short URLs. The service also handles URL redirection and utilizes a Bloom Filter for collision detection.

Features
URL Shortening: Convert long URLs into short, manageable URLs.
Redirection: Redirect short URLs to their corresponding long URLs.
Deletion: Remove short URLs from the service.
Collision Handling: Uses a Bloom Filter to handle potential collisions.
Configurable Base URL: The base URL for short links is configurable via application properties.
Technologies
Java 17
Spring Boot 3.x
Guava Bloom Filter
ConcurrentHashMap
SHA-1 for Hashing
Base64 Encoding
Prerequisites
JDK 17
Maven 3.x
