-- V1__init_schema.sql
-- Radiopharmaceutical Clinical Dose Management Platform Core Schema

CREATE TABLE IF NOT EXISTS isotopes (
    id VARCHAR(36) PRIMARY KEY,
    symbol VARCHAR(16) NOT NULL UNIQUE,
    name VARCHAR(64) NOT NULL,
    half_life_minutes DOUBLE PRECISION NOT NULL,
    base_unit VARCHAR(16) NOT NULL DEFAULT 'mCi',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS clinical_protocols (
    id VARCHAR(36) PRIMARY KEY,
    protocol_code VARCHAR(32) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    sponsor VARCHAR(128) NOT NULL,
    phase VARCHAR(16) NOT NULL,
    target_isotope_id VARCHAR(36) NOT NULL REFERENCES isotopes(id),
    target_activity_mci DOUBLE PRECISION NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS clinical_sites (
    id VARCHAR(36) PRIMARY KEY,
    site_code VARCHAR(32) NOT NULL UNIQUE,
    name VARCHAR(128) NOT NULL,
    address VARCHAR(255) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    contact_email VARCHAR(128) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS dose_orders (
    id VARCHAR(36) PRIMARY KEY,
    order_number VARCHAR(64) NOT NULL UNIQUE,
    protocol_id VARCHAR(36) NOT NULL REFERENCES clinical_protocols(id),
    site_id VARCHAR(36) NOT NULL REFERENCES clinical_sites(id),
    requested_dose_time TIMESTAMP WITH TIME ZONE NOT NULL,
    ordered_activity_mci DOUBLE PRECISION NOT NULL,
    synthesized_activity_mci DOUBLE PRECISION,
    compensated_activity_mci DOUBLE PRECISION,
    status VARCHAR(32) NOT NULL DEFAULT 'ORDER_SUBMITTED',
    patient_id_hash VARCHAR(64) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS batch_allocations (
    id VARCHAR(36) PRIMARY KEY,
    batch_number VARCHAR(64) NOT NULL UNIQUE,
    order_id VARCHAR(36) NOT NULL REFERENCES dose_orders(id),
    synthesis_start_time TIMESTAMP WITH TIME ZONE NOT NULL,
    synthesis_end_time TIMESTAMP WITH TIME ZONE,
    initial_activity_mci DOUBLE PRECISION NOT NULL,
    qa_released_by VARCHAR(64),
    qa_release_timestamp TIMESTAMP WITH TIME ZONE,
    qa_notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Seed Canonical Radiopharmaceutical Isotopes
INSERT INTO isotopes (id, symbol, name, half_life_minutes, base_unit)
VALUES 
    ('iso-f18', 'F-18', 'Fluorine-18', 109.77, 'mCi'),
    ('iso-ga68', 'Ga-68', 'Gallium-68', 67.71, 'mCi'),
    ('iso-lu177', 'Lu-177', 'Lutetium-177', 9571.68, 'mCi'),
    ('iso-c11', 'C-11', 'Carbon-11', 20.33, 'mCi')
ON CONFLICT (symbol) DO NOTHING;
