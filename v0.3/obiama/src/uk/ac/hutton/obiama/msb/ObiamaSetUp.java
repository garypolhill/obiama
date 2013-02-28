/*
 * uk.ac.hutton.obiama.msb: ObiamaSetUp.java Copyright (C) 2013 The James Hutton
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
package uk.ac.hutton.obiama.msb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.inference.OWLReasonerFactory;

import uk.ac.hutton.obiama.exception.Bug;
import uk.ac.hutton.obiama.exception.ErrorHandler;
import uk.ac.hutton.obiama.exception.FileFormatException;
import uk.ac.hutton.obiama.exception.Panic;
import uk.ac.hutton.obiama.exception.UsageException;
import uk.ac.hutton.obiama.model.CommandLineArgument;
import uk.ac.hutton.obiama.model.FCmpOntology;
import uk.ac.hutton.obiama.model.Log;
import uk.ac.hutton.obiama.model.FCmpOntology.FloatingPointComparisonOntologyException;
import uk.ac.hutton.obiama.random.RNGFactory;
import uk.ac.hutton.util.CSVException;
import uk.ac.hutton.util.CSVReader;
import uk.ac.hutton.util.FloatingPointComparison;
import uk.ac.hutton.util.LanguageDefaultFCmp;
import uk.ac.hutton.util.Reflection;
import uk.ac.hutton.util.Table;

/**
 * ObiamaSetUp
 * 
 * Singleton class to help with setting up OBIAMA, managing the arguments list.
 * Call {@link #getObiamaOptions()} to process the arguments list, using
 * {@link #addArgument()} and {@link #addArguments()} to include your own
 * command-line arguments for validation purposes. The class also contains
 * methods to check for jars, libraries and reasoners, and to manage ontology
 * search paths.
 * 
 * @author Gary Polhill
 */
public class ObiamaSetUp {

  /**
   * List of arguments taken by Obiama
   */
  private static final CommandLineArgument argList[] = {
    new CommandLineArgument("--ontology", "-O", "ontology URI", "Model structure (logical) ontology URI"),
    new CommandLineArgument("--ontology-search-path", "-P", "path list",
        "Colon-separated list of directories to search for physical ontology URIs"),
    new CommandLineArgument("--ontology-uri-map", "-M", "map URI",
        "File containing map of logical to physical ontology URIs"),
    new CommandLineArgument("--reasoner-class", "-C", "reasoner class", "Class to use for reasoner"),
    new CommandLineArgument("--use-reasoner", "-R", null, "Force the use of the reasoner"),
    new CommandLineArgument("--schedule", "-T", "schedule URI", "File to load schedule from"),
    new CommandLineArgument("--main-schedule", "-m", "schedule instance URI",
        "Name of instance in schedule ontology to use for the main schedule"),
    new CommandLineArgument("--initial-schedule", "-i", "schedule instance URI",
        "Name of instance in schedule ontology to use for initialisation"),
    new CommandLineArgument("--rng", "-G", "RNG class", "Class to use for random number generation"),
    new CommandLineArgument("--rng-param", "-S", "RNG parameter", "Comma-separated list of param=value pairs "
      + "(e.g. seed=12345 -- though in fact if you just give a number and it will be assumed you mean the seed) "
      + "for the random number generator"),
    new CommandLineArgument("--provenance", "-v", "implementation", "Provenance implementation to use"),
    new CommandLineArgument("--history-provenance", "-H", "URI", "(Logical) URI of history provenance ontology"),
    new CommandLineArgument("--state-ontology", "-s", "state URI", "State ontology to initialise with"),
    new CommandLineArgument("--save-last", "-d", "directory", "Directory to save the last state to"),
    new CommandLineArgument("--save-dir", "-D", "directory", "Directory to save all states to"),
    new CommandLineArgument("--save-inferred", "-I", null, "Save inferred ontology when saving states"),
    new CommandLineArgument("--fcmp", "-F", "FCMP class", "Class to use for floating point comparisons"),
    new CommandLineArgument("--fcmp-args", "-f", "FCMP class args", "Arguments for floating point comparison class "
      + "(as comma-separated list of arg=value pairs)"),
    new CommandLineArgument("--log", "-L", "Log file", "Log file to use for this run"),
    new CommandLineArgument("--log-messages", "-l", "Message list", "Messages to include in the log file"),
    new CommandLineArgument("--separate-run-dirs", "-z", null,
        "Save states from different runs to different subdirectories of the --save-dir argument"),
    new CommandLineArgument("--run-dir-prefix", "-Z", "prefix",
        "Prefix to apply to run subdirectories (implies --separate-run-dirs)"),
    new CommandLineArgument("--background", "-B", null, "Stipulate non-GUI mode") };

  /**
   * Private singleton instance
   */
  private static ObiamaSetUp i = new ObiamaSetUp();

  /**
   * Map built from arguments to the program of argument property name (which is
   * the long argument name with the double-dash prefix removed and other dashes
   * replaced with dots--so --ontology-search-path becomes
   * ontology.search.path), to argument value. For command line options not
   * taking an argument, the argument value mapped to is "true" if given on the
   * command line and does not appear otherwise.
   */
  private static Map<String, String> obiamaArgs;

  /**
   * List of reasoner classes known about at the time of writing. These are used
   * to search for the presence of a reasoner automatically, but are not
   * intended to be an exhaustive specification of the reasoners OBIAMA could
   * work with.
   */
  private static final String reasoners[] = { "org.mindswap.pellet.owlapi.Reasoner",
    "org.mindswap.pellet.owlapi.Reasoner", "eu.trowl.owl.rel.reasoner.dl.RELReasonerFactory",
    "uk.ac.manchester.cs.factplusplus.owlapi.Reasoner" };

  /**
   * Jars required for each of the reasoners in the <code>reasoners[]</code>
   * array
   */
  private static final String reasonerJars[][] = {
    { "pellet\\.jar", "aterm-java-\\d+(\\.\\d+)*\\.jar", "relaxngDatatype\\.jar", "xsdlib\\.jar" },
    { "pellet-owlapi\\.jar", "pellet-core\\.jar", "pellet-el\\.jar", "pellet-datatypes\\.jar", "pellet-rules\\.jar",
      "aterm-java-\\d+(\\.\\d+)*\\.jar", "relaxngDatatype\\.jar", "xsdlib\\.jar" }, { "TrOWLCore\\.jar" },
    { "FaCTpp-OWLAPI-v\\d+(\\.\\d+)*.jar" } };

  /**
   * Libraries required for each of the reasoners in the
   * <code>reasoners[]</code> array
   */
  private static final String reasonerLibs[][] = { {}, {}, {}, { "libFaCTPlusPlusJNI.jnilib" } };

  /**
   * String constant containing name of shell environment variable with path to
   * search for ontologies
   */
  public static final String OBIAMA_ONTOLOGY_PATH = "OBIAMA_ONTOLOGY_PATH";

  /**
   * Default log file name
   */
  public static final String DEFAULT_LOG_FILE = "OBIAMA.log";

  /**
   * Name of the shell environment variable to use as default log file
   */
  public static final String OBIAMA_LOG_FILE = "OBIAMA_LOG_FILE";

  /**
   * Map of valid command-line arguments to (the application using) OBIAMA
   */
  private Map<String, CommandLineArgument> argMap;

  /**
   * Search path for this run
   */
  private static Set<String> ontologySearchPath = null;

  /**
   * Map of ontology logical to physical URIs
   */
  private static Map<String, String> ontologyURIMap = null;

  /**
   * Default floating point comparison method
   */
  private static FloatingPointComparison fcmp = null;

  /**
   * Arguments for the random number generator, if any
   */
  private static Map<String, String> rngArgs = null;

  /**
   * Name of the 'command' (the class with the main method that started this
   * program)
   */
  private static String command = null;

  /**
   * Constructor. Processes the arguments array argList into a the map argMap
   * from short and long commandline option name to CommandLineArgument object.
   */
  private ObiamaSetUp() {
    argMap = new HashMap<String, CommandLineArgument>();
    for(int i = 0; i < argList.length; i++) {
      addPrivateArgument(argList[i]);
    }
  }

  /**
   * <!-- reset -->
   * 
   * Reset the ObiamaSetUp class (this is mainly for the purpose of testing)
   */
  public static void reset() {
    i = new ObiamaSetUp();
    ontologySearchPath = null;
    ontologyURIMap = null;
    fcmp = null;
  }

  /**
   * <!-- addArgument -->
   * 
   * Add a single argument to the set of valid command-line arguments to (the
   * application using) OBIAMA.
   * 
   * @param arg The argument to add
   */
  public static void addArgument(CommandLineArgument arg) {
    i.addPrivateArgument(arg);
  }

  /**
   * <!-- addPrivateArgument -->
   * 
   * Add an argument to the singleton argument list.
   * 
   * @param arg The argument to add
   */
  private void addPrivateArgument(CommandLineArgument arg) {
    if(argMap.containsKey(arg.getLongOption())
      || (arg.getShortOption() != null && argMap.containsKey(arg.getShortOption()))) {
      throw new Bug("The command-line argument option \"" + arg.getLongOption() + "\", with short option \""
        + arg.getShortOption()
        + "\" has a short or long argument option that is already used by another command line argument option.");
    }
    argMap.put(arg.getLongOption(), arg);
    if(arg.getShortOption() != null) argMap.put(arg.getShortOption(), arg);
  }

  /**
   * <!-- addArguments -->
   * 
   * Add an array of arguments to the set of valid command-line arguments to
   * (the application using) OBIAMA.
   * 
   * @param args The array of arguments to add
   */
  public static void addArguments(CommandLineArgument[] args) {
    for(int j = 0; j < args.length; j++) {
      addArgument(args[j]);
    }
  }

  /**
   * <!-- addArguments -->
   * 
   * Add an iterable collection of arguments to the set of valid command-line
   * arguments to (the application using) OBIAMA.
   * 
   * @param args The collection of arguments to add
   */
  public static void addArguments(Iterable<CommandLineArgument> args) {
    for(CommandLineArgument arg: args) {
      addArgument(arg);
    }
  }

  /**
   * reasonerPresent
   * 
   * Test for the presence of a known reasoner class.
   * 
   * @return true if one of the default reasoner class names can be found in the
   *         jars loaded with the current application environment.
   */
  public static boolean reasonerPresent() {
    for(String name: reasoners) {
      if(reasonerPresent(name)) return true;
    }
    return false;
  }

  /**
   * reasonerPresent
   * 
   * Test for the presence of a specific reasoner class name, and that it
   * conforms to the OWLReasoner interface.
   * 
   * @param name The name of the reasoner class to test for (should be fully
   *          qualified with package)
   * @return true if the specified name can be found
   */
  public static boolean reasonerPresent(final String name) {
    if(name == null) return false;
    if(Reflection.classPresent(name)) {
      return true;
      /*
      try {
        Class<?> clazz = Class.forName(name);
        return Reflection.classImplements(clazz, OWLReasoner.class)
          || Reflection.classImplements(clazz, OWLReasonerFactory.class);
      }
      catch(ClassNotFoundException e) {
        // The class should already be known to exist through the classPresent()
        // call
        throw new Panic();
      }
      */
    }
    return false;
  }

  /**
   * <!-- knownReasonerWithValidEnvironment -->
   * 
   * @return The name of the first known reasoner class with all jars and
   *         libraries present
   */
  public static String knownReasonerWithValidEnvironment() {
    if(reasoners.length != reasonerJars.length || reasoners.length != reasonerLibs.length) {
      throw new Bug();
    }
    Map<String, String> reasonerSetUpErrors = new HashMap<String, String>();
    for(int j = 0; j < reasoners.length; j++) {
      if(!reasonerPresent(reasoners[j])) {
        reasonerSetUpErrors.put(reasoners[j], "Cannot find class");
        continue;
      }

      boolean jarsPresent = true;
      boolean libsPresent = true;

      for(String jar: reasonerJars[j]) {
        if(!Reflection.jarPatternPresent(jar)) {
          jarsPresent = false;
          reasonerSetUpErrors.put(reasoners[j], "Cannot find JAR file: " + jar);
          break;
        }
      }
      if(jarsPresent) {
        for(String lib: reasonerLibs[j]) {
          if(!Reflection.libraryPatternPresent(lib)) {
            libsPresent = false;
            reasonerSetUpErrors.put(reasoners[j], "Cannot find library: " + lib);
            break;
          }
        }
      }
      if(jarsPresent && libsPresent) return reasoners[j];
    }
    Log.noKnownReasoner(reasonerSetUpErrors);
    return null;
  }

  /**
   * <!-- knownReasonerRequirementsMessage -->
   * 
   * @return A string informing the user of known reasoner classes and their
   *         requirements
   */
  public static String knownReasonerRequirementsMessage() {
    StringBuffer buf = new StringBuffer("Known reasoner classes are: ");

    for(int j = 0; j < reasoners.length; j++) {
      if(j > 0) buf.append("; ");
      if(j > 0 && j == reasoners.length - 1) buf.append("and ");
      buf.append(reasoners[j]);
      if(reasonerJars[j].length == 0) {
        buf.append(", which does not require any jars");
      }
      else {
        buf.append(", which requires jars ");
        for(int k = 0; k < reasonerJars[j].length; k++) {
          if(k > 0 && k < reasonerJars[j].length - 1) buf.append(", ");
          else if(k > 0 && k == reasonerJars[j].length - 1) buf.append(" and ");
          buf.append(reasonerJars[j][k]);
        }
      }
      if(reasonerLibs[j].length == 0) {
        buf.append(reasonerJars[j].length == 0 ? "or libraries" : ", but does not require any libraries");
      }
      else {
        buf.append(reasonerJars[j].length == 0 ? ", but does require libraries " : ", and libraries ");
        for(int k = 0; k < reasonerLibs[j].length; k++) {
          if(k > 0 && k < reasonerLibs[j].length - 1) buf.append(", ");
          else if(k > 0 && k == reasonerLibs[j].length - 1) buf.append(" and ");
          buf.append(reasonerLibs[j][k]);
        }
      }
    }
    buf.append(". ");
    return buf.toString();
  }

  /**
   * getObiamaOptions
   * 
   * Process the arguments list to the application to extract information
   * required by OBIAMA. All arguments are parsed as key-value pairs from names
   * to values--see {@link uk.ac.hutton.obiama.model.CommandLineArgument}.
   * 
   * @param args The arguments passed to the application
   * @return Map containing parsed argument values
   * @throws UsageException
   */
  public static Map<String, String> getObiamaOptions(String cmd, String[] args) throws UsageException {
    command = cmd;
    obiamaArgs = CommandLineArgument.parseArgs(cmd, i.argMap.values(), args);
    if(obiamaArgs.containsKey("fcmp")) {
      try {
        fcmp = FCmpOntology.getFloatingPointComparison(getFCmpClassName(), getFCmpArgs());
      }
      catch(FloatingPointComparisonOntologyException e) {
        ErrorHandler.redo(e, "processing command-line arguments");
      }
    }
    else {
      fcmp = new LanguageDefaultFCmp();
    }
    try {
      String logFile = System.getenv(OBIAMA_LOG_FILE) == null ? DEFAULT_LOG_FILE : System.getenv(OBIAMA_LOG_FILE);
      if(obiamaArgs.containsKey("log")) logFile = obiamaArgs.get("log");
      if(!logFile.startsWith(File.separator)) {
        if(obiamaArgs.containsKey("save.last")) {
          logFile = obiamaArgs.get("save.last") + File.separator + logFile;
        }
        else if(obiamaArgs.containsKey("save.dir")) {
          logFile = obiamaArgs.get("save.dir") + File.separator + logFile;
        }
      }
      if(obiamaArgs.containsKey("log.messages")) {
        Log.open(logFile, Log.Messages.parseMessageList(obiamaArgs.get("log.messages")));
      }
      else {
        Log.open(logFile);
      }

      ErrorHandler.note("Logging to file " + Log.logfile());
    }
    catch(IOException e) {
      ErrorHandler.warn(e, "attempting to create log file " + Log.logfile(),
          "logging messages will be sent to the standard error stream");
    }
    Log.commandLineArguments(obiamaArgs);
    if(obiamaArgs.containsKey("rng.param")) {
      rngArgs = RNGFactory.parseRNGParams(obiamaArgs.get("rng.param"));
    }
    return new HashMap<String, String>(obiamaArgs);
  }

  /**
   * <!-- removeObiamaOptions -->
   * 
   * Remove from the map supplied as argument any command-line arguments that
   * are OBIAMA options defined in this class
   * 
   * @param opts A map of options (as returned from
   *          <code>getObiamaOptions()</code>
   * @return A map of non-OBIAMA arguments
   */
  public static Map<String, String> removeObiamaOptions(Map<String, String> opts) {
    Map<String, String> nonObiamaOpts = new HashMap<String, String>(opts);
    for(int j = 0; j < argList.length; j++) {
      if(nonObiamaOpts.containsKey(argList[j].getPropertyName())) nonObiamaOpts.remove(argList[j].getPropertyName());
    }
    return nonObiamaOpts;
  }

  /**
   * <!-- getOntologyURI -->
   * 
   * @return The ontology URI string supplied to the --ontology option
   */
  public static String getOntologyURIStr() {
    return obiamaArgs.get("ontology");
  }

  public static URI getOntologyURI() {
    try {
      return new URI(obiamaArgs.get("ontology"));
    }
    catch(URISyntaxException e) {
      ErrorHandler.redo(e, "creating model structure ontology URI from \"" + obiamaArgs.get("ontology") + "\"");
      throw new Bug();
    }
  }

  /**
   * getOntologySearchPath
   * 
   * Compute the list of directories in which to search for physical ontology
   * URIs. This is comprised of the following:
   * 
   * <ol>
   * <li>The current working directory</li>
   * <li>Any directories specified in the OBIAMA_ONTOLOGY_PATH environment
   * variable</li>
   * <li>Any directories specified in the --ontology-search-path command-line
   * option argument</li>
   * </ol>
   * 
   * @return
   */
  public static Set<String> getOntologySearchPath() {
    if(ontologySearchPath != null) return ontologySearchPath;

    Set<String> path = new HashSet<String>();
    addPaths(path, System.getProperty("user.dir"));
    addPaths(path, System.getenv(OBIAMA_ONTOLOGY_PATH));
    addPaths(path, obiamaArgs.get("ontology.search.path"));

    ontologySearchPath = path;
    return ontologySearchPath;
  }

  /**
   * <!-- getOntologyURIMap -->
   * 
   * @return The map of logical to physical URIs, read in from the argument to
   *         the --ontology-uri-map command-line option (or an empty map if this
   *         is not supplied)
   */
  public static Map<String, String> getOntologyURIMap() {
    if(ontologyURIMap != null) return ontologyURIMap;

    ontologyURIMap = new HashMap<String, String>();

    String csvFile = obiamaArgs.get("ontology.uri.map");
    if(csvFile == null) return ontologyURIMap;
    try {
      CSVReader reader = new CSVReader(obiamaArgs.get("ontology.uri.map"));
      Table<String> table = reader.getTable();
      if(table.ncols() < 2) {
        throw new FileFormatException(obiamaArgs.get("ontology.uri.map"), "two columns", table.ncols() + " columns", 1,
            0);
      }
      for(int row = 0; row < table.nrows(); row++) {
        if(row == 0 && table.atRC(row, 0).equalsIgnoreCase("logical")
          && table.atRC(row, 1).equalsIgnoreCase("physical")) {
          continue;
        }
        ontologyURIMap.put(table.atRC(row, 0), table.atRC(row, 1));
      }
    }
    catch(FileNotFoundException e) {
      ErrorHandler.redo(e,
          "attemping to load logical to physical ontology URI mappings from " + obiamaArgs.get("ontology.uri.map"));
      throw new Panic();
    }
    catch(IOException e) {
      ErrorHandler.redo(e,
          "attemping to load logical to physical ontology URI mappings from " + obiamaArgs.get("ontology.uri.map"));
      throw new Panic();
    }
    catch(CSVException e) {
      ErrorHandler.redo(e,
          "attemping to load logical to physical ontology URI mappings from " + obiamaArgs.get("ontology.uri.map"));
      throw new Panic();
    }
    catch(FileFormatException e) {
      ErrorHandler.redo(e,
          "attemping to load logical to physical ontology URI mappings from " + obiamaArgs.get("ontology.uri.map"));
      throw new Panic();
    }

    return ontologyURIMap;
  }

  /**
   * <!-- getReasonerClass -->
   * 
   * @return The reasoner class name supplied to the --reasoner-class option
   */
  public static String getReasonerClass() {
    return obiamaArgs.get("reasoner.class");
  }

  /**
   * <!-- getRequestedReasonerClass -->
   * 
   * @return The requested reasoner class, either default (if --use-reasoner
   *         given) or the class with name given as argument to --reasoner-class
   * @throws ClassNotFoundException
   */
  public static Class<?> getRequestedReasonerClass() throws ClassNotFoundException {
    String className = null;
    if(obiamaArgs.containsKey("reasoner.class")) {
      className = obiamaArgs.get("reasoner.class");
    }
    if(obiamaArgs.containsKey("use.reasoner")) {
      className = knownReasonerWithValidEnvironment();
    }
    if(!reasonerPresent(className)) throw new ClassNotFoundException(className);
    return Class.forName(className);
  }

  /**
   * <!-- getUseReasoner -->
   * 
   * @return <code>true</code> if --use-reasoner or --reasoner-class was given
   *         on the command line
   */
  public static boolean getUseReasoner() {
    return obiamaArgs.containsKey("use.reasoner") || obiamaArgs.get("reasoner.class") != null;
  }

  /**
   * <!-- getScheduleURI -->
   * 
   * @return The argument to the --schedule option
   */
  public static String getScheduleURI() {
    return obiamaArgs.get("schedule");
  }

  /**
   * <!-- getMainScheduleInstanceURI -->
   * 
   * @return the argument to the --main-schedule option
   */
  public static String getMainScheduleInstanceURI() {
    return obiamaArgs.get("main.schedule");
  }

  /**
   * <!-- getInitialScheduleInstanceURI -->
   * 
   * @return the argument to the --initial-schedule option
   */
  public static String getInitialScheduleInstanceURI() {
    return obiamaArgs.get("initial.schedule");
  }

  /**
   * <!-- getRNGClassName -->
   * 
   * @return The argument to the --rng option
   */
  public static String getRNGClassName() {
    return obiamaArgs.get("rng");
  }

  /**
   * <!-- getRNGSeed -->
   * 
   * @return The value of the seed RNG parameter
   */
  public static Long getRNGSeed() {
    if(rngArgs == null) return null;
    return rngArgs.containsKey("seed") ? new Long(rngArgs.get("seed")) : null;
  }

  /**
   * <!-- getRNGParams -->
   * 
   * @return A map of parameter-value pairs for the RNG
   */
  public static Map<String, String> getRNGParams() {
    if(rngArgs == null) return new HashMap<String, String>();
    return new HashMap<String, String>(rngArgs);
  }

  /**
   * <!-- getStateOntologyURI -->
   * 
   * @return The argument to the --state-ontology option
   */
  public static String getStateOntologyURI() {
    return obiamaArgs.get("state.ontology");
  }

  /**
   * <!-- getSaveLastURI -->
   * 
   * @return The directory specified by the --save-last, --separate-run-dirs and
   *         --run-dir-prefix options
   */
  public static String getSaveLast() {
    return getRunSubDir(obiamaArgs.get("save.last"));
  }

  /**
   * <!-- getRunSubDir -->
   * 
   * @param dir
   * @return The subdirectory of <code>dir</code> (if any) to use to save run
   *         data to
   */
  private static String getRunSubDir(String dir) {
    if(obiamaArgs.containsKey("separate.run.dirs") || obiamaArgs.containsKey("run.dir.prefix")) {
      String rundir = "run";
      if(obiamaArgs.containsKey("run.dir.prefix")) rundir = obiamaArgs.get("run.dir.prefix") + "-" + rundir;
      rundir = RunID.getRunSubDir(dir, rundir);
      return dir + File.separator + rundir;
    }
    else {
      return dir;
    }
  }

  /**
   * <!-- getSaveDir -->
   * 
   * @return The directory specified by the --save-dir, --separate-run-dirs and
   *         --run-dir-prefix options
   */
  public static String getSaveDir() {
    return getRunSubDir(obiamaArgs.get("save.dir"));
  }

  /**
   * <!-- getSaveInferred -->
   * 
   * @return Whether the --save-inferred option was given
   */
  public static boolean getSaveInferred() {
    return obiamaArgs.containsKey("save.inferred");
  }

  /**
   * <!-- getNonGUIMode -->
   * 
   * @return <code>true</code> if --background was given on the command line
   */
  public static boolean getNonGUIMode() {
    return obiamaArgs.containsKey("background");
  }

  /**
   * <!-- getGUIMode -->
   * 
   * @return <code>true</code> if --background was not given on the command-line
   */
  public static boolean getGUIMode() {
    return getNonGUIMode() ? false : true;
  }

  /**
   * <!-- getFCmpClassName -->
   * 
   * @return the class name given as argument to the --fcmp option
   */
  public static String getFCmpClassName() {
    return obiamaArgs.get("fcmp");
  }

  /**
   * <!-- getFCmpArgs -->
   * 
   * @return the property/value pairs given as argument to the --fcmp-args
   *         option
   */
  public static Map<String, String> getFCmpArgs() {
    String args = obiamaArgs.get("fcmp.args");
    String argValuePairs[] = args.split(",");
    Map<String, String> fcmpArgValues = new HashMap<String, String>();
    for(String argValuePair: argValuePairs) {
      String argValue[] = argValuePair.split("=");
      if(argValue.length != 2) {
        ErrorHandler.warn(new UsageException("--fcmp-args", "invalid format for \"" + argValuePair
          + "\": no \"=\" found", i.argMap.get("--fcmp-args").getHelpSynopsis()),
            "processing command line argument --fcmp-args", "settings in this argument-value pair will be ignored");
      }
      fcmpArgValues.put(argValue[0], argValue[1]);
    }
    return fcmpArgValues;
  }

  /**
   * <!-- getFCmp -->
   * 
   * @return the default floating point comparison method, as configured by
   *         command-line options, or the language default method if no options
   *         given
   */
  public static FloatingPointComparison getFCmp() {
    return fcmp;
  }

  /**
   * <!-- getProvenanceImplementation -->
   * 
   * @return The provenance implementation requested (could be <code>null</code>
   *         )
   */
  public static String getProvenanceImplementation() {
    return obiamaArgs.get("provenance");
  }

  /**
   * <!-- getHistoryProvenanceURI -->
   * 
   * @return A <code>String</code> containing the URI to save the history
   *         provenance to
   * @throws UsageException
   */
  public static String getHistoryProvenanceURI() throws UsageException {
    if(obiamaArgs.containsKey("provenance") && !obiamaArgs.containsKey("history.provenance")) {
      throw new UsageException("--provenance-history",
          "You must specify the history provenance URI if you request provenance", usage(command));
    }
    return obiamaArgs.get("history.provenance");
  }

  /**
   * addPaths
   * 
   * Add the directories contained in the paths argument (a string of colon
   * (Unix/Mac) or semicolon (Windows) separated directories) to the set in the
   * path argument.
   * 
   * @param path The set of directory names extracted from the paths
   * @param paths The colon (or semicolon) separated list of directory names
   */
  private static void addPaths(Set<String> path, String paths) {
    if(paths == null) return;
    for(String dir: paths.split(System.getProperty("path.separator"))) {
      path.add(dir);
    }
  }

  /**
   * usage
   * 
   * Provide some information on command-line option usage for OBIAMA.
   * 
   * @return A string containing a formatted list of command-line options for
   *         OBIAMA.
   */
  public static String usage(String command) {
    return CommandLineArgument.usage(command, i.argMap.values());
  }

}
