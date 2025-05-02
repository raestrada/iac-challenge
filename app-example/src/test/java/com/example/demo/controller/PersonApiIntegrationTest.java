package com.example.demo.controller;

import com.example.demo.dto.PersonDTO;
import com.example.demo.model.Person;
import com.example.demo.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PersonApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PersonRepository personRepository;

    private String baseUrl;

    @BeforeEach
    public void setUp() {
        this.baseUrl = "http://localhost:" + port;
        personRepository.deleteAll();
    }

    @Test
    public void createPersonSuccessTest() {
        // Create valid person DTO
        PersonDTO person = new PersonDTO();
        person.setEmail("john.doe@example.com");
        person.setName("John Doe");
        person.setBirthday("2000-01-01");
        person.setHobbies(Arrays.asList("reading", "hiking", "cooking"));

        // Send POST request
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                baseUrl + "/person",
                HttpMethod.POST,
                new HttpEntity<>(person, createJsonHeaders()),
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        // Verify response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).containsKey("id");
        assertThat(response.getBody()).containsKey("message");
        assertThat(response.getBody().get("message")).isEqualTo("Person created successfully");

        // Verify database state
        Long savedId = ((Number) response.getBody().get("id")).longValue();
        assertThat(personRepository.findById(savedId)).isPresent();
    }

    @Test
    public void createPersonWithInvalidEmailTest() {
        // Create person with invalid email
        PersonDTO person = new PersonDTO();
        person.setEmail("invalid-email"); // Invalid email format
        person.setName("John Doe");
        person.setBirthday("2000-01-01");

        // Send POST request
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                baseUrl + "/person",
                HttpMethod.POST,
                new HttpEntity<>(person, createJsonHeaders()),
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        // Verify response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsKey("message");
        assertThat(response.getBody().get("message")).isEqualTo("Validation error");
        assertThat(response.getBody()).containsKey("errors");
    }

    @Test
    public void createPersonWithDuplicateEmailTest() {
        // Create first person
        PersonDTO person1 = new PersonDTO();
        person1.setEmail("duplicate@example.com");
        person1.setName("First Person");
        person1.setBirthday("2000-01-01");

        // Save first person
        restTemplate.exchange(
                baseUrl + "/person",
                HttpMethod.POST,
                new HttpEntity<>(person1, createJsonHeaders()),
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        // Create second person with same email
        PersonDTO person2 = new PersonDTO();
        person2.setEmail("duplicate@example.com"); // Same email
        person2.setName("Second Person");
        person2.setBirthday("1990-01-01");

        // Send POST request
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                baseUrl + "/person",
                HttpMethod.POST,
                new HttpEntity<>(person2, createJsonHeaders()),
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        // Verify response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).containsKey("message");
        assertThat(response.getBody().get("message").toString())
                .contains("Email already exists");
    }

    @Test
    public void getPersonByIdSuccessTest() {
        // Create and save a person
        PersonDTO personDTO = new PersonDTO();
        personDTO.setEmail("get.test@example.com");
        personDTO.setName("Get Test");
        personDTO.setBirthday("1995-05-05");
        personDTO.setHobbies(Arrays.asList("testing", "coding"));

        ResponseEntity<Map<String, Object>> createResponse = restTemplate.exchange(
                baseUrl + "/person",
                HttpMethod.POST,
                new HttpEntity<>(personDTO, createJsonHeaders()),
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        Long personId = ((Number) createResponse.getBody().get("id")).longValue();

        // Get the person by ID
        ResponseEntity<Person> getResponse = restTemplate.getForEntity(
                baseUrl + "/person/" + personId,
                Person.class
        );

        // Verify response
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().getEmail()).isEqualTo("get.test@example.com");
        assertThat(getResponse.getBody().getName()).isEqualTo("Get Test");
        assertThat(getResponse.getBody().getHobbies()).contains("testing", "coding");
    }

    @Test
    public void getPersonByIdNotFoundTest() {
        // Request a non-existent ID
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                baseUrl + "/person/999",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        // Verify response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).containsKey("message");
        assertThat(response.getBody().get("message").toString())
                .contains("Person not found");
    }

    @Test
    public void getHomePageTest() {
        // Add some test persons
        PersonDTO person1 = new PersonDTO();
        person1.setEmail("home1@example.com");
        person1.setName("Home Test 1");
        person1.setBirthday("1990-01-15");

        PersonDTO person2 = new PersonDTO();
        person2.setEmail("home2@example.com");
        person2.setName("Home Test 2");
        person2.setBirthday("1992-02-20");

        // Save persons
        restTemplate.exchange(
                baseUrl + "/person",
                HttpMethod.POST,
                new HttpEntity<>(person1, createJsonHeaders()),
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        restTemplate.exchange(
                baseUrl + "/person",
                HttpMethod.POST,
                new HttpEntity<>(person2, createJsonHeaders()),
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        // Get home page
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/",
                String.class
        );

        // Verify response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Person Management API");
        assertThat(response.getBody()).contains("Total Persons");
        
        // Check that it contains our count (2)
        assertThat(response.getBody()).contains(">2<");
    }

    private HttpHeaders createJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
