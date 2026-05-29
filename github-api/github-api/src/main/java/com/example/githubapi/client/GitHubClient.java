package com.example.githubapi.client;

import com.example.githubapi.exception.GitHubUserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GitHubClient {

    private final RestClient restClient;

    public List<Map<String, Object>> getRepositories(String username) {

        try {

            List<Map<String, Object>> response = restClient.get()
                    .uri("/users/{username}/repos", username)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            return response != null ? response : List.of();

        } catch (RestClientResponseException ex) {

            if (ex.getStatusCode().value() == 404) {
                throw new GitHubUserNotFoundException("User not found");
            }

            throw ex;
        }
    }

    public List<Map<String, Object>> getBranches(String owner, String repo) {

        List<Map<String, Object>> response = restClient.get()
                .uri("/repos/{owner}/{repo}/branches", owner, repo)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        return response != null ? response : List.of();
    }
}