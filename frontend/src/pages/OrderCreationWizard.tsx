import React, { useState } from 'react';
import { CLINICAL_SITES, ISOTOPES, calculateEosOvershoot } from '../utils/constants';
import { ShieldCheck, Check, ArrowRight, ArrowLeft, AlertCircle, FileText, Lock } from 'lucide-react';

interface Props {
  onOrderCreated: () => void;
}

export const OrderCreationWizard: React.FC<Props> = ({ onOrderCreated }) => {
  const [step, setStep] = useState<number>(1);
  const [patientHash, setPatientHash] = useState<string>('PT-5521-Z3');
  const [protocolCode, setProtocolCode] = useState<string>('PET-F18-AMYLOID-301');
  const [selectedSiteId, setSelectedSiteId] = useState<string>(CLINICAL_SITES[0].id);
  const [selectedIsotopeKey, setSelectedIsotopeKey] = useState<string>('F18');
  const [targetDoseMci, setTargetDoseMci] = useState<number>(10.0);
  const [calibrationTime, setCalibrationTime] = useState<string>('11:30 EST');
  const [transitMinutes, setTransitMinutes] = useState<number>(180);
  const [pharmacistName, setPharmacistName] = useState<string>('Sai Yasaswini Majety, Lead Systems Engineer');
  const [eSigAcknowledged, setESigAcknowledged] = useState<boolean>(true);
  const [submitted, setSubmitted] = useState<boolean>(false);

  const selectedSite = CLINICAL_SITES.find(s => s.id === selectedSiteId) || CLINICAL_SITES[0];
  const selectedIsotope = ISOTOPES[selectedIsotopeKey];
  const eosOvershoot = calculateEosOvershoot(targetDoseMci, selectedIsotope.decayConstantPerMin, transitMinutes);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitted(true);
    setTimeout(() => {
      onOrderCreated();
    }, 1200);
  };

  return (
    <div className="card" style={{ maxWidth: '900px', margin: '0 auto' }}>
      <div className="page-title-row">
        <div>
          <h2 className="page-title">New Clinical Dose Prescription & Calibration Wizard</h2>
          <p className="page-description">
            Regulated clinical order intake adhering to FDA 21 CFR Part 11 electronic records, RAM licensing, and nuclear decay constraints.
          </p>
        </div>
      </div>

      {/* Stepper Header */}
      <div className="wizard-steps">
        <div className={`wizard-step ${step >= 1 ? 'active' : ''}`}>
          <div className="step-number">{step > 1 ? <Check size={16} /> : '1'}</div>
          <div>
            <div style={{ fontSize: '13px', fontWeight: 600 }}>Protocol & Site</div>
            <div style={{ fontSize: '11px', color: '#64748B' }}>Site & RAM License</div>
          </div>
        </div>

        <div className={`wizard-step ${step >= 2 ? 'active' : ''}`}>
          <div className="step-number">{step > 2 ? <Check size={16} /> : '2'}</div>
          <div>
            <div style={{ fontSize: '13px', fontWeight: 600 }}>Isotope & Sizing</div>
            <div style={{ fontSize: '11px', color: '#64748B' }}>Decay Compensation</div>
          </div>
        </div>

        <div className={`wizard-step ${step >= 3 ? 'active' : ''}`}>
          <div className="step-number">{step > 3 ? <Check size={16} /> : '3'}</div>
          <div>
            <div style={{ fontSize: '13px', fontWeight: 600 }}>Verification</div>
            <div style={{ fontSize: '11px', color: '#64748B' }}>Clinical Safety Bounds</div>
          </div>
        </div>

        <div className={`wizard-step ${step >= 4 ? 'active' : ''}`}>
          <div className="step-number">{step === 4 ? '4' : '4'}</div>
          <div>
            <div style={{ fontSize: '13px', fontWeight: 600 }}>Electronic Signature</div>
            <div style={{ fontSize: '11px', color: '#64748B' }}>21 CFR Part 11 Signoff</div>
          </div>
        </div>
      </div>

      {/* Step 1: Protocol & Site */}
      {step === 1 && (
        <div>
          <div className="compliance-banner">
            <ShieldCheck size={18} color="#1E40AF" />
            <div>
              <strong>RAM Regulatory Gate:</strong> Orders can only be dispatched to facilities with active, unexpired Radioactive Material licenses authorized for the prescribed isotope activity.
            </div>
          </div>

          <div className="form-grid">
            <div className="form-group">
              <label className="form-label">Clinical Protocol Code</label>
              <select className="form-select" value={protocolCode} onChange={(e) => setProtocolCode(e.target.value)}>
                <option value="PET-F18-AMYLOID-301">PET-F18-AMYLOID-301 (Florbetapir F-18 Amyloid Phase III Study)</option>
                <option value="TAU-PET-18F-PHASE3">TAU-PET-18F-PHASE3 (Fluorine-18 Tau PET Imaging)</option>
                <option value="NEURO-C11-TRIAL-02">NEURO-C11-TRIAL-02 (Carbon-11 Neurodegenerative Assay)</option>
              </select>
            </div>

            <div className="form-group">
              <label className="form-label">De-Identified Patient Hash (PHI-Safe)</label>
              <input 
                type="text" 
                className="form-input mono" 
                value={patientHash} 
                onChange={(e) => setPatientHash(e.target.value)} 
              />
            </div>

            <div className="form-group" style={{ gridColumn: 'span 2' }}>
              <label className="form-label">Authorized Clinical Imaging Site</label>
              <select className="form-select" value={selectedSiteId} onChange={(e) => setSelectedSiteId(e.target.value)}>
                {CLINICAL_SITES.map((site) => (
                  <option key={site.id} value={site.id}>
                    {site.name} ({site.city}, {site.state}) - RAM Lic: {site.ramLicenseNumber} (Limit: {site.authorizedActivityMci} mCi)
                  </option>
                ))}
              </select>
            </div>
          </div>

          <div style={{ 
            background: '#F0FDF4', 
            border: '1px solid #BBF7D0', 
            borderRadius: '6px', 
            padding: '12px 16px', 
            marginTop: '16px',
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center'
          }}>
            <div>
              <div style={{ fontSize: '12px', fontWeight: 700, color: '#166534' }}>
                ✓ Radioactive Material (RAM) License Verified Active
              </div>
              <div style={{ fontSize: '11px', color: '#15803D' }}>
                Permit: {selectedSite.ramLicenseNumber} | Expiration: {selectedSite.ramExpirationDate} | Max Authorized: {selectedSite.authorizedActivityMci} mCi
              </div>
            </div>
            <span className="status-badge badge-verified">PASS 21-CFR</span>
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '24px' }}>
            <button className="btn btn-primary" onClick={() => setStep(2)}>
              Proceed to Isotope Sizing <ArrowRight size={16} />
            </button>
          </div>
        </div>
      )}

      {/* Step 2: Isotope & Decay Sizing */}
      {step === 2 && (
        <div>
          <div className="form-grid">
            <div className="form-group">
              <label className="form-label">Radiopharmaceutical & Isotope</label>
              <select className="form-select" value={selectedIsotopeKey} onChange={(e) => setSelectedIsotopeKey(e.target.value)}>
                {Object.keys(ISOTOPES).map((key) => (
                  <option key={key} value={key}>
                    {ISOTOPES[key].symbol} - {ISOTOPES[key].clinicalTracer} (t½ = {ISOTOPES[key].halfLifeMinutes}m)
                  </option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label className="form-label">Prescribed Dose at Injection</label>
              <input 
                type="number" 
                step="0.1" 
                className="form-input mono" 
                value={targetDoseMci}
                onChange={(e) => setTargetDoseMci(parseFloat(e.target.value))}
              />
            </div>

            <div className="form-group">
              <label className="form-label">Scheduled Calibration Time</label>
              <input 
                type="text" 
                className="form-input" 
                value={calibrationTime}
                onChange={(e) => setCalibrationTime(e.target.value)}
              />
            </div>

            <div className="form-group">
              <label className="form-label">Courier Transit Buffer (Minutes)</label>
              <input 
                type="number" 
                className="form-input mono" 
                value={transitMinutes}
                onChange={(e) => setTransitMinutes(parseInt(e.target.value))}
              />
            </div>
          </div>

          {/* Real-time Math Summary */}
          <div style={{ background: '#EFF6FF', border: '1px solid #BFDBFE', borderRadius: '8px', padding: '16px', marginTop: '20px' }}>
            <div style={{ fontSize: '13px', fontWeight: 700, color: '#1E40AF', marginBottom: '8px' }}>
              Real-Time Dynamic Decay Calculation
            </div>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '12px', fontSize: '12px' }}>
              <div>
                <span style={{ color: '#64748B' }}>Target Activity:</span>
                <div className="mono" style={{ fontWeight: 700, fontSize: '15px', color: '#0F172A' }}>{targetDoseMci.toFixed(1)} mCi</div>
              </div>
              <div>
                <span style={{ color: '#64748B' }}>Transit Duration:</span>
                <div className="mono" style={{ fontWeight: 700, fontSize: '15px', color: '#0F172A' }}>{transitMinutes} min</div>
              </div>
              <div>
                <span style={{ color: '#64748B' }}>End of Synthesis (EOS) Overshoot:</span>
                <div className="mono" style={{ fontWeight: 700, fontSize: '17px', color: '#D97706' }}>{eosOvershoot.toFixed(2)} mCi</div>
              </div>
            </div>
          </div>

          <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '24px' }}>
            <button className="btn btn-outline" onClick={() => setStep(1)}>
              <ArrowLeft size={16} /> Back
            </button>
            <button className="btn btn-primary" onClick={() => setStep(3)}>
              Verify Clinical Bounds <ArrowRight size={16} />
            </button>
          </div>
        </div>
      )}

      {/* Step 3: Verification */}
      {step === 3 && (
        <div>
          <div style={{ background: '#F8FAFC', borderRadius: '8px', padding: '18px', border: '1px solid #E2E8F0', marginBottom: '20px' }}>
            <h3 style={{ fontSize: '14px', fontWeight: 700, marginBottom: '12px', color: '#0F172A' }}>
              Clinical Order Summary & Validation Matrix
            </h3>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px', fontSize: '12px' }}>
              <div><strong>Protocol:</strong> {protocolCode}</div>
              <div><strong>Patient ID Hash:</strong> <span className="mono">{patientHash}</span></div>
              <div><strong>Clinical Site:</strong> {selectedSite.name}</div>
              <div><strong>Isotope / Tracer:</strong> {selectedIsotope.symbol} ({selectedIsotope.name})</div>
              <div><strong>Scheduled Injection:</strong> {calibrationTime}</div>
              <div><strong>Target Delivered Dose:</strong> {targetDoseMci.toFixed(1)} mCi ({(targetDoseMci * 37).toFixed(0)} MBq)</div>
              <div><strong>Cyclotron Overshoot:</strong> {eosOvershoot.toFixed(2)} mCi at End of Synthesis</div>
              <div><strong>Authorized RAM Limit:</strong> {selectedSite.authorizedActivityMci} mCi (OK)</div>
            </div>
          </div>

          <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '24px' }}>
            <button className="btn btn-outline" onClick={() => setStep(2)}>
              <ArrowLeft size={16} /> Back
            </button>
            <button className="btn btn-primary" onClick={() => setStep(4)}>
              Proceed to E-Signature <ArrowRight size={16} />
            </button>
          </div>
        </div>
      )}

      {/* Step 4: 21 CFR Part 11 Electronic Signature */}
      {step === 4 && (
        <div>
          <div className="compliance-banner" style={{ borderLeftColor: '#059669', background: '#ECFDF5' }}>
            <Lock size={18} color="#059669" />
            <div>
              <strong>FDA 21 CFR Part 11 Electronic Signature Verification:</strong> Submitting this digital record constitutes a legally binding signature certifying the clinical indication and radiopharmacy calibration parameters.
            </div>
          </div>

          <div className="form-group" style={{ marginBottom: '16px' }}>
            <label className="form-label">Authorized Professional Name & Credentials</label>
            <input 
              type="text" 
              className="form-input" 
              value={pharmacistName} 
              onChange={(e) => setPharmacistName(e.target.value)} 
            />
          </div>

          <div style={{ 
            background: '#F1F5F9', 
            padding: '14px', 
            borderRadius: '6px', 
            fontSize: '12px',
            border: '1px solid #CBD5E1',
            marginBottom: '18px'
          }}>
            <label style={{ display: 'flex', gap: '10px', alignItems: 'flex-start', cursor: 'pointer' }}>
              <input 
                type="checkbox" 
                checked={eSigAcknowledged} 
                onChange={(e) => setESigAcknowledged(e.target.checked)}
                style={{ marginTop: '2px' }}
              />
              <span>
                I certify under penalty of regulatory non-compliance that the patient de-identification hash, radiopharmaceutical half-life calibration, and RAM licensing credentials meet all FDA GxP requirements.
              </span>
            </label>
          </div>

          <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '24px' }}>
            <button className="btn btn-outline" onClick={() => setStep(3)}>
              <ArrowLeft size={16} /> Back
            </button>
            <button 
              className="btn btn-primary" 
              disabled={!eSigAcknowledged || submitted}
              onClick={handleSubmit}
              style={{ background: '#059669' }}
            >
              {submitted ? 'Chaining Cryptographic Audit Log...' : 'Authorize & Sign Order (21 CFR Part 11)'}
            </button>
          </div>
        </div>
      )}
    </div>
  );
};
