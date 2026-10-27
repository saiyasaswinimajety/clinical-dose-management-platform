import React, { useState } from 'react';
import { DoseOrder, OrderStatus } from '../types';
import { INITIAL_ORDERS } from '../utils/constants';
import { 
  Activity, 
  CheckCircle2, 
  Clock, 
  Truck, 
  FileCheck, 
  ShieldCheck, 
  Plus, 
  Search,
  Filter,
  AlertTriangle
} from 'lucide-react';

interface Props {
  onNavigateToNewOrder: () => void;
  onNavigateToDecay: (order: DoseOrder) => void;
}

export const DoseOrdersDashboard: React.FC<Props> = ({ onNavigateToNewOrder, onNavigateToDecay }) => {
  const [orders] = useState<DoseOrder[]>(INITIAL_ORDERS);
  const [statusFilter, setStatusFilter] = useState<string>('ALL');
  const [searchTerm, setSearchTerm] = useState<string>('');

  const filteredOrders = orders.filter((order) => {
    const matchesStatus = statusFilter === 'ALL' || order.status === statusFilter;
    const matchesSearch = 
      order.orderNumber.toLowerCase().includes(searchTerm.toLowerCase()) ||
      order.patientHash.toLowerCase().includes(searchTerm.toLowerCase()) ||
      order.site.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      order.protocolCode.toLowerCase().includes(searchTerm.toLowerCase());
    return matchesStatus && matchesSearch;
  });

  const getStatusBadge = (status: OrderStatus) => {
    switch (status) {
      case 'ORDER_SUBMITTED':
        return <span className="status-badge badge-submitted"><Clock size={12} /> Submitted</span>;
      case 'RADIO_SYNTHESIS':
        return <span className="status-badge badge-synthesis"><Activity size={12} /> Radio-Synthesis</span>;
      case 'DECAY_COMPENSATED':
        return <span className="status-badge badge-decay"><CheckCircle2 size={12} /> Decay-Compensated</span>;
      case 'QA_RELEASED':
        return <span className="status-badge badge-qa"><FileCheck size={12} /> QA Released</span>;
      case 'DISPATCHED':
        return <span className="status-badge badge-dispatched"><Truck size={12} /> Dispatched</span>;
      default:
        return <span className="status-badge badge-submitted">{status}</span>;
    }
  };

  return (
    <div>
      {/* Top Stats Banner */}
      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon-wrapper" style={{ background: '#EFF6FF', color: '#1E40AF' }}>
            <Activity size={24} />
          </div>
          <div>
            <div className="stat-value">4</div>
            <div className="stat-label">Active Regulated Batches</div>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon-wrapper" style={{ background: '#ECFDF5', color: '#059669' }}>
            <CheckCircle2 size={24} />
          </div>
          <div>
            <div className="stat-value">100%</div>
            <div className="stat-label">RAM License Compliance</div>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon-wrapper" style={{ background: '#FFFBEB', color: '#D97706' }}>
            <Clock size={24} />
          </div>
          <div>
            <div className="stat-value">&lt; 15 min</div>
            <div className="stat-label">Hot Lab Calibration Window</div>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon-wrapper" style={{ background: '#F5F3FF', color: '#7C3AED' }}>
            <ShieldCheck size={24} />
          </div>
          <div>
            <div className="stat-value">21 CFR 11</div>
            <div className="stat-label">Cryptographic Audit Chain</div>
          </div>
        </div>
      </div>

      {/* Main Table Card */}
      <div className="card">
        <div className="page-title-row">
          <div>
            <h2 className="page-title">Clinical Dose Order Grid</h2>
            <p className="page-description">
              Precision radiopharmaceutical supply-demand synchronization, dynamic decay calculation, and batch lifecycle tracking.
            </p>
          </div>
          <button className="btn btn-primary" onClick={onNavigateToNewOrder}>
            <Plus size={16} /> New Clinical Dose Order
          </button>
        </div>

        {/* Filters Row */}
        <div style={{ display: 'flex', gap: '12px', marginBottom: '16px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', background: '#F8FAFC', padding: '6px 12px', borderRadius: '6px', border: '1px solid #E2E8F0', flex: 1 }}>
            <Search size={16} color="#64748B" />
            <input 
              type="text" 
              placeholder="Search by Order ID, Patient Hash, Protocol, or Clinical Site..." 
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              style={{ border: 'none', background: 'transparent', outline: 'none', width: '100%', fontSize: '13px' }}
            />
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <Filter size={16} color="#64748B" />
            <select 
              value={statusFilter} 
              onChange={(e) => setStatusFilter(e.target.value)}
              className="form-select"
              style={{ padding: '6px 12px', fontSize: '13px' }}
            >
              <option value="ALL">All Statuses</option>
              <option value="ORDER_SUBMITTED">Submitted</option>
              <option value="RADIO_SYNTHESIS">Radio-Synthesis</option>
              <option value="DECAY_COMPENSATED">Decay-Compensated</option>
              <option value="QA_RELEASED">QA Released</option>
              <option value="DISPATCHED">Dispatched</option>
            </select>
          </div>
        </div>

        {/* Orders Table */}
        <div className="table-container">
          <table className="clinical-table">
            <thead>
              <tr>
                <th>Order Number</th>
                <th>Patient Hash</th>
                <th>Protocol</th>
                <th>Radiotracer & Isotope</th>
                <th>Prescribed Dose</th>
                <th>EOS Sizing</th>
                <th>Scheduled Calibration</th>
                <th>Clinical Site</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredOrders.map((order) => (
                <tr key={order.id}>
                  <td>
                    <span className="mono" style={{ fontWeight: 600, color: '#1E40AF' }}>
                      {order.orderNumber}
                    </span>
                  </td>
                  <td>
                    <span className="mono" style={{ background: '#F1F5F9', padding: '2px 6px', borderRadius: '4px' }}>
                      {order.patientHash}
                    </span>
                  </td>
                  <td>
                    <span style={{ fontWeight: 500 }}>{order.protocolCode}</span>
                  </td>
                  <td>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                      <span style={{ 
                        background: '#FEF3C7', 
                        color: '#92400E', 
                        fontWeight: 700, 
                        padding: '2px 6px', 
                        borderRadius: '4px',
                        fontSize: '11px' 
                      }}>
                        {order.isotope.symbol}
                      </span>
                      <span>{order.isotope.clinicalTracer}</span>
                    </div>
                  </td>
                  <td>
                    <span style={{ fontWeight: 600 }}>{order.targetDoseMci.toFixed(1)} mCi</span>
                  </td>
                  <td>
                    <span className="mono" style={{ color: '#D97706', fontWeight: 600 }}>
                      {order.eosOvershootMci.toFixed(1)} mCi
                    </span>
                  </td>
                  <td>
                    <div style={{ fontSize: '12px' }}>{order.calibrationTime}</div>
                  </td>
                  <td>
                    <div style={{ fontSize: '12px', fontWeight: 500 }}>{order.site.name}</div>
                    <div style={{ fontSize: '10px', color: '#64748B' }}>
                      RAM: {order.site.ramLicenseNumber} {order.site.isRamValid && <span style={{ color: '#059669' }}>● Valid</span>}
                    </div>
                  </td>
                  <td>{getStatusBadge(order.status)}</td>
                  <td>
                    <div style={{ display: 'flex', gap: '6px' }}>
                      <button 
                        className="btn btn-outline btn-sm"
                        onClick={() => onNavigateToDecay(order)}
                        title="Inspect Decay Curve & Nuclear Calculations"
                      >
                        <Activity size={12} /> Decay Math
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
