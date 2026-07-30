package com.example.bookapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateBookRequest(
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must be 255 characters or less")
    String title,

    @NotBlank(message = "Author is required")
    @Size(max = 255, message = "Author must be 255 characters or less")
    String author,

    @NotBlank(message = "ISBN is required")
    @Size(max = 13, message = "ISBN must be 13 characters")
    String isbn
) {}
