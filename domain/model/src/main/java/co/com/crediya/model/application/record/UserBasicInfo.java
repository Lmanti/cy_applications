package co.com.crediya.model.application.record;

public record UserBasicInfo(
    Long idNumber,
    String name,
    String lastname,
    String email,
    Double baseSalary
) {}
