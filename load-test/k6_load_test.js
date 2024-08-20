import http from 'k6/http';
import { sleep } from 'k6';
import { uuidv4 } from 'https://jslib.k6.io/k6-utils/1.4.0/index.js'; // A utility to generate unique identifiers

/**
 * This load test is primarily concerned with assessing the current performance of the system
 */

export let options = {
    insecureSkipTLSVerify: true,
    noConnectionReuse: false,
    stages: [
        {duration: '5m', target: 100}, // simulate ramp up of traffic from one to 100 users over 5mins
        {duration: '10m', target: 100}, // stay at 100 users for 10 minutes
        {duration: '5m', target: 0}// ramp down to 0 users
    ],
    thresholds:{
        http_req_duration: ['p(99)<150'],  // Ensure 99% of requests are faster than 150ms
    },
};

// Function to generate a dynamic long URL string
function generateDynamicUrl() {
    return `https://example.com/${uuidv4()}`;
}

export default () => {
    let url = 'http://localhost:8080/api/v1/data/shorten';
    let payload = JSON.stringify({
        "longUrlString": generateDynamicUrl()  // Use the dynamic URL generator here
    });

    let params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    http.post(url, payload, params);
    sleep(1);  // Add sleep to simulate user think time
};
