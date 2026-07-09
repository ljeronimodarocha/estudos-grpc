package com.example.bookapp.service;

import com.example.bookapp.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookService {
    Book saveBook(Book book);
    Optional<Book> getBookById(Long id);
    List<Book> getAllBooks();
    void deleteBook(Long id);
}
