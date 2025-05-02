package com.example.demo.service;

import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Person;
import com.example.demo.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    @Transactional
    public Person createPerson(Person person) {
        // Check if email already exists
        if (personRepository.existsByEmail(person.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + person.getEmail());
        }
        
        return personRepository.save(person);
    }

    @Transactional(readOnly = true)
    public Person getPersonById(Long id) {
        return personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public long countPersons() {
        return personRepository.count();
    }
    
    @Transactional(readOnly = true)
    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }
}
