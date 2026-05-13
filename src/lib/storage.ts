/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import { Batch, ClimateEntry } from '../types';

const BATCHES_KEY = 'reshme_batches';
const CLIMATE_KEY = 'reshme_climate';

export const storage = {
  getBatches: (): Batch[] => {
    const data = localStorage.getItem(BATCHES_KEY);
    return data ? JSON.parse(data) : [];
  },
  saveBatch: (batch: Batch) => {
    const batches = storage.getBatches();
    const index = batches.findIndex(b => b.id === batch.id);
    if (index >= 0) {
      batches[index] = batch;
    } else {
      batches.push(batch);
    }
    localStorage.setItem(BATCHES_KEY, JSON.stringify(batches));
  },
  deleteBatch: (id: string) => {
    const batches = storage.getBatches().filter(b => b.id !== id);
    localStorage.setItem(BATCHES_KEY, JSON.stringify(batches));
    const climate = storage.getClimateEntries().filter(e => e.batchId !== id);
    localStorage.setItem(CLIMATE_KEY, JSON.stringify(climate));
  },
  getClimateEntries: (): ClimateEntry[] => {
    const data = localStorage.getItem(CLIMATE_KEY);
    return data ? JSON.parse(data) : [];
  },
  saveClimateEntry: (entry: ClimateEntry) => {
    const entries = storage.getClimateEntries();
    entries.push(entry);
    localStorage.setItem(CLIMATE_KEY, JSON.stringify(entries));
  },
  getActiveBatch: (): Batch | undefined => {
    return storage.getBatches().find(b => b.isActive);
  }
};
