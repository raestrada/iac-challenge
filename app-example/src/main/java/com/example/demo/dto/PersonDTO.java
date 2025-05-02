package com.example.demo.dto;

import com.example.demo.model.Person;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonDTO {

    @NotBlank(message = "Email is required")
    @Size(max = 100, message = "Email must be less than 100 characters")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Email must be valid")
    private String email;

    @NotBlank(message = "Name is required")
    @Size(max = 200, message = "Name must be less than 200 characters")
    private String name;

    @NotBlank(message = "Birthday is required")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Birthday must be in format YYYY-MM-DD")
    private String birthday;

    private List<@Size(max = 60, message = "Each hobby must be less than 60 characters") String> hobbies = new ArrayList<>();

    public Person toPerson() {
        Person person = new Person();
        person.setEmail(this.email);
        person.setName(this.name);
        
        try {
            LocalDate birthdayDate = LocalDate.parse(this.birthday, DateTimeFormatter.ISO_DATE);
            person.setBirthday(birthdayDate);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Please use YYYY-MM-DD");
        }
        
        if (this.hobbies != null) {
            person.setHobbies(new ArrayList<>(this.hobbies));
        }
        
        return person;
    }
    
    public static PersonDTO fromPerson(Person person) {
        PersonDTO dto = new PersonDTO();
        dto.setEmail(person.getEmail());
        dto.setName(person.getName());
        
        if (person.getBirthday() != null) {
            dto.setBirthday(person.getBirthday().format(DateTimeFormatter.ISO_DATE));
        }
        
        if (person.getHobbies() != null) {
            dto.setHobbies(new ArrayList<>(person.getHobbies()));
        }
        
        return dto;
    }
}
