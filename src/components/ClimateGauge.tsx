/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import { motion } from 'motion/react';
import { Info } from 'lucide-react';

interface GaugeProps {
  value: number;
  label: string;
  unit: string;
  min: number;
  max: number;
  status: 'safe' | 'caution' | 'danger';
  icon: 'temp' | 'hum';
}

export default function ClimateGauge({ value, label, unit, min, max, status }: GaugeProps) {
  const getGradient = () => {
    // Specific gradients from design
    if (status === 'safe') return 'conic-gradient(#10b981 0% 100%)';
    if (status === 'caution') return 'conic-gradient(#f59e0b 0% 100%)';
    return 'conic-gradient(#ef4444 0% 100%)';
  };

  const getStatusText = () => {
    switch (status) {
      case 'safe': return 'Optimal';
      case 'caution': return 'Caution';
      case 'danger': return 'Critical';
    }
  };

  const getStatusColor = () => {
    switch (status) {
      case 'safe': return 'text-emerald-600';
      case 'caution': return 'text-amber-600';
      case 'danger': return 'text-rose-600';
    }
  };

  return (
    <motion.div 
      initial={{ opacity: 0, scale: 0.9 }}
      animate={{ opacity: 1, scale: 1 }}
      className="bg-white p-6 rounded-3xl border border-slate-200 shadow-sm flex flex-col items-center"
    >
      <h3 className="text-slate-500 text-[10px] font-bold uppercase tracking-wider mb-4 text-center">{label}</h3>
      
      <div className="flex justify-center mb-4">
        <div 
          className="climate-gauge" 
          style={{ background: getGradient() }}
        >
          <div className="gauge-inner">
            <span className="text-3xl font-black text-slate-900">{value}{unit}</span>
            <span className={`text-[10px] font-bold uppercase ${getStatusColor()}`}>
              {getStatusText()}
            </span>
          </div>
        </div>
      </div>

      <div className="flex items-center gap-1 text-[10px] text-slate-400 font-medium italic">
        <Info size={12} />
        <span>Ideal Range: {min}{unit} – {max}{unit}</span>
      </div>
    </motion.div>
  );
}
