package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CheckoutService {

    Logger LOGGER = LoggerFactory.getLogger(CheckoutService.class);

    HttpClient client;
    String uri;

    public CheckoutService(String uri) {
        this.client = HttpClient.newHttpClient();
        this.uri = uri;
    }

    public PaymentMethod getPaymentMethod(String id) throws Exception {
        String endpoint = this.uri + "/paymentMethods/" + id;
        LOGGER.info(endpoint);

        var request = HttpRequest.newBuilder(URI.create(endpoint))
                .header("accept", "application/json").GET().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if(response.statusCode() != 200) {
            LOGGER.warn(response.body());
            throw new RuntimeException("An error has occurred. Http Status " + response.statusCode());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        PaymentMethod paymentMethod = objectMapper.readValue(response.body(), PaymentMethod.class);

        return paymentMethod;
    }
}
