package com.codebase.assistant.dto;

import com.codebase.assistant.enums.RepoStatus;

import java.time.Instant;
import java.util.UUID;

public record RepoResponse(UUID repoId, String gitUrl,
                           RepoStatus status, Instant createdAt) {
}
