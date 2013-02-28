/*
 * uk.ac.hutton.obiama.observer: AbstractObserver.java
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
package uk.ac.hutton.obiama.observer;

import java.util.HashSet;
import java.util.Set;

import uk.ac.hutton.obiama.action.AbstractProcess;
import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;

/**
 * <!-- AbstractObserver -->
 * 
 * @author Gary Polhill
 */
public abstract class AbstractObserver extends AbstractProcess implements Observer {
  Set<Visualiser> visualisers;

  /**
   * <!-- initialiseLocal -->
   * 
   * @see uk.ac.hutton.obiama.action.AbstractProcess#initialiseLocal()
   * @throws IntegrationInconsistencyException
   */
  @Override
  public void initialiseLocal() throws IntegrationInconsistencyException {
    visualisers = new HashSet<Visualiser>();
    initialise();
  }

  /**
   * <!-- initialise -->
   * 
   * Initialisation method for subclasses to implement
   */
  protected abstract void initialise() throws IntegrationInconsistencyException;
  
  /**
   * <!-- addVisualiser -->
   *
   * @see uk.ac.hutton.obiama.observer.Observer#addVisualiser(uk.ac.hutton.obiama.observer.Visualiser)
   * @param visualiser
   */
  @Override
  public void addVisualiser(Visualiser visualiser) {
    visualisers.add(visualiser);
  }
  
  @Override
  public void step() throws IntegrationInconsistencyException {
    gatherData();
    for(Visualiser visualiser: visualisers) {
      getDataFor(visualiser);
      visualiser.step();
    }
  }
  
  protected abstract void gatherData() throws IntegrationInconsistencyException;
  
  protected abstract void getDataFor(Visualiser visualiser);
}
