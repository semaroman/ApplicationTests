package org.example;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ApplicationTests {
    @Autowired
    TestRestTemplate restTemplate;

    @Container
    private static final GenericContainer<?> devapp = new GenericContainer<>("devapp")
            .withExposedPorts(8080);
    @Container
    private static final GenericContainer<?> prodapp = new GenericContainer<>("prodapp")
            .withExposedPorts(8081);

    @BeforeAll
    void setUp() {
        devapp.start();
        prodapp.start();
    }

    @Test
    void testDev() {
        Integer devappPort = devapp.getMappedPort(8080);
        ResponseEntity<String> devappEntity = restTemplate.getForEntity(
                "http://localhost:" + devappPort + "/profile", String.class);
        assertEquals(devappEntity.getBody(), "Current profile is dev");
    }

    @Test
    void testProd() {
        Integer prodappPort = prodapp.getMappedPort(8081);
        ResponseEntity<String> prodappEntity = restTemplate.getForEntity(
                "http://localhost:" + prodappPort + "/profile", String.class);
        assertEquals(prodappEntity.getBody(), "Current profile is production");
    }
}