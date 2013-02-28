/*
 * uk.ac.hutton.obiama.msb: OntologyIOHelper.java
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
package uk.ac.hutton.obiama.msb;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owl.model.MissingImportEvent;
import org.semanticweb.owl.model.MissingImportListener;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyLoaderListener;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLOntologyURIMapper;
import org.semanticweb.owl.util.AutoURIMapper;
import org.semanticweb.owl.util.SimpleURIMapper;

import uk.ac.hutton.obiama.exception.ErrorHandler;
import uk.ac.hutton.obiama.model.Log;

/**
 * <!-- OntologyIOHelper -->
 * 
 * Class to help with loading in OWL ontologies. This provides facilities to
 * allow logical to physical URIs to be mapped, or locations to search for
 * physical instantiations of logical URIs, and to allow the imports closure
 * (the recursive set of all ontologies imported by an ontology) of an ontology
 * to be loaded and recorded.
 * 
 * @author Gary Polhill
 */
public class OntologyIOHelper {
  /**
   * Set of logical URIs of ontologies that can be ignored if they are imported
   * and the import fails for some reason.
   */
  private Set<String> ignoreFailedImports;

  /**
   * String to indicate that a logical URI can be ignored in the Map arguments
   * to the addSpecifiedMappers() methods.
   */
  public static final String IGNORE_FAILED_IMPORT_URI = "IGNORE";

  private static Set<OWLOntologyManager> configuredManagers = new HashSet<OWLOntologyManager>();

  /**
   * When loading an ontology, this ivar contains the logical URI being loaded.
   */
  private URI currentURI;
  
  /**
   * A set of ontology URI mappers
   */
  private Set<OWLOntologyURIMapper> mappers;

  /**
   * Initialise the set of ignorable imports.
   */
  public OntologyIOHelper() {
    ignoreFailedImports = new HashSet<String>();
    mappers = new HashSet<OWLOntologyURIMapper>();
  }

  /**
   * <!-- configure -->
   * 
   * Configure the IOHelper with the manager. If an ontology URI map has been
   * specified on the command line, then configure the IOHelper to use those. If
   * not, get the ontology search path to search for directories containing
   * physical URIs.
   * 
   * @param manager
   */
  public void configure(OWLOntologyManager manager) {
    if(configuredManagers.contains(manager)) return;
    Map<String, String> logPhysMap = ObiamaSetUp.getOntologyURIMap();
    if(logPhysMap.size() == 0) {
      addPathMappers(manager, ObiamaSetUp.getOntologySearchPath());
    }
    else {
      try {
        addSpecifiedMappers(manager, logPhysMap);
      }
      catch(URISyntaxException e) {
        ErrorHandler.redo(e, "Initialising logical to physical ontology URI map");
      }
    }
    manager.setSilentMissingImportsHandling(true);
    manager.addMissingImportListener(this.new AllowFailIgnoreListener());
    manager.addOntologyLoaderListener(this.new OntologyLoaderListener());
    configuredManagers.add(manager);
  }

  /**
   * <!-- addSpecifiedMappers -->
   * 
   * Allow the user to specify a mapping from logical to physical URIs, as
   * strings. If the physical URI is equal to IGNORE_FAILED_IMPORT_URI, then the
   * corresponding logical URI can be ignored if it is an imported ontology the
   * loading of which fails.
   * 
   * @param manager The OWLOntologyManager to which to add the mappings
   * @param userMap The map of String logical to physical URIs
   * @throws URISyntaxException
   */
  public void addSpecifiedMappers(OWLOntologyManager manager, Map<String, String> userMap) throws URISyntaxException {
    addSpecifiedMappers(manager, userMap, true);
  }

  /**
   * <!-- addSpecifiedMappers -->
   * 
   * As per addSpecifiedMappers, but the allowIgnore argument, if false, allows
   * the 'ignorable' facility to be switched off--i.e. all values in the Map are
   * expected to be physical URIs (and all keys logical URIs). All URI strings
   * are expected to have valid URI syntax.
   * 
   * @param manager The OWLOntologyManager to which to add the mappings
   * @param userMap The map of String logical to physical URIs
   * @param allowIgnore true if physical URIs can be equal to
   *          IGNORE_FAILED_IMPORT_URI to allow the corresponding failed imports
   *          to be ignored, false if not.
   * @throws URISyntaxException
   */
  public void addSpecifiedMappers(OWLOntologyManager manager, Map<String, String> userMap, boolean allowIgnore)
      throws URISyntaxException {
    if(userMap != null) {
      for(String logicalURIStr: userMap.keySet()) {
        String physicalURIStr = userMap.get(logicalURIStr);
        if(allowIgnore && physicalURIStr.equals(IGNORE_FAILED_IMPORT_URI)) {
          ignoreFailedImports.add(logicalURIStr);
          continue;
        }
        URI logicalURI = new URI(logicalURIStr);
        URI physicalURI = new URI(userMap.get(logicalURIStr));
        OWLOntologyURIMapper mapper = new SimpleURIMapper(logicalURI, physicalURI);
        manager.addURIMapper(mapper);
        mappers.add(mapper);
      }
    }
  }

  /**
   * <!-- ignoreFailedImport -->
   * 
   * Stipulate that the imported logical URI argument can be ignored if loading
   * fails.
   * 
   * @param importURI Logical URI of an imported ontology
   */
  public void ignoreFailedImport(String importURI) {
    if(!ignoreFailedImports.contains(importURI)) {
      ignoreFailedImports.add(importURI);
    }
  }

  /**
   * <!-- addPathMappers -->
   * 
   * Searches the set of directories provided for ontologies, and automatically
   * adds a mapper from logical to physical URIs for all ontologies found in
   * those directories.
   * 
   * @param manager OWLOntologyManager to which to add the mappings
   * @param dirs Set of directories
   * @return Set of directories given that were not searched because they were
   *         not directories or because they were not readable; or null if all
   *         directories were searched.
   */
  public Set<String> addPathMappers(OWLOntologyManager manager, final Set<String> dirs) {
    Set<String> notUsed = new HashSet<String>();
    if(dirs != null) {
      for(String dirStr: dirs) {
        File dir = new File(dirStr);
        if(dir.isDirectory() && dir.canRead()) {
          // Do not search recursively
          OWLOntologyURIMapper mapper = new AutoURIMapper(dir, false);
          manager.addURIMapper(mapper);
          mappers.add(mapper);
        }
        else
          notUsed.add(dirStr);
      }
    }
    return notUsed.isEmpty() ? null : notUsed;
  }

  /**
   * <!-- loadOntology -->
   * 
   * Load an ontology
   * 
   * @param uri Logical URI of ontology to load
   * @param manager OWLOntologyManager into which to load the ontology
   * @return The loaded ontology
   * @throws OWLOntologyCreationException
   */
  public OWLOntology loadOntology(final URI uri, OWLOntologyManager manager) throws OWLOntologyCreationException {
    try {
      currentURI = uri;
      for(OWLOntologyURIMapper mapper: mappers) {
        URI physicalURI = mapper.getPhysicalURI(uri);
        if(physicalURI != null) {
          try {
            System.out.println("Trying to load " + uri + " from physical URI " + physicalURI);
            return manager.loadOntologyFromPhysicalURI(physicalURI);
          }
          catch(NonIgnoredFailedImportException e) {
            System.out.println("Failed because: " + e);
          }
        }
      }
      return manager.loadOntology(uri);
    }
    catch(NonIgnoredFailedImportException e) {
      throw e.getCause();
    }
  }

  public static OWLOntology load(final URI uri, OWLOntologyManager manager) throws OWLOntologyCreationException {
    OntologyIOHelper helper = new OntologyIOHelper();
    helper.configure(manager);
    return helper.loadOntology(uri, manager);
  }

  /**
   * <!-- loadOntologyClosure -->
   * 
   * Load an ontology and its closure
   * 
   * @param uri Logical URI of the ontology to load
   * @param manager OWLOntologyManager
   * @param closure Set in which to put the closure of the loaded ontology
   * @return The loaded ontology
   * @throws OWLOntologyCreationException
   */
  public OWLOntology loadOntologyClosure(final URI uri, OWLOntologyManager manager, Set<OWLOntology> closure)
      throws OWLOntologyCreationException {
    return loadOntologyClosure(uri, manager, closure, new HashSet<OWLOntology>());
  }

  public static OWLOntology loadClosure(final URI uri, OWLOntologyManager manager, Set<OWLOntology> closure)
      throws OWLOntologyCreationException {
    OntologyIOHelper helper = new OntologyIOHelper();
    helper.configure(manager);
    return helper.loadOntologyClosure(uri, manager, closure);
  }

  /**
   * <!-- loadOntologyClosure -->
   * 
   * Load an ontology and its closure, but don't put in the closure set any
   * ontology that is already known
   * 
   * @param uri Logical URI of ontology to load
   * @param manager OWLOntologyManager
   * @param closure Set in which to put the closure of the loaded ontology
   *          (except those already known)
   * @param known Set of known ontologies
   * @return
   * @throws OWLOntologyCreationException
   */
  public OWLOntology loadOntologyClosure(final URI uri, OWLOntologyManager manager, Set<OWLOntology> closure,
      final Set<OWLOntology> known) throws OWLOntologyCreationException {
    OWLOntology ontology = loadOntology(uri, manager);
    for(OWLOntology inClosure: manager.getImportsClosure(ontology)) {
      if(!known.contains(inClosure)) closure.add(inClosure);
    }
    return ontology;
  }

  public static OWLOntology loadClosure(final URI uri, OWLOntologyManager manager, Set<OWLOntology> closure,
      final Set<OWLOntology> known) throws OWLOntologyCreationException {
    OntologyIOHelper helper = new OntologyIOHelper();
    helper.configure(manager);
    return helper.loadOntologyClosure(uri, manager, closure, known);
  }

  public class OntologyLoaderListener implements OWLOntologyLoaderListener {

    /**
     * <!-- finishedLoadingOntology -->
     * 
     * @see org.semanticweb.owl.model.OWLOntologyLoaderListener#finishedLoadingOntology(org.semanticweb.owl.model.OWLOntologyLoaderListener.LoadingFinishedEvent)
     * @param event
     */
    public void finishedLoadingOntology(LoadingFinishedEvent event) {
      if(event.isSuccessful()) {
        Log.loadOntologySuccessfully(event.getOntologyURI(), event.getPhysicalURI(), event.isImported());
      }
      else {
        Log.loadOntologyFail(event.getOntologyURI(), event.getPhysicalURI(), event.isImported(), event.getException());
      }
    }

    /**
     * <!-- startedLoadingOntology -->
     * 
     * @see org.semanticweb.owl.model.OWLOntologyLoaderListener#startedLoadingOntology(org.semanticweb.owl.model.OWLOntologyLoaderListener.LoadingStartedEvent)
     * @param event
     */
    @Override
    public void startedLoadingOntology(LoadingStartedEvent event) {
      // Do nothing
    }

  }

  /**
   * AllowFailIgnoreListener
   * 
   * Listener called when an import fails.
   * 
   * @author Gary Polhill
   */
  public class AllowFailIgnoreListener implements MissingImportListener {

    /**
     * importMissing
     * 
     * Implementation of the MissingImportListener interface, which allows some
     * imported ontologies to be ignored at the user's request, should they fail
     * to load. The interface does not provide for an exception to be thrown by
     * this method, so a subclass of RuntimeException is created to allow this
     * to happen.
     * 
     * @param event The event that caused the import to fail
     * @throws NonIgnoredFailedImportException
     * @see org.semanticweb.owl.model.MissingImportListener
     */
    public void importMissing(MissingImportEvent event) {
      String failedImport = event.getImportedOntologyURI().toString();

      if(!ignoreFailedImports.contains(failedImport)) {
        throw new NonIgnoredFailedImportException(failedImport, event.getCreationException());
      }
      else {
        Log.loadOntologyIgnore(event.getImportedOntologyURI(), event.getCreationException());
      }
    }
  }

  /**
   * NonIgnoredFailedImportException
   * 
   * 
   * This is an exception thrown by the AllowFailIgnoreListener when an import
   * has failed that the user has not requested to ignore. Though it is bad form
   * to make such an exception a RuntimeException, it is necessary to do so in
   * order to have AllowFailIgnoreListener follow the MissingImportListener
   * interface without breaking it. The class is private, so cannot be accessed
   * outside this class, and is caught when loading the ontology.
   * 
   * @author Gary Polhill
   */
  private class NonIgnoredFailedImportException extends RuntimeException {
    /**
     * Serial ID
     */
    private static final long serialVersionUID = 8570256991484292217L;

    private OWLOntologyCreationException cause;

    public OWLOntologyCreationException getCause() {
      return cause;
    }

    public NonIgnoredFailedImportException(String uriStr, OWLOntologyCreationException reason) {
      super("Failed to load imported URI " + uriStr + " during load of ontology URI " + currentURI.toString());
      cause = reason;
    }
  }

}
