package com.example.bookapp.controller;

import com.example.bookapp.dto.CreateBookRequest;
import com.example.bookapp.exception.PermissionDeniedException;
import com.example.bookapp.filter.UserAuthenticationDetails;
import com.example.bookapp.model.Book;
import com.example.bookapp.service.BookService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
@Validated
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/{id}")
    public Optional<Book> getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @PostMapping
    public Book createBook(@Valid @RequestBody CreateBookRequest dto) {
        Long currentUserId = extractUserId();
        Book book = new Book();
        book.setTitle(dto.title());
        book.setAuthor(dto.author());
        book.setIsbn(dto.isbn());
        book.setOwnerId(currentUserId);
        return bookService.saveBook(book);
    }

    @PutMapping("/{id}")
    public Book updateBook(@PathVariable Long id, @RequestBody Book bookDetails) {
        Long currentUserId = extractUserId();
        Book book = bookService.getBookById(id).orElseThrow(() -> new RuntimeException("Book not found"));
        if (!book.getOwnerId().equals(currentUserId)) {
            throw new PermissionDeniedException("You do not have permission to update this book");
        }
        book.setTitle(bookDetails.getTitle());
        book.setAuthor(bookDetails.getAuthor());
        book.setIsbn(bookDetails.getIsbn());
        return bookService.saveBook(book);
    }

    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        Long currentUserId = extractUserId();
        Book book = bookService.getBookById(id).orElseThrow(() -> new RuntimeException("Book not found"));
        if (!book.getOwnerId().equals(currentUserId)) {
            throw new PermissionDeniedException("You do not have permission to delete this book");
        }
        bookService.deleteBook(id);
    }

    private Long extractUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getDetails() instanceof UserAuthenticationDetails details) {
            return details.getUserId();
        }
        throw new RuntimeException("Unable to extract user ID from authentication");
    }
}
