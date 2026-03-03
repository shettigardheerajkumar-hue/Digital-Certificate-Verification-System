package com.digitalcert.dao;

import com.digitalcert.model.Certificate;

import java.util.List;
import java.util.Optional;

public interface CertificateDao {

    Certificate save(Certificate certificate);

    Optional<Certificate> findById(long id);

    Optional<Certificate> findByCertificateId(String certificateId);

    List<Certificate> findAll();

    Certificate update(Certificate certificate);

    boolean deleteById(long id);
}

