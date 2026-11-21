package com.codebase.assistant.service;

import com.codebase.assistant.dto.CreateRepoRequest;
import com.codebase.assistant.dto.RepoResponse;
import com.codebase.assistant.enums.RepoStatus;
import com.codebase.assistant.model.Repo;
import com.codebase.assistant.repository.RepoRepository;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class RepoService {

    private final RepoRepository repoRepository;

    public RepoService(RepoRepository repoRepository) {
        this.repoRepository = repoRepository;
    }

    public RepoResponse createRepo(CreateRepoRequest request) {
        Repo repo = new Repo();
        repo.setGitUrl(request.gitUrl());
        repo.setRepoStatus(RepoStatus.CODEBLOCK);
        Repo saved = repoRepository.save(repo);
        return buildRepoResponse(saved);
    }

    public RepoResponse getRepo(UUID id) {
        Repo repo = repoRepository.findById(id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatusCode.valueOf(404)));
        return buildRepoResponse(repo);
    }

    public List<RepoResponse> getAllRepo() {
        return repoRepository.findAll().stream().map(this::buildRepoResponse).toList();
    }

    private RepoResponse buildRepoResponse(Repo repo){
        return new RepoResponse(
                repo.getRepoId(),
                repo.getGitUrl(),
                repo.getRepoStatus(),
                repo.getCreatedAt());
    }
}
