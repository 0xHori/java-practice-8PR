package ru.edu.pr05;

import java.time.LocalDate;

public final class App {

    /** Идентификатор заявки. */
    private static final String TRIP_ID = "T-01";

    /** Сотрудник. */
    private static final Employee EMPLOYEE = new Employee("E1", "Ivanov Ivan");

    /** Дата начала командировки. */
    private static final LocalDate START = LocalDate.of(2026, 10, 10);

    /** Дата окончания командировки. */
    private static final LocalDate END = LocalDate.of(2026, 10, 12);

    /** Дата завершения (строго после END). */
    private static final LocalDate TODAY_AFTER = END.plusDays(1);

    /** Причина отклонения (>= 20 символов для Варианта 1). */
    private static final String REJECTION_REASON = "Недостаточно оснований для командировки";

    /** Команды по умолчанию: DRAFT → SUBMITTED → APPROVED → COMPLETED. */
    private static final String[] DEFAULT_COMMANDS = {"submit", "approve", "complete"};

    private App() {
    }

    public static void main(String[] args) {
        String[] commands = (args.length > 0) ? args : DEFAULT_COMMANDS;

        BusinessTripService service = new BusinessTripService();
        BusinessTrip trip = service.create(TRIP_ID, EMPLOYEE, START, END);

        System.out.println("Заявка: " + trip.getId()
                + " | " + trip.getEmployee().fullName()
                + " | " + trip.getStart() + ".." + trip.getEnd());
        System.out.println("Начальный статус: " + trip.getStatus());
        System.out.println();

        for (String cmd : commands) {
            applyCommand(trip, cmd);
        }

        System.out.println();
        System.out.println("Итоговый статус: " + trip.getStatus());
        if (trip.getRejectionReason() != null) {
            System.out.println("Причина отклонения: " + trip.getRejectionReason());
        }
        System.out.println("Заявок в сервисе: " + service.getAll().size());
    }

    private static void applyCommand(BusinessTrip trip, String cmd) {
        System.out.print("> " + cmd + " -> ");
        try {
            switch (cmd) {
                case "submit" -> trip.submit();
                case "approve" -> trip.approve();
                case "cancel" -> trip.cancel();
                case "complete" -> trip.complete(TODAY_AFTER);
                default -> {
                    if (cmd.startsWith("reject:")) {
                        trip.reject(cmd.substring("reject:".length()));
                    } else {
                        System.out.println("неизвестная команда");
                        return;
                    }
                }
            }
            System.out.println("OK, статус: " + trip.getStatus());
        } catch (RuntimeException e) {
            System.out.println("ошибка: " + e.getMessage());
        }
    }
}
