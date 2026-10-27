import React, { useState } from 'react';
import { ISOTOPES } from '../utils/constants';
import { calculateDecay, calculateEosOvershoot } from '../utils/constants';
import { Activity, Clock, ShieldAlert, Zap, Layers, RefreshCw } from 'lucide-react';

export const DecayCalibrationEngine: React.FC = () => {
  const [selectedIsotopeKey, setSelectedIsotopeKey] = useState<string>('F18');
  const [targetDoseMci, setTargetDoseMci] = useState<number>(10.0);
  const [transitMinutes, setTransitMinutes] = useState<number>(180);

  const isotope = ISOTOPES[selectedIsotopeKey];
  const eosOvershoot = calculateEosOvershoot(targetDoseMci, isotope.decayConstantPerMin, transitMinutes);

  // Generate SVG curve points across 0 to 300 minutes
  const totalChartMinutes = 300;
  const numSteps = 50;
  const svgWidth = 720;
  const svgHeight = 260;
  const padding = 40;

  const points: { x: number; y: number; time: number; activity: number }[] = [];
  for (let i = 0; i <= numSteps; i++) {
    const time = (i / numSteps) * totalChartMinutes;
    // Activity decaying from eosOvershoot at t=0 down to t
    const activity = calculateDecay(eosOvershoot, isotope.decayConstantPerMin, time);
    const x = padding + (time / totalChartMinutes) * (svgWidth - 2 * padding);
    const y = svgHeight - padding - (activity / (eosOvershoot * 1.15)) * (svgHeight - 2 * padding);
    points.push({ x, y, time, activity });
  }

  const polylineStr = points.map(p => `${p.x.toFixed(1)},${p.y.toFixed(1)}`).join(' ');

  // Coordinates of calibration target point
  const calibX = padding + (transitMinutes / totalChartMinutes) * (svgWidth - 2 * padding);
  const calibY = svgHeight - padding - (targetDoseMci / (eosOvershoot * 1.15)) * (svgHeight - 2 * padding);

  return (
    <div>
      <div className="card">
        <div className="page-title-row">
          <div>
            <h2 className="page-title">Nuclear Physics: Exponential Decay & Calibration Engine</h2>
            <p className="page-description">
              Continuous first-order radioactive decay compensation: A(t) = A₀ · e^(−λt). Dynamic cyclotron synthesis overshoot sizing.
            </p>
          </div>
          <div style={{ display: 'flex', gap: '8px' }}>
            {Object.keys(ISOTOPES).map((key) => (
              <button
                key={key}
                onClick={() => setSelectedIsotopeKey(key)}
                className={`btn btn-sm ${selectedIsotopeKey === key ? 'btn-primary' : 'btn-outline'}`}
              >
                {ISOTOPES[key].symbol} ({ISOTOPES[key].name})
              </button>
            ))}
          </div>
        </div>

        {/* Physics Formula Header Card */}
        <div style={{
          background: 'linear-gradient(135deg, #0F172A 0%, #1E293B 100%)',
          borderRadius: '8px',
          padding: '16px 20px',
          color: 'white',
          marginBottom: '20px',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center'
        }}>
          <div>
            <div style={{ fontSize: '11px', textTransform: 'uppercase', color: '#94A3B8', letterSpacing: '0.8px' }}>
              Physical Decay Constant (λ = ln(2) / t½)
            </div>
            <div className="mono" style={{ fontSize: '18px', fontWeight: 600, color: '#38BDF8', marginTop: '2px' }}>
              λ = {isotope.decayConstantPerMin.toFixed(7)} min⁻¹ &nbsp;|&nbsp; Half-Life t½ = {isotope.halfLifeMinutes} min
            </div>
            <div style={{ fontSize: '12px', color: '#CBD5E1', marginTop: '4px' }}>
              {isotope.clinicalTracer} &nbsp;•&nbsp; {isotope.radiationType}
            </div>
          </div>
          <div style={{ textAlign: 'right' }}>
            <div style={{ fontSize: '11px', color: '#94A3B8', textTransform: 'uppercase' }}>Required Cyclotron Overshoot (t_EOS)</div>
            <div className="mono" style={{ fontSize: '26px', fontWeight: 700, color: '#F59E0B' }}>
              {eosOvershoot.toFixed(2)} mCi
            </div>
            <div style={{ fontSize: '11px', color: '#A7F3D0' }}>
              Yields exact {targetDoseMci.toFixed(1)} mCi at {transitMinutes} min
            </div>
          </div>
        </div>

        {/* Interactive Controls & Chart Grid */}
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 2.5fr', gap: '24px' }}>
          {/* Controls */}
          <div style={{ background: '#F8FAFC', padding: '18px', borderRadius: '8px', border: '1px solid #E2E8F0' }}>
            <h3 style={{ fontSize: '14px', fontWeight: 700, marginBottom: '14px', color: '#1E293B' }}>
              Calibration Sizing Inputs
            </h3>

            <div className="form-group" style={{ marginBottom: '16px' }}>
              <label className="form-label">Prescribed Injection Dose (A_calib)</label>
              <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                <input 
                  type="range" 
                  min="1" 
                  max="30" 
                  step="0.5"
                  value={targetDoseMci}
                  onChange={(e) => setTargetDoseMci(parseFloat(e.target.value))}
                  style={{ flex: 1 }}
                />
                <span className="mono" style={{ fontWeight: 700, fontSize: '14px', width: '60px' }}>
                  {targetDoseMci.toFixed(1)} mCi
                </span>
              </div>
            </div>

            <div className="form-group" style={{ marginBottom: '16px' }}>
              <label className="form-label">Transit Duration (Δt = t_calib - t_EOS)</label>
              <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                <input 
                  type="range" 
                  min="15" 
                  max="280" 
                  step="5"
                  value={transitMinutes}
                  onChange={(e) => setTransitMinutes(parseInt(e.target.value))}
                  style={{ flex: 1 }}
                />
                <span className="mono" style={{ fontWeight: 700, fontSize: '14px', width: '60px' }}>
                  {transitMinutes} min
                </span>
              </div>
              <span style={{ fontSize: '11px', color: '#64748B' }}>
                Includes packaging, airport ground transfer, and hospital hot-lab intake.
              </span>
            </div>

            <div style={{ borderTop: '1px solid #E2E8F0', paddingTop: '12px', marginTop: '12px' }}>
              <div style={{ fontSize: '12px', fontWeight: 600, color: '#334155', marginBottom: '6px' }}>
                Sizing Multiplier:
              </div>
              <div className="mono" style={{ fontSize: '16px', fontWeight: 700, color: '#2563EB' }}>
                {(eosOvershoot / targetDoseMci).toFixed(2)}x Initial Activity
              </div>
              <div style={{ fontSize: '11px', color: '#64748B', marginTop: '2px' }}>
                Radioactive loss during transit: {(100 - (100 / (eosOvershoot / targetDoseMci))).toFixed(1)}%
              </div>
            </div>
          </div>

          {/* Interactive SVG Chart */}
          <div style={{ background: '#FFFFFF', padding: '16px', borderRadius: '8px', border: '1px solid #E2E8F0' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
              <div style={{ fontSize: '13px', fontWeight: 600, color: '#0F172A' }}>
                Exponential Radioactivity Decay Profile [0 to {totalChartMinutes} min]
              </div>
              <div style={{ display: 'flex', gap: '12px', fontSize: '11px', color: '#64748B' }}>
                <span>● Curve: Activity (mCi)</span>
                <span style={{ color: '#D97706' }}>▲ t_EOS (Overshoot)</span>
                <span style={{ color: '#059669' }}>★ t_calib (Target Window)</span>
              </div>
            </div>

            <svg viewBox={`0 0 ${svgWidth} ${svgHeight}`} style={{ width: '100%', height: 'auto', background: '#F8FAFC', borderRadius: '6px' }}>
              {/* Grid Lines */}
              <line x1={padding} y1={svgHeight - padding} x2={svgWidth - padding} y2={svgHeight - padding} stroke="#CBD5E1" strokeWidth="1" />
              <line x1={padding} y1={padding} x2={padding} y2={svgHeight - padding} stroke="#CBD5E1" strokeWidth="1" />

              {/* Decay Curve Polyline */}
              <polyline
                fill="none"
                stroke="#2563EB"
                strokeWidth="2.5"
                points={polylineStr}
              />

              {/* Fill under curve */}
              <polygon
                fill="rgba(37, 99, 235, 0.08)"
                points={`${padding},${svgHeight - padding} ${polylineStr} ${points[points.length - 1].x},${svgHeight - padding}`}
              />

              {/* Calibration target line */}
              <line 
                x1={calibX} 
                y1={padding} 
                x2={calibX} 
                y2={svgHeight - padding} 
                stroke="#10B981" 
                strokeWidth="1.5" 
                strokeDasharray="4 3" 
              />

              {/* Point at t_EOS */}
              <circle cx={points[0].x} cy={points[0].y} r="5" fill="#D97706" />
              <text x={points[0].x + 8} y={points[0].y + 4} fill="#D97706" fontSize="11" fontWeight="700" fontFamily="JetBrains Mono">
                t_EOS: {eosOvershoot.toFixed(1)} mCi
              </text>

              {/* Point at t_calib */}
              <circle cx={calibX} cy={calibY} r="6" fill="#059669" />
              <text x={calibX - 50} y={calibY - 10} fill="#059669" fontSize="11" fontWeight="700" fontFamily="JetBrains Mono">
                t_calib: {targetDoseMci.toFixed(1)} mCi ({transitMinutes}m)
              </text>

              {/* Axis Labels */}
              <text x={padding} y={svgHeight - 15} fill="#64748B" fontSize="10">0 min (t_EOS)</text>
              <text x={svgWidth / 2} y={svgHeight - 15} fill="#64748B" fontSize="10">Transit Time (Minutes)</text>
              <text x={svgWidth - padding - 40} y={svgHeight - 15} fill="#64748B" fontSize="10">{totalChartMinutes} min</text>
            </svg>
          </div>
        </div>
      </div>
    </div>
  );
};
