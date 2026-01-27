package com.exercise.mongo.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "enrollments")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Enrollment {
    @Id
    private String id;
    private String studentId;
    private Integer courseId;
    private String semester; // kapan
    private Integer score; // hasil
}
