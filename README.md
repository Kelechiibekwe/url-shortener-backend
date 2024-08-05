# URL Shortener Service

## Overview

This URL shortener service is built using Spring Boot. It offers the ability to shorten long URLs, retrieve the original URLs from their short counterparts, and delete short URLs. The service also handles URL redirection and employs a Bloom Filter to efficiently manage and detect collisions.

## Features

- **URL Shortening:** Convert long URLs into shorter, manageable URLs.
- **Redirection:** Redirect from short URLs to their corresponding long URLs.
- **Deletion:** Remove short URLs from the system.
- **Collision Handling:** Uses a Bloom Filter to manage potential hash collisions.
- **Configurable Base URL:** Base URL for short links can be configured via application properties.

## Technologies

- **Java 17**
- **Spring Boot 3.x**
- **Guava Bloom Filter**
- **ConcurrentHashMap**
- **SHA-1 for Hashing**
- **Base64 Encoding**

## Prerequisites

- JDK 17
- Maven 3.x


## How It Works
- Shortening: A long URL is hashed using SHA-1, then Base64 encoded, and truncated to a fixed length of 7 characters. A Bloom Filter is used to check for collisions, and a random suffix is appended if necessary.
- Redirection: Short URLs are resolved to their long counterparts and redirected.
Deletion: Short URLs can be removed from the system, freeing up space.