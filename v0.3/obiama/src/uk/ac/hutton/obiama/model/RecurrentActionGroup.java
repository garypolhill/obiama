/*
 * uk.ac.hutton.obiama.model: RecurrentActionGroup.java
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
 * RecurrentActionGroup
 * 
 * An action to be repeated a specified number of times
 * 
 * @author Gary Polhill
 */
public class RecurrentActionGroup extends AbstractScheduledAction {

  /**
   * The action to be repeated
   */
  protected AbstractScheduledAction repeatedAction;

  /**
   * The number of repetitions of that action
   */
  protected Integer nRepetitions;

  /**
   * The repeat interval for the timed schedule
   */
  protected Double interval;

  /**
   * Constructor
   * 
   * @param repeatedAction the action to be repeated
   * @param nRepetitions the number of repetitions to make
   * @param msb the model state broker
   * @param actionURI the URI of the action in the schedule ontology
   * @param assertedNonTimed <code>true</code> if the action is asserted
   *          non-timed
   * @throws ScheduleException
   */
  public RecurrentActionGroup(AbstractScheduledAction repeatedAction, int nRepetitions, ModelStateBroker msb,
      URI actionURI, boolean assertedNonTimed) throws ScheduleException {
    super(msb, actionURI, assertedNonTimed);
    init(repeatedAction);
    this.nRepetitions = nRepetitions;
    interval = null;
  }

  /**
   * Constructor for timed actions
   * 
   * @param repeatedAction the action to be repeated
   * @param interval the repeat interval
   * @param msb the model state broker
   * @param actionURI the URI of the action in the schedule ontology
   * @param time the time at which the repetitions start
   * @throws ScheduleException
   */
  public RecurrentActionGroup(AbstractScheduledAction repeatedAction, double interval, ModelStateBroker msb,
      URI actionURI, double time) throws ScheduleException {
    super(msb, actionURI, time);
    init(repeatedAction);
    if(interval <= 0.0 || Double.isNaN(interval) || Double.isInfinite(interval)) {
      throw new ScheduleException(actionURI, "non-positive, infinite or not-a-number interval: " + interval);
    }
    this.interval = interval;
    nRepetitions = null;
  }

  /**
   * <!-- init -->
   * 
   * Initialise the action
   * 
   * @param repeatedAction the action to be repeated
   * @throws ScheduleException
   */
  private void init(AbstractScheduledAction repeatedAction) throws ScheduleException {
    if(repeatedAction.isTimed()) {
      throw new ScheduleException(this, "Timed action " + repeatedAction.getURI()
        + " cannot be added to a repeating action");
    }
    repeatedAction.assertNonTimed();
    this.repeatedAction = repeatedAction;
  }

  /**
   * <!-- getNRepetitions -->
   * 
   * Note that this method will throw a NullPointerException for timed recurrent
   * action groups
   * 
   * @return the number of repetitions of the non-timed recurrent action group
   */
  public int getNRepetitions() {
    return nRepetitions;
  }

  /**
   * <!-- getInterval -->
   * 
   * Note that this method will throw a NullPointerException for non-timed
   * recurrent action groups
   * 
   * @return the repeat interval for timed recurrent action groups
   */
  public double getInterval() {
    return interval;
  }
  
  /**
   * <!-- getRepeatedAction -->
   *
   * @return the repeated action of this action group
   */
  public AbstractScheduledAction getRepeatedAction() {
    return repeatedAction;
  }

  /*
   * (non-Javadoc)
   * 
   * @see uk.ac.hutton.obiama.model.AbstractScheduledActivity#step()
   */
  @Override
  public void stepImpl() throws IntegrationInconsistencyException, ScheduleException {
    if(isTimed()) {
      throw new ScheduleException(this, "A timed repeating action cannot be stepped");
    }
    for(int i = 1; i <= nRepetitions; i++) {
      repeatedAction.step();
    }
  }
  
  /**
   * <!-- getActionList -->
   * 
   * Return a list of actions corresponding to the requested number of repetitions
   *
   * @see uk.ac.hutton.obiama.model.AbstractScheduledAction#getActionList()
   */
  @Override
  public LinkedList<AbstractScheduledAction> getActionList() throws ScheduleException {
    if(isTimed()) {
      throw new ScheduleException(this, "timed action cannot be used to generate action list");
    }
    LinkedList<AbstractScheduledAction> actionList = new LinkedList<AbstractScheduledAction>();
    for(int i = 1; i <= nRepetitions; i++) {
      actionList.addAll(repeatedAction.getActionList());
    }
    return actionList;
  }

  /**
   * <!-- getActionSet -->
   * 
   * Return the set of actions in the repeated action (group)
   *
   * @see uk.ac.hutton.obiama.model.AbstractScheduledAction#getActionSet()
   */
  @Override
  public Set<Action> getActionSet() {
    return repeatedAction.getActionSet();
  }

  /**
   * <!-- allCreators -->
   *
   * @see uk.ac.hutton.obiama.model.AbstractScheduledAction#allCreators()
   * @return
   */
  @Override
  boolean allCreators() {
    return repeatedAction.allCreators();
  }

}
