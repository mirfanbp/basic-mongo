package com.exercise.mongo.domain;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "students")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Student {
    private String id;      // String is OK for simplicity (Mongo ObjectId as string)
    private String name;
    private String address;
    private String phone;
    private String age;
}
