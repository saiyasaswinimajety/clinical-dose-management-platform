import React, { useState } from 'react';
import { HotLabAssay } from '../types';
import { INITIAL_ASSAYS } from '../utils/constants';
import { Activity, CheckCircle2, AlertTriangle, ShieldCheck, FileSpreadsheet, Plus } from 'lucide-react';

export const HotLabAssayReconciliation: React.FC = () => {
  const [assays] = useState<HotLabAssay[]>(INITIAL_ASSAYS);

  return (
    <div className="card">
      <div className="page-title-row">
        <div>
          <h2 className="page-title">Hot Lab Assay Reconciliation & Clinical Loop Closure</h2>
          <p className="page-description">
            Post-delivery patient administration logging adhering to USP &lt;823&gt;. Syringe residual subtraction, net administered activity, and protocol variance compliance.
          </p>
        </div>
        <button className="btn btn-primary">
          <Plus size={16} /> Record Hot Lab Assay
        </button>
      </div>

      <div className="compliance-banner">
        <ShieldCheck size={18} color="#1E40AF" />
        <div>
          <strong>Protocol Compliance Threshold:</strong> Administered dose must fall within ±10% of prescribed protocol activity (9.0 mCi to 11.0 mCi for 18F-FDG Oncology PET scans).
        </div>
      </div>

      <div className="table-container">
        <table className="clinical-table">
          <thead>
            <tr>
              <th>Order Number</th>
              <th>Patient Hash</th>
              <th>Radiotracer</th>
              <th>Scheduled Time</th>
              <th>Actual Injection</th>
              <th>Pre-Injection Assay</th>
              <th>Syringe Residual</th>
              <th>Net Delivered Activity</th>
              <th>Protocol Variance</th>
              <th>Certifying Radiopharmacist</th>
            </tr>
          </thead>
          <tbody>
            {assays.map((assay) => (
              <tr key={assay.orderNumber}>
                <td>
                  <span className="mono" style={{ fontWeight: 600, color: '#1E40AF' }}>
                    {assay.orderNumber}
                  </span>
                </td>
                <td>
                  <span className="mono" style={{ background: '#F1F5F9', padding: '2px 6px', borderRadius: '4px' }}>
                    {assay.patientHash}
                  </span>
                </td>
                <td>{assay.tracer}</td>
                <td><span className="mono" style={{ fontSize: '11px' }}>{assay.scheduledCalibTime}</span></td>
                <td><span className="mono" style={{ fontSize: '11px', fontWeight: 600 }}>{assay.actualInjectionTime}</span></td>
                <td><span className="mono">{assay.preInjectionVialMci.toFixed(2)} mCi</span></td>
                <td><span className="mono" style={{ color: '#D97706' }}>{assay.residualSyringeMci.toFixed(2)} mCi</span></td>
                <td>
                  <span className="mono" style={{ fontWeight: 700, color: '#059669', fontSize: '14px' }}>
                    {assay.netDeliveredMci.toFixed(2)} mCi
                  </span>
                </td>
                <td>
                  <span className="status-badge badge-verified" style={{ fontSize: '11px' }}>
                    {assay.complianceVariance > 0 ? `+${assay.complianceVariance}%` : `${assay.complianceVariance}%`}
                  </span>
                </td>
                <td>
                  <div style={{ fontSize: '12px', fontWeight: 500 }}>{assay.radiopharmacist}</div>
                  <div style={{ fontSize: '10px', color: '#64748B' }}>Dose Calibrator: {assay.calibratorSerial}</div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};
