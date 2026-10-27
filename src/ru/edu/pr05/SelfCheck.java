package ru.edu.pr05;

import java.time.LocalDate;

public class SelfCheck {

    private static int passed = 0;
    private static int failed = 0;

    private static final LocalDate START = LocalDate.of(2026, 10, 10);
    private static final LocalDate END = LocalDate.of(2026, 10, 12);
    private static final Employee EMP = new Employee("E1", "Ivanov Ivan");

    public static void main(String[] args) {
        testConstructorValid();
        testConstructorBlankId();
        testConstructorNullEmployee();
        testConstructorNullStart();
        testConstructorNullEnd();
        testConstructorStartAfterEnd();

        testSubmitFromDraft();
        testSubmitFromSubmitted();
        testSubmitFromApproved();

        testApproveFromSubmitted();
        testApproveFromDraft();
        testApproveFromApproved();

        testRejectFromSubmittedValid();
        testRejectMinLengthBoundary();
        testRejectTooShort();
        testRejectNullReason();
        testRejectFromDraft();

        testCancelFromDraft();
        testCancelFromSubmitted();
        testCancelFromApproved();
        testCancelFromCompleted();
        testCancelFromRejected();

        testCompleteFromApproved();
        testCompleteTodayEqualsEnd();
        testCompleteTodayBeforeEnd();
        testCompleteFromSubmitted();
        testCompleteNullToday();

        testServiceCreate();
        testServiceFindByStatus();
        testServiceGetAllImmutable();

        System.out.println();
        System.out.println("Пройдено: " + passed + ", провалено: " + failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void testConstructorValid() {
        BusinessTrip t = new BusinessTrip("T1", EMP, START, END);
        check("Конструктор: корректные данные, статус DRAFT", t.getStatus() == TripStatus.DRAFT);
    }

    private static void testConstructorBlankId() {
        checkThrows("Конструктор: пустой id",
                () -> new BusinessTrip(" ", EMP, START, END));
    }

    private static void testConstructorNullEmployee() {
        checkThrows("Конструктор: null сотрудник",
                () -> new BusinessTrip("T1", null, START, END));
    }

    private static void testConstructorNullStart() {
        checkThrows("Конструктор: null start",
                () -> new BusinessTrip("T1", EMP, null, END));
    }

    private static void testConstructorNullEnd() {
        checkThrows("Конструктор: null end",
                () -> new BusinessTrip("T1", EMP, START, null));
    }

    private static void testConstructorStartAfterEnd() {
        checkThrows("Конструктор: end раньше start",
                () -> new BusinessTrip("T1", EMP, END, START));
    }

    private static void testSubmitFromDraft() {
        BusinessTrip t = newTrip();
        t.submit();
        check("submit из DRAFT → SUBMITTED", t.getStatus() == TripStatus.SUBMITTED);
    }

    private static void testSubmitFromSubmitted() {
        BusinessTrip t = newTrip();
        t.submit();
        checkThrows("submit из SUBMITTED → ошибка", t::submit);
    }

    private static void testSubmitFromApproved() {
        BusinessTrip t = newTrip();
        t.submit();
        t.approve();
        checkThrows("submit из APPROVED → ошибка", t::submit);
    }

    private static void testApproveFromSubmitted() {
        BusinessTrip t = newTrip();
        t.submit();
        t.approve();
        check("approve из SUBMITTED → APPROVED", t.getStatus() == TripStatus.APPROVED);
    }

    private static void testApproveFromDraft() {
        BusinessTrip t = newTrip();
        checkThrows("approve из DRAFT → ошибка", t::approve);
    }

    private static void testApproveFromApproved() {
        BusinessTrip t = newTrip();
        t.submit();
        t.approve();
        checkThrows("approve из APPROVED → ошибка", t::approve);
    }

    private static void testRejectFromSubmittedValid() {
        BusinessTrip t = newTrip();
        t.submit();
        t.reject("Недостаточно оснований для командировки");
        check("reject из SUBMITTED → REJECTED",
                t.getStatus() == TripStatus.REJECTED
                        && t.getRejectionReason().equals("Недостаточно оснований для командировки"));
    }

    private static void testRejectMinLengthBoundary() {
        BusinessTrip t = newTrip();
        t.submit();
        String exact20 = "12345678901234567890"; // ровно 20
        t.reject(exact20);
        check("reject: ровно 20 символов принято", t.getStatus() == TripStatus.REJECTED);
    }

    private static void testRejectTooShort() {
        BusinessTrip t = newTrip();
        t.submit();
        checkThrows("reject: 19 символов отклонено",
                () -> t.reject("1234567890123456789"));
    }

    private static void testRejectNullReason() {
        BusinessTrip t = newTrip();
        t.submit();
        checkThrows("reject: null причина отклонена", () -> t.reject(null));
    }

    private static void testRejectFromDraft() {
        BusinessTrip t = newTrip();
        checkThrows("reject из DRAFT → ошибка",
                () -> t.reject("Достаточно длинная причина"));
    }

    private static void testCancelFromDraft() {
        BusinessTrip t = newTrip();
        t.cancel();
        check("cancel из DRAFT → CANCELLED", t.getStatus() == TripStatus.CANCELLED);
    }

    private static void testCancelFromSubmitted() {
        BusinessTrip t = newTrip();
        t.submit();
        t.cancel();
        check("cancel из SUBMITTED → CANCELLED", t.getStatus() == TripStatus.CANCELLED);
    }

    private static void testCancelFromApproved() {
        BusinessTrip t = newTrip();
        t.submit();
        t.approve();
        t.cancel();
        check("cancel из APPROVED → CANCELLED", t.getStatus() == TripStatus.CANCELLED);
    }

    private static void testCancelFromCompleted() {
        BusinessTrip t = newTrip();
        t.submit();
        t.approve();
        t.complete(END.plusDays(1));
        checkThrows("cancel из COMPLETED → ошибка", t::cancel);
    }

    private static void testCancelFromRejected() {
        BusinessTrip t = newTrip();
        t.submit();
        t.reject("Недостаточно оснований для командировки");
        checkThrows("cancel из REJECTED → ошибка", t::cancel);
    }

    private static void testCompleteFromApproved() {
        BusinessTrip t = newTrip();
        t.submit();
        t.approve();
        t.complete(END.plusDays(1));
        check("complete из APPROVED после end → COMPLETED", t.getStatus() == TripStatus.COMPLETED);
    }

    private static void testCompleteTodayEqualsEnd() {
        BusinessTrip t = newTrip();
        t.submit();
        t.approve();
        checkThrows("complete: today == end отклонено", () -> t.complete(END));
    }

    private static void testCompleteTodayBeforeEnd() {
        BusinessTrip t = newTrip();
        t.submit();
        t.approve();
        checkThrows("complete: today < end отклонено", () -> t.complete(END.minusDays(1)));
    }

    private static void testCompleteFromSubmitted() {
        BusinessTrip t = newTrip();
        t.submit();
        checkThrows("complete из SUBMITTED → ошибка", () -> t.complete(END.plusDays(1)));
    }

    private static void testCompleteNullToday() {
        BusinessTrip t = newTrip();
        t.submit();
        t.approve();
        checkThrows("complete: null today отклонено", () -> t.complete(null));
    }

    private static void testServiceCreate() {
        BusinessTripService service = new BusinessTripService();
        BusinessTrip t = service.create("T-01", EMP, START, END);
        check("Service: create → DRAFT", t.getStatus() == TripStatus.DRAFT
                && service.getAll().size() == 1);
    }

    private static void testServiceFindByStatus() {
        BusinessTripService service = new BusinessTripService();
        BusinessTrip a = service.create("T-01", EMP, START, END);
        service.create("T-02", EMP, START, END);
        a.submit();
        check("Service: findByStatus(SUBMITTED) → 1", service.findByStatus(TripStatus.SUBMITTED).size() == 1);
        check("Service: findByStatus(DRAFT) → 1", service.findByStatus(TripStatus.DRAFT).size() == 1);
        checkThrows("Service: findByStatus(null)", () -> service.findByStatus(null));
    }

    private static void testServiceGetAllImmutable() {
        BusinessTripService service = new BusinessTripService();
        service.create("T-01", EMP, START, END);
        checkThrows("Service: getAll() неизменяем",
                () -> service.getAll().add(new BusinessTrip("T-X", EMP, START, END)));
    }

    private static BusinessTrip newTrip() {
        return new BusinessTrip("T1", EMP, START, END);
    }

    private static void check(String name, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("[OK]   " + name);
        } else {
            failed++;
            System.out.println("[FAIL] " + name);
        }
    }

    private static void checkThrows(String name, Runnable action) {
        try {
            action.run();
            failed++;
            System.out.println("[FAIL] " + name + " (исключение не брошено)");
        } catch (RuntimeException e) {
            passed++;
            System.out.println("[OK]   " + name + " -> " + e.getMessage());
        }
    }
}
