package ru.edu.pr05;

public record Employee(String id, String fullName) {

    public Employee {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID сотрудника не может быть пустым");
        }
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("ФИО сотрудника не может быть пустым");
        }
    }
}
