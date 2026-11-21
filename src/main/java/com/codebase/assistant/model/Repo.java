package com.codebase.assistant.model;

import com.codebase.assistant.enums.RepoStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "repo", schema = "rag")
public class Repo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "repo_id")
    private UUID repoId;

    @Column(name = "git_url", nullable = false)
    private String gitUrl;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private RepoStatus repoStatus;

    // Hibernate sets the time on insert, so NULL is never sent to the NOT NULL column.
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public UUID getRepoId() {
        return repoId;
    }

    public void setRepoId(UUID repoId) {
        this.repoId = repoId;
    }

    public String getGitUrl() {
        return gitUrl;
    }

    public void setGitUrl(String gitUrl) {
        this.gitUrl = gitUrl;
    }

    public RepoStatus getRepoStatus() {
        return repoStatus;
    }

    public void setRepoStatus(RepoStatus repoStatus) {
        this.repoStatus = repoStatus;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
