package com.example.githubapi.dto;

public record BranchDto(
        String name,
        String lastCommitSha
) {}
