package com.example.bookapp.service;

import com.example.bookapp.model.Book;
import com.example.bookapp.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BookServiceImplTest {

    private BookServiceImpl bookService;

    @Mock
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bookService = new BookServiceImpl(bookRepository);
    }

    @Test
    void saveBook_callsRepositorySave() {
        // Arrange
        Book book = new Book();
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setIsbn("123456");

        // Act
        bookService.saveBook(book);

        // Assert
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void getBookById_returnsBookWhenFound() {
        // Arrange
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        // Act
        Optional<Book> result = bookService.getBookById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("Test Book", result.get().getTitle());
    }

    @Test
    void getBookById_returnsEmptyWhenNotFound() {
        // Arrange
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Book> result = bookService.getBookById(999L);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void getAllBooks_returnsAllBooks() {
        // Arrange
        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Book 1");

        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Book 2");

        when(bookRepository.findAll()).thenReturn(List.of(book1, book2));

        // Act
        List<Book> result = bookService.getAllBooks();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Book 1", result.get(0).getTitle());
        assertEquals("Book 2", result.get(1).getTitle());
    }

    @Test
    void deleteBook_deletesBookById() {
        // Arrange
        Long bookId = 1L;

        // Act
        bookService.deleteBook(bookId);

        // Assert
        verify(bookRepository, times(1)).deleteById(bookId);
    }
}
