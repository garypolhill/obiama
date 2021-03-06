/* uk.ac.hutton.obiama.model: RepastAction.java
 *
 * Copyright (C) 2013 The James Hutton Institute
 *
 * This file is part of obiama-0.3.
 *
 * obiama-0.3 is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of 
 * the License, or (at your option) any later version.
 *
 * obiama-0.3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with obiama-0.3. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * Contact information:
 *   Gary Polhill
 *   The James Hutton Institute, Craigiebuckler, Aberdeen. AB15 8QH. UK.
 *   gary.polhill@hutton.ac.uk
 */
package uk.ac.hutton.obiama.model;

import uchicago.src.sim.engine.BasicAction;
import uk.ac.hutton.obiama.exception.ErrorHandler;
import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.exception.ScheduleException;

/**
 * <!-- RepastAction -->
 *
 * @author Gary Polhill
 */
public class RepastAction extends BasicAction {
  
  private AbstractScheduledAction action;

  public RepastAction(AbstractScheduledAction action) {
    this.action = action;
  }

  /**
   * <!-- execute -->
   *
   * @see uchicago.src.sim.engine.BasicAction#execute()
   */
  @Override
  public void execute() {
    try {
      action.step();
    }
    catch(IntegrationInconsistencyException e) {
      e.printStackTrace();
      ErrorHandler.redo(e, "stepping action " + action.getURI());
    }
    catch(ScheduleException e) {
      e.printStackTrace();
      ErrorHandler.redo(e, "stepping action " + action.getURI());
    }
  }

}
