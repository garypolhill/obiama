/*
 * uk.ac.hutton.obiama.action: Process.java Copyright (C) 2013 The James Hutton
 * Institute
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
package uk.ac.hutton.obiama.action;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;

import uk.ac.hutton.obiama.exception.Bug;
import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.exception.ScheduleException;
import uk.ac.hutton.obiama.msb.Concept;
import uk.ac.hutton.obiama.msb.ModelStateBroker;
import uk.ac.hutton.obiama.msb.Var;
import uk.ac.hutton.util.FloatingPointComparison;

/**
 * Process
 * 
 * A process is anything done by an agent: anything that causes a change to the
 * model structure ontology, or is a communication between agents.
 * 
 * @author Gary Polhill
 */
public interface Process {
  /**
   * <!-- getVars -->
   * 
   * Return the set of variables this Process uses
   * 
   * @return The set of variables this Process uses
   */
  public Set<Var> getVars();

  /**
   * <!-- getConcepts -->
   * 
   * Return the set of concepts this Process uses (if any)
   * 
   * @return The set of concepts this Process uses (null if none)
   */
  public Set<Concept> getConcepts();

  /**
   * <!-- getParameters -->
   * 
   * Return the set of parameters this Process uses (if any)
   * 
   * @return The set of parameters this Process uses (null if none)
   */
  public Set<ActionParameter> getParameters();

  /**
   * <!-- getFCmp -->
   * 
   * Return the floating point comparison method this Process uses for the given
   * Var. This method is used by FunctionalDataVar in the msb package when
   * configuring values for vars.
   * 
   * @param var The Var
   * @return The floating point comparison method (null if none specific)
   */
  public FloatingPointComparison getFCmp(Var var);
  
  /**
   * <!-- getModelStateBroker -->
   *
   * @return The Model State Broker used by this process
   */
  public ModelStateBroker getModelStateBroker();
  
  /**
   * <!-- getBaseURI -->
   *
   * @return The base URI used for model entities referred to by this Process
   */
  public String getBaseURI();
  
  /**
   * <!-- getURIExtension -->
   *
   * @return The URI extension used for model entities referred to by this Process
   */
  public String getURIExtension();

  /**
   * <!-- Initialise -->
   * 
   * Initialise the process
   * 
   * @param uri URI of the scheduled process (possibly calling this one)
   * @param uriBase base URI for entities referred to by the process
   * @param uriExtension URI extension for entities referred to by the process
   * @param msb the Model State Broker
   * @param inputParams Parameters
   * @param fcmps Floating point comparison methods
   * @throws IntegrationInconsistencyException
   * @throws ScheduleException 
   */
  public void initialise(URI uri, String uriBase, String uriExtension, ModelStateBroker msb,
      Map<String, ActionParameter> inputParams, Map<URI, FloatingPointComparison> fcmps)
      throws IntegrationInconsistencyException, ScheduleException;

  /**
   * <!-- getURI -->
   * 
   * @return The URI of this process
   */
  public URI getURI();
  
  public Process getOriginator();
  
  public class Adjust {
    public final static String OWL_SUFFIX = ".owl";
    public final static String URI_EXTENSION = "-";

    public static URI URI(URI entity, String base, String extension) {
      if(entity.getScheme() == null || entity.getPath() == null) {
        throw new Bug("Action has no scheme or path for entity " + entity);
      }
      URI modifiedURI;
      if(base == null && extension == null) modifiedURI = entity;
      else if(base == null) {
        StringBuffer buf = new StringBuffer();
        buf.append(entity.toString());
        buf.delete(buf.lastIndexOf("#"), buf.length());
        modifiedURI = getURIFor(entity.getFragment(), buf.toString(), extension);
      }
      else {
        modifiedURI = getURIFor(entity.getFragment(), base, extension);
      }
      return modifiedURI;
    }
    
    /**
     * <!-- getURIFor -->
     * 
     * Provide the URI to use for an ontological entity in a standard way. The
     * entityName should be a simple name for an entity (it may optionally begin
     * with a #). Since the same action class may be used twice, the baseURI and
     * extendURI arguments can be used to adjust entity names in actions such that
     * they point to different entities in the model structure ontology.
     * 
     * @param entityName The name of the entity, e.g. "#entity"
     * @param baseURI Base URI, expected to be of the form
     *          "http://www.xyz.org/ontology.owl"
     * @param extendURI Extension to use on the base URI to identify a 'virtual'
     *          ontology for a particular action e.g. "action01"
     * @return a URI--e.g. "http://www.xyz.org/ontology-action01.owl#entity"
     */
    private static URI getURIFor(String entityName, String baseURI, String extendURI) {
      StringBuffer uriStr = new StringBuffer();
      if(baseURI != null) {
        if(extendURI == null) {
          uriStr.append(baseURI);
        }
        else {
          if(baseURI.endsWith(OWL_SUFFIX)) {
            uriStr.append(baseURI.substring(0, baseURI.length() - OWL_SUFFIX.length()));
          }
          else
            uriStr.append(baseURI);
          uriStr.append(URI_EXTENSION);
          uriStr.append(extendURI);
          if(baseURI.endsWith(OWL_SUFFIX)) uriStr.append(OWL_SUFFIX);
        }
      }
      if(!entityName.startsWith("#")) uriStr.append("#");
      uriStr.append(entityName);
      try {
        return new URI(uriStr.toString());
      }
      catch(URISyntaxException e) {
        throw new Bug("URI " + uriStr + " formed from base " + baseURI + ", extension " + extendURI + " and fragment "
          + entityName + " is invalid: " + e);
      }
    }

  }
  
  
}
