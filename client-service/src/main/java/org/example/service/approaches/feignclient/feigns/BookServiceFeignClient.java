package org.example.service.approaches.feignclient.feigns;

import org.example.model.Book;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        name = "book-service",
        url = "http://${book-service.url}${book-service.uri}",
        fallback = BookServiceFeignClientFallback.class
)
@Primary
public interface BookServiceFeignClient {

    @GetMapping
    ResponseEntity<List<Book>> getAll();

    @GetMapping("/{id}")
    ResponseEntity<Book> getById(@PathVariable Integer id);

    @PostMapping
    ResponseEntity<Book> create(@RequestBody Book book);

    @DeleteMapping("/{id}")
    void delete(@PathVariable Integer id);
}
