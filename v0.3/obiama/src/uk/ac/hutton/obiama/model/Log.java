/*
 * uk.ac.hutton.obiama.model: Log.java Copyright (C) 2013 The James Hutton Institute
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uk.ac.hutton.obiama.exception.ErrorHandler;
import uk.ac.hutton.obiama.msb.Var;
import uk.ac.hutton.util.Panic;
import uk.ac.hutton.util.StringTools;

/**
 * Log
 * 
 * A logger for OBIAMA, allowing various formats to be used. The class can be
 * used both as a global log file (using static methods), and to create
 * individual log files as objects. The log file name is configurable, but note
 * that the name may be changed if a file with that name already exists.
 * 
 * @author Gary Polhill
 */
public class Log {
  /**
   * Messages
   * 
   * The set of messages this logger can recognise.
   * 
   * @author Gary Polhill
   */
  public enum Messages {
    // Messages used by this class (cannot be configured to not be recorded)
    ADD_MESSAGE,
    REMOVE_MESSAGE,
    // Messages from outside the class
    COMPARISON, LOAD_ONTOLOGY_SUCCESS, LOAD_ONTOLOGY_FAIL, LOAD_ONTOLOGY_IGNORE, COMMAND_LINE_ARGUMENTS,
    SAVE_ONTOLOGY_SUCCESS, SAVE_ONTOLOGY_FAIL, SEED, RNG, RESET, RUN_DIR, IGNORING_EXCEPTIONS, NO_KNOWN_REASONER,
    UPDATE, ACTION, ADD_AXIOM, REMOVE_AXIOM, START_INITIAL_SCHEDULE, STOP_INITIAL_SCHEDULE, START_MAIN_SCHEDULE,
    STOP_MAIN_SCHEDULE, QUERY;

    public static final String DEFAULT_SEPARATOR = ",";

    /**
     * <!-- parseMessageList -->
     * 
     * Parse a list of messages using the default separator
     * 
     * @param messageList A list of messages
     * @return The array of <code>Messages</code> corresponding to that list
     */

    public static Messages[] parseMessageList(String messageList) {
      return parseMessageList(messageList, DEFAULT_SEPARATOR);
    }

    /**
     * <!-- parseMessageList -->
     * 
     * Parse a list of messages using a specified separator
     * 
     * @param messageList A list of messages
     * @param separator The separator used to separate items in the list
     * @return The array of <code>Messages</code> corresponding to the list
     */
    public static Messages[] parseMessageList(String messageList, String separator) {
      Set<Messages> messageSet = new HashSet<Messages>();

      for(String messageName: messageList.split(separator)) {
        Messages message = Messages.valueOf(messageName);
        if(message == null) {
          ErrorHandler.warn(new Exception("Message \"" + message + "\" not recognised by OBIAMA logger"),
              "parsing message list \"" + messageList + "\"", "the request to log " + message + " will be ignored");
        }
        messageSet.add(message);
      }

      return messageSet.toArray(new Messages[0]);
    }
  }

  /**
   * Formats
   * 
   * The formats that messages can be written in.
   * 
   * @author Gary Polhill
   */
  public enum Formats {
    CSV, TXT, LOG, XML;

    /**
     * The default format to write log files in.
     */
    public static final Formats DEFAULT_FORMAT = LOG;

    /**
     * <!-- message -->
     * 
     * Format a message for printing in this format.
     * 
     * @param messageType The message it is
     * @param argLabels Metadata for the arguments
     * @param args The arguments
     * @return A formatted message string
     */
    private String message(Messages messageType, String[] argLabels, Object... args) {
      switch(this) {
      case CSV:
        // This really ought to be formatted nicely using a CSVWriter... (e.g.
        // what if one of the arguments contains a comma or quote when converted
        // to a string?)
        return messageType.toString() + "," + StringTools.join(",", args);
      case TXT:
        StringBuffer msg = new StringBuffer(messageType.toString());
        msg.append(": ");
        for(int i = 0; i < (argLabels.length > args.length ? argLabels.length : args.length); i++) {
          if(i > 0) msg.append("; ");
          if(i < argLabels.length && i < args.length) {
            msg.append(argLabels[i]);
            msg.append(" = ");
            msg.append(args[i].toString());
          }
          else if(i < argLabels.length) {
            msg.append(argLabels[i]);
            msg.append(" (not supplied)");
          }
          else if(i < args.length) {
            msg.append(" (no label) = ");
            msg.append(args[i].toString());
          }
          else {
            throw new Panic();
          }
        }
        return msg.toString();
      case LOG:
        StringBuffer logmsg = new StringBuffer(messageType.toString());
        logmsg.append(":\n");
        for(int i = 0; i < (argLabels.length > args.length ? argLabels.length : args.length); i++) {
          logmsg.append("\t");
          if(i < argLabels.length && i < args.length) {
            logmsg.append(argLabels[i] == null ? "(no label)" : argLabels[i]);
            logmsg.append(args[i] == null ? " (not supplied)" : " = " + args[i].toString());
          }
          else if(i < argLabels.length) {
            logmsg.append(argLabels[i] == null ? "(no label)" : argLabels[i]);
            logmsg.append(" (not supplied)");
          }
          else if(i < args.length) {
            logmsg.append("(no label)");
            logmsg.append(args[i] == null ? " (not supplied)" : " = " + args[i].toString());
          }
          else {
            throw new Panic();
          }
          logmsg.append("\n");
        }
        return logmsg.toString();
      case XML:
        StringBuffer xmlmsg = new StringBuffer("  <");
        xmlmsg.append(messageType.toString());
        xmlmsg.append(">\n");
        for(int i = 0; i < (argLabels.length > args.length ? argLabels.length : args.length); i++) {
          xmlmsg.append("    <");
          if(i < argLabels.length && i < args.length) {
            xmlmsg.append(argLabels[i] == null ? "no-metadata" : argLabels[i]);
            xmlmsg.append(">");
            xmlmsg.append(args[i] == null ? "(not supplied)" : args[i].toString());
            xmlmsg.append("</");
            xmlmsg.append(argLabels[i] == null ? "no-metadata" : argLabels[i]);
          }
          else if(i < argLabels.length) {
            xmlmsg.append(argLabels[i] == null ? "no-metadata" : argLabels[i]);
            xmlmsg.append(">(not supplied)</");
            xmlmsg.append(argLabels[i] == null ? "no-metadata" : argLabels[i]);
          }
          else if(i < args.length) {
            xmlmsg.append("no-metadata>");
            xmlmsg.append(args[i] == null ? "(not supplied)" : args[i].toString());
            xmlmsg.append("</no-metadata");
          }
          else {
            throw new Panic();
          }
          xmlmsg.append(">\n");
        }
        xmlmsg.append("  </");
        xmlmsg.append(messageType.toString());
        xmlmsg.append(">\n");
        return xmlmsg.toString();
      default:
        throw new Panic();
      }
    }

    /**
     * <!-- getSuffix -->
     * 
     * The suffix that log files of the given format are expected to have.
     * 
     * @return The format's suffix
     */
    public String getSuffix() {
      switch(this) {
      case CSV:
        return ".csv";
      case TXT:
        return ".txt";
      case LOG:
        return ".log";
      case XML:
        return ".xml";
      default:
        throw new Panic();
      }
    }

    /**
     * <!-- getFormat -->
     * 
     * @param filename Name of a proposed log file
     * @return The log file format most appropriate to the suffix of the log
     *         file name
     */
    public static Formats getFormat(String filename) {
      for(Formats format: Formats.values()) {
        if(filename.substring(filename.length() - format.getSuffix().length()).equalsIgnoreCase(format.getSuffix())) {
          return format;
        }
      }
      return DEFAULT_FORMAT;
    }

    /**
     * <!-- header -->
     * 
     * Generate the header string to use for the log, if any
     * 
     * @return String to start a log file with
     */
    private String header() {
      switch(this) {
      case CSV:
      case TXT:
      case LOG:
        return null;
      case XML:
        return "<?xml version=\"1.0\"?>\n<obiama-log>\n";
      default:
        throw new Panic();
      }
    }

    /**
     * <!-- footer -->
     * 
     * Generate a footer string to use for the log, if any
     * 
     * @return String to append to the end of a log file
     */
    private String footer() {
      switch(this) {
      case CSV:
      case TXT:
      case LOG:
        return null;
      case XML:
        return "</obiama-log>\n";
      default:
        throw new Panic();
      }
    }

  }

  /**
   * Static instance for use with global logger
   */
  private static Log i = new Log();

  /**
   * The messages this log file is recording
   */
  private Set<Messages> logItems;

  /**
   * The writer this log file is recording to
   */
  private PrintWriter fp;

  /**
   * The name of the log file being written
   */
  private String logfile;

  /**
   * The format of the log file
   */
  private Formats format;

  /**
   * Basic constructor, LOG format, no file, and no messages.
   */
  public Log() {
    logItems = new HashSet<Messages>();
    fp = null;
    format = Formats.DEFAULT_FORMAT;
  }

  /**
   * Configure a log file in the default format with no messages.
   * 
   * @param logfile the name of the log file
   * @throws IOException
   */
  public Log(String logfile) throws IOException {
    this(logfile, Formats.getFormat(logfile));
  }

  /**
   * Configure a log file in the specified format with no messages.
   * 
   * @param logfile the name of the log file
   * @param format the format to write the log file in
   * @throws IOException
   */
  public Log(String logfile, Formats format) throws IOException {
    this();
    this.format = format;
    openLog(logfile);
  }

  /**
   * Configure a log file in the default format with the specified messages
   * 
   * @param logfile the name of the log file
   * @param msgs the set of messages to record in the log file
   * @throws IOException
   */
  public Log(String logfile, Messages... msgs) throws IOException {
    this(logfile);
    logMessages(msgs);
  }

  /**
   * Configure a log file in the specified format recording the specified
   * messages
   * 
   * @param logfile the name of the log file
   * @param format the format for the log file
   * @param msgs the set of messages to record in the log file
   * @throws IOException
   */
  public Log(String logfile, Formats format, Messages... msgs) throws IOException {
    this(logfile, format);
    logMessages(msgs);
  }

  /**
   * <!-- open -->
   * 
   * Open a global log file, recording all messages. If the global log file is
   * already open it will be closed.
   * 
   * @param logfile The name of the log file to open
   * @throws IOException
   */
  public static final void open(String logfile) throws IOException {
    open(logfile, Formats.getFormat(logfile));
  }

  /**
   * <!-- open -->
   * 
   * Open a global log file, recording the specified messages. If the global log
   * file is already open it will be closed.
   * 
   * @param logfile The name of the log file to open
   * @param msgs The messages to log
   * @throws IOException
   */
  public static final void open(String logfile, Messages... msgs) throws IOException {
    open(logfile, Formats.getFormat(logfile), msgs);
  }

  /**
   * <!-- open -->
   * 
   * Open a global log file with the specified format. If the global log file is
   * already open it will be closed.
   * 
   * @param logfile The name of the log file to open
   * @param format The format of the log file
   * @throws IOException
   */
  public static final void open(String logfile, Formats format) throws IOException {
    open(logfile, format, Messages.values());
  }

  /**
   * <!-- open -->
   * 
   * Open a global log file with the specified format and messages to record in
   * it. If the global log file is already open it will be closed.
   * 
   * @param logfile The name of the log file to open
   * @param format The format for the log file
   * @param msgs The messages to record in it
   * @throws IOException
   */
  public static final void open(String logfile, Formats format, Messages... msgs) throws IOException {
    messages(msgs);
    i.format = format;
    i.openLog(logfile);
  }

  /**
   * <!-- openLog -->
   * 
   * Record data in the specified log file. If an earlier configured log file is
   * open already, it will be closed. If a file with the same name as the log
   * file exists, a file name will be found that does not already exist.
   * 
   * @param logfile The name of the log file to open.
   * @throws IOException
   */
  public final void openLog(String logfile) throws IOException {
    if(fp != null) closeLog();
    File file = new File(logfile);
    for(int i = 0; file.exists(); i++) {
      String suffix = format.getSuffix();
      if(logfile.endsWith(suffix)) {
        file = new File(logfile.substring(0, logfile.length() - suffix.length() + 1) + i + suffix);
      }
      else {
        file = new File(logfile + "." + i);
      }
    }
    this.logfile = file.getAbsolutePath();
    fp = new PrintWriter(new FileWriter(file));
    write(format.header());
  }

  /**
   * <!-- messages -->
   * 
   * Set the messages to record in the global log file
   * 
   * @param msgs The messages to record in the global log file
   */
  public static final void messages(Messages... msgs) {
    i.logItems.clear();
    i.logMessages(msgs);
  }

  /**
   * <!-- ignore -->
   * 
   * Set the messages to ignore in the global log file
   * 
   * @param msgs
   */
  public static final void ignore(Messages... msgs) {
    i.ignoreMessages(msgs);
  }

  /**
   * <!-- logMessages -->
   * 
   * Add some messages to be recorded in the log file
   * 
   * @param msgs
   */
  public final void logMessages(Messages... msgs) {
    for(Messages msg: msgs) {
      if(!logItems.contains(msg)) {
        logItems.add(msg);
        write(format.message(Messages.ADD_MESSAGE, new String[] { "message" }, msg.toString()));
      }
    }
  }

  /**
   * <!-- logAll -->
   * 
   * Add all messages to the log file
   */
  public final void logAll() {
    logMessages(Messages.values());
  }

  /**
   * <!-- ignoreMessages -->
   * 
   * Remove some messages from being recorded in the log file
   * 
   * @param msgs
   */
  public final void ignoreMessages(Messages... msgs) {
    for(Messages msg: msgs) {
      if(logItems.contains(msg)) {
        logItems.remove(msg);
        write(format.message(Messages.REMOVE_MESSAGE, new String[] { "message" }, msg.toString()));
      }
    }
  }

  /**
   * <!-- close -->
   * 
   * Close the global log file
   */
  public static void close() {
    i.closeLog();
  }

  /**
   * <!-- closeLog -->
   * 
   * Close the log file, writing any end-of-file text stipulated by the format
   */
  public final void closeLog() {
    write(format.footer());
    fp.close();
    fp = null;
  }

  /**
   * <!-- logfile -->
   * 
   * Get the name of the global log file. This may be different from that
   * configured if a file with the requested name already existed.
   * 
   * @return The name of the global log file
   */
  public static String logfile() {
    return i.logfile;
  }

  /**
   * <!-- getLogFile -->
   * 
   * Get the name of the log file, which may be different from that requested.
   * 
   * @return The name of the log file
   */
  public String getLogFile() {
    return logfile;
  }

  /**
   * <!-- write -->
   * 
   * Write a message to the log file, and flush
   * 
   * @param message The message to write
   */
  public final void write(String message) {
    if(message != null) {
      if(fp == null) System.err.println(message);
      else {
        fp.write(message);
        fp.flush();
      }
    }
  }

  // // // // // // // // // // // // // // // // // // // // // // // // // //
  //
  // Methods for logging specific messages
  //
  // // // // // // // // // // // // // // // // // // // // // // // // // //

  /**
   * <!-- comparison -->
   * 
   * Log a comparison of floating point numbers
   * 
   * @param var The property being compared
   * @param value The value of the (data) property
   * @param comparator The object the value is being compared with
   * @param result The result of the comparison.
   */
  public static final void comparison(Var var, Number value, Object comparator, int result) {
    i.logComparison(var, value, comparator, result);
  }

  public final void logComparison(Var var, Number value, Object comparator, int result) {
    if(logItems.contains(Messages.COMPARISON)) {
      write(format.message(Messages.COMPARISON, new String[] { "property", "value", "compared-with", "result" },
          var.getURI(), value, comparator, result));
    }
  }

  /**
   * <!-- loadOntologySuccessfully -->
   * 
   * Log successful loading of an ontology
   * 
   * @param logicalURI Logical URI of the successfully loaded ontology
   * @param physicalURI Physical URI
   * @param imported Whether the ontology is imported
   */
  public static final void loadOntologySuccessfully(URI logicalURI, URI physicalURI, boolean imported) {
    i.logLoadOntologySuccessfully(logicalURI, physicalURI, imported);
  }

  public final void logLoadOntologySuccessfully(URI logicalURI, URI physicalURI, boolean imported) {
    if(logItems.contains(Messages.LOAD_ONTOLOGY_SUCCESS)) {
      write(format.message(Messages.LOAD_ONTOLOGY_SUCCESS,
          new String[] { "logical-uri", "physical-uri", "is-imported" }, logicalURI, physicalURI, imported));
    }
  }

  /**
   * <!-- loadOntologyFail -->
   * 
   * Log failed loading of an ontology
   * 
   * @param logicalURI Logical URI of the ontology not loaded
   * @param physicalURI Physical URI of the ontology not loaded
   * @param imported Whether the ontology is imported
   * @param cause The Exception causing the failure
   */
  public static final void loadOntologyFail(URI logicalURI, URI physicalURI, boolean imported, Exception cause) {
    i.logLoadOntologyFail(logicalURI, physicalURI, imported, cause);
  }

  public final void logLoadOntologyFail(URI logicalURI, URI physicalURI, boolean imported, Exception cause) {
    if(logItems.contains(Messages.LOAD_ONTOLOGY_FAIL)) {
      write(format.message(Messages.LOAD_ONTOLOGY_FAIL, new String[] { "logical-uri", "physical-uri", "is-imported",
        "exception", "message" }, logicalURI, physicalURI, imported, cause.getClass().getName(), cause.getMessage()));
    }
  }

  /**
   * <!-- loadOntologyIgnore -->
   * 
   * Log an ignored ontology load failure
   * 
   * @param logicalURI URI of the ontology the failed loading of which has been
   *          ignored
   * @param cause Cause of the load failure
   */
  public static final void loadOntologyIgnore(URI logicalURI, Exception cause) {
    i.logLoadOntologyIgnore(logicalURI, cause);
  }

  public final void logLoadOntologyIgnore(URI logicalURI, Exception cause) {
    if(logItems.contains(Messages.LOAD_ONTOLOGY_IGNORE)) {
      write(format.message(Messages.LOAD_ONTOLOGY_IGNORE, new String[] { "logical-uri", "exception", "message" },
          logicalURI, cause.getClass().getName(), cause.getMessage()));
    }
  }

  /**
   * <!-- saveOntologySuccess -->
   * 
   * Log saved ontologies
   * 
   * @param logicalURI URI of the ontology saved
   * @param physicalURI Location it was saved to
   */
  public static final void saveOntologySuccess(URI logicalURI, URI physicalURI) {
    i.logSaveOntologySuccess(logicalURI, physicalURI);
  }

  public final void logSaveOntologySuccess(URI logicalURI, URI physicalURI) {
    if(logItems.contains(Messages.SAVE_ONTOLOGY_SUCCESS)) {
      write(format.message(Messages.SAVE_ONTOLOGY_SUCCESS, new String[] { "logical-uri", "physical-uri" }, logicalURI,
          physicalURI));
    }
  }

  /**
   * <!-- saveOntologyFail -->
   * 
   * Log failures to save ontologies
   * 
   * @param logicalURI URI of the ontology saved (can be null if not known, but
   *          there is some other problem with the location)
   * @param physicalURI Location trying to save it to
   * @param cause Exception when doing so
   */
  public static final void saveOntologyFail(URI logicalURI, URI physicalURI, Exception cause) {
    i.logSaveOntologyFail(logicalURI, physicalURI, cause);
  }

  public final void logSaveOntologyFail(URI logicalURI, URI physicalURI, Exception cause) {
    if(logItems.contains(Messages.SAVE_ONTOLOGY_FAIL)) {
      if(logicalURI == null) {
        write(format.message(Messages.SAVE_ONTOLOGY_FAIL, new String[] { "physical-uri", "exception", "message" },
            physicalURI, cause.getClass().getName(), cause.getMessage()));
      }
      else {
        write(format.message(Messages.SAVE_ONTOLOGY_FAIL, new String[] { "logical-uri", "physical-uri", "exception",
          "message" }, logicalURI, physicalURI, cause.getClass().getName(), cause.getMessage()));
      }
    }
  }

  /**
   * <!-- commandLineArguments -->
   * 
   * Log the command line arguments as parsed
   * 
   * @param parsedArgs Map (returned from
   *          {@link uk.ac.hutton.obiama.msb.ObiamaSetUp#getObiamaOptions(String, String[])}
   *          )
   */
  public static final void commandLineArguments(Map<String, String> parsedArgs) {
    i.logCommandLineArguments(parsedArgs);
  }

  public final void logCommandLineArguments(Map<String, String> parsedArgs) {
    if(logItems.contains(Messages.COMMAND_LINE_ARGUMENTS)) {
      String[] args = parsedArgs.keySet().toArray(new String[0]);
      String[] values = new String[args.length];
      for(int i = 0; i < args.length; i++) {
        values[i] = parsedArgs.get(args[i]);
      }
      write(format.message(Messages.COMMAND_LINE_ARGUMENTS, args, (Object[])values));
    }
  }

  /**
   * <!-- seed -->
   * 
   * Log the seed used for the run (other information will be in the command
   * line arguments)
   * 
   * @param seed The seed
   */
  public static final void seed(long seed) {
    i.logSeed(seed);
  }

  public final void logSeed(long seed) {
    if(logItems.contains(Messages.SEED)) write(format.message(Messages.SEED, new String[] { "seed" }, seed));
  }

  /**
   * <!-- rng -->
   * 
   * Log RNG creation
   * 
   * @param name Name of the RNG class being created
   * @param params Any parameters it has
   */
  public static final void rng(String name, Map<String, String> params) {
    i.logRng(name, params);
  }

  public final void logRng(String name, Map<String, String> params) {
    if(logItems.contains(Messages.RNG)) {
      if(params == null) params = new HashMap<String, String>();
      String[] args = new String[params.size() + 1];
      args[0] = "RNG-class";
      int j = 1;
      for(String arg: params.keySet()) {
        args[j] = arg;
        j++;
      }

      String[] values = new String[args.length];
      values[0] = name;
      for(int i = 1; i < args.length; i++) {
        values[i] = params.get(args[i]);
      }
      write(format.message(Messages.RNG, args, (Object[])values));
    }
  }

  /**
   * <!-- reset -->
   * 
   * Log that the MSB has reset.
   * 
   * @param runID Run ID for the new run
   */
  public static final void reset(String runID) {
    i.logReset(runID);
  }

  public final void logReset(String runID) {
    if(logItems.contains(Messages.RESET)) write(format.message(Messages.RESET, new String[] { "runID" }, runID));
  }

  /**
   * <!-- runDir -->
   * 
   * Log a new run ID
   * 
   * @param runID Run ID
   * @param dir Directory it will be saved to
   */
  public static final void runDir(String runID, String dir) {
    i.logRunDir(runID, dir);
  }

  public final void logRunDir(String runID, String dir) {
    if(logItems.contains(Messages.RUN_DIR)) {
      write(format.message(Messages.RUN_DIR, new String[] { "runID", "subdir" }, runID, dir));
    }
  }

  /**
   * <!-- ignoring -->
   * 
   * Log the fact that some exceptions are being ignored.
   * 
   * @param exceptionName Name of an exception class that is being ignored
   * @param args Arguments to exception class constructor describing ignored
   *          situation
   */
  public static void ignoring(String exceptionName, Object[] args) {
    i.logIgnoring(exceptionName, args);
  }

  private void logIgnoring(String exceptionName, Object[] args) {
    if(logItems.contains(Messages.IGNORING_EXCEPTIONS)) {
      StringBuffer buff = new StringBuffer();
      for(int j = 0; j < args.length; j++) {
        if(j > 0) buff.append(" ");
        buff.append(args[j].toString());
      }
      write(format
          .message(Messages.IGNORING_EXCEPTIONS, new String[] { "exception", "situation" }, exceptionName, buff));
    }
  }

  /**
   * <!-- noKnownReasoner -->
   * 
   * @param errs
   */
  public static void noKnownReasoner(Map<String, String> errs) {
    i.logNoKnownReasoner(errs);
  }

  /**
   * <!-- logNoKnownReasoner -->
   * 
   * @param errs
   */
  private void logNoKnownReasoner(Map<String, String> errs) {
    if(logItems.contains(Messages.NO_KNOWN_REASONER)) {
      String[] reasonerErrorArray = new String[errs.keySet().size() * 2];
      String[] fields = new String[errs.keySet().size() * 2];
      String[] keys = errs.keySet().toArray(new String[0]);
      for(int i = 0; i < keys.length; i++) {
        reasonerErrorArray[i * 2] = keys[i];
        reasonerErrorArray[(i * 2) + 1] = errs.get(keys[i]);
        fields[i * 2] = "reasoner";
        fields[(i * 2) + 1] = "error";
      }
      write(format.message(Messages.NO_KNOWN_REASONER, fields, (Object[])reasonerErrorArray));
    }
  }

  /**
   * <!-- update -->
   * 
   * Log a change to the ontology
   */
  public static void update(int nRemoved, int nAdded) {
    i.logUpdate(nRemoved, nAdded);
  }

  /**
   * <!-- logUpdate -->
   * 
   */
  private void logUpdate(int nRemoved, int nAdded) {
    if(logItems.contains(Messages.UPDATE)) {
      write(format.message(Messages.UPDATE, new String[] { "n-removed-axioms", "n-added-axioms" }, nRemoved, nAdded));
    }
  }

  /**
   * <!-- action -->
   * 
   * Log an action stepping
   * 
   * @param actionURI
   */
  public static void action(URI actionURI) {
    i.logAction(actionURI);
  }

  /**
   * <!-- logAction -->
   * 
   * @param actionURI
   */
  private void logAction(URI actionURI) {
    if(logItems.contains(Messages.ACTION)) {
      write(format.message(Messages.ACTION, new String[] { "action-URI" }, actionURI));
    }
  }

  /**
   * <!-- addedAxiom -->
   * 
   * @param axiom
   * @param uri
   */
  public static void addedAxiom(String axiom, URI uri) {
    i.logAddedAxiom(axiom, uri);
  }

  /**
   * <!-- logAddedAxiom -->
   * 
   * @param axiom
   * @param uri
   */
  private void logAddedAxiom(String axiom, URI uri) {
    if(logItems.contains(Messages.ADD_AXIOM)) {
      write(format.message(Messages.ADD_AXIOM, new String[] { "axiom", "ontology" }, axiom, uri));
    }
  }

  /**
   * <!-- removedAxiom -->
   * 
   * @param axiom
   * @param uri
   */
  public static void removedAxiom(String axiom, URI uri) {
    i.logRemovedAxiom(axiom, uri);
  }

  /**
   * <!-- logRemovedAxiom -->
   * 
   * @param axiom
   * @param uri
   */
  private void logRemovedAxiom(String axiom, URI uri) {
    if(logItems.contains(Messages.REMOVE_AXIOM)) {
      write(format.message(Messages.REMOVE_AXIOM, new String[] { "axiom", "ontology" }, axiom, uri));
    }
  }

  /**
   * <!-- startInitialSchedule -->
   * 
   * @param uri
   */
  public static void startInitialSchedule(URI uri) {
    i.logStartInitialSchedule(uri);
  }

  /**
   * <!-- logStartInitialSchedule -->
   * 
   * @param uri
   */
  private void logStartInitialSchedule(URI uri) {
    if(logItems.contains(Messages.START_INITIAL_SCHEDULE)) {
      write(format.message(Messages.START_INITIAL_SCHEDULE, new String[] { "schedule-uri" }, uri));
    }
  }

  /**
   * <!-- stopInitialSchedule -->
   *
   * @param uri
   */
  public static void stopInitialSchedule(URI uri) {
    i.logStopInitialSchedule(uri);
  }

  /**
   * <!-- logStopInitialSchedule -->
   *
   * @param uri
   */
  private void logStopInitialSchedule(URI uri) {
    if(logItems.contains(Messages.STOP_INITIAL_SCHEDULE)) {
      write(format.message(Messages.STOP_INITIAL_SCHEDULE, new String[] { "schedule-uri" }, uri));
    }
  }


  /**
   * <!-- startMainSchedule -->
   * 
   * @param uri
   */
  public static void startMainSchedule(URI uri) {
    i.logStartMainSchedule(uri);
  }

  /**
   * <!-- logStartMainSchedule -->
   * 
   * @param uri
   */
  private void logStartMainSchedule(URI uri) {
    if(logItems.contains(Messages.START_MAIN_SCHEDULE)) {
      write(format.message(Messages.START_MAIN_SCHEDULE, new String[] { "schedule-uri" }, uri));
    }
  }

  /**
   * <!-- stopMainSchedule -->
   *
   * @param uri
   */
  public static void stopMainSchedule(URI uri) {
    i.logStopMainSchedule(uri);
  }

  /**
   * <!-- logStopMainSchedule -->
   *
   * @param uri
   */
  private void logStopMainSchedule(URI uri) {
    if(logItems.contains(Messages.STOP_MAIN_SCHEDULE)) {
      write(format.message(Messages.STOP_MAIN_SCHEDULE, new String[] { "schedule-uri" }, uri));
    }
  }

  public static void query(URI agent, URI requester, String className, URI queryID, Object result, Object[] args) {
    i.logQuery(agent, requester, className, queryID, result, args);
  }
  
  private void logQuery(URI agent, URI requester, String className, URI queryID, Object result, Object[] args) {
    if(logItems.contains(Messages.QUERY)) {
      String[] labels = new String[args.length + 5];
      Object[] values = new Object[args.length + 5];
      
      labels[0] = "query-id";
      values[0] = queryID;
      
      labels[1] = "query-class";
      values[1] = className;
      
      labels[2] = "asking-agent";
      values[2] = requester;
      
      labels[3] = "answering-agent";
      values[3] = agent;
      
      labels[4] = "result";
      values[4] = result;
      
      for(int i = 0; i < args.length; i++) {
        labels[5 + i] = "arg-" + Integer.toString(i + 1);
        values[5 + i] = args[i];
      }
      write(format.message(Messages.QUERY, labels, values));
    }
  }
}
