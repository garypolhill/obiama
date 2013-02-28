/*
 * uk.ac.hutton.obiama.msb: AnonymousURI.java
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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import uk.ac.hutton.obiama.exception.Bug;
import uk.ac.hutton.obiama.exception.ErrorHandler;
import uk.ac.hutton.obiama.exception.Panic;

/**
 * AnonymousURI
 * 
 * Class to generate anonymous URIs for use in temporary ontologies. Allows the
 * creation of ontologies with various prefixes for the URI (i.e. everything up
 * to and including the beginning of the name of the OWL file).
 * 
 * @author Gary Polhill
 */
public class AnonymousURI {
  /**
   * The default prefix to use for the URI of temporary ontologies
   */
  private static String defaultUriPrefix =
    "http://www.hutton.ac.uk/obiama/internal/tmp-";

  /**
   * Counters of ontologies created for each prefix used
   */
  private static Map<String, Integer> counter = new HashMap<String, Integer>();
  
  /**
   * The URI prefix used by an instance
   */
  private String instUriPrefix;

  /**
   * Constructor providing an instance using the default prefix
   */
  public AnonymousURI() {
    instUriPrefix = defaultUriPrefix;
  }

  /**
   * Constructor creating an instance with a non-default prefix
   * 
   * @param uriPrefix The non-default prefix to use
   */
  public AnonymousURI(final String uriPrefix) {
    instUriPrefix = new String(uriPrefix);
  }

  /**
   * <!-- createAnonymousURI -->
   *
   * Convenience method to return a URI from a default prefix
   *
   * @return the URI
   */
  public static URI createAnonymousURI() {
    AnonymousURI anon = new AnonymousURI();
    return anon.getAnonymousURI();
  }
  
  /**
   * <!-- createAnonymousURI -->
   *
   * Convenience method returning a URI from a non-default prefix
   *
   * @param prefix the non-default prefix
   * @return the URI
   */
  public static URI createAnonymousURI(final String prefix) {
    AnonymousURI anon = new AnonymousURI(prefix);
    return anon.getAnonymousURI();
  }

  /**
   * <!-- incCounter -->
   *
   * Increment the counter of a prefix
   *
   * @param prefix the prefix of which to increment the counter
   */
  private static void incCounter(final String prefix) {
    if(counter.containsKey(prefix)) {
      counter.put(prefix, counter.get(prefix) + 1);
    }
    else {
      counter.put(prefix, new Integer(1));
    }
  }

  /**
   * <!-- getCounter -->
   * 
   * Return the counter of a prefix
   *
   * @param prefix the prefix of which to return the counter
   * @return the counter of the prefix
   */
  private static int getCounter(final String prefix) {
    if(counter.containsKey(prefix)) {
      return counter.get(prefix);
    }
    else {
      throw new Bug();
    }
  }
  
  /**
   * <!-- getURIPrefix -->
   * 
   * Return the prefix used by this instance
   *
   * @return the prefix
   */
  public final String getURIPrefix() {
    return instUriPrefix;
  }

  /**
   * <!-- getAnonymousURI -->
   *
   * Return an anonymous URI using the prefix of this instance
   *
   * @return an anonymous URI
   */
  public URI getAnonymousURI() {
    incCounter(instUriPrefix);
    String uriname =
      new String(instUriPrefix + RunID.getRunID() + "-"
        + +getCounter(instUriPrefix) + ".owl");
    try {
      URI uri = new URI(uriname);
      return uri;
    }
    catch(URISyntaxException e) {
      ErrorHandler.fatal(e, "creating anonymous URI " + uriname);
      throw new Panic();
    }
  }

}
