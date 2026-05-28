package com.example.githubapi.controller;

import com.example.githubapi.dto.RepositoryDto;
import com.example.githubapi.service.GitHubService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/github")
@RequiredArgsConstructor
public class GitHubController {

    private final GitHubService gitHubService;

    @GetMapping("/{username}")
    public List<RepositoryDto> getUserRepos(@PathVariable String username) {
        return gitHubService.getRepositories(username);
    }
}
