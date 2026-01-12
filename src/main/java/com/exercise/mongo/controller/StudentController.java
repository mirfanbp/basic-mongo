package com.exercise.mongo.controller;

import com.exercise.mongo.domain.Student;
import com.exercise.mongo.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

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

    @GetMapping("/api/students/{id}")
    public ResponseEntity<Student> getById(@PathVariable String id) {
        return studentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/api/students/{id}")
    public ResponseEntity<Student> update(@PathVariable String id, @RequestBody Student s) {
        return ResponseEntity.ok(studentService.update(id, s));
    }

    @DeleteMapping("/api/students/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
