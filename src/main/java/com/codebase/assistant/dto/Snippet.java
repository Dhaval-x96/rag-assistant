package com.codebase.assistant.dto;

public record Snippet(String file_path, int start_line, int end_line,
                      String code, double distance) {}
