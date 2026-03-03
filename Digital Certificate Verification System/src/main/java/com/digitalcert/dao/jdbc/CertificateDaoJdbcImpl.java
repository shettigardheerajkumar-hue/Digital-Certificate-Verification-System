package com.digitalcert.dao.jdbc;

import com.digitalcert.dao.CertificateDao;
import com.digitalcert.model.Certificate;
import com.digitalcert.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CertificateDaoJdbcImpl implements CertificateDao {

    private final DBConnectionUtil dbConnectionUtil;

    public CertificateDaoJdbcImpl(DBConnectionUtil dbConnectionUtil) {
        this.dbConnectionUtil = dbConnectionUtil;
    }

    @Override
    public Certificate save(Certificate certificate) {
        String sql = """
                INSERT INTO certificates
                (certificate_id, holder_name, course_name, institution_name,
                 issue_date, expiry_date, status, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        LocalDateTime now = LocalDateTime.now();

        try (Connection conn = dbConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, certificate.getCertificateId());
            ps.setString(2, certificate.getHolderName());
            ps.setString(3, certificate.getCourseName());
            ps.setString(4, certificate.getInstitutionName());
            ps.setObject(5, certificate.getIssueDate());
            if (certificate.getExpiryDate() != null) {
                ps.setObject(6, certificate.getExpiryDate());
            } else {
                ps.setNull(6, java.sql.Types.DATE);
            }
            ps.setString(7, certificate.getStatus());
            ps.setTimestamp(8, Timestamp.valueOf(now));
            ps.setTimestamp(9, Timestamp.valueOf(now));

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("Creating certificate failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long id = generatedKeys.getLong(1);
                    certificate.setId(id);
                    certificate.setCreatedAt(now);
                    certificate.setUpdatedAt(now);
                    return certificate;
                } else {
                    throw new RuntimeException("Creating certificate failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving certificate: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Certificate> findById(long id) {
        String sql = "SELECT * FROM certificates WHERE id = ?";
        try (Connection conn = dbConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding certificate by id: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Certificate> findByCertificateId(String certificateId) {
        String sql = "SELECT * FROM certificates WHERE certificate_id = ?";
        try (Connection conn = dbConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, certificateId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding certificate by certificate_id: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Certificate> findAll() {
        String sql = "SELECT * FROM certificates ORDER BY id";
        List<Certificate> result = new ArrayList<>();
        try (Connection conn = dbConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(mapRow(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Error listing certificates: " + e.getMessage(), e);
        }
    }

    @Override
    public Certificate update(Certificate certificate) {
        if (certificate.getId() == null) {
            throw new IllegalArgumentException("Certificate ID must not be null for update");
        }

        String sql = """
                UPDATE certificates
                SET certificate_id = ?, holder_name = ?, course_name = ?, institution_name = ?,
                    issue_date = ?, expiry_date = ?, status = ?, updated_at = ?
                WHERE id = ?
                """;

        LocalDateTime now = LocalDateTime.now();

        try (Connection conn = dbConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, certificate.getCertificateId());
            ps.setString(2, certificate.getHolderName());
            ps.setString(3, certificate.getCourseName());
            ps.setString(4, certificate.getInstitutionName());
            ps.setObject(5, certificate.getIssueDate());
            if (certificate.getExpiryDate() != null) {
                ps.setObject(6, certificate.getExpiryDate());
            } else {
                ps.setNull(6, java.sql.Types.DATE);
            }
            ps.setString(7, certificate.getStatus());
            ps.setTimestamp(8, Timestamp.valueOf(now));
            ps.setLong(9, certificate.getId());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("Updating certificate failed, no rows affected.");
            }
            certificate.setUpdatedAt(now);
            return certificate;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating certificate: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteById(long id) {
        String sql = "DELETE FROM certificates WHERE id = ?";
        try (Connection conn = dbConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting certificate: " + e.getMessage(), e);
        }
    }

    private Certificate mapRow(ResultSet rs) throws SQLException {
        Certificate certificate = new Certificate();
        certificate.setId(rs.getLong("id"));
        certificate.setCertificateId(rs.getString("certificate_id"));
        certificate.setHolderName(rs.getString("holder_name"));
        certificate.setCourseName(rs.getString("course_name"));
        certificate.setInstitutionName(rs.getString("institution_name"));

        java.sql.Date issueDate = rs.getDate("issue_date");
        if (issueDate != null) {
            certificate.setIssueDate(issueDate.toLocalDate());
        }

        java.sql.Date expiryDate = rs.getDate("expiry_date");
        if (expiryDate != null) {
            certificate.setExpiryDate(expiryDate.toLocalDate());
        }

        certificate.setStatus(rs.getString("status"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            certificate.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            certificate.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return certificate;
    }
}

