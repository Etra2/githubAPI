package com.example.githubapi.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class GitHubIntegrationTest {

    private static WireMockServer wireMockServer;

    private final RestTemplate restTemplate = new RestTemplate();

    @BeforeAll
    static void startWireMock() {

        wireMockServer = new WireMockServer(8089);

        wireMockServer.start();

        configureFor("localhost", 8089);
    }

    @AfterAll
    static void stopWireMock() {

        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @BeforeEach
    void resetMocks() {

        wireMockServer.resetAll();
    }

    @Test
    void shouldReturnOnlyNonForkRepositoriesWithBranches() {

        stubFor(get(urlEqualTo("/users/octocat/repos"))
                .willReturn(okJson("""
                        [
                          {
                            "name": "repo1",
                            "fork": false,
                            "owner": {
                              "login": "octocat"
                            }
                          },
                          {
                            "name": "fork-repo",
                            "fork": true,
                            "owner": {
                              "login": "octocat"
                            }
                          }
                        ]
                        """)));

        stubFor(get(urlEqualTo("/repos/octocat/repo1/branches"))
                .willReturn(okJson("""
                        [
                          {
                            "name": "main",
                            "commit": {
                              "sha": "abc123"
                            }
                          }
                        ]
                        """)));

        var response = restTemplate.getForEntity(
                "http://localhost:8080/api/github/octocat",
                String.class
        );

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        String body = response.getBody();

        Assertions.assertNotNull(body);

        Assertions.assertTrue(body.contains("repo1"));

        Assertions.assertFalse(body.contains("fork-repo"));

        Assertions.assertTrue(body.contains("main"));

        Assertions.assertTrue(body.contains("abc123"));
    }

    @Test
    void shouldReturn404WhenUserDoesNotExist() {

        stubFor(get(urlEqualTo("/users/unknown/repos"))
                .willReturn(aResponse().withStatus(404)));

        HttpClientErrorException exception =
                Assertions.assertThrows(
                        HttpClientErrorException.class,
                        () -> restTemplate.getForEntity(
                                "http://localhost:8080/api/github/unknown",
                                String.class
                        )
                );

        Assertions.assertEquals(
                HttpStatus.NOT_FOUND,
                exception.getStatusCode()
        );

        Assertions.assertTrue(
                exception.getResponseBodyAsString()
                        .contains("User not found")
        );
    }
}