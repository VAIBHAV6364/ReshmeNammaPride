/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import { InstarStage, INSTAR_REQUIREMENTS, InstarSpecs } from '../types';

export function calculateInstar(startDateStr: string): InstarStage {
  const startDate = new Date(startDateStr);
  const now = new Date();
  const diffTime = Math.abs(now.getTime() - startDate.getTime());
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

  if (diffDays <= 3) return InstarStage.STAGE_1;
  if (diffDays <= 6) return InstarStage.STAGE_2;
  if (diffDays <= 10) return InstarStage.STAGE_3;
  if (diffDays <= 15) return InstarStage.STAGE_4;
  if (diffDays <= 20) return InstarStage.STAGE_5;
  return InstarStage.COCOON;
}

export interface Advice {
  type: 'optimal' | 'warning' | 'danger';
  title: string;
  message: string;
  actions: string[];
}

export function getClimateAdvice(temp: number, hum: number, stage: InstarStage): Advice {
  const specs = INSTAR_REQUIREMENTS[stage];
  const actions: string[] = [];
  let type: 'optimal' | 'warning' | 'danger' = 'optimal';
  let title = "Conditions are Optimal";
  let message = "The environment is perfect for the current growth stage.";

  const isTempLow = temp < specs.minTemp;
  const isTempHigh = temp > specs.maxTemp;
  const isHumLow = hum < specs.minHum;
  const isHumHigh = hum > specs.maxHum;

  if (isTempHigh) {
    actions.push("Increase ventilation by opening windows");
    actions.push("Spread wet gunny bags on the floor");
    actions.push("Ensure mulberry leaves are fresh and kept in shade");
    type = 'danger';
    title = "Temperature too High!";
  } else if (isTempLow) {
    actions.push("Provide artificial heating safely (heaters/charcoal)");
    actions.push("Close windows and prevent draft");
    type = 'warning';
    title = "Temperature too Low";
  }

  if (isHumHigh) {
    actions.push("Improve cross-ventilation");
    actions.push("Sprinkle lime powder on the rearing beds");
    if (type !== 'danger') {
      type = 'warning';
      title = "Humidity too High";
    }
  } else if (isHumLow) {
    actions.push("Sprinkle water on the floor and walls");
    actions.push("Use wet curtains/curtains soaked in disinfectant");
    type = 'danger';
    title = "Humidity too Low!";
  }

  if (actions.length > 0) {
    message = `Critical alert for ${stage === InstarStage.COCOON ? 'Cocooning' : `Instar ${stage}`} stage. Please take corrective actions immediately.`;
  }

  return { type, title, message, actions };
}

export function getClimateStatus(val: number, min: number, max: number): 'safe' | 'caution' | 'danger' {
  if (val >= min && val <= max) return 'safe';
  const margin = 2; // allowance before danger
  if (val >= min - margin && val <= max + margin) return 'caution';
  return 'danger';
}
