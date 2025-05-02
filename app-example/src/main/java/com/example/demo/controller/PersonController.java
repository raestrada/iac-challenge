package com.example.demo.controller;

import com.example.demo.dto.PersonDTO;
import com.example.demo.model.Person;
import com.example.demo.service.PersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/person")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createPerson(@Valid @RequestBody PersonDTO personDTO) {
        // Convert DTO to entity
        Person person = personDTO.toPerson();
        
        // Save the person
        Person savedPerson = personService.createPerson(person);
        
        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("id", savedPerson.getId());
        response.put("message", "Person created successfully");
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> getPersonById(@PathVariable Long id) {
        Person person = personService.getPersonById(id);
        return ResponseEntity.ok(person);
    }
}
