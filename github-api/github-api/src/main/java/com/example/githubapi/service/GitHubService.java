package com.example.githubapi.service;

import com.example.githubapi.client.GitHubClient;
import com.example.githubapi.dto.BranchDto;
import com.example.githubapi.dto.RepositoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GitHubService {

    private final GitHubClient gitHubClient;

    public List<RepositoryDto> getRepositories(String username) {

        List<Map<String, Object>> repos = gitHubClient.getRepositories(username);

        List<RepositoryDto> result = new ArrayList<>();

        for (Map<String, Object> repo : repos) {

            Boolean fork = (Boolean) repo.get("fork");
            if (Boolean.TRUE.equals(fork)) {
                continue;
            }

            String repoName = (String) repo.get("name");

            Map<String, Object> owner = (Map<String, Object>) repo.get("owner");
            if (owner == null || repoName == null) {
                continue;
            }

            String ownerLogin = (String) owner.get("login");
            if (ownerLogin == null) {
                continue;
            }

            List<Map<String, Object>> branchesRaw =
                    gitHubClient.getBranches(ownerLogin, repoName);

            List<BranchDto> branches = new ArrayList<>();

            for (Map<String, Object> branch : branchesRaw) {

                String branchName = (String) branch.get("name");

                Map<String, Object> commit =
                        (Map<String, Object>) branch.get("commit");

                String sha = commit != null ? (String) commit.get("sha") : null;

                if (branchName != null && sha != null) {
                    branches.add(new BranchDto(branchName, sha));
                }
            }

            result.add(new RepositoryDto(repoName, ownerLogin, branches));
        }

        return result;
    }
}