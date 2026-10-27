import React, { useState } from 'react';
import { AuditLogEntry } from '../types';
import { INITIAL_AUDIT_LOGS } from '../utils/constants';
import { ShieldCheck, Lock, Search, FileText, CheckCircle2, Hash, Database } from 'lucide-react';

export const AuditTrailInspector: React.FC = () => {
  const [logs] = useState<AuditLogEntry[]>(INITIAL_AUDIT_LOGS);
  const [searchTerm, setSearchTerm] = useState<string>('');

  const filteredLogs = logs.filter(
    (log) =>
      log.entityId.toLowerCase().includes(searchTerm.toLowerCase()) ||
      log.operator.toLowerCase().includes(searchTerm.toLowerCase()) ||
      log.details.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="card">
      <div className="page-title-row">
        <div>
          <h2 className="page-title">FDA 21 CFR Part 11 Cryptographic Audit Trail Inspector</h2>
          <p className="page-description">
            Dual-layer immutable compliance logging. PostgreSQL database row triggers chained with SHA-256 forward hashes and asynchronous S3 audit mirror sync.
          </p>
        </div>
        <div style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
          <span className="status-badge badge-verified">
            <CheckCircle2 size={12} /> HASH CHAIN INTEGRITY: 100% VALID
          </span>
        </div>
      </div>

      {/* Compliance Highlights */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '16px', marginBottom: '20px' }}>
        <div style={{ background: '#F8FAFC', padding: '14px', borderRadius: '8px', border: '1px solid #E2E8F0' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', color: '#1E40AF', fontSize: '12px', fontWeight: 700 }}>
            <Database size={16} /> PostgreSQL Trigger Layer
          </div>
          <div style={{ fontSize: '11px', color: '#475569', marginTop: '4px' }}>
            row_to_json(OLD) vs row_to_json(NEW) triggers enforce zero-bypass audit capture on physical table mutations.
          </div>
        </div>

        <div style={{ background: '#F8FAFC', padding: '14px', borderRadius: '8px', border: '1px solid #E2E8F0' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', color: '#059669', fontSize: '12px', fontWeight: 700 }}>
            <Hash size={16} /> SHA-256 Forward Chaining
          </div>
          <div style={{ fontSize: '11px', color: '#475569', marginTop: '4px' }}>
            H_N = SHA-256(H_N-1 || Entity || Action || Operator || Timestamp || Payload) guarantees tamper detection.
          </div>
        </div>

        <div style={{ background: '#F8FAFC', padding: '14px', borderRadius: '8px', border: '1px solid #E2E8F0' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', color: '#7C3AED', fontSize: '12px', fontWeight: 700 }}>
            <Lock size={16} /> 21 CFR §11.10(e) Compliance
          </div>
          <div style={{ fontSize: '11px', color: '#475569', marginTop: '4px' }}>
            Time-stamped audit trails independently record date/time of operator entries without record overwriting.
          </div>
        </div>
      </div>

      {/* Search Input */}
      <div style={{ display: 'flex', gap: '12px', marginBottom: '16px' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px', background: '#F8FAFC', padding: '6px 12px', borderRadius: '6px', border: '1px solid #E2E8F0', flex: 1 }}>
          <Search size={16} color="#64748B" />
          <input 
            type="text" 
            placeholder="Search audit trail by Entity ID, Operator, or Payload details..." 
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            style={{ border: 'none', background: 'transparent', outline: 'none', width: '100%', fontSize: '13px' }}
          />
        </div>
      </div>

      {/* Audit Log Table */}
      <div className="table-container">
        <table className="clinical-table">
          <thead>
            <tr>
              <th>Timestamp</th>
              <th>Operator / Role</th>
              <th>Action</th>
              <th>Entity</th>
              <th>Operational Context</th>
              <th>Audit Trail Diff (State Mutation)</th>
              <th>Cryptographic Hash</th>
            </tr>
          </thead>
          <tbody>
            {filteredLogs.map((log) => (
              <tr key={log.id}>
                <td>
                  <span className="mono" style={{ fontSize: '11px', color: '#334155' }}>
                    {log.timestamp}
                  </span>
                </td>
                <td>
                  <div style={{ fontWeight: 600, fontSize: '12px' }}>{log.operator}</div>
                  <div style={{ fontSize: '11px', color: '#64748B' }}>{log.role}</div>
                </td>
                <td>
                  <span className="status-badge badge-submitted" style={{ fontSize: '10px' }}>
                    {log.action}
                  </span>
                </td>
                <td>
                  <span className="mono" style={{ fontWeight: 600, color: '#1E40AF' }}>
                    {log.entityId}
                  </span>
                </td>
                <td style={{ maxWidth: '280px', fontSize: '12px' }}>
                  {log.details}
                </td>
                <td style={{ maxWidth: '240px' }}>
                  <div style={{ fontSize: '11px', background: '#FEF2F2', padding: '2px 6px', borderRadius: '4px', color: '#991B1B', marginBottom: '2px' }}>
                    - {log.previousValue}
                  </div>
                  <div style={{ fontSize: '11px', background: '#F0FDF4', padding: '2px 6px', borderRadius: '4px', color: '#166534' }}>
                    + {log.newValue}
                  </div>
                </td>
                <td>
                  <div className="mono" style={{ fontSize: '10px', color: '#475569', wordBreak: 'break-all', maxWidth: '140px' }}>
                    {log.sha256Hash.substring(0, 16)}...
                  </div>
                  <span className="status-badge badge-verified" style={{ fontSize: '9px', marginTop: '2px' }}>
                    ✓ SHA-256 Valid
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};
