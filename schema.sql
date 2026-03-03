-- Schema for Digital Certificate Verification System
-- MySQL example

-- Create database
CREATE DATABASE IF NOT EXISTS digital_certificates;
USE digital_certificates;

-- Table for application users (administrators)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

-- Table for certificates
CREATE TABLE IF NOT EXISTS certificates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    certificate_id VARCHAR(100) NOT NULL UNIQUE,
    holder_name VARCHAR(255) NOT NULL,
    course_name VARCHAR(255) NOT NULL,
    institution_name VARCHAR(255) NOT NULL,
    issue_date DATE NOT NULL,
    expiry_date DATE NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

