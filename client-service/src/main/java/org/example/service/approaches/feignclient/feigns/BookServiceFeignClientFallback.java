package org.example.service.approaches.feignclient.feigns;

import lombok.extern.slf4j.Slf4j;
import org.example.model.Book;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class BookServiceFeignClientFallback implements BookServiceFeignClient {

    private final String errorMessage = "Remote book-service is not available.";

    @Override
    public ResponseEntity<List<Book>> getAll() {
        log.error(errorMessage);
        return ResponseEntity.internalServerError().build();
    }

    @Override
    public ResponseEntity<Book> getById(Integer id) {
        log.error(errorMessage);
        return ResponseEntity.internalServerError().build();
    }

    @Override
    public ResponseEntity<Book> create(Book book) {
        log.error(errorMessage);
        return ResponseEntity.internalServerError().build();
    }

    @Override
    public void delete(Integer id) {
        log.error(errorMessage);
    }
}
