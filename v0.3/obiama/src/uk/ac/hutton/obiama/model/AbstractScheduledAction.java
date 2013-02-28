/*
 * uk.ac.hutton.obiama.model: AbstractScheduledAction.java
 * 
 * Copyright (C) 2013 The James Hutton Institute
 * 
 * This file is part of obiama-0.3.
 * 
 * obiama-0.3 is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * obiama-0.3 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with obiama-0.3. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact information: Gary Polhill, The James Hutton Institute, Craigiebuckler,
 * Aberdeen. AB15 8QH. UK. gary.polhill@hutton.ac.uk
 */
package uk.ac.hutton.obiama.model;

import java.net.URI;
import java.util.LinkedList;
import java.util.Set;

import uk.ac.hutton.obiama.action.Action;
import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.exception.ScheduleException;
import uk.ac.hutton.obiama.msb.ModelStateBroker;

/**
 * AbstractScheduledAction
 * 
 * An abstract class for wrapping actions so they can be called from a schedule
 * 
 * @author Gary Polhill
 */
public abstract class AbstractScheduledAction extends AbstractNonScheduledAction {

  /**
   * The time (if any) that this action is scheduled to start at
   */
  protected Double time;

  /**
   * <code>true</code> if the action has been asserted not timed, and so cannot
   * be converted to a timed action
   */
  protected boolean assertedNonTimed;

  /**
   * Constructor passing in the model state broker
   * 
   * @param msb the model state broker
   * @param actionURI the URI of the action
   * @param assertedNonTimed <code>true</code> if the action is asserted
   *          non-timed
   */
  AbstractScheduledAction(ModelStateBroker msb, URI actionURI, boolean assertedNonTimed) {
    super(msb, actionURI);
    this.assertedNonTimed = assertedNonTimed;
    time = null;
  }

  /**
   * Constructor for timed activities
   * 
   * @param msb the model state broker
   * @param actionURI the URI of the action
   * @param time the time at which the activity occurs
   * @throws ScheduleException
   */
  AbstractScheduledAction(ModelStateBroker msb, URI actionURI, double time) throws ScheduleException {
    this(msb, actionURI, false);
    if(time < 0.0 || Double.isInfinite(time) || Double.isNaN(time)) {
      throw new ScheduleException(actionURI, "negative, infinite, or not-a-number start time: " + time);
    }
    this.time = time;
  }

  /**
   * <!-- assertNonTimed -->
   * 
   * Assert the action is not a timed action
   * 
   * @throws ScheduleException
   */
  public void assertNonTimed() throws ScheduleException {
    if(isTimed()) throw new ScheduleException(this, "cannot assert timed action is non-timed");
    assertedNonTimed = true;
  }

  /**
   * <!-- isAssertedNonTimed -->
   * 
   * @return <code>true</code> if the action has been asserted non-timed
   */
  public boolean isAssertedNonTimed() {
    return assertedNonTimed;
  }

  /**
   * <!-- getTime -->
   * 
   * This method will throw a NullPointerException if isTimed() is false.
   * 
   * @return the time at which the activity occurs
   */
  public double getTime() {
    return time;
  }

  /**
   * <!-- setTime -->
   * 
   * Set the time the action occurs--the action must not already have been
   * assigned a different time.
   * 
   * @param time the time at which the action occurs
   */
  public void setTime(double time) throws ScheduleException {
    if(time < 0.0 || Double.isInfinite(time) || Double.isNaN(time)) {
      throw new ScheduleException(actionURI, "negative, infinite, or not-a-number start time: " + time);
    }
    if(assertedNonTimed) {
      throw new ScheduleException(actionURI, "attempt to convert asserted non-timed action to timed action");
    }
    if(this.time != null && this.time != time) {
      throw new ScheduleException(this, "Attempt to change time from " + this.time + " to " + time);
    }
    this.time = time;
  }

  /**
   * <!-- synchroniseWith -->
   * 
   * Set the time of this action to that of another--the action must not have
   * already have been assigned a different time.
   * 
   * @param event the action to synchronise this one with
   */
  public void synchroniseWith(AbstractScheduledAction event) throws ScheduleException {
    if(assertedNonTimed) {
      throw new ScheduleException(actionURI, "attempt to convert asserted non-timed action to timed action");
    }
    if(this.time != null && this.time != event.time) {
      throw new ScheduleException(this, "Cannot synchronise with " + event.getURI()
        + " as they occur at different times (" + this.time + " and " + event.time + ", respectively)");
    }
    this.time = event.time;
  }

  /**
   * <!-- isTimed -->
   * 
   * @return <code>true</code> if the activity has a time
   */
  public boolean isTimed() {
    return time != null;
  }

  /**
   * <!-- step -->
   * 
   * Run a step of the simulation
   * 
   * @throws IntegrationInconsistencyException
   * @throws ScheduleException
   */
  public void step() throws IntegrationInconsistencyException, ScheduleException {
    Log.action(actionURI);
    stepImpl();
  }

  /**
   * <!-- stepImpl -->
   * 
   * Implement one step of the simulation
   * 
   * @throws IntegrationInconsistencyException
   * @throws ScheduleException
   */
  protected abstract void stepImpl() throws IntegrationInconsistencyException, ScheduleException;

  /**
   * <!-- getActionList -->
   * 
   * Default method to return the action as a list of items to be stepped.
   * 
   * @return the action embedded in a singleton linked list
   * @throws ScheduleException
   */
  public LinkedList<AbstractScheduledAction> getActionList() throws ScheduleException {
    if(isTimed()) {
      throw new ScheduleException(this, "timed action cannot be used to generate action list");
    }
    LinkedList<AbstractScheduledAction> actionList = new LinkedList<AbstractScheduledAction>();
    actionList.addLast(this);
    return actionList;
  }

  /**
   * <!-- getActionSet -->
   * 
   * @return the set of actions contained in this action (group)
   */
  public abstract Set<Action> getActionSet();

  /**
   * <!-- allCreators -->
   * 
   * @return <code>true</code> if all actions in this action (group) are
   *         {@link uk.ac.hutton.obiama.action.Creator}s
   */
  abstract boolean allCreators();
}
