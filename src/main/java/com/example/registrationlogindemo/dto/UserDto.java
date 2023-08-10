package com.example.registrationlogindemo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto
{
    private Long id;
    @NotEmpty(message = "Το Όνομα δεν πρέπει να είναι κενό!")
    private String firstName;
    @NotEmpty(message = "Το Επώνυμο δεν πρέπει να είναι κενό!")
    private String lastName;
    @NotEmpty(message = "Το email δεν πρέπει να είναι κενό!")
    @Email
    private String email;
    @NotEmpty(message = "Ο κωδικός δεν πρέπει να είναι κενός!")
    private String password;
    private String phNo;
    private Boolean isDoctor;
    private String doc_specialty;
    private String status;
    @NotEmpty(message = "Ο ΑΜΚΑ δεν πρέπει να είναι κενός!")
    private String amka;
}
