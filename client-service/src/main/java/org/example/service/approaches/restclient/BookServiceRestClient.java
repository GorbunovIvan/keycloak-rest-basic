package org.example.service.approaches.restclient;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.model.Book;
import org.example.service.BookService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class BookServiceRestClient implements BookService {

    @Value("${book-service.url}")
    private String bookServiceUrl;

    @Value("${book-service.uri}")
    private String bookServiceUri;

    private String bookServiceFullUrl;

    private final KeycloakForBookServiceRestClient keycloakForBookService;

    private final RestClient restClient;

    public BookServiceRestClient(KeycloakForBookServiceRestClient keycloakForBookService) {
        this.keycloakForBookService = keycloakForBookService;
        this.restClient = RestClient.builder().build();
    }

    @PostConstruct
    void init() {
        this.bookServiceFullUrl = String.format("http://%s%s", bookServiceUrl, bookServiceUri);
    }

    @Override
    public List<Book> getAll() {

        ResponseEntity<List<Book>> response = restClient.get()
                .uri(bookServiceFullUrl)
                .headers(this::addSecurityInfoToRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, resp) -> log.error("{}. {}", resp, resp.getStatusText()))
                .toEntity(new ParameterizedTypeReference<>() {});

        var books = response.getBody();
        if (books == null) {
            log.error("Failed to get books from remote service");
            return Collections.emptyList();
        }

        return books;
    }

    @Override
    public Book getById(Integer id) {

        ResponseEntity<Book> response = restClient.get()
                .uri(bookServiceFullUrl + "/{id}", id)
                .headers(this::addSecurityInfoToRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, resp) -> log.error("{}. {}", resp, resp.getStatusText()))
                .toEntity(new ParameterizedTypeReference<>() {});

        var book = response.getBody();
        if (book == null) {
            log.error("Failed to get book from remote service");
        }

        return book;
    }

    @Override
    public Book create(Book book) {

        ResponseEntity<Book> response = restClient.post()
                .uri(bookServiceFullUrl)
                .headers(this::addSecurityInfoToRequest)
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

    @Override
    public void delete(Integer id) {
        restClient.delete()
                .uri(bookServiceFullUrl + "/{id}", id)
                .headers(this::addSecurityInfoToRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, resp) -> log.error("{}. {}", resp, resp.getStatusText()));
    }

    private void addSecurityInfoToRequest(HttpHeaders httpHeaders) {
        keycloakForBookService.addSecurityInfoToHttpHeaders(httpHeaders);
    }
}
