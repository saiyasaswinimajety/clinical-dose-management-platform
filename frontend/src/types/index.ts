export type OrderStatus =
  | 'ORDER_SUBMITTED'
  | 'RADIO_SYNTHESIS'
  | 'DECAY_COMPENSATED'
  | 'QA_RELEASED'
  | 'DISPATCHED'
  | 'DELIVERED'
  | 'ASSAY_RECONCILED';

export interface Isotope {
  symbol: string;
  name: string;
  halfLifeMinutes: number;
  decayConstantPerMin: number;
  radiationType: 'Positron (β+)' | 'Beta (β-)' | 'Isomeric Gamma (γ)';
  clinicalTracer: string;
  defaultUnit: 'mCi' | 'MBq';
}

export interface ClinicalSite {
  id: string;
  name: string;
  city: string;
  state: string;
  ramLicenseNumber: string;
  ramExpirationDate: string;
  authorizedActivityMci: number;
  isRamValid: boolean;
  transitBufferMinutes: number;
}

export interface DoseOrder {
  id: string;
  orderNumber: string;
  patientHash: string;
  protocolCode: string;
  site: ClinicalSite;
  isotope: Isotope;
  targetDoseMci: number;
  calibrationTime: string; // ISO string or display
  synthesisEndTime: string;
  eosOvershootMci: number;
  status: OrderStatus;
  cyclotronBatch: string;
  qaSignoff?: {
    pharmacistName: string;
    signedAt: string;
    tokenHash: string;
  };
  notes?: string;
}

export interface AuditLogEntry {
  id: string;
  timestamp: string;
  operator: string;
  role: string;
  action: 'CREATE' | 'UPDATE' | 'QA_RELEASE' | 'DECAY_CALC' | 'DISPATCH' | 'E_SIGNATURE';
  entityType: 'DoseOrder' | 'MfgSchedule' | 'RAMLicense' | 'AssayRecord';
  entityId: string;
  details: string;
  previousValue: string;
  newValue: string;
  sha256Hash: string;
  verified: boolean;
}

export interface HotLabAssay {
  orderNumber: string;
  patientHash: string;
  tracer: string;
  scheduledCalibTime: string;
  actualInjectionTime: string;
  preInjectionVialMci: number;
  residualSyringeMci: number;
  netDeliveredMci: number;
  protocolTargetMci: number;
  complianceVariance: number;
  radiopharmacist: string;
  calibratorSerial: string;
}
