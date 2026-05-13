/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import { motion } from 'motion/react';
import { AlertCircle, CheckCircle2, AlertTriangle } from 'lucide-react';
import { Advice } from '../lib/instarEngine';

interface AdviceCardProps {
  advice: Advice;
}

export default function AdviceCard({ advice }: AdviceCardProps) {
  const isOptimal = advice.type === 'optimal';
  const isDanger = advice.type === 'danger';

  const getBorderColor = () => {
    if (isOptimal) return 'border-emerald-500';
    if (isDanger) return 'border-red-500';
    return 'border-amber-500';
  };

  const getBgColor = () => {
    if (isOptimal) return 'bg-emerald-50';
    if (isDanger) return 'bg-red-50';
    return 'bg-amber-50';
  };

  const getIconBg = () => {
    if (isOptimal) return 'bg-emerald-100';
    if (isDanger) return 'bg-red-100';
    return 'bg-amber-100';
  };

  const getIconColor = () => {
    if (isOptimal) return 'text-emerald-600';
    if (isDanger) return 'text-red-600';
    return 'text-amber-600';
  };

  const getTextColor = () => {
    if (isOptimal) return 'text-emerald-900';
    if (isDanger) return 'text-red-900';
    return 'text-amber-900';
  };

  const getSubTextColor = () => {
    if (isOptimal) return 'text-emerald-700';
    if (isDanger) return 'text-red-700';
    return 'text-amber-700';
  };

  return (
    <motion.div
      initial={{ opacity: 0, y: 10 }}
      animate={{ opacity: 1, y: 0 }}
      className={`${getBgColor()} border-l-4 ${getBorderColor()} rounded-2xl p-6 shadow-sm flex gap-4 items-start`}
    >
      <div className={`h-12 w-12 ${getIconBg()} rounded-full flex items-center justify-center flex-shrink-0`}>
        {isOptimal ? (
          <CheckCircle2 size={24} className={getIconColor()} />
        ) : isDanger ? (
          <AlertCircle size={24} className={getIconColor()} />
        ) : (
          <AlertTriangle size={24} className={getIconColor()} />
        )}
      </div>
      
      <div className="flex-1">
        <h4 className={`${getTextColor()} font-black text-lg`}>{advice.title}</h4>
        <p className={`${getSubTextColor()} mt-1 text-sm leading-relaxed`}>{advice.message}</p>
        
        {advice.actions.length > 0 && (
          <div className="flex flex-wrap gap-2 mt-4">
            {advice.actions.map((action, i) => (
              <span 
                key={i}
                className="bg-white border border-slate-200 text-slate-700 px-3 py-1.5 rounded-lg text-xs font-bold shadow-sm"
              >
                {action}
              </span>
            ))}
          </div>
        )}
      </div>
    </motion.div>
  );
}
