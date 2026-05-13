/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

export interface Batch {
  id: string;
  startDate: string; // ISO string
  breed: string;
  isActive: boolean;
  harvestDate?: string;
}

export interface ClimateEntry {
  id: string;
  batchId: string;
  timestamp: string;
  temperature: number;
  humidity: number;
}

export enum InstarStage {
  STAGE_1 = 1,
  STAGE_2 = 2,
  STAGE_3 = 3,
  STAGE_4 = 4,
  STAGE_5 = 5,
  COCOON = 6
}

export interface InstarSpecs {
  minTemp: number;
  maxTemp: number;
  minHum: number;
  maxHum: number;
  description: string;
}

export const INSTAR_REQUIREMENTS: Record<InstarStage, InstarSpecs> = {
  [InstarStage.STAGE_1]: { minTemp: 26, maxTemp: 28, minHum: 85, maxHum: 90, description: "1st Instar: Very small, extremely sensitive." },
  [InstarStage.STAGE_2]: { minTemp: 25, maxTemp: 27, minHum: 80, maxHum: 85, description: "2nd Instar: Slightly stronger, growing steadily." },
  [InstarStage.STAGE_3]: { minTemp: 24, maxTemp: 26, minHum: 75, maxHum: 80, description: "3rd Instar: Rapid growth period begins." },
  [InstarStage.STAGE_4]: { minTemp: 23, maxTemp: 25, minHum: 70, maxHum: 75, description: "4th Instar: Heavy feeding stage." },
  [InstarStage.STAGE_5]: { minTemp: 22, maxTemp: 24, minHum: 65, maxHum: 70, description: "5th Instar: Maximum growth, preparing to spin." },
  [InstarStage.COCOON]: { minTemp: 22, maxTemp: 24, minHum: 60, maxHum: 65, description: "Cocooning: Spinning silk. Maintain calm environment." }
};
