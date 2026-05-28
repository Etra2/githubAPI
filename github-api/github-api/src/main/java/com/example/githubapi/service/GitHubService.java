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
            if (fork != null && fork) continue;

            String repoName = (String) repo.get("name");

            Map<String, Object> owner = (Map<String, Object>) repo.get("owner");
            String ownerLogin = (String) owner.get("login");

            List<Map<String, Object>> branchesRaw =
                    gitHubClient.getBranches(ownerLogin, repoName);

            List<BranchDto> branches = new ArrayList<>();

            for (Map<String, Object> branch : branchesRaw) {

                String branchName = (String) branch.get("name");

                Map<String, Object> commit =
                        (Map<String, Object>) branch.get("commit");

                String sha = (String) commit.get("sha");

                branches.add(new BranchDto(branchName, sha));
            }

            result.add(new RepositoryDto(repoName, ownerLogin, branches));
        }

        return result;
    }
}
