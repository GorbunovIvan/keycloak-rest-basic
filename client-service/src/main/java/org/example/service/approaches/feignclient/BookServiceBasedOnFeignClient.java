package org.example.service.approaches.feignclient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.Book;
import org.example.service.BookService;
import org.example.service.approaches.feignclient.feigns.BookServiceFeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Primary
@RequiredArgsConstructor
@Slf4j
public class BookServiceBasedOnFeignClient implements BookService {

    private final BookServiceFeignClient bookServiceFeignClient;

    public List<Book> getAll() {

        ResponseEntity<List<Book>> response = bookServiceFeignClient.getAll();

        var books = response.getBody();
        if (books == null) {
            log.error("Failed to get books from remote service");
            return Collections.emptyList();
        }

        return books;
    }

    public Book getById(Integer id) {

        ResponseEntity<Book> response = bookServiceFeignClient.getById(id);

        var book = response.getBody();
        if (book == null) {
            log.error("Failed to get book from remote service");
        }

        return book;
    }

    public Book create(Book book) {

        ResponseEntity<Book> response = bookServiceFeignClient.create(book);

        var bookCreated = response.getBody();
        if (bookCreated == null) {
            log.error("Failed to create book in remote service");
        }

        return bookCreated;
    }

    public void delete(Integer id) {
        bookServiceFeignClient.delete(id);
    }
}
