package com.codebase.assistant.dto;

import java.util.List;

public record AskResponse(String question, String answer, List<Snippet> snippets) {}