/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React, { useState, useEffect, useMemo } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { 
  LayoutDashboard, 
  PlusCircle, 
  History, 
  Bug, 
  Calendar, 
  Thermometer, 
  Droplets,
  Plus,
  Trash2,
  AlertTriangle,
  CheckCircle,
  Leaf
} from 'lucide-react';
import { Batch, ClimateEntry, InstarStage, INSTAR_REQUIREMENTS } from './types';
import { calculateInstar, getClimateAdvice, getClimateStatus } from './lib/instarEngine';
import { storage } from './lib/storage';
import ClimateGauge from './components/ClimateGauge';
import AdviceCard from './components/AdviceCard';

export default function App() {
  const [activeTab, setActiveTab] = useState<'dashboard' | 'history' | 'new-batch'>('dashboard');
  const [activeBatch, setActiveBatch] = useState<Batch | undefined>(storage.getActiveBatch());
  const [entries, setEntries] = useState<ClimateEntry[]>([]);
  const [newTemp, setNewTemp] = useState(25);
  const [newHum, setNewHum] = useState(75);
  const [showEntrySuccess, setShowEntrySuccess] = useState(false);

  // Load data
  useEffect(() => {
    setEntries(storage.getClimateEntries());
  }, []);

  const currentInstar = useMemo(() => {
    if (!activeBatch) return undefined;
    return calculateInstar(activeBatch.startDate);
  }, [activeBatch]);

  const latestEntry = useMemo(() => {
    if (!activeBatch) return undefined;
    const batchEntries = entries.filter(e => e.batchId === activeBatch.id);
    return batchEntries.sort((a, b) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime())[0];
  }, [activeBatch, entries]);

  const currentAdvice = useMemo(() => {
    if (!currentInstar || !latestEntry) return undefined;
    return getClimateAdvice(latestEntry.temperature, latestEntry.humidity, currentInstar);
  }, [currentInstar, latestEntry]);

  const handleStartBatch = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const formData = new FormData(e.currentTarget);
    const newBatch: Batch = {
      id: crypto.randomUUID(),
      breed: formData.get('breed') as string,
      startDate: formData.get('startDate') as string,
      isActive: true
    };
    
    // Deactivate others
    const batches = storage.getBatches().map(b => ({ ...b, isActive: false }));
    batches.forEach(b => storage.saveBatch(b));
    
    storage.saveBatch(newBatch);
    setActiveBatch(newBatch);
    setActiveTab('dashboard');
  };

  const handleAddEntry = () => {
    if (!activeBatch) return;
    const entry: ClimateEntry = {
      id: crypto.randomUUID(),
      batchId: activeBatch.id,
      timestamp: new Date().toISOString(),
      temperature: newTemp,
      humidity: newHum
    };
    storage.saveClimateEntry(entry);
    setEntries(storage.getClimateEntries());
    setShowEntrySuccess(true);
    setTimeout(() => setShowEntrySuccess(false), 2000);
  };

  return (
    <div className="min-h-screen bg-slate-50 font-sans text-slate-900 pb-24 selection:bg-emerald-100">
      {/* Header */}
      <header className="bg-emerald-900 text-white px-8 py-6 flex justify-between items-center shadow-lg sticky top-0 z-40">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">Reshme-Namma <span className="font-light opacity-80">Pride</span></h1>
          <p className="text-[10px] text-emerald-300 uppercase font-semibold tracking-[0.2em]">Sericulture Guard System</p>
        </div>
        <div className="flex items-center gap-4">
          <div className="hidden sm:block text-right">
            <p className="text-sm font-bold opacity-90">Farmer: Project 52</p>
            <p className="text-[10px] opacity-60 uppercase tracking-widest">Sidlaghatta Zone</p>
          </div>
          <div className="h-10 w-10 rounded-full bg-emerald-700 flex items-center justify-center border border-emerald-500 font-black text-xs shadow-inner">
            P52
          </div>
        </div>
      </header>

      <main className="max-w-md mx-auto px-4 py-6">
        <AnimatePresence mode="wait">
          {activeTab === 'dashboard' && (
            <motion.div
              key="dashboard"
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -10 }}
              className="space-y-6"
            >
              {!activeBatch ? (
                <div className="text-center py-12 space-y-4">
                  <div className="bg-slate-200 w-20 h-20 rounded-full flex items-center justify-center mx-auto text-slate-400">
                    <History size={40} />
                  </div>
                  <h2 className="text-2xl font-bold text-slate-800">No active batch</h2>
                  <p className="text-slate-500 max-w-[240px] mx-auto">Start a new silk batch to begin tracking rearing conditions.</p>
                  <button 
                    onClick={() => setActiveTab('new-batch')}
                    className="bg-emerald-600 text-white px-8 py-4 rounded-2xl font-bold shadow-lg shadow-emerald-200 flex items-center gap-2 mx-auto"
                  >
                    <Plus size={20} /> Start New Batch
                  </button>
                </div>
              ) : (
                <>
                  {/* Batch Summary Card */}
                  <div className="bg-white rounded-3xl p-6 shadow-sm border border-slate-200">
                    <h3 className="text-slate-500 text-[10px] font-black uppercase tracking-widest mb-4">Active Batch Details</h3>
                    <div className="flex justify-between items-end mb-2">
                      <h2 className="text-3xl font-black text-slate-900">{activeBatch.breed}</h2>
                      <span className="bg-emerald-100 text-emerald-700 px-3 py-1 rounded-full text-[10px] font-bold uppercase border border-emerald-200 tracking-tighter">
                        {currentInstar === InstarStage.COCOON ? 'Spinning' : 'Healthy'}
                      </span>
                    </div>
                    <p className="text-slate-500 text-sm mb-6 flex items-center gap-2">
                      <Bug size={14} /> Breed: Local Multivoltine Hybrid
                    </p>
                    
                    <div className="space-y-4">
                      <div className="flex justify-between text-xs font-bold uppercase tracking-wider">
                        <span className="text-slate-400">Current Phase</span>
                        <span className="text-emerald-700">{currentInstar === InstarStage.COCOON ? 'Final Stage' : `${currentInstar}th Instar`}</span>
                      </div>
                      
                      {/* Instar Stepper */}
                      <div className="flex gap-2">
                        {[1, 2, 3, 4, 5].map((i) => (
                          <div 
                            key={i} 
                            className={`h-2.5 rounded-full flex-1 transition-all duration-500 ${
                              i < (currentInstar || 0) ? 'bg-emerald-200' : 
                              i === currentInstar ? 'bg-emerald-600 shadow-md shadow-emerald-100' : 'bg-slate-100 border border-slate-200'
                            }`} 
                          />
                        ))}
                      </div>
                      
                      <div className="pt-2 flex justify-between text-[10px] font-bold text-slate-400 uppercase tracking-widest italic">
                        <span>Day {Math.ceil(Math.abs(new Date().getTime() - new Date(activeBatch.startDate).getTime()) / (1000 * 60 * 60 * 24))} of 20</span>
                        <span>{20 - Math.ceil(Math.abs(new Date().getTime() - new Date(activeBatch.startDate).getTime()) / (1000 * 60 * 60 * 24))} days left</span>
                      </div>
                    </div>
                  </div>

                  {/* Climate Entry Section */}
                  <div className="bg-white rounded-3xl p-8 shadow-sm border border-slate-200 space-y-6">
                    <div className="flex items-center justify-between">
                      <h3 className="text-slate-500 text-[10px] font-black uppercase tracking-widest">New Climate Log</h3>
                      {showEntrySuccess && (
                        <motion.span 
                          initial={{ scale: 0 }} animate={{ scale: 1 }}
                          className="text-[10px] font-black uppercase text-emerald-600 flex items-center gap-1"
                        >
                          <CheckCircle size={14} /> Recorded
                        </motion.span>
                      )}
                    </div>
                    
                    <div className="space-y-6">
                      <div className="flex items-center gap-4">
                        <div className="h-10 w-10 bg-rose-50 text-rose-500 rounded-xl flex items-center justify-center flex-shrink-0">
                          <Thermometer size={20} />
                        </div>
                        <div className="flex-1">
                          <div className="flex justify-between text-[10px] font-black text-slate-400 mb-2 tracking-widest uppercase">
                            <span>Ambient Temp</span>
                            <span className="text-slate-800 text-xs">{newTemp}°C</span>
                          </div>
                          <input 
                            type="range" min="10" max="45" value={newTemp} onChange={(e) => setNewTemp(Number(e.target.value))}
                            className="w-full h-1.5 bg-slate-100 rounded-lg appearance-none cursor-pointer accent-rose-500"
                          />
                        </div>
                      </div>

                      <div className="flex items-center gap-4">
                        <div className="h-10 w-10 bg-blue-50 text-blue-500 rounded-xl flex items-center justify-center flex-shrink-0">
                          <Droplets size={20} />
                        </div>
                        <div className="flex-1">
                          <div className="flex justify-between text-[10px] font-black text-slate-400 mb-2 tracking-widest uppercase">
                            <span>Room Humidity</span>
                            <span className="text-slate-800 text-xs">{newHum}%</span>
                          </div>
                          <input 
                            type="range" min="10" max="100" value={newHum} onChange={(e) => setNewHum(Number(e.target.value))}
                            className="w-full h-1.5 bg-slate-100 rounded-lg appearance-none cursor-pointer accent-blue-500"
                          />
                        </div>
                      </div>
                    </div>

                    <button 
                      onClick={handleAddEntry}
                      className="w-full bg-emerald-600 text-white py-4 rounded-2xl font-black shadow-lg shadow-emerald-50 active:scale-[0.98] transition-all hover:bg-emerald-700 flex items-center justify-center gap-3"
                    >
                      <PlusCircle size={20} /> Log Climate Data
                    </button>
                  </div>

                  {/* Advisor Section */}
                  {currentAdvice && latestEntry && (
                    <div className="space-y-4">
                      <div className="grid grid-cols-2 gap-4">
                        <ClimateGauge 
                          value={latestEntry.temperature}
                          label="Temperature"
                          unit="°C"
                          min={INSTAR_REQUIREMENTS[currentInstar!].minTemp}
                          max={INSTAR_REQUIREMENTS[currentInstar!].maxTemp}
                          icon="temp"
                          status={getClimateStatus(latestEntry.temperature, INSTAR_REQUIREMENTS[currentInstar!].minTemp, INSTAR_REQUIREMENTS[currentInstar!].maxTemp)}
                        />
                        <ClimateGauge 
                          value={latestEntry.humidity}
                          label="Humidity"
                          unit="%"
                          min={INSTAR_REQUIREMENTS[currentInstar!].minHum}
                          max={INSTAR_REQUIREMENTS[currentInstar!].maxHum}
                          icon="hum"
                          status={getClimateStatus(latestEntry.humidity, INSTAR_REQUIREMENTS[currentInstar!].minHum, INSTAR_REQUIREMENTS[currentInstar!].maxHum)}
                        />
                      </div>
                      <AdviceCard advice={currentAdvice} />
                    </div>
                  )}
                </>
              )}
            </motion.div>
          )}

          {activeTab === 'new-batch' && (
            <motion.div
              key="new-batch"
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -10 }}
              className="space-y-6"
            >
              <div className="bg-white rounded-3xl p-8 border border-slate-100 shadow-sm">
                <h2 className="text-2xl font-black mb-2 text-slate-800">Start New Batch</h2>
                <p className="text-sm text-slate-500 mb-8">Set up the growth curve for your new silkworm rearing session.</p>
                
                <form onSubmit={handleStartBatch} className="space-y-6">
                  <div>
                    <label className="block text-xs font-black uppercase text-slate-400 tracking-widest mb-3">Breed Particulars</label>
                    <input 
                      name="breed" 
                      required 
                      placeholder="e.g. CSR2 x CSR4"
                      className="w-full bg-slate-50 border-2 border-slate-100 rounded-2xl px-6 py-4 focus:border-emerald-500 outline-none transition-colors font-bold text-lg"
                    />
                  </div>
                  <div>
                    <label className="block text-xs font-black uppercase text-slate-400 tracking-widest mb-3">Start Date</label>
                    <input 
                      name="startDate" 
                      type="date" 
                      required
                      defaultValue={new Date().toISOString().split('T')[0]}
                      className="w-full bg-slate-50 border-2 border-slate-100 rounded-2xl px-6 py-4 focus:border-emerald-500 outline-none transition-colors font-bold text-lg"
                    />
                  </div>
                  <div className="bg-amber-50 rounded-2xl p-4 flex gap-3 border border-amber-100">
                    <AlertTriangle className="text-amber-600 flex-shrink-0" size={20} />
                    <p className="text-xs text-amber-800 font-medium">Starting a new batch will automatically archive any current active batch.</p>
                  </div>
                  <button 
                    type="submit"
                    className="w-full bg-emerald-600 text-white py-5 rounded-2xl font-black text-lg shadow-xl shadow-emerald-100 active:scale-95 transition-all flex items-center justify-center gap-3"
                  >
                    Confirm & Start Rearing
                  </button>
                </form>
              </div>
            </motion.div>
          )}

          {activeTab === 'history' && (
            <motion.div
              key="history"
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -10 }}
              className="space-y-4"
            >
              <h2 className="text-2xl font-black mb-4 px-2">Batch Archive</h2>
              {storage.getBatches().length === 0 ? (
                <p className="text-center py-12 text-slate-400 font-medium italic">No historical records found.</p>
              ) : (
                <div className="space-y-4">
                  {storage.getBatches().sort((a, b) => new Date(b.startDate).getTime() - new Date(a.startDate).getTime()).map(batch => (
                    <div key={batch.id} className="bg-white p-6 rounded-3xl border border-slate-100 shadow-sm flex items-center justify-between">
                      <div>
                        <div className="flex items-center gap-2 mb-1">
                          <h3 className="font-bold text-slate-800">{batch.breed}</h3>
                          {batch.isActive && <span className="bg-emerald-100 text-emerald-700 text-[10px] font-bold px-2 py-0.5 rounded-full uppercase">Active</span>}
                        </div>
                        <p className="text-xs text-slate-400 font-medium">{new Date(batch.startDate).toLocaleDateString()}</p>
                      </div>
                      <button 
                        onClick={() => {
                          if (confirm('Permanently delete this batch data?')) {
                            storage.deleteBatch(batch.id);
                            if (activeBatch?.id === batch.id) setActiveBatch(undefined);
                            // Refresh
                            setEntries(storage.getClimateEntries());
                          }
                        }}
                        className="p-3 text-slate-300 hover:text-rose-500 hover:bg-rose-50 rounded-xl transition-colors"
                      >
                        <Trash2 size={20} />
                      </button>
                    </div>
                  ))}
                </div>
              )}
            </motion.div>
          )}
        </AnimatePresence>
      </main>

      {/* Bottom Navigation */}
      <nav className="fixed bottom-0 left-0 right-0 bg-white bg-opacity-90 backdrop-blur-xl border-t border-slate-100 px-6 py-4 z-40">
        <div className="max-w-md mx-auto flex items-center justify-between">
          <button 
            onClick={() => setActiveTab('dashboard')}
            className={`flex flex-col items-center gap-1 transition-all ${activeTab === 'dashboard' ? 'text-emerald-600 scale-110' : 'text-slate-400 opacity-60'}`}
          >
            <LayoutDashboard size={24} strokeWidth={activeTab === 'dashboard' ? 3 : 2} />
            <span className="text-[10px] font-black uppercase tracking-tighter">Guard</span>
          </button>
          
          <button 
            onClick={() => setActiveTab('new-batch')}
            className={`flex flex-col items-center gap-1 transition-all ${activeTab === 'new-batch' ? 'text-emerald-600 scale-110' : 'text-slate-400 opacity-60'}`}
          >
            <PlusCircle size={24} strokeWidth={activeTab === 'new-batch' ? 3 : 2} />
            <span className="text-[10px] font-black uppercase tracking-tighter">New Batch</span>
          </button>

          <button 
            onClick={() => setActiveTab('history')}
            className={`flex flex-col items-center gap-1 transition-all ${activeTab === 'history' ? 'text-emerald-600 scale-110' : 'text-slate-400 opacity-60'}`}
          >
            <History size={24} strokeWidth={activeTab === 'history' ? 3 : 2} />
            <span className="text-[10px] font-black uppercase tracking-tighter">Archive</span>
          </button>
        </div>
      </nav>
    </div>
  );
}

