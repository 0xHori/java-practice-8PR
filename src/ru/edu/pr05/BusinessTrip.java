package ru.edu.pr05;

import java.time.LocalDate;

public class BusinessTrip {

    public static final int MIN_REJECTION_REASON_LENGTH = 20;

    private final String id;
    private final Employee employee;
    private final LocalDate start;
    private final LocalDate end;

    private TripStatus status = TripStatus.DRAFT;
    private String rejectionReason;

    public BusinessTrip(String id, Employee employee, LocalDate start, LocalDate end) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID заявки не может быть пустым");
        }
        if (employee == null) {
            throw new IllegalArgumentException("Сотрудник не может быть null");
        }
        if (start == null) {
            throw new IllegalArgumentException("Дата начала не может быть null");
        }
        if (end == null) {
            throw new IllegalArgumentException("Дата окончания не может быть null");
        }
        if (end.isBefore(start)) {
            throw new IllegalArgumentException(
                    "Дата окончания не может быть раньше даты начала: " + end + " < " + start);
        }
        this.id = id;
        this.employee = employee;
        this.start = start;
        this.end = end;
    }

    public void submit() {
        requireStatus(TripStatus.DRAFT, "submit");
        this.status = TripStatus.SUBMITTED;
    }

    public void approve() {
        requireStatus(TripStatus.SUBMITTED, "approve");
        this.status = TripStatus.APPROVED;
    }

    public void reject(String reason) {
        requireStatus(TripStatus.SUBMITTED, "reject");
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Причина отклонения обязательна");
        }
        String trimmed = reason.trim();
        if (trimmed.length() < MIN_REJECTION_REASON_LENGTH) {
            throw new IllegalArgumentException(
                    "Причина отклонения должна содержать минимум "
                            + MIN_REJECTION_REASON_LENGTH + " символов, получено " + trimmed.length());
        }
        this.rejectionReason = trimmed;
        this.status = TripStatus.REJECTED;
    }

    public void cancel() {
        if (status == TripStatus.COMPLETED) {
            throw new IllegalStateException("Нельзя отменить завершённую командировку");
        }
        if (status != TripStatus.DRAFT
                && status != TripStatus.SUBMITTED
                && status != TripStatus.APPROVED) {
            throw new IllegalStateException("Отмена невозможна из статуса " + status);
        }
        this.status = TripStatus.CANCELLED;
    }

    public void complete(LocalDate today) {
        requireStatus(TripStatus.APPROVED, "complete");
        if (today == null) {
            throw new IllegalArgumentException("Дата завершения не может быть null");
        }
        if (!today.isAfter(end)) {
            throw new IllegalArgumentException(
                    "Завершить можно только после даты окончания командировки ("
                            + end + "), передано: " + today);
        }
        this.status = TripStatus.COMPLETED;
    }

    private void requireStatus(TripStatus expected, String action) {
        if (status != expected) {
            throw new IllegalStateException(
                    "Операция " + action + " невозможна из статуса " + status
                            + " (ожидается " + expected + ")");
        }
    }

    public String getId() {
        return id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public LocalDate getStart() {
        return start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public TripStatus getStatus() {
        return status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }
}
