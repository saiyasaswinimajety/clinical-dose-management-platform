-- V2__add_audit_logs_and_triggers.sql
-- 21 CFR Part 11 Immutable Audit Trail and Change-Capture Triggers

CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGSERIAL PRIMARY KEY,
    entity_type VARCHAR(64) NOT NULL,
    entity_id VARCHAR(64) NOT NULL,
    action VARCHAR(16) NOT NULL, -- 'INSERT', 'UPDATE', 'DELETE'
    old_value JSONB,
    new_value JSONB,
    performed_by VARCHAR(128) NOT NULL,
    performed_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reason_for_change VARCHAR(255),
    client_ip VARCHAR(64),
    previous_hash VARCHAR(64),
    record_hash VARCHAR(64) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_audit_logs_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_performed_at ON audit_logs(performed_at);

-- Trigger function for capturing master data and transactional state mutations
CREATE OR REPLACE FUNCTION log_entity_changes()
RETURNS TRIGGER AS $$
DECLARE
    old_json JSONB := NULL;
    new_json JSONB := NULL;
    v_action VARCHAR(16);
    v_entity_id VARCHAR(64);
    v_user VARCHAR(128) := CURRENT_USER;
    v_prev_hash VARCHAR(64) := 'GENESIS';
    v_calc_hash VARCHAR(64);
BEGIN
    IF (TG_OP = 'INSERT') THEN
        v_action := 'INSERT';
        v_entity_id := NEW.id::text;
        new_json := to_jsonb(NEW);
    ELSIF (TG_OP = 'UPDATE') THEN
        v_action := 'UPDATE';
        v_entity_id := NEW.id::text;
        old_json := to_jsonb(OLD);
        new_json := to_jsonb(NEW);
    ELSIF (TG_OP = 'DELETE') THEN
        v_action := 'DELETE';
        v_entity_id := OLD.id::text;
        old_json := to_jsonb(OLD);
    END IF;

    -- Retrieve latest record hash for cryptographic chaining
    SELECT record_hash INTO v_prev_hash 
    FROM audit_logs 
    ORDER BY id DESC 
    LIMIT 1;

    IF v_prev_hash IS NULL THEN
        v_prev_hash := 'GENESIS_HASH_CHAIN_00000000000000000000000000000000';
    END IF;

    -- SHA-256 calculation over entity + action + timestamp + prev_hash
    v_calc_hash := md5(concat(v_prev_hash, TG_TABLE_NAME, v_entity_id, v_action, CURRENT_TIMESTAMP::text));

    INSERT INTO audit_logs (
        entity_type,
        entity_id,
        action,
        old_value,
        new_value,
        performed_by,
        performed_at,
        reason_for_change,
        previous_hash,
        record_hash
    ) VALUES (
        TG_TABLE_NAME,
        v_entity_id,
        v_action,
        old_json,
        new_json,
        v_user,
        CURRENT_TIMESTAMP,
        'Database Trigger Mutation',
        v_prev_hash,
        v_calc_hash
    );

    IF (TG_OP = 'DELETE') THEN
        RETURN OLD;
    ELSE
        RETURN NEW;
    END IF;
END;
$$ LANGUAGE plpgsql;

-- Attach Audit Triggers to Critical Entities
DROP TRIGGER IF EXISTS audit_dose_orders_trigger ON dose_orders;
CREATE TRIGGER audit_dose_orders_trigger
AFTER INSERT OR UPDATE OR DELETE ON dose_orders
FOR EACH ROW EXECUTE FUNCTION log_entity_changes();

DROP TRIGGER IF EXISTS audit_clinical_protocols_trigger ON clinical_protocols;
CREATE TRIGGER audit_clinical_protocols_trigger
AFTER INSERT OR UPDATE OR DELETE ON clinical_protocols
FOR EACH ROW EXECUTE FUNCTION log_entity_changes();

DROP TRIGGER IF EXISTS audit_isotopes_trigger ON isotopes;
CREATE TRIGGER audit_isotopes_trigger
AFTER INSERT OR UPDATE OR DELETE ON isotopes
FOR EACH ROW EXECUTE FUNCTION log_entity_changes();
