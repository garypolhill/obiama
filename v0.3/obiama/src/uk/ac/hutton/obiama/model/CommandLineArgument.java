/*
 * uk.ac.hutton.obiama.model: CommandLineArgument.java
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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.ac.hutton.obiama.exception.Bug;
import uk.ac.hutton.obiama.exception.Panic;
import uk.ac.hutton.obiama.exception.UsageException;

/**
 * CommandLineArgument
 * 
 * This is a class to manage command-line arguments, both optional and required.
 * Optional arguments can be either a flag, or an option, with the former being
 * a boolean value assumed by default to be false, and the latter taking an
 * argument. Optional arguments have a command-line indicator, which can be
 * supplied in one of two ways, either as a short indicator (consisting of a
 * dash followed by an upper or lower case letter), or a long indicator
 * (consisting of two dashes followed by a word). In the case of a flag, the
 * presence of the indicator sets it to true; in the case of an option, the next
 * word on the command line is assumed to be the argument. The list of optional
 * arguments can be terminated using two dashes (i.e. an 'empty' long
 * indicator). All subsequent arguments are required arguments, which are
 * expected to have a specific order.
 * 
 * @author Gary Polhill
 */
public class CommandLineArgument implements Comparable<CommandLineArgument> {
  /**
   * Number of dashes to use to prefix long command-line option names.
   */
  private static final int N_LONGOPT_DASHES = 2;

  /**
   * The long command-line option name, including the double-dash prefix
   */
  private String longoption;

  /**
   * The short command-line option name, including the single-dash prefix
   */
  private String shortoption;

  /**
   * The name for the argument to the command-line option, if any
   */
  private String optionArgumentName;

  /**
   * The name to use when looking up the value of the command-line option. This
   * is derived automatically from the long command-line option name by removing
   * the first two dashes and replacing all other dashes with dots.
   */
  private String propertyName;

  /**
   * A description of the command-line option
   */
  private String helpText;

  /**
   * <code>true</code> if the argument is required rather than optional.
   */
  private boolean required;

  /**
   * The name of the argument
   */
  private String argumentName;

  /**
   * For required arguments, the order in which it must appear (first place is
   * 0).
   */
  private int order;

  /**
   * Constructor for required arguments. The order must be unique in any given
   * set of arguments.
   * 
   * @param name The name of the argument (to use in the synopsis)
   * @param help A brief description of the argument
   * @param order The order in which the argument should appear (0 first).
   */
  public CommandLineArgument(String name, String help, int order) {
    longoption = null;
    if(name == null) throw new Bug();
    argumentName = name;
    Matcher substitution = Pattern.compile(" ").matcher(name);
    propertyName = substitution.replaceAll(".");
    shortoption = null;
    optionArgumentName = null;
    helpText = help;
    required = true;
    if(order < 0) throw new Bug();
    this.order = order;
  }

  /**
   * Constructor for flags.
   * 
   * @param lopt The long flag name (must be supplied), including initial dashes
   * @param sopt The short flag name (may be null), including initial dash
   * @param help Brief description of the flag
   */
  public CommandLineArgument(String lopt, String sopt, String help) {
    this(lopt, sopt, null, help);
  }

  /**
   * Constructor for options.
   * 
   * @param lopt Long option name (must be supplied), including initial dashes
   * @param sopt Short option name (may be null), including initial dash
   * @param argname Name to use for describing argument
   * @param help Brief description of the command-line option
   */
  public CommandLineArgument(String lopt, String sopt, String argname, String help) {
    longoption = lopt;
    if(lopt == null) throw new Bug();
    Matcher substitution = Pattern.compile("-").matcher(lopt.substring(N_LONGOPT_DASHES));
    propertyName = substitution.replaceAll(".");
    shortoption = sopt;
    optionArgumentName = argname;
    helpText = help;
    argumentName = null;
    required = false;
    order = -1;
  }

  /**
   * <!-- sort -->
   * 
   * Sort a collection of command-line arguments. Options and flags are sorted
   * first, in alphabetical order of short option (if given) and long option (if
   * not), then required arguments, in the order specified.
   * 
   * @param args The command-line arguments collection
   * @return A sorted collection
   */
  public static LinkedList<CommandLineArgument> sort(Iterable<CommandLineArgument> args) {
    LinkedList<CommandLineArgument> argList = new LinkedList<CommandLineArgument>();

    for(CommandLineArgument arg: args) {
      argList.add(arg);
    }
    Collections.sort(argList);

    return argList;
  }

  /**
   * <!-- sort -->
   * 
   * Convenience method for sorting arrays of command-line arguments
   * 
   * @param args The command-line arguments array
   * @return A sorted collection
   */
  public static LinkedList<CommandLineArgument> sort(CommandLineArgument[] args) {
    return sort(Arrays.asList(args));
  }

  /**
   * <!-- getOptions -->
   * 
   * Return the optional command-line arguments, as a map of the short (if
   * given) and long option indicators to the argument objects
   * 
   * @param args A collection of command-line arguments
   * @return A map containing the optional arguments
   */
  public static Map<String, CommandLineArgument> getOptions(Iterable<CommandLineArgument> args) {
    LinkedList<CommandLineArgument> argList = sort(args);

    Map<String, CommandLineArgument> options = new HashMap<String, CommandLineArgument>();

    for(CommandLineArgument arg: argList) {
      if(!arg.required) {
        if(arg.shortoption != null) options.put(arg.shortoption, arg);
        options.put(arg.longoption, arg);
      }
    }

    return options;
  }

  /**
   * <!-- getOptions -->
   * 
   * Convenience method allowing arguments to be passed as an array
   * 
   * @param args Array of command-line arguments
   * @return Map of the optional arguments
   */
  public static Map<String, CommandLineArgument> getOptions(CommandLineArgument[] args) {
    return getOptions(Arrays.asList(args));
  }

  /**
   * <!-- getArguments -->
   * 
   * Return the required arguments, as an array in the order expected.
   * 
   * @param args Collection of command-line arguments
   * @return Array of the required arguments, in the order specified.
   */
  public static CommandLineArgument[] getArguments(Iterable<CommandLineArgument> args) {
    LinkedList<CommandLineArgument> argList = sort(args);

    while(argList.size() > 0 && !argList.getFirst().required)
      argList.removeFirst();

    return argList.toArray(new CommandLineArgument[0]);
  }

  /**
   * <!-- getArguments -->
   * 
   * Convenience method allowing arguments to be passed as an array
   * 
   * @param args Array of command-line arguments
   * @return Array of the required arguments
   */
  public static CommandLineArgument[] getArguments(CommandLineArgument[] args) {
    return getArguments(Arrays.asList(args));
  }

  /**
   * <!-- parseArgs -->
   * 
   * Parse a set of command line arguments as passed to <code>main()</code>
   * using a collection of command-line argument objects to validate them. Each
   * argument, optional or required may appear only once. The results are
   * returned as a map from the argument's property name (derived from optional
   * arguments by translating the <code>--long-argument-name</code> to
   * <code>long.argument.name</code>, and from required arguments by translating
   * the <code>synopsis description text</code> to
   * <code>synopsis.description.text</code>) to its value (which in the case of
   * required arguments, is the value given on the command-line, for options,
   * the argument given to the option, and for flags, the word "true"; optional
   * arguments not appearing on the command line are not present in the map).
   * 
   * @param command A name to use for the 'command' in the synopsis--the class
   *          name is most appropriate
   * @param valid A collection of valid command-line argument objects
   * @param args The array of string command-line arguments supplied to
   *          <code>main()</code>
   * @return A map of argument property names to values
   * @throws UsageException
   */
  public static Map<String, String> parseArgs(String command, Iterable<CommandLineArgument> valid, String[] args)
      throws UsageException {
    Map<String, String> arguments = new HashMap<String, String>();
    Map<String, CommandLineArgument> options = getOptions(valid);
    CommandLineArgument[] required = getArguments(valid);

    int j = 0;
    boolean in_options = true;
    for(int i = 0; i < args.length; i++) {
      if(args[i].equals("--") && in_options) in_options = false;
      else if(in_options && args[i].startsWith("-")) {
        if(options.containsKey(args[i])) {
          CommandLineArgument arg = options.get(args[i]);
          if(arguments.containsKey(arg.getPropertyName())) {
            if(arg.hasArgument()) {
              if(i + 1 < args.length) {
                throw new UsageException(args[i], "Option specified more than once (this: " + args[i + 1] + ", prev: "
                    + arguments.get(arg.getPropertyName()) + ")", usage(command, valid));
              }
              else {
                throw new UsageException(args[i], "Option specified more than once", usage(command, valid));
              }
            }
            else {
              throw new UsageException(args[i], "Flag specified more than once", usage(command, valid));
            }
          }
          String optarg = "true";
          if(arg.isOption()) {
            if(i + 1 < args.length) optarg = args[++i];
            else
              throw new UsageException(args[i], "This option expects an argument (" + arg.optionArgumentName + ")",
                  usage(command, valid));
          }
          arguments.put(arg.getPropertyName(), optarg);
        }
        else {
          throw new UsageException(args[i], "Option or flag not recognised. Use -- to indicate end of options "
            + "if this a required argument that happens to begin with a dash", usage(command, valid));
        }
      }
      else {
        in_options = false;
        if(j >= required.length) throw new UsageException(args[i], "Unexpected argument", usage(command, valid));
        arguments.put(required[j].propertyName, args[i]);
        j++;
      }
    }
    if(j < required.length)
      throw new UsageException(args[args.length - 1], "Further arguments expected", usage(command, valid));

    return arguments;
  }

  /**
   * <!-- parseArgs -->
   * 
   * Convenience method allowing the valid command-line arguments to be passed
   * as an array.
   * 
   * @param command The command (class name containing <code>main()</code>
   * @param valid Array of valid command-line arguments
   * @param args Array of string command-line arguments given
   * @return Map of argument property names to values
   * @throws UsageException
   */
  public static Map<String, String> parseArgs(String command, CommandLineArgument[] valid, String[] args)
      throws UsageException {
    return parseArgs(command, Arrays.asList(valid), args);
  }

  /**
   * <!-- reconstructArgs -->
   * 
   * Reconstruct a list of command-line arguments from a map of argument
   * property names to values (as returned from <code>parseArgs()</code>) and a
   * collection of command-line argument objects. The returned list of string
   * arguments will be based on an intersection of the <code>opts</code> map and
   * the <code>args</code> set.
   * 
   * @param opts The map of argument property names to values
   * @param args Command-line argument objects from which to reconstruct
   *          arguments.
   * @return An array of the intersection of <code>opts</code> and
   *         <code>args</code> arguments, reconstructed in command-line argument
   *         form.
   */
  public static String[] reconstructArgs(Map<String, String> opts, Iterable<CommandLineArgument> args) {
    LinkedList<CommandLineArgument> argList = sort(args);
    LinkedList<String> reconstructedArgList = new LinkedList<String>();
    for(CommandLineArgument arg: argList) {
      if(opts.containsKey(arg.getPropertyName())) {
        if(arg.required) {
          reconstructedArgList.addLast(opts.get(arg.getPropertyName()));
        }
        else {
          reconstructedArgList.addLast(arg.shortoption == null ? arg.longoption : arg.shortoption);
          if(arg.hasArgument()) {
            reconstructedArgList.addLast(opts.get(arg.getPropertyName()));
          }
        }
      }
    }
    return reconstructedArgList.toArray(new String[0]);
  }

  /**
   * <!-- reconstructArgs -->
   * 
   * Convenience method allowing reconstruction from an array of command-line
   * arguments.
   * 
   * @param opts Map of supplied arguments
   * @param args Array of arguments from which to reconstruct the arguments list
   * @return Array of reconstructed arguments
   */
  public static String[] reconstructArgs(Map<String, String> opts, CommandLineArgument[] args) {
    return reconstructArgs(opts, Arrays.asList(args));
  }

  /**
   * <!-- usage -->
   * 
   * Create a usage synopsis for a command
   * 
   * @param command The name of the command (e.g. class name containing
   *          <code>main()</code>
   * @param args Collection of valid arguments
   * @return Usage synopsis string
   */
  public static String usage(String command, Iterable<CommandLineArgument> args) {
    StringBuffer buff = new StringBuffer("Usage: " + command);
    LinkedList<CommandLineArgument> argList = sort(args);

    for(CommandLineArgument arg: argList) {
      buff.append(" " + arg.getSynopsis());
    }

    return buff.toString();
  }

  /**
   * <!-- usage -->
   * 
   * Convenience method
   * 
   * @param command Name of the command
   * @param args Array of valid arguments
   * @return Synopsis
   */
  public static String usage(String command, CommandLineArgument[] args) {
    return usage(command, Arrays.asList(args));
  }

  /**
   * <!-- help -->
   * 
   * Construct a help message describing the usage of the command in full.
   * 
   * @param command Name of the command
   * @param args Collection of valid arguments
   * @return Help text
   */
  public static String help(String command, Iterable<CommandLineArgument> args) {
    StringBuffer buff = new StringBuffer(usage(command, args));
    LinkedList<CommandLineArgument> argList = sort(args);

    buff.append("\nRequired arguments:\n");

    for(CommandLineArgument arg: argList) {
      if(arg.required) buff.append(" " + arg.getHelpSynopsis());
    }

    buff.append("\nOptions:\n");

    for(CommandLineArgument arg: argList) {
      if(!arg.required) buff.append(" " + arg.getHelpSynopsis());
    }

    return buff.toString();
  }

  /**
   * <!-- help -->
   * 
   * Convenience method
   * 
   * @param command Name of the command
   * @param args Array of valid arguments
   * @return Help text
   */
  public static String help(String command, CommandLineArgument[] args) {
    return help(command, Arrays.asList(args));
  }

  /**
   * getLongOption
   * 
   * @return The long option name
   */
  public String getLongOption() {
    return longoption;
  }

  /**
   * getPropertyName
   * 
   * @return The 'property' name, derived from the long option name by removing
   *         the initial dashes and replacing all others with dots, and in the
   *         case of required arguments, from the argument name by replacing
   *         spaces with dots.
   */
  public String getPropertyName() {
    return propertyName;
  }

  /**
   * getShortOption
   * 
   * @return The short option name
   */
  public String getShortOption() {
    return shortoption;
  }

  /**
   * hasArgument
   * 
   * @return <code>false</code> if this command-line argument is a flag, and
   *         <code>true</code> if it is an option or a required argument
   */
  public boolean hasArgument() {
    return optionArgumentName != null || required;
  }

  /**
   * <!-- getArgumentName -->
   * 
   * @return The name of the (required) argument (<code>null</code> for optional
   *         arguments).
   */
  public String getArgumentName() {
    return argumentName;
  }

  /**
   * getOptionArgumentName
   * 
   * @return A name to use for the command-line option argument, or null if
   *         there isn't one (i.e. if it is a required argument or a flag)
   */
  public String getOptionArgumentName() {
    return optionArgumentName;
  }

  /**
   * <!-- getOrder -->
   * 
   * @return The order of the (required) argument (-1 for optional arguments)
   */
  public int getOrder() {
    return order;
  }

  /**
   * <!-- isFlag -->
   * 
   * @return <code>true</code> if the argument is a flag
   */
  public boolean isFlag() {
    return !required && optionArgumentName == null;
  }

  /**
   * <!-- isOption -->
   * 
   * @return <code>true</code> if the argument is an option
   */
  public boolean isOption() {
    return !required && optionArgumentName != null;
  }

  /**
   * <!-- isRequired -->
   * 
   * @return <code>true</code> if the argument is required
   */
  public boolean isRequired() {
    return required;
  }

  /**
   * getHelpText
   * 
   * @return The description of the command-line option
   */
  public String getHelpText() {
    return helpText;
  }

  /**
   * <!-- getHelpSynopsis -->
   * 
   * @return A synopsis of the command-line argument including detailed
   *         help-text
   */
  public String getHelpSynopsis() {
    if(required) return argumentName + ":\n\t" + helpText + "\n";

    String optionDesc = shortoption == null ? "   " + longoption : shortoption + " " + longoption;
    if(optionArgumentName == null) return optionDesc + ":\n\t" + helpText + "\n";
    else
      return optionDesc + "<" + optionArgumentName + ">:\n\t" + helpText + "\n";
  }

  /**
   * <!-- getSynopsis -->
   * 
   * @return A brief synopsis of the command-line argument
   */
  public String getSynopsis() {
    if(required) return "<" + argumentName + ">";

    String optionName = shortoption == null ? longoption : shortoption;
    if(optionArgumentName == null) return "[" + optionName + "]";
    else
      return "[" + optionName + " <" + optionArgumentName + ">]";
  }

  /**
   * <!-- compareTo -->
   * 
   * Used for sorting CommandLineArguments. Optional arguments sort before
   * required arguments in ascending order of short option name (if supplied) or
   * long option name (if not) followed by required arguments in the order
   * specified.
   * 
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  public int compareTo(CommandLineArgument arg) {
    if(arg.required && required) {
      if(order == arg.order) throw new Bug();
      return ((Integer)order).compareTo(arg.order);
    }
    else if(!arg.required && !required) {
      return shortoption.compareTo(arg.shortoption);
    }
    else if(arg.required && !required) {
      // Optional arguments before required
      return -1;
    }
    else if(!arg.required && required) {
      // Optional arguments before required
      return 1;
    }
    throw new Panic();
  }

}
