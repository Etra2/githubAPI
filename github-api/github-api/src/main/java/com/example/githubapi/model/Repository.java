package com.example.githubapi.model;

import java.util.List;

public record Repository(
        String name,
        String ownerLogin,
        boolean fork,
        List<Branch> branches
) {}
