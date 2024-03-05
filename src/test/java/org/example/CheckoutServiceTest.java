package org.example;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
public class CheckoutServiceTest {

    Logger LOGGER = LoggerFactory.getLogger(CheckoutServiceTest.class);

    CheckoutService checkoutService;

    // define path to OpenAPI file
    private final static String OPENAPI_FILE = "src/test/resources/checkout-basic.yaml";

    @ClassRule
    public static GenericContainer container = new GenericContainer(
            new ImageFromDockerfile("my-test-cont", false)
                    .withFileFromFile("openapi.yaml", new File(OPENAPI_FILE))
                    .withFileFromFile("Dockerfile", new File("Dockerfile"))
    )
            .withExposedPorts(8080);

    @Before
    public void setUp() {
        Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(LOGGER);
        container.followOutput(logConsumer);

        var uri = "http://" + container.getHost() + ":" + container.getMappedPort(8080);
        checkoutService = new CheckoutService(uri);
    }

    @Test
    public void getPaymentMethod() throws Exception {
        PaymentMethod paymentMethod = checkoutService.getPaymentMethod("googlepay");

        assertEquals("googlepay", paymentMethod.getName());
        assertEquals("wallet", paymentMethod.getType());
    }

}
