import React, { useState } from 'react';
import { DoseOrdersDashboard } from './pages/DoseOrdersDashboard';
import { DecayCalibrationEngine } from './pages/DecayCalibrationEngine';
import { OrderCreationWizard } from './pages/OrderCreationWizard';
import { AuditTrailInspector } from './pages/AuditTrailInspector';
import { HotLabAssayReconciliation } from './pages/HotLabAssayReconciliation';
import { DoseOrder } from './types';
import { 
  Table, 
  Activity, 
  PlusCircle, 
  ShieldCheck, 
  ClipboardCheck, 
  UserCheck, 
  AlertCircle 
} from 'lucide-react';

type TabType = 'orders' | 'decay' | 'wizard' | 'audit' | 'assay';

export const App: React.FC = () => {
  const [activeTab, setActiveTab] = useState<TabType>('orders');
  const [selectedPersona, setSelectedPersona] = useState<string>('Hospital Clinical Coordinator');

  return (
    <div style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
      {/* Top Header Bar */}
      <header className="header-bar">
        <div className="brand-container">
          <div className="logo-badge">
            <span>Lilly</span>
          </div>
          <div>
            <div className="brand-title">Avid Radiopharmaceuticals • Dose Management Platform</div>
            <div className="brand-subtitle">FDA 21 CFR Part 11 Regulated Life Sciences Cloud Portal</div>
          </div>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
          <div className="persona-badge">
            <UserCheck size={14} color="#38BDF8" />
            <span>Role:</span>
            <select
              value={selectedPersona}
              onChange={(e) => setSelectedPersona(e.target.value)}
              style={{
                background: 'transparent',
                border: 'none',
                color: 'white',
                fontWeight: 600,
                outline: 'none',
                cursor: 'pointer',
                fontSize: '12px'
              }}
            >
              <option value="Hospital Clinical Coordinator" style={{ color: '#0F172A' }}>
                Hospital Clinical Coordinator (Penn Medicine)
              </option>
              <option value="Radiopharmacist / Production Lead" style={{ color: '#0F172A' }}>
                Radiopharmacist / Production Lead (Avid Cyclotron)
              </option>
              <option value="QA Release Auditor" style={{ color: '#0F172A' }}>
                QA Release Auditor (Eli Lilly Compliance)
              </option>
            </select>
          </div>
        </div>
      </header>

      {/* Navigation Tabs */}
      <nav className="nav-tabs">
        <button
          className={`nav-tab-button ${activeTab === 'orders' ? 'active' : ''}`}
          onClick={() => setActiveTab('orders')}
        >
          <Table size={16} /> Clinical Dose Orders
        </button>

        <button
          className={`nav-tab-button ${activeTab === 'decay' ? 'active' : ''}`}
          onClick={() => setActiveTab('decay')}
        >
          <Activity size={16} /> Nuclear Decay Math & Sizing
        </button>

        <button
          className={`nav-tab-button ${activeTab === 'wizard' ? 'active' : ''}`}
          onClick={() => setActiveTab('wizard')}
        >
          <PlusCircle size={16} /> New Prescription Wizard
        </button>

        <button
          className={`nav-tab-button ${activeTab === 'assay' ? 'active' : ''}`}
          onClick={() => setActiveTab('assay')}
        >
          <ClipboardCheck size={16} /> Hot Lab Assay Reconciliation
        </button>

        <button
          className={`nav-tab-button ${activeTab === 'audit' ? 'active' : ''}`}
          onClick={() => setActiveTab('audit')}
        >
          <ShieldCheck size={16} /> 21 CFR Part 11 Audit Trail
        </button>
      </nav>

      {/* Main Body */}
      <main className="main-content">
        {activeTab === 'orders' && (
          <DoseOrdersDashboard
            onNavigateToNewOrder={() => setActiveTab('wizard')}
            onNavigateToDecay={() => setActiveTab('decay')}
          />
        )}

        {activeTab === 'decay' && <DecayCalibrationEngine />}

        {activeTab === 'wizard' && (
          <OrderCreationWizard onOrderCreated={() => setActiveTab('orders')} />
        )}

        {activeTab === 'assay' && <HotLabAssayReconciliation />}

        {activeTab === 'audit' && <AuditTrailInspector />}
      </main>

      {/* Footer */}
      <footer style={{
        marginTop: 'auto',
        background: '#0B192C',
        color: '#94A3B8',
        padding: '14px 28px',
        fontSize: '11px',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        borderTop: '1px solid #1E293B'
      }}>
        <div>
          Clinical Dose Management Platform v3.2.0 • GxP &amp; 21 CFR Part 11 Validated System
        </div>
        <div>
          Engineered by Sai Yasaswini Majety (Full Stack Systems Engineer) • React 18, TypeScript, Python, Spring Boot, Temporal.io
        </div>
      </footer>
    </div>
  );
};
