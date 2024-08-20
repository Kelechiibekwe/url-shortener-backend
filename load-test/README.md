# Load Testing Script for URL Shortener Service

## Overview

This load test is designed to evaluate the performance of the URL shortener service by simulating traffic from multiple users. The test ramps up the number of virtual users (VUs) over a period of time, maintains the load for a duration, and then ramps down. The main metric of interest is the response time, ensuring that 99% of the requests are handled within 150 milliseconds.

### Test Script Breakdown

- **Script Location**: `load-tests/k6-load-test.js`
- **Test Purpose**: To assess the system's ability to handle a steady load of traffic and measure the response time under stress.
- **Simulated Scenario**:
    - **Ramp-Up**: Gradually increase from 1 to 100 virtual users over 5 minutes.
    - **Steady-State**: Maintain 100 virtual users for 10 minutes.
    - **Ramp-Down**: Gradually decrease from 100 to 0 users over 5 minutes.

### Test Configuration

- **TLS Verification**: Skipped for simplicity (`insecureSkipTLSVerify: true`).
- **Connection Reuse**: Disabled to simulate a more realistic, albeit resource-intensive, scenario (`noConnectionReuse: false`).
- **Thresholds**: The test ensures that 99% of requests have a response time of less than 150 milliseconds.

### How the Script Works

1. **Dynamic URL Generation**:
    - The script generates unique URLs for each request using the `uuidv4` function. This prevents repeated requests and simulates real-world usage where each user shortens a different URL.

2. **HTTP Request**:
    - The script sends a POST request to the URL shortener service at `http://localhost:8080/api/v1/data/shorten` with a dynamically generated URL in the payload.

3. **User Think Time**:
    - A `sleep(1)` function is added to simulate a brief pause (1 second) between consecutive requests, mimicking realistic user behavior.


### Test Results

The load test was executed on the URL shortener service, and the results demonstrated that the service handled the simulated traffic effectively:

- **Throughput**: The system processed approximately 74.32 requests per second during the test.
- **Response Time**: 99% of the requests were completed in under 150 milliseconds, meeting the performance threshold set for the test.
- **Error Rate**: There were no errors recorded during the test, indicating that the service was able to handle the load without any failures.

A screenshot of the test results is included in the repository and can be found in the `load-tests` directory (`load-tests/test-results-screenshot.png`). This screenshot visually summarizes the key performance metrics collected during the test.
