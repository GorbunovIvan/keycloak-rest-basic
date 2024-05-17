package org.example.model;

import lombok.*;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class Book {
    private Integer id;
    private String title;
    private Integer year;
}
