package com.example.bookapp.controller;

import com.example.bookapp.dto.CreateBookRequest;
import com.example.bookapp.exception.PermissionDeniedException;
import com.example.bookapp.filter.UserAuthenticationDetails;
import com.example.bookapp.model.Book;
import com.example.bookapp.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BookControllerAuthorizationTest {

    private BookController bookController;
    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookService = mock(BookService.class);
        bookController = new BookController(bookService);
    }

    @Test
    void getAllBooks_returnsAllBooks() {
        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Book 1");
        book1.setOwnerId(1L);

        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Book 2");
        book2.setOwnerId(2L);

        when(bookService.getAllBooks()).thenReturn(List.of(book1, book2));

        List<Book> result = bookController.getAllBooks();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Book 1", result.get(0).getTitle());
        assertEquals("Book 2", result.get(1).getTitle());
        verify(bookService, times(1)).getAllBooks();
    }

    @Test
    void getBookById_returnsBook() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setOwnerId(1L);
        when(bookService.getBookById(1L)).thenReturn(Optional.of(book));

        Optional<Book> result = bookController.getBookById(1L);

        assertTrue(result.isPresent());
        assertEquals("Test Book", result.get().getTitle());
        assertEquals(1L, result.get().getOwnerId());
        verify(bookService, times(1)).getBookById(1L);
    }

    @Test
    void createBook_setsOwnerId() {
        SecurityContextHolder.getContext().setAuthentication(createAuthentication(1L));

        CreateBookRequest dto = new CreateBookRequest("New Book", "New Author", "123456");

        Book saved = new Book();
        saved.setTitle("New Book");
        saved.setOwnerId(1L);

        when(bookService.saveBook(any(Book.class))).thenReturn(saved);

        Book result = bookController.createBook(dto);

        assertNotNull(result);
        assertEquals("New Book", result.getTitle());
        verify(bookService, times(1)).saveBook(any(Book.class));
    }

    @Test
    void updateBook_ownerCanUpdate() {
        SecurityContextHolder.getContext().setAuthentication(createAuthentication(1L));

        Book existingBook = new Book();
        existingBook.setId(1L);
        existingBook.setTitle("Old Title");
        existingBook.setOwnerId(1L);

        Book updatedBook = new Book();
        updatedBook.setTitle("New Title");
        updatedBook.setAuthor("New Author");
        updatedBook.setIsbn("111111");

        when(bookService.getBookById(1L)).thenReturn(Optional.of(existingBook));
        when(bookService.saveBook(any(Book.class))).thenAnswer(invocation -> {
            Book b = invocation.getArgument(0);
            b.setId(existingBook.getId());
            return b;
        });

        Book result = bookController.updateBook(1L, updatedBook);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("New Title", result.getTitle());
        verify(bookService, times(1)).getBookById(1L);
        verify(bookService, times(1)).saveBook(any(Book.class));
    }

    @Test
    void updateBook_nonOwnerDenied() {
        SecurityContextHolder.getContext().setAuthentication(createAuthentication(99L));

        Book existingBook = new Book();
        existingBook.setId(1L);
        existingBook.setTitle("Owner Book");
        existingBook.setOwnerId(1L);

        when(bookService.getBookById(1L)).thenReturn(Optional.of(existingBook));

        PermissionDeniedException exception = assertThrows(PermissionDeniedException.class, () -> 
            bookController.updateBook(1L, new Book())
        );

        assertTrue(exception.getMessage().contains("permission"));
        verify(bookService, never()).saveBook(any(Book.class));
    }

    @Test
    void deleteBook_ownerCanDelete() {
        SecurityContextHolder.getContext().setAuthentication(createAuthentication(1L));

        Book existingBook = new Book();
        existingBook.setId(1L);
        existingBook.setOwnerId(1L);

        when(bookService.getBookById(1L)).thenReturn(Optional.of(existingBook));

        bookController.deleteBook(1L);

        verify(bookService, times(1)).deleteBook(1L);
    }

    @Test
    void deleteBook_nonOwnerDenied() {
        SecurityContextHolder.getContext().setAuthentication(createAuthentication(99L));

        Book existingBook = new Book();
        existingBook.setId(1L);
        existingBook.setOwnerId(1L);

        when(bookService.getBookById(1L)).thenReturn(Optional.of(existingBook));

        PermissionDeniedException exception = assertThrows(PermissionDeniedException.class, () -> 
            bookController.deleteBook(1L)
        );

        assertTrue(exception.getMessage().contains("permission"));
        verify(bookService, never()).deleteBook(1L);
    }

    private UsernamePasswordAuthenticationToken createAuthentication(Long userId) {
        UserAuthenticationDetails details = new UserAuthenticationDetails(userId);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("testuser", "credentials");
        token.setDetails(details);
        return token;
    }
}
