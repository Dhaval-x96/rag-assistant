package com.codebase.assistant.service;

import com.codebase.assistant.dto.AskRequest;
import com.codebase.assistant.dto.AskResponse;
import com.codebase.assistant.dto.CreateRepoRequest;
import com.codebase.assistant.dto.IndexerResponse;
import com.codebase.assistant.dto.RepoResponse;
import com.codebase.assistant.enums.RepoStatus;
import com.codebase.assistant.model.Repo;
import com.codebase.assistant.repository.RepoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class RepoService {

    private final RepoRepository repoRepository;
    private final RestClient restClient;


    public RepoService(RepoRepository repoRepository, @Value("${indexer.base-url}") String baseUrl) {
        this.repoRepository = repoRepository;
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public RepoResponse createRepo(CreateRepoRequest request) {
        Repo repo = new Repo();
        repo.setGitUrl(request.gitUrl());
        repo.setRepoStatus(RepoStatus.QUEUED);
        Repo saved = repoRepository.save(repo);
        try {
            IndexerResponse response = startIndexing(saved.getRepoId());
            if (response == null || !"accepted".equals(response.status())) {
                saved = markFailed(saved);
            }
        } catch (RestClientException e) {
            saved = markFailed(saved);
        }

        return buildRepoResponse(saved);
    }

    public AskResponse askResponses(UUID uuid, AskRequest request) {
        Repo repo = repoRepository.findById(uuid).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Repo not found"));

        if (repo.getRepoStatus() != RepoStatus.DONE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Repo is not indexed yet");
        }

        return restClient.post()
                .uri("/repo/{id}/ask", uuid)
                .body(request)
                .retrieve()
                .body(AskResponse.class);
    }

    public RepoResponse getRepo(UUID id) {
        Repo repo = repoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404)));
        return buildRepoResponse(repo);
    }

    public List<RepoResponse> getAllRepo() {
        return repoRepository.findAll().stream().map(this::buildRepoResponse).toList();
    }

    private Repo markFailed(Repo repo) {
        repo.setRepoStatus(RepoStatus.FAILED);
        return repoRepository.save(repo);
    }

    private RepoResponse buildRepoResponse(Repo repo) {
        return new RepoResponse(
                repo.getRepoId(),
                repo.getGitUrl(),
                repo.getRepoStatus(),
                repo.getCreatedAt());
    }

    private IndexerResponse startIndexing(UUID uuid) {
        return this.restClient.post()
                .uri("/index/{id}", uuid)
                .retrieve().body(IndexerResponse.class);
    }
}
