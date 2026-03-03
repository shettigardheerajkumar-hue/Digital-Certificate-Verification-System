package com.digitalcert.service;

import com.digitalcert.dao.CertificateDao;
import com.digitalcert.model.Certificate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class CertificateService {

    private final CertificateDao certificateDao;

    public CertificateService(CertificateDao certificateDao) {
        this.certificateDao = certificateDao;
    }

    public Certificate createCertificate(Certificate certificate) {
        validateCertificateForCreate(certificate);

        Optional<Certificate> existing = certificateDao.findByCertificateId(certificate.getCertificateId());
        if (existing.isPresent()) {
            throw new RuntimeException("Certificate with ID '" + certificate.getCertificateId() + "' already exists.");
        }

        certificate.setStatus("ACTIVE");
        return certificateDao.save(certificate);
    }

    public List<Certificate> listCertificates() {
        return certificateDao.findAll();
    }

    public Optional<Certificate> getCertificateById(long id) {
        return certificateDao.findById(id);
    }

    public Optional<Certificate> getByCertificateId(String certificateId) {
        return certificateDao.findByCertificateId(certificateId);
    }

    public Certificate updateCertificate(Certificate certificate) {
        if (certificate.getId() == null) {
            throw new IllegalArgumentException("Certificate ID must not be null for update.");
        }
        validateCertificateBasicFields(certificate);

        return certificateDao.update(certificate);
    }

    public boolean deleteCertificate(long id) {
        return certificateDao.deleteById(id);
    }

    public boolean verifyCertificate(String certificateId) {
        Optional<Certificate> certOpt = certificateDao.findByCertificateId(certificateId);
        if (certOpt.isEmpty()) {
            return false;
        }
        Certificate cert = certOpt.get();
        updateStatusIfExpired(cert);
        if ("REVOKED".equalsIgnoreCase(cert.getStatus())) {
            return false;
        }
        return "ACTIVE".equalsIgnoreCase(cert.getStatus());
    }

    public String verifyCertificateMessage(String certificateId) {
        Optional<Certificate> certOpt = certificateDao.findByCertificateId(certificateId);
        if (certOpt.isEmpty()) {
            return "Certificate NOT VALID: No certificate found with ID '" + certificateId + "'.";
        }

        Certificate cert = certOpt.get();
        updateStatusIfExpired(cert);

        StringBuilder sb = new StringBuilder();
        sb.append("Certificate Details:\n")
                .append("Internal ID: ").append(cert.getId()).append("\n")
                .append("Certificate ID: ").append(cert.getCertificateId()).append("\n")
                .append("Holder Name: ").append(cert.getHolderName()).append("\n")
                .append("Course: ").append(cert.getCourseName()).append("\n")
                .append("Institution: ").append(cert.getInstitutionName()).append("\n")
                .append("Issue Date: ").append(cert.getIssueDate()).append("\n")
                .append("Expiry Date: ").append(cert.getExpiryDate()).append("\n")
                .append("Status: ").append(cert.getStatus()).append("\n\n");

        if ("REVOKED".equalsIgnoreCase(cert.getStatus())) {
            sb.append("Result: Certificate is NOT VALID (REVOKED).");
        } else if ("EXPIRED".equalsIgnoreCase(cert.getStatus())) {
            sb.append("Result: Certificate has EXPIRED.");
        } else if ("ACTIVE".equalsIgnoreCase(cert.getStatus())) {
            sb.append("Result: Certificate is VALID.");
        } else {
            sb.append("Result: Certificate status is UNKNOWN.");
        }

        return sb.toString();
    }

    private void updateStatusIfExpired(Certificate cert) {
        LocalDate today = LocalDate.now();
        LocalDate expiryDate = cert.getExpiryDate();
        if (expiryDate != null && expiryDate.isBefore(today) && !"EXPIRED".equalsIgnoreCase(cert.getStatus())) {
            cert.setStatus("EXPIRED");
            certificateDao.update(cert);
        }
    }

    private void validateCertificateForCreate(Certificate certificate) {
        validateCertificateBasicFields(certificate);
        if (certificate.getIssueDate() == null) {
            throw new IllegalArgumentException("Issue date must not be null.");
        }
    }

    private void validateCertificateBasicFields(Certificate certificate) {
        if (certificate.getCertificateId() == null || certificate.getCertificateId().isBlank()) {
            throw new IllegalArgumentException("Certificate ID must not be empty.");
        }
        if (certificate.getHolderName() == null || certificate.getHolderName().isBlank()) {
            throw new IllegalArgumentException("Holder name must not be empty.");
        }
        if (certificate.getCourseName() == null || certificate.getCourseName().isBlank()) {
            throw new IllegalArgumentException("Course name must not be empty.");
        }
        if (certificate.getInstitutionName() == null || certificate.getInstitutionName().isBlank()) {
            throw new IllegalArgumentException("Institution name must not be empty.");
        }
    }
}

