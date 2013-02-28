/*
 * uk.ac.hutton.obiama.msb: RunID.java
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
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.xml.sax.InputSource;

import corejava.Format;

import uk.ac.hutton.obiama.exception.Bug;
import uk.ac.hutton.obiama.exception.ErrorHandler;
import uk.ac.hutton.obiama.model.Log;
import uk.ac.hutton.util.Panic;

/**
 * RunID
 * 
 * Create a unique identifier for each run. By default this takes the format
 * <code>obiama-<i>host</i>-<i>time</i></code>, where <code><i>host</i></code>
 * is a hexadecimal representation of the local host IP address where the RunID
 * is executed, and <code><i>time</i></code> is a hexadecimal representation of
 * the time the RunID is first requested. If the local host IP address cannot be
 * obtained, the string "unknown" will be used instead.
 * 
 * The user can optionally set the RunID to have a different prefix than
 * "obiama-", and to have a suffix. Provision is also made for a Run-ID to be
 * obtained from a web service returning an XML file with a configurable XPath
 * from which to obtain the RunID to include. By default this is
 * <code>/OBIAMA/Run-ID</code>, but a different XPath can be used. The full Run
 * ID this has the format
 * <code><i>prefix</i>-<i>host</i>-<i>serviceID</i>-<i>time<i>-<i>suffix</i></code>
 * , where <code><i>serviceID</i></code> is the value returned by the XPath of
 * the service URL. The value returned must be a valid OWL name.
 * 
 * @author Gary Polhill
 */
public final class RunID {
  /**
   * The prefix to use in front of the RunID
   */
  private static String prefix = "obiama-";

  /**
   * Whether or not the prefix has been set. It can only be set once.
   */
  private static boolean prefixSet = false;

  /**
   * The suffix to use at the end of the RunID
   */
  private static String suffix = null;

  /**
   * Whether or not the suffix has been set. It can only be set once.
   */
  private static boolean suffixSet = false;

  /**
   * The URL from which to obtain an identifier to insert in the RunID, if
   * required. The URL is expected to return an XML file.
   */
  private static URL runIdServiceURL = null;

  /**
   * Whether or not to use the RunID service URL to insert an identifier in the
   * RunID. This is also used to check that the runIdServiceURL has only been
   * set once.
   */
  private static boolean useRunIdService = false;

  /**
   * The XPath to use to extract the RunID from the XML file returned by the
   * runIdServiceURL.
   */
  private static String runIdServiceXPath = "/OBIAMA/Run-ID";

  /**
   * Whether or not the runIdServiceXPath has been set. It can only be set once.
   */
  private static boolean runIdServiceXPathSet = false;

  /**
   * Directory to save the run to, if requested.
   */
  private static Map<String, String> runIdDir;

  /**
   * The RunID
   */
  private static String runId = null;

  /**
   * Disable the constructor.
   */
  private RunID() {
    throw new Bug();
  }

  /**
   * <!-- setPrefix -->
   * 
   * Set the prefix to use in the RunID. By default this is "
   * <code>obiama-</code>".
   * 
   * @param prefix The prefix to use. It must be a valid OWL name.
   */
  public static void setPrefix(String prefix) {
    // The prefix can only be set once
    if(prefixSet) throw new Bug();
    RunID.prefix = prefix;
    if(!RunID.prefix.endsWith("-")) RunID.prefix += "-";
    // TODO check that the prefix is valid
    prefixSet = true;
  }

  /**
   * <!-- setSuffix -->
   * 
   * Set the suffix to use in the RunID. By default there isn't one.
   * 
   * @param suffix The suffix to use. It must be a valid OWL name.
   */
  public static void setSuffix(String suffix) {
    // The suffix can only be set once
    if(suffixSet) throw new Bug();
    RunID.suffix = suffix;
    // TODO check that the suffix is valid
    suffixSet = true;
  }

  /**
   * <!-- setRunIDServiceURL -->
   * 
   * Set the URL from which to get an ID to insert in the RunID. The URL should
   * return an XML file, from which the runIdServiceXPath can be used to extract
   * the ID to insert.
   * 
   * @param url The URL
   * @throws MalformedURLException
   */
  public static void setRunIDServiceURL(String url) throws MalformedURLException {
    // The run ID service URL can only be set once
    if(useRunIdService) throw new Bug();
    runIdServiceURL = new URL(url);
    useRunIdService = true;
  }

  /**
   * <!-- setRunIDServiceXPath -->
   * 
   * Set the XPath from which to extract the ID to insert in the RunID, in the
   * XML returned by the Run ID Service URL.
   * 
   * @param xpath The XPath
   */
  public static void setRunIDServiceXPath(String xpath) {
    // The run ID service XPath can only be set once
    if(runIdServiceXPathSet) throw new Bug();
    runIdServiceXPath = xpath;
    runIdServiceXPathSet = true;
  }

  /**
   * <!-- getRunID -->
   * 
   * Obtain the RunID. This is generated on first call.
   * 
   * @return The RunID.
   */
  public static String getRunID() {
    if(runId == null) {
      StringBuffer buff = new StringBuffer(prefix);
      try {
        InetAddress local = InetAddress.getLocalHost();
        BigInteger addr = new BigInteger(local.getAddress());
        buff.append(Long.toHexString(addr.longValue()));
      }
      catch(UnknownHostException e) {
        buff.append("unknown");
      }
      if(useRunIdService) {
        buff.append("-");
        buff.append(getRunIdServiceId());
      }
      buff.append("-");
      long now = System.currentTimeMillis();
      buff.append(Long.toHexString(now));
      if(suffix != null) buff.append("-" + suffix);
      runId = buff.toString();
    }
    return runId;
  }

  /**
   * <!-- reset -->
   * 
   * Allow the run ID to be reset for a new run.
   */
  public static void reset() {
    runId = null;
  }

  /**
   * <!-- getRunDir -->
   * 
   * Create a run directory to save data for the current run in, if that
   * directory does not already exist. This method should strictly only need to
   * be called from ObiamSetUp.
   * 
   * @param saveDir Top-level directory under which the run directory will be
   *          put
   * @param runDirPrefix Prefix for all run directories
   * @return The run directory to use to save data for the current run in
   */
  public static String getRunSubDir(String saveDir, String runDirPrefix) {
    if(runId == null) getRunID();
    if(!runIdDir.containsKey(runId)) {
      String subdir;
      for(short i = (short)1; i != (short)0; i++) {
        subdir = runDirPrefix + "-" + String.format("%0" + (Short.SIZE / 4) + "x", (int)i);
        File file = new File(saveDir + File.separator + subdir);
        if(!file.exists()) {
          try {
            file.mkdirs();
          }
          catch(SecurityException e) {
            ErrorHandler.redo(e, "creating run directory " + file.getAbsolutePath());
            throw new Panic();
          }
          runIdDir.put(runId, subdir);
          break;
        }
      }
      if(!runIdDir.containsKey(runId)) {
        ErrorHandler.redo(new Exception("Failed to create a unique run directory in " + saveDir + " with prefix "
          + runDirPrefix + ". Try a different --save-last, --save-dir or --run-dir-prefix"), "creating run directory");
        throw new Panic();
      }
      Log.runDir(runId, runIdDir.get(runId));
    }
    return runIdDir.get(runId);
  }

  /**
   * <!-- getRunIdServiceId -->
   * 
   * Get an ID to insert in the RunID from a URL providing that service. The
   * service should return an XML file, from which the XPath given can be used
   * to extract the ID to insert.
   * 
   * @return The ID to insert.
   */
  private static String getRunIdServiceId() {
    XPath xpath = XPathFactory.newInstance().newXPath();
    InputSource source;
    try {
      source = new InputSource(runIdServiceURL.openStream());
      return xpath.evaluate(runIdServiceXPath, source);
      // TODO check that the value returned is valid
    }
    catch(IOException e) {
      ErrorHandler.warn(e, "obtaining run ID from service " + runIdServiceURL,
          "without using a run ID from the service");
      return ("unavailable");
    }
    catch(XPathExpressionException e) {
      ErrorHandler.warn(e, "obtaining run ID using XPath " + runIdServiceXPath + " from service " + runIdServiceURL,
          "without using a run ID from the service");
      return ("unavailable");
    }
  }
}
