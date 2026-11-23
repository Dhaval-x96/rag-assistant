package com.codebase.assistant.controller;

import com.codebase.assistant.dto.AskRequest;
import com.codebase.assistant.dto.AskResponse;
import com.codebase.assistant.dto.CreateRepoRequest;
import com.codebase.assistant.dto.RepoResponse;
import com.codebase.assistant.service.RepoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class RepoController {

    private final RepoService repoService;

    public RepoController(RepoService repoService) {
        this.repoService = repoService;
    }

    @PostMapping("/repos")
    public ResponseEntity<RepoResponse> createRepo(@RequestBody CreateRepoRequest request){
        return new ResponseEntity<>(this.repoService.createRepo(request), HttpStatus.CREATED);
    }

    @PostMapping("/repos/{id}/ask")
    public ResponseEntity<AskResponse> askQuestion(@PathVariable UUID id, @RequestBody AskRequest request){
        return new ResponseEntity<>(this.repoService.askResponses(id,request), HttpStatus.OK);
    }

    @GetMapping("/repos/{id}")
    public ResponseEntity<RepoResponse> getRepo(@PathVariable UUID id){
        return new ResponseEntity<>(this.repoService.getRepo(id), HttpStatus.OK);
    }

    @GetMapping("/repos")
    public ResponseEntity<List<RepoResponse>> getAllRepo(){
        return new ResponseEntity<>(this.repoService.getAllRepo(), HttpStatus.OK);
    }
}
