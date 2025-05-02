package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "persons")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Email is required")
    @Size(max = 100, message = "Email must be less than 100 characters")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Email must be valid")
    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @NotBlank(message = "Name is required")
    @Size(max = 200, message = "Name must be less than 200 characters")
    @Column(nullable = false, length = 200)
    private String name;

    @NotNull(message = "Birthday is required")
    @Past(message = "Birthday must be in the past")
    @Column(nullable = false)
    private LocalDate birthday;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "person_hobbies", joinColumns = @JoinColumn(name = "person_id"))
    @Column(name = "hobby", length = 60)
    @Size(max = 60, message = "Each hobby must be less than 60 characters")
    private List<String> hobbies = new ArrayList<>();
}
