package com.digitalcert.ui;

import com.digitalcert.model.Certificate;
import com.digitalcert.service.CertificateService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleMenu {

    private final CertificateService certificateService;
    private final Scanner scanner;

    public ConsoleMenu(CertificateService certificateService) {
        this.certificateService = certificateService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Enter your choice: ");
            switch (choice) {
                case 1 -> addCertificate();
                case 2 -> viewAllCertificates();
                case 3 -> searchByInternalId();
                case 4 -> searchByCertificateId();
                case 5 -> updateCertificate();
                case 6 -> deleteCertificate();
                case 7 -> verifyCertificate();
                case 8 -> running = false;
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
        System.out.println("Exiting application. Goodbye!");
    }

    private void printMenu() {
        System.out.println("\n=== Digital Certificate Verification System ===");
        System.out.println("1. Add new certificate");
        System.out.println("2. View all certificates");
        System.out.println("3. Search certificate by internal ID");
        System.out.println("4. Search certificate by certificate ID");
        System.out.println("5. Update certificate");
        System.out.println("6. Delete certificate");
        System.out.println("7. Verify certificate by certificate ID");
        System.out.println("8. Exit");
    }

    private void addCertificate() {
        System.out.println("\n-- Add New Certificate --");
        String certificateId = readString("Certificate ID (unique / QR content): ");
        String holderName = readString("Holder name: ");
        String courseName = readString("Course name: ");
        String institutionName = readString("Institution name: ");
        LocalDate issueDate = readDate("Issue date (YYYY-MM-DD): ");
        LocalDate expiryDate = readOptionalDate("Expiry date (YYYY-MM-DD) or leave blank for none: ");

        Certificate certificate = new Certificate();
        certificate.setCertificateId(certificateId);
        certificate.setHolderName(holderName);
        certificate.setCourseName(courseName);
        certificate.setInstitutionName(institutionName);
        certificate.setIssueDate(issueDate);
        certificate.setExpiryDate(expiryDate);

        try {
            Certificate created = certificateService.createCertificate(certificate);
            System.out.println("Certificate created successfully with internal ID: " + created.getId());
        } catch (RuntimeException ex) {
            System.out.println("Error creating certificate: " + ex.getMessage());
        }
    }

    private void viewAllCertificates() {
        System.out.println("\n-- All Certificates --");
        List<Certificate> certificates = certificateService.listCertificates();
        if (certificates.isEmpty()) {
            System.out.println("No certificates found.");
            return;
        }
        certificates.forEach(System.out::println);
    }

    private void searchByInternalId() {
        System.out.println("\n-- Search by Internal ID --");
        long id = readLong("Enter internal ID: ");
        Optional<Certificate> certificateOpt = certificateService.getCertificateById(id);
        certificateOpt.ifPresentOrElse(
                System.out::println,
                () -> System.out.println("Certificate not found.")
        );
    }

    private void searchByCertificateId() {
        System.out.println("\n-- Search by Certificate ID --");
        String certificateId = readString("Enter certificate ID: ");
        Optional<Certificate> certificateOpt = certificateService.getByCertificateId(certificateId);
        certificateOpt.ifPresentOrElse(
                System.out::println,
                () -> System.out.println("Certificate not found.")
        );
    }

    private void updateCertificate() {
        System.out.println("\n-- Update Certificate --");
        long id = readLong("Enter internal ID of certificate to update: ");
        Optional<Certificate> existingOpt = certificateService.getCertificateById(id);
        if (existingOpt.isEmpty()) {
            System.out.println("Certificate not found.");
            return;
        }
        Certificate existing = existingOpt.get();
        System.out.println("Existing certificate: " + existing);

        String holderName = readStringOrDefault("Holder name [" + existing.getHolderName() + "]: ", existing.getHolderName());
        String courseName = readStringOrDefault("Course name [" + existing.getCourseName() + "]: ", existing.getCourseName());
        String institutionName = readStringOrDefault("Institution name [" + existing.getInstitutionName() + "]: ", existing.getInstitutionName());
        LocalDate issueDate = readDateOrDefault("Issue date (YYYY-MM-DD) [" + existing.getIssueDate() + "]: ", existing.getIssueDate());
        LocalDate expiryDate = readOptionalDateOrDefault("Expiry date (YYYY-MM-DD) [" + existing.getExpiryDate() + "] (leave blank for none): ", existing.getExpiryDate());

        existing.setHolderName(holderName);
        existing.setCourseName(courseName);
        existing.setInstitutionName(institutionName);
        existing.setIssueDate(issueDate);
        existing.setExpiryDate(expiryDate);

        try {
            Certificate updated = certificateService.updateCertificate(existing);
            System.out.println("Certificate updated: " + updated);
        } catch (RuntimeException ex) {
            System.out.println("Error updating certificate: " + ex.getMessage());
        }
    }

    private void deleteCertificate() {
        System.out.println("\n-- Delete Certificate --");
        long id = readLong("Enter internal ID of certificate to delete: ");
        boolean deleted = certificateService.deleteCertificate(id);
        if (deleted) {
            System.out.println("Certificate deleted successfully.");
        } else {
            System.out.println("Certificate not found or could not be deleted.");
        }
    }

    private void verifyCertificate() {
        System.out.println("\n-- Verify Certificate --");
        String certificateId = readString("Enter certificate ID (from QR or printed ID): ");
        String result = certificateService.verifyCertificateMessage(certificateId);
        System.out.println(result);
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine();
            try {
                return Integer.parseInt(line.trim());
            } catch (NumberFormatException ex) {
                System.out.println("Invalid number. Please try again.");
            }
        }
    }

    private long readLong(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine();
            try {
                return Long.parseLong(line.trim());
            } catch (NumberFormatException ex) {
                System.out.println("Invalid number. Please try again.");
            }
        }
    }

    private String readString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine();
            if (line != null && !line.trim().isEmpty()) {
                return line.trim();
            }
            System.out.println("Input cannot be empty. Please try again.");
        }
    }

    private String readStringOrDefault(String prompt, String defaultValue) {
        System.out.print(prompt);
        String line = scanner.nextLine();
        if (line == null || line.trim().isEmpty()) {
            return defaultValue;
        }
        return line.trim();
    }

    private LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine();
            try {
                return LocalDate.parse(line.trim());
            } catch (Exception ex) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            }
        }
    }

    private LocalDate readOptionalDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine();
            if (line == null || line.trim().isEmpty()) {
                return null;
            }
            try {
                return LocalDate.parse(line.trim());
            } catch (Exception ex) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD or leave blank.");
            }
        }
    }

    private LocalDate readDateOrDefault(String prompt, LocalDate defaultValue) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine();
            if (line == null || line.trim().isEmpty()) {
                return defaultValue;
            }
            try {
                return LocalDate.parse(line.trim());
            } catch (Exception ex) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            }
        }
    }

    private LocalDate readOptionalDateOrDefault(String prompt, LocalDate defaultValue) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine();
            if (line == null || line.trim().isEmpty()) {
                return defaultValue;
            }
            try {
                return LocalDate.parse(line.trim());
            } catch (Exception ex) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD or leave blank.");
            }
        }
    }
}

