package org.example.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.model.Book;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@Slf4j
public class BookService {

    @Value("${book-service.url}")
    private String bookServiceUrl;

    @Value("${book-service.uri}")
    private String bookServiceUri;

    private String bookServiceFullUrl;

    private final RestClient restClient;

    public BookService() {
        this.restClient = RestClient.builder().build();
    }

    @PostConstruct
    void init() {
        this.bookServiceFullUrl = String.format("http://%s%s", bookServiceUrl, bookServiceUri);
    }

    public List<Book> getAll() {

        ResponseEntity<List<Book>> response = restClient.get()
                .uri(bookServiceFullUrl)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, resp) -> log.error("{}. {}", resp, resp.getStatusText()))
                .toEntity(new ParameterizedTypeReference<>() {});

        var books = response.getBody();
        if (books == null) {
            log.error("Failed to get books from remote service");
        }

        return books;
    }

    public Book getById(Integer id) {

        ResponseEntity<Book> response = restClient.get()
                .uri(bookServiceFullUrl + "/{id}", id)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, resp) -> log.error("{}. {}", resp, resp.getStatusText()))
                .toEntity(new ParameterizedTypeReference<>() {});

        var book = response.getBody();
        if (book == null) {
            log.error("Failed to get book from remote service");
        }

        return book;
    }

    public Book create(Book book) {

        ResponseEntity<Book> response = restClient.post()
                .uri(bookServiceFullUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(book)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, resp) -> log.error("{}. {}", resp, resp.getStatusText()))
                .toEntity(new ParameterizedTypeReference<>() {});

        var bookCreated = response.getBody();
        if (bookCreated == null) {
            log.error("Failed to create book in remote service");
        }

        return bookCreated;
    }

    public void delete(Integer id) {
        restClient.delete()
                .uri(bookServiceFullUrl + "/{id}", id)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, resp) -> log.error("{}. {}", resp, resp.getStatusText()));
    }
}
