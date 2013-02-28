/* uk.ac.hutton.obiama.observer: ClassCounter.java
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
package uk.ac.hutton.obiama.observer;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.msb.Concept;
import uk.ac.hutton.obiama.msb.Instance;

/**
 * <!-- ClassCounter -->
 *
 * @author Gary Polhill
 */
public class ClassCounter extends AbstractObserver {  
  public static final URI COUNTED_THING = URI.create(Observer.ONTOLOGY_URI + "#CountedThing");
  List<Integer> data;
  
  Concept countedConcept;

  /**
   * <!-- initialise -->
   *
   * @see uk.ac.hutton.obiama.observer.AbstractObserver#initialise()
   * @throws IntegrationInconsistencyException
   */
  @Override
  public void initialise() throws IntegrationInconsistencyException {
    countedConcept = getConcept(COUNTED_THING);
    data = new LinkedList<Integer>();
  }

  /**
   * <!-- step -->
   * @throws IntegrationInconsistencyException 
   *
   * @see uk.ac.hutton.obiama.observer.Observer#step()
   */
  @Override
  public void gatherData() throws IntegrationInconsistencyException {
    Set<Instance> instances = countedConcept.getInstances();
    data.add(instances == null ? null : instances.size());
  }


  /**
   * <!-- saveData -->
   *
   * @see uk.ac.hutton.obiama.observer.Observer#saveData(java.lang.String)
   * @param destination
   */
  @Override
  public void saveData(String destination) {
    // TODO Auto-generated method stub
    
  }

  /**
   * <!-- getDataFor -->
   *
   * @see uk.ac.hutton.obiama.observer.AbstractObserver#getDataFor(uk.ac.hutton.obiama.observer.Visualiser)
   * @param visualiser
   */
  @Override
  protected void getDataFor(Visualiser visualiser) {
    // TODO Auto-generated method stub
    
  }
}
