package com.example.githubapi.client;

import com.example.githubapi.exception.GitHubUserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GitHubClient {

    private final RestClient restClient;

    public List<Map<String, Object>> getRepositories(String username) {

        try {
            return restClient.get()
                    .uri("/users/{username}/repos", username)
                    .retrieve()
                    .body(List.class);

        } catch (HttpClientErrorException.NotFound ex) {
            throw new GitHubUserNotFoundException("User not found");
        }
    }

    public List<Map<String, Object>> getBranches(String owner, String repo) {

        return restClient.get()
                .uri("/repos/{owner}/{repo}/branches", owner, repo)
                .retrieve()
                .body(List.class);
    }
}