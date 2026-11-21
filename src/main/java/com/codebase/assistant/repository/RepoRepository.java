package com.codebase.assistant.repository;

import com.codebase.assistant.model.Repo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RepoRepository extends JpaRepository<Repo, UUID> {
}
