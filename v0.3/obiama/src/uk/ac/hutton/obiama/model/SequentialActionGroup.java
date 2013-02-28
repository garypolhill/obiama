/*
 * uk.ac.hutton.obiama.model: SequentialActionGroup.java
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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import uk.ac.hutton.obiama.action.Action;
import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.exception.ScheduleException;
import uk.ac.hutton.obiama.msb.ModelStateBroker;

/**
 * SequentialActionGroup
 * 
 * A scheduled sequence of actions
 * 
 * @author Gary Polhill
 */
public class SequentialActionGroup extends AbstractScheduledAction {
  /**
   * The action sequence
   */
  protected LinkedList<AbstractScheduledAction> actionSequence;

  /**
   * For timed sequences, the elapsed time between each action in the sequence
   */
  protected Double increment;

  /**
   * Constructor
   * 
   * @param msb the model state broker
   * @param actionURI the URI of the action sequence in the schedule ontology
   * @param assertedNonTimed <code>true</code> if the action is asserted
   *          non-timed
   * @param firstAction the first action in the sequence
   * @throws ScheduleException
   */
  public SequentialActionGroup(ModelStateBroker msb, URI actionURI, boolean assertedNonTimed,
      AbstractScheduledAction firstAction) throws ScheduleException {
    super(msb, actionURI, assertedNonTimed);
    init(firstAction);
    increment = null;
    if(firstAction instanceof SequentialActionGroup) {
      synchroniseWith(firstAction);
    }
    else if(firstAction.isTimed()) {
      throw new ScheduleException(actionURI, "Non-timed action sequence with timed sub-action " + firstAction.getURI());
    }
  }

  /**
   * Constructor for timed sequences
   * 
   * @param msb the model state broker
   * @param actionURI the URI of the action sequence in the schedule ontology
   * @param firstAction the first action in the sequence
   * @param time the time at which the sequence starts
   * @param increment the time gap between consecutive members of the sequence
   * @throws ScheduleException
   */
  public SequentialActionGroup(ModelStateBroker msb, URI actionURI, AbstractScheduledAction firstAction, double time,
      double increment) throws ScheduleException {
    super(msb, actionURI, time);
    init(firstAction);
    if(increment <= 0.0 || Double.isNaN(increment) || Double.isInfinite(increment)) {
      throw new ScheduleException(actionURI, "non-positive, infinite, or not-a-number increment: " + increment);
    }
    this.increment = increment;
    firstAction.synchroniseWith(this);
  }

  /**
   * <!-- init -->
   * 
   * Initialise the action sequence
   * 
   * @param firstAction first action in the sequence
   * @throws ScheduleException
   */
  private void init(AbstractScheduledAction firstAction) throws ScheduleException {
    actionSequence = new LinkedList<AbstractScheduledAction>();
    if(firstAction instanceof SequentialActionGroup) {
      actionSequence.addAll(((SequentialActionGroup)firstAction).actionSequence);
    }
    else {
      addAction(firstAction);
    }
  }

  /**
   * <!-- getIncrement -->
   * 
   * Note this method will throw a NullPointerException for non-timed sequences
   * 
   * @return the increment (of a timed sequence)
   */
  public double getIncrement() {
    return increment;
  }
  
  /**
   * <!-- getActionSequence -->
   *
   * @return the sequence of actions
   */
  public LinkedList<AbstractScheduledAction> getActionSequence() {
    return actionSequence;
  }

  /**
   * <!-- addAction -->
   * 
   * Add an action to the sequence
   * 
   * @param action
   * @throws ScheduleException
   */
  public void addAction(AbstractScheduledAction action) throws ScheduleException {
    if(isTimed()) {
      action.setTime(time + (actionSequence.size() * increment));
    }
    else if(action.isTimed()) {
      throw new ScheduleException(this, "Cannot add timed action " + action.getURI() + " to non-timed sequence");
    }
    actionSequence.addLast(action);
  }

  /**
   * <!-- addAction -->
   * 
   * Add a sequence to the sequence. A non-timed sequence can always be added to
   * another non-timed sequence. A timed sequence can be added to another timed
   * sequence, so long as they both have the same increment, and the start time
   * of the sequence being added is the same as the time at which the next
   * action in this sequence would occur. A timed sequence can be added to a
   * non-timed sequence, with the assumption that any existing actions on the
   * non-timed sequence occur before the start time of the timed sequence. A
   * non-timed sequence can be added to a timed sequence--all actions in the
   * non-timed sequence then become timed.
   * 
   * @param sequence
   * @throws ScheduleException
   */
  public void addAction(SequentialActionGroup sequence) throws ScheduleException {
    if(!isTimed() && sequence.isTimed()) {
      setTime(sequence.time - (actionSequence.size() * sequence.increment));
      increment = sequence.increment;
      int i = 0;
      for(AbstractScheduledAction existingAction: actionSequence) {
        existingAction.setTime(time + (i * increment));
        i++;
      }
    }
    else if(isTimed() && sequence.isTimed()) {
      if(increment != sequence.increment) {
        throw new ScheduleException(this, "Cannot add timed sequence " + sequence.getURI()
          + " with different increment (respectively, " + increment + " and " + sequence.increment + ")");
      }
      sequence.setTime(time + (actionSequence.size() * increment));
    }
    else if(isTimed() && !sequence.isTimed()) {
      int i = actionSequence.size();
      for(AbstractScheduledAction sequenceAction: sequence.actionSequence) {
        sequenceAction.setTime(time + (i * increment));
        i++;
      }
    }
    actionSequence.addAll(sequence.actionSequence);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * uk.ac.hutton.obiama.model.AbstractScheduledActivity#synchroniseWith(uk
   * .ac.hutton.obiama.model.AbstractScheduledActivity)
   */
  @Override
  public void synchroniseWith(AbstractScheduledAction event) throws ScheduleException {
    if(!isTimed()) {
      throw new ScheduleException(this, "Non-timed action sequence cannot be synchronised with " + event.getURI());
    }
    super.synchroniseWith(event);
  }

  /**
   * <!-- synchroniseWith -->
   * 
   * Synchronise two sequences. They must have the same start time and
   * increment.
   * 
   * @param event the sequence to synchronise with
   * @throws ScheduleException
   */
  public void synchroniseWith(SequentialActionGroup event) throws ScheduleException {
    boolean wasTimed = isTimed();
    super.synchroniseWith(event);
    if(!wasTimed && event.isTimed()) {
      this.increment = event.increment;
    }
    else if(this.increment != event.increment) {
      throw new ScheduleException(this, "(time " + time + ", increment" + increment + ") cannot be synchronised with "
        + event.getURI() + " (time " + event.time + ", increment " + event.increment + ")");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see uk.ac.hutton.obiama.model.AbstractScheduledActivity#step()
   */
  @Override
  public void stepImpl() throws IntegrationInconsistencyException, ScheduleException {
    if(isTimed()) throw new ScheduleException(this, "A timed sequence cannot be stepped");
    for(AbstractScheduledAction action: actionSequence) {
      action.step();
    }
  }
  
  /**
   * <!-- getActionList -->
   * 
   * Return the sequence as a list of actions
   *
   * @see uk.ac.hutton.obiama.model.AbstractScheduledAction#getActionList()
   */
  @Override
  public LinkedList<AbstractScheduledAction> getActionList() throws ScheduleException {
    if(isTimed()) {
      throw new ScheduleException(this, "timed action cannot be used to generate action list");
    }
    LinkedList<AbstractScheduledAction> actionList = new LinkedList<AbstractScheduledAction>();
    for(AbstractScheduledAction action: actionSequence) {
      actionList.addAll(action.getActionList());
    }
    return actionList;
  }

  /**
   * <!-- getActionSet -->
   * 
   * Return the actions contained in this sequence
   *
   * @see uk.ac.hutton.obiama.model.AbstractScheduledAction#getActionSet()
   */
  @Override
  public Set<Action> getActionSet() {
    Set<Action> actionSet = new HashSet<Action>();
    for(AbstractScheduledAction action: actionSequence) {
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
    for(AbstractScheduledAction action: actionSequence) {
      if(!action.allCreators()) return false;
    }
    return true;
  }

}
