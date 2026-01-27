package com.exercise.mongo.controller;

import com.exercise.mongo.domain.Student;
import com.exercise.mongo.model.StudentAverageScoreByAddress;
import com.exercise.mongo.model.StudentCountByAddress;
import com.exercise.mongo.service.StudentAggregationService;
import com.exercise.mongo.service.StudentService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/students")
public class StudentController {

    // before
        // Controller → Repository → Mongo
    // after
        // Controller → AggregationService → MongoTemplate → Mongo

    // ---
    // Embedding -> lebih cepat, lebih simpel

    private final StudentService studentService;
    private final StudentAggregationService studentAggregationService;

    @PostMapping("/api/students")
    public ResponseEntity<Student> create(@RequestBody Student s) {
        Student created = studentService.create(s);
        URI location = URI.create("/api/students/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public List<Student> all() {
        return studentService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getById(@PathVariable String id) {
        return studentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Student> update(@PathVariable String id, @RequestBody Student s) {
        return ResponseEntity.ok(studentService.update(id, s));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats/by-address")
    public List<StudentCountByAddress> countAllByAddress() {

        return studentAggregationService.countAllByAddress();
    }

    @GetMapping("/stats/by-address/{address}")
    public StudentCountByAddress countByAddress(@PathVariable String address) {
        return studentAggregationService.countByAddress(address);
    }

    @GetMapping("/stats/avg-math-by-address")
    public List<StudentAverageScoreByAddress> averageMathByAddress() {

        return studentAggregationService.averageScoreByAddresses();
    }

    @GetMapping("/stats/avg-math")
    public List<StudentAverageScoreByAddress> averageMath(
            @RequestParam int minAge,
            @RequestParam int maxAge,
            @RequestParam int page,
            @RequestParam int size
    ) {
        return studentAggregationService.averageScoreByAddresses(
                minAge, maxAge, page, size
        );
    }


}
