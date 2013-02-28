/*
 * uk.ac.hutton.obiama.model: ConcurrentActionGroup.java 
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
import java.util.HashSet;
import java.util.Set;

import uk.ac.hutton.obiama.action.Action;
import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.exception.ScheduleException;
import uk.ac.hutton.obiama.msb.ModelStateBroker;

/**
 * ConcurrentActionGroup
 * 
 * A scheduled activity that runs a series of actions in parallel. (This simply
 * means that the actions are expected not to interfere with each other.)
 * 
 * @author Gary Polhill
 */
public class ConcurrentActionGroup extends AbstractScheduledAction {
  /**
   * The set of model actions this concurrent activity must run
   */
  protected Set<AbstractNoUpdateAction> modelActions;

  /**
   * Constructor
   * 
   * @param msb The model state broker
   * @param actionURI the URI of the action
   * @param assertedNonTimed <code>true</code> if the action is asserted
   *          non-timed
   */
  public ConcurrentActionGroup(ModelStateBroker msb, URI actionURI, boolean assertedNonTimed) {
    super(msb, actionURI, assertedNonTimed);
    init();
  }

  public ConcurrentActionGroup(ModelStateBroker msb, URI actionURI, double time) throws ScheduleException {
    super(msb, actionURI, time);
    init();
  }

  private void init() {
    modelActions = new HashSet<AbstractNoUpdateAction>();
  }

  /**
   * <!-- addAction -->
   * 
   * Add a scheduled action to the concurrent actions
   * 
   * @param modelAction The scheduled action
   * @throws ScheduleException
   */
  public void addAction(AbstractNoUpdateAction modelAction) throws ScheduleException {
    checkSynchrony(modelAction);
    modelActions.add(modelAction);
  }

  /**
   * <!-- addAction -->
   * 
   * Add another set of concurrent actions to this set
   * 
   * @param modelAction The set of concurrent actions
   * @throws ScheduleException
   */
  public void addAction(ConcurrentActionGroup modelAction) throws ScheduleException {
    checkSynchrony(modelAction);
    modelActions.addAll(modelAction.modelActions);
  }

  /**
   * <!-- checkSynchrony -->
   * 
   * Check the synchrony of this action with another, updating the start time of
   * this action or the other if one of them is non-timed.
   * 
   * @param modelAction
   * @throws ScheduleException
   */
  private void checkSynchrony(AbstractScheduledAction modelAction) throws ScheduleException {
    if(isTimed()) {
      modelAction.synchroniseWith(this);
    }
    else if(modelAction.isTimed()) {
      synchroniseWith(modelAction);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see uk.ac.hutton.obiama.model.AbstractScheduledActivity#synchroniseWith(
   * uk.ac.hutton.obiama.model.AbstractScheduledActivity)
   */
  @Override
  public void synchroniseWith(AbstractScheduledAction modelAction) throws ScheduleException {
    super.synchroniseWith(modelAction);
    for(AbstractNoUpdateAction currentAction: modelActions) {
      currentAction.synchroniseWith(this);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see uk.ac.hutton.obiama.model.AbstractScheduledActivity#setTime(double)
   */
  public void setTime(double time) throws ScheduleException {
    super.setTime(time);
    for(AbstractNoUpdateAction currentAction: modelActions) {
      currentAction.setTime(time);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see uk.ac.hutton.obiama.model.AbstractScheduledActivity#step()
   * 
   * This method could theoretically be multi-threaded, were it not for the
   * following issues: (i) The step() method throws exceptions, which the run()
   * method of a thread would need to handle. (ii) Actions do not have instances
   * for each agent, and so threads would interfere with each other. There are
   * possibly ways round both these issues...
   */
  public void stepImpl() throws IntegrationInconsistencyException {
    for(AbstractNoUpdateAction action: modelActions) {
      action.stepNoUpdate();
    }
    if(allCreators()) msb.updateCreators();
    else
      msb.update();
  }

  /**
   * <!-- getActionSet -->
   * 
   * Return all the action contained in this concurrent action
   * 
   * @see uk.ac.hutton.obiama.model.AbstractScheduledAction#getActionSet()
   */
  @Override
  public Set<Action> getActionSet() {
    Set<Action> actionSet = new HashSet<Action>();
    for(AbstractNoUpdateAction action: modelActions) {
      actionSet.addAll(action.getActionSet());
    }
    return actionSet;
  }

  /**
   * <!-- allCreators -->
   * 
   * @see uk.ac.hutton.obiama.model.AbstractScheduledAction#allCreators()
   * @return
   */
  @Override
  boolean allCreators() {
    for(AbstractNoUpdateAction action: modelActions) {
      if(!action.allCreators()) return false;
    }
    return true;
  }
}
