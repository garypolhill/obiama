/*
 * uk.ac.hutton.obiama.action: DecisionTree.java
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
package uk.ac.hutton.obiama.action;

import java.io.PrintWriter;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owl.vocab.XSDVocabulary;

import uk.ac.hutton.obiama.exception.Bug;
import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.exception.NeedDataGotObjectPropertyException;
import uk.ac.hutton.obiama.exception.NeedFunctionalGotNonFunctionalPropertyException;
import uk.ac.hutton.obiama.exception.NeedNonFunctionalGotFunctionalPropertyException;
import uk.ac.hutton.obiama.exception.NeedObjectGotDataPropertyException;
import uk.ac.hutton.obiama.msb.Value;
import uk.ac.hutton.obiama.msb.Var;
import uk.ac.hutton.util.HeadedTable;
import uk.ac.hutton.util.Panic;
import uk.ac.hutton.util.SetCreator;

/**
 * <!-- DecisionTree -->
 * 
 * <p>
 * Implement a decision tree. This is a binary tree at each node of which is a
 * boolean test, with branches determining further tests, or providing a value
 * to use.
 * </p>
 * 
 * <p>
 * The tree is built from a {@link HeadedTable}, which can be loaded in from a
 * CSV file. The table should have the following format (column ordering isn't
 * important), with the root node on the first line (that isn't the header
 * line). Nodes may only be referred to once. Node IDs can be any string.
 * </p>
 * 
 * <table>
 * <tr>
 * <th>Node ID</th>
 * <th>Property URI</th>
 * <th>Operator</th>
 * <th>Operand</th>
 * <th>True</th>
 * <th>False</th>
 * </tr>
 * <tr>
 * <td>1</td>
 * <td>#test1</td>
 * <td>eq</td>
 * <td>"value"</td>
 * <td>[2]</td>
 * <td>#value1</td>
 * </tr>
 * <tr>
 * <td>2</td>
 * <td>#test2</td>
 * <td>match</td>
 * <td>/^start/</td>
 * <td>[3]</td>
 * <td>[4]</td>
 * </tr>
 * <tr>
 * <td>3</td>
 * <td>#test3</td>
 * <td>gt</td>
 * <td>4</td>
 * <td>#value2</td>
 * <td>#value3</td>
 * </tr>
 * </table>
 * 
 * <p>
 * The operator used will determine whether the property is functional or
 * non-functional, data or object. The following are functional data property
 * operators:
 * </p>
 * <ul>
 * <li><b>eq</b> equality</li>
 * <li><b>ne</b> inequality</li>
 * <li><b>gt</b> more than (if defined for data type)</li>
 * <li><b>ge</b> more than or equal (if defined for data type)</li>
 * <li><b>lt</b> less than (if defined for data type)</li>
 * <li><b>le</b> less than or equal (if defined for data type)</li>
 * <li><b>match</b> matches (strings only)</li>
 * <li><b>mismatch</b> does not match (strings only)</li>
 * <li><b>within</b> the value is one of the <code>|</code>-separated operands</li>
 * <li><b>outwith</b> the value is not one of the <code>|</code>-separated
 * operands</li>
 * </ul>
 * 
 * <p>
 * These are non-functional data property operators:
 * </p>
 * 
 * <ul>
 * <li><b>in</b> the operand is contained in the set of values</li>
 * <li><b>out</b> the operand is not contained in the set of values</li>
 * </ul>
 * 
 * <p>
 * Functional object property operators:
 * </p>
 * 
 * <ul>
 * <li><b>is</b> the operand is the same as the object property value</li>
 * <li><b>isnt</b> the operand is not the same as the object property value</li>
 * <li><b>oneof</b> the value is one of the <code>|</code>-separated operands</li>
 * <li><b>noneof</b> the value is not one of the <code>|</code>-separated
 * operands</li>
 * </ul>
 * 
 * <p>
 * Non-functional object property operators:
 * </p>
 * 
 * <ul>
 * <li><b>has</b> the operand is one of the object property values</li>
 * <li><b>hasnt</b> the operand is not one of the object property values</li>
 * <li><b>#eq</b> the object property values have cardinality exactly the
 * operand</li>
 * <li><b>#ne</b> the object property values have cardinality not equal the
 * operand</li>
 * <li><b>#gt</b> the object property values have cardinality more than the
 * operand</li>
 * <li><b>#ge</b> the object property values have cardinality at least the
 * operand</li>
 * <li><b>#lt</b> the object property values have cardinality less than the
 * operand</li>
 * <li><b>#le</b> the object property values have cardinality not more than the
 * operand</li>
 * </ul>
 * 
 * <p>
 * The following operators are for a possible future implementation. The tree
 * doesn't test all the properties associated with all branches when making a
 * decision -- only the branches associated with the route taken through the
 * tree. If the property were 'self', it could test the decision maker not the
 * object property value. This would allow decision trees to work with instances
 * of multiple classes having potentially different properties.
 * </p>
 * 
 * <ul>
 * <li><b>isa</b> the object property value belongs to the operand OWL class</li>
 * <li><b>isnta</b> the object property value does not belong to the operand OWL
 * class</li>
 * <li><b>all</b> all object property values belong to the operand OWL class</li>
 * <li><b>none</b> no object property values belong to the operand OWL class</li>
 * <li><b>some</b> at least one object property value belongs to the operand OWL
 * class</li>
 * </ul>
 * 
 * <p>
 * The type of #choice will be determined by the values of the node that are not
 * [x] (referring to another node). Strings should be in double quotes. URIs in
 * angle brackets. URI fragments should begin with a #. Integers should match
 * /^[+-]?\d+$/, double precision floating points
 * /^[+-]?\d+\.\d+([Ee][+-]?\d+)?$/, longs /^[+-]?\d+L$/, single precision
 * floating points /^[+-]?\d+\.\d+([Ee][+-]?\d+)?$F/. Any other entry will be
 * treated as xsd:anySimpleType.
 * </p>
 * 
 * @author Gary Polhill
 */
public class DecisionTree {
  /**
   * The process using the decision tree. This is needed for getting vars and in
   * exceptions.
   */
  private AbstractProcess process;

  /**
   * Branches associated with particular node IDs. At present, this isn't really
   * used other than to check that the tree has a proper structure.
   */
  private Map<String, Node> tree;

  /**
   * The root node
   */
  private Node root;

  /**
   * The datatype of the value chosen by the decision tree. These must be
   * consistent.
   */
  private XSDVocabulary dataType;

  /**
   * Base URI to use for fragment entries in the value.
   */
  private URI base;

  /**
   * Column heading for node identifier
   */
  public static final String NODE_ID_HEADING = "Node ID";

  /**
   * Column heading for property
   */
  public static final String PROPERTY_URI_HEADING = "Property URI";

  /**
   * Column heading for operator
   */
  public static final String OPERATOR_HEADING = "Operator";

  /**
   * Column heading for operand
   */
  public static final String OPERAND_HEADING = "Operand";

  /**
   * Column heading for value to use if true
   */
  public static final String VALUE_IF_TRUE_HEADING = "True";

  /**
   * Column heading for value to use if false
   */
  public static final String VALUE_IF_FALSE_HEADING = "False";

  /**
   * <!-- checkDataType -->
   * 
   * Called by {@link Leaf} when building the tree to check consistency of entry
   * datatypes.
   * 
   * @param type Type expected according to formatting of the entry string
   * @throws DecisionTree.Exception
   */
  private void checkDataType(XSDVocabulary type) throws DecisionTree.Exception {
    if(dataType == null) {
      dataType = type;
    }
    else if(dataType != type) {
      throw new DecisionTree.Exception("Inconsistent type: " + type + ". Previous types have been: " + dataType);
    }
  }

  /**
   * <!-- buildTree -->
   * 
   * Entry method for building a tree from a {@link HeadedTable}. The columns of
   * the table are checked to make sure the expected columns are there.
   * 
   * @param tree
   * @param table
   * @return The root node of the tree
   * @throws IntegrationInconsistencyException
   * @throws DecisionTree.Exception
   */
  private Node buildTree(Map<String, Node> tree, HeadedTable<String> table) throws IntegrationInconsistencyException,
      DecisionTree.Exception {
    if(!table.getColumnHeadingsSet().containsAll(
        SetCreator.createSet(NODE_ID_HEADING, PROPERTY_URI_HEADING, OPERATOR_HEADING, OPERAND_HEADING,
            VALUE_IF_TRUE_HEADING, VALUE_IF_FALSE_HEADING))) {
      throw new DecisionTree.Exception("The decision tree table does not contain all the required headings\n");
    }

    Map<String, Integer> rows = new HashMap<String, Integer>();
    for(int i = 0; i < table.nrows(); i++) {
      rows.put(table.atRC(i, NODE_ID_HEADING), i);
    }
    return buildTree(tree, table, 0, rows);
  }

  /**
   * <!-- buildTree -->
   * 
   * Builder method for the tree. This recursively constructs the tree.
   * 
   * @param tree
   * @param table
   * @param i
   * @return The branch created.
   * @throws IntegrationInconsistencyException
   * @throws DecisionTree.Exception 
   */
  private Node buildTree(Map<String, Node> tree, HeadedTable<String> table, int row, Map<String, Integer> rows)
      throws IntegrationInconsistencyException, DecisionTree.Exception {
    String myID = table.atRC(row, NODE_ID_HEADING);

    String trueValue = table.atRC(row, VALUE_IF_TRUE_HEADING);
    String falseValue = table.atRC(row, VALUE_IF_FALSE_HEADING);

    Node trueNode;
    if(trueValue.startsWith("[") && trueValue.endsWith("]") && trueValue.length() > 3) {
      String nodeID = trueValue.substring(1, trueValue.length() - 1);
      if(!rows.containsKey(nodeID)) {
        throw new DecisionTree.Exception("Node " + nodeID + " referred to in node " + myID + " is not defined");
      }
      trueNode = buildTree(tree, table, rows.get(nodeID), rows);
    }
    else {
      trueNode = new Leaf(trueValue, myID);
    }
    Node falseNode;
    if(falseValue.startsWith("[") && falseValue.endsWith("]") && falseValue.length() > 3) {
      String nodeID = falseValue.substring(1, falseValue.length() - 1);
      if(!rows.containsKey(nodeID)) {
        throw new DecisionTree.Exception("Node " + nodeID + " referred to in node " + myID + " is not defined");
      }
      falseNode = buildTree(tree, table, rows.get(nodeID), rows);
    }
    else {
      falseNode = new Leaf(falseValue, myID);
    }
    
    if(tree.containsKey(myID)) {
      throw new DecisionTree.Exception("Node " + myID + " is defined twice");
    }

    Operator op = Operator.parseOperator(table.atRC(row, OPERATOR_HEADING));

    Var var = process.getVar(URI.create(table.atRC(row, PROPERTY_URI_HEADING)));

    Decision decision = new Decision(var, op, table.atRC(row, OPERAND_HEADING));

    Branch node = new Branch(decision, trueNode, falseNode, myID);

    tree.put(node.getID(), node);

    return node;
  }

  /**
   * Build a decision tree from a {@link HeadedTable}.
   * 
   * @param process
   * @param table
   * @param base
   * @throws IntegrationInconsistencyException
   * @throws DecisionTree.Exception 
   */
  public DecisionTree(AbstractProcess process, HeadedTable<String> table, URI base)
      throws IntegrationInconsistencyException, DecisionTree.Exception {
    this.process = process;
    dataType = null;
    this.base = base;
    tree = new HashMap<String, Node>();
    root = buildTree(tree, table);
  }

  /**
   * <!-- decide -->
   * 
   * @param agent
   * @return The leaf node of the tree according to properties of the agent
   * @throws IntegrationInconsistencyException
   */
  public String decide(URI agent) throws IntegrationInconsistencyException {
    return root.getValue(agent);
  }

  public void writeDOT(PrintWriter fp) {
    fp.println("digraph G {");
    fp.println("  node [shape = \"diamond\", fontname = \"Helvetica\"];");
    fp.println("  edge [fontname = \"Helvetica\"];");

    root.writeDOT(fp);

    fp.println("}");
  }

  // ///////////////////////////////////////////////////////////////////////////

  /**
   * <!-- Operator -->
   * 
   * enum for the operators that can be used in the decision tree
   * 
   * @author Gary Polhill
   */
  public enum Operator {
    EQ, NE, GT, GE, LT, LE, MATCH, MISMATCH, WITHIN, OUTWITH, IN, OUT, IS, ISNT, ONEOF, NONEOF, HAS, HASNT, _EQ, _NE,
    _GT, _GE, _LT, _LE;
    // ISA, ISNTA, ALL, NONE, SOME,

    /**
     * Map of operator names to Operator
     */
    private static Map<String, Operator> ops = null;

    /**
     * <!-- decide -->
     * 
     * Test the operator against a value
     * 
     * @param value Value from the model state ontology
     * @param test Value to test the operator against
     * @param process Process running (for exceptions)
     * @return <code>true</code> if the test is true.
     * @throws IntegrationInconsistencyException
     */
    public boolean decide(Value<?> value, String test, Process process) throws IntegrationInconsistencyException {
      IntegrationInconsistencyException e = getVarException(process, value.getVar());
      if(e != null) throw e;
      switch(this) {
      case EQ:
        return value.compareToString(test) == 0;
      case NE:
        return value.compareToString(test) != 0;
      case GT:
        return value.compareToString(test) > 0;
      case GE:
        return value.compareToString(test) >= 0;
      case LT:
        return value.compareToString(test) < 0;
      case LE:
        return value.compareToString(test) <= 0;
      case MATCH:
        return value.toString().matches(test);
      case MISMATCH:
        return !value.toString().matches(test);
      case WITHIN:
        return within(value, test);
      case OUTWITH:
        return !within(value, test);
      case IN:
        return value.hasString(test);
      case OUT:
        return !value.hasString(test);
      case IS:
        return value.compareToString(test) == 0;
      case ISNT:
        return value.compareToString(test) != 0;
      case ONEOF:
        return within(value, test);
      case NONEOF:
        return !within(value, test);
      case HAS:
        return value.hasString(test);
      case HASNT:
        return !value.hasString(test);
      case _EQ:
        return value.nElements() == Integer.parseInt(test);
      case _NE:
        return value.nElements() != Integer.parseInt(test);
      case _GT:
        return value.nElements() > Integer.parseInt(test);
      case _GE:
        return value.nElements() >= Integer.parseInt(test);
      case _LT:
        return value.nElements() < Integer.parseInt(test);
      case _LE:
        return value.nElements() <= Integer.parseInt(test);
      default:
        throw new Panic();
      }
    }

    /**
     * <!-- within -->
     * 
     * Helper method for {@link #decide(Value,String,Process)}
     * 
     * @param value
     * @param test
     * @return
     * @throws IntegrationInconsistencyException
     */
    private boolean within(Value<?> value, String test) throws IntegrationInconsistencyException {
      Set<String> values = SetCreator.createSet(test.split("|"));
      String valueStr = value.getString();
      return values.contains(valueStr);
    }

    /**
     * <!-- toString -->
     * 
     * @see java.lang.Enum#toString()
     * @return String forms for each operator
     */
    public String toString() {
      switch(this) {
      case EQ:
        return "eq";
      case NE:
        return "ne";
      case GT:
        return "gt";
      case GE:
        return "ge";
      case LT:
        return "lt";
      case LE:
        return "le";
      case MATCH:
        return "match";
      case MISMATCH:
        return "mismatch";
      case WITHIN:
        return "within";
      case OUTWITH:
        return "outwith";
      case IN:
        return "in";
      case OUT:
        return "out";
      case IS:
        return "is";
      case ISNT:
        return "isnt";
      case ONEOF:
        return "oneof";
      case NONEOF:
        return "noneof";
      case HAS:
        return "has";
      case HASNT:
        return "hasnt";
      case _EQ:
        return "#eq";
      case _NE:
        return "#ne";
      case _GT:
        return "#gt";
      case _GE:
        return "#ge";
      case _LT:
        return "#lt";
      case _LE:
        return "#le";
      default:
        throw new Panic();
      }
    }

    /**
     * <!-- parseOperator -->
     * 
     * @param arg
     * @return The operator associated with the <code>arg</code>, or
     *         <code>null</code> if it isn't a recognised operator string.
     */
    public static Operator parseOperator(String arg) {
      if(arg == null) throw new Bug();

      if(ops == null) {
        for(Operator o: Operator.values()) {
          ops.put(o.toString(), o);
        }
      }

      if(ops.containsKey(arg)) {
        return ops.get(arg);
      }
      else {
        return null;
      }
    }

    /**
     * <!-- checkVar -->
     * 
     * Confirm that the <code>var</code> can be used with this operator.
     * 
     * @param process
     * @param var
     * @return <code>true</code> if this operator can be used with a variable of
     *         this type (i.e. data/object property, functional/non-functional),
     *         <code>false</code> otherwise.
     */
    public boolean checkVar(Process process, Var var) {
      return getVarException(process, var) == null;
    }

    /**
     * <!-- getVarException -->
     * 
     * @param process
     * @param var
     * @return The exception to throw if this variable has the wrong type for
     *         this operator.
     */
    private IntegrationInconsistencyException getVarException(Process process, Var var) {
      switch(this) {
      case EQ:
      case NE:
      case GT:
      case GE:
      case LT:
      case LE:
      case MATCH:
      case MISMATCH:
      case WITHIN:
      case OUTWITH:
        if(var.isNonFunctional()) return new NeedFunctionalGotNonFunctionalPropertyException(process, var.getURI());
        else if(var.isObjectVar()) return new NeedDataGotObjectPropertyException(process, var.getURI());
        return null;
      case IN:
      case OUT:
        if(var.isFunctional()) return new NeedNonFunctionalGotFunctionalPropertyException(process, var.getURI());
        else if(var.isObjectVar()) return new NeedDataGotObjectPropertyException(process, var.getURI());
        return null;
      case IS:
      case ISNT:
      case ONEOF:
      case NONEOF:
        if(var.isNonFunctional()) return new NeedFunctionalGotNonFunctionalPropertyException(process, var.getURI());
        else if(var.isDataVar()) return new NeedObjectGotDataPropertyException(process, var.getURI());
        return null;
      case HAS:
      case HASNT:
      case _EQ:
      case _NE:
      case _GT:
      case _GE:
      case _LT:
      case _LE:
        if(var.isFunctional()) return new NeedNonFunctionalGotFunctionalPropertyException(process, var.getURI());
        else if(var.isDataVar()) return new NeedObjectGotDataPropertyException(process, var.getURI());
        return null;
      default:
        throw new Panic();
      }
    }
  }

  /**
   * <!-- Node -->
   * 
   * Abstract class for a node in the tree.
   * 
   * @author Gary Polhill
   */
  private abstract class Node {
    /**
     * Node identifier
     */
    String id;

    /**
     * Constructor.
     * 
     * @param id Node identifier
     */
    protected Node(String id) {
      this.id = id;
    }

    /**
     * <!-- getID -->
     * 
     * @return the identifier
     */
    public String getID() {
      return id;
    }

    /**
     * <!-- decide -->
     * 
     * @param chooserAgent
     * @return The {@link Leaf} node obtained from following the tree down using
     *         properties of the <code>chooserAgent</code>.
     * @throws IntegrationInconsistencyException
     */
    protected abstract Node decide(URI chooserAgent) throws IntegrationInconsistencyException;

    /**
     * <!-- getValue -->
     * 
     * @param chooserAgent
     * @return The value stored at the {@link Leaf} node obtained from following
     *         the tree down using properties of the <code>chooserAgent</code>
     * @throws IntegrationInconsistencyException
     */
    public abstract String getValue(URI chooserAgent) throws IntegrationInconsistencyException;

    public abstract void writeDOT(PrintWriter fp);
  }

  /**
   * <!-- Branch -->
   * 
   * A {@link Node} that contains a decision.
   * 
   * @author Gary Polhill
   */
  private class Branch extends Node {
    /**
     * The decision to take
     */
    private final Decision decision;

    /**
     * The {@link Node} to follow if the decision is <code>true</code>
     */
    private final Node trueValue;

    /**
     * The {@link Node} to follow if the decision is <code>false</code>
     */
    private final Node falseValue;

    /**
     * Constructor
     * 
     * @param decision {@link Decision} to determine which {@link} Node to
     *          follow
     * @param trueValue {@link Node} if <code>decision</code> is
     *          <code>true</code>
     * @param falseValue {@link Node} if <code>decision</code> is
     *          <code>false</code>
     * @param id Node identifier
     */
    public Branch(Decision decision, Node trueValue, Node falseValue, String id) {
      super(id);
      this.decision = decision;
      this.trueValue = trueValue;
      this.falseValue = falseValue;
    }

    /**
     * <!-- decide -->
     * 
     * Recursively use this node (and subnodes) to determine the {@link Leaf}
     * node appropriate to properties of the <code>chooserAgent</code>
     * 
     * @see uk.ac.hutton.obiama.action.DecisionTree.Node#decide(java.net.URI)
     * @param chooserAgent
     * @return
     * @throws IntegrationInconsistencyException
     */
    protected Node decide(URI chooserAgent) throws IntegrationInconsistencyException {
      if(decision.decide(chooserAgent)) {
        return trueValue.decide(chooserAgent);
      }
      else {
        return falseValue.decide(chooserAgent);
      }
    }

    /**
     * <!-- getValue -->
     * 
     * @see uk.ac.hutton.obiama.action.DecisionTree.Node#getValue(java.net.URI)
     * @param chooserAgent
     * @return Value associated with the {@link Leaf} node accessed using the
     *         decision tree
     * @throws IntegrationInconsistencyException
     */
    public String getValue(URI chooserAgent) throws IntegrationInconsistencyException {
      return decide(chooserAgent).getValue(chooserAgent);
    }

    public void writeDOT(PrintWriter fp) {
      fp.println("  " + getID() + " -> " + trueValue.getID() + " [label = \"[" + decision.toString() + "]\"];");
      fp.println("  " + getID() + " -> " + falseValue.getID() + ";");
      fp.println("  " + getID() + " [label = \" \"];");

      trueValue.writeDOT(fp);
      falseValue.writeDOT(fp);
    }
  }

  /**
   * <!-- Leaf -->
   * 
   * A leaf node is a terminator for the tree.
   * 
   * @author Gary Polhill
   */
  private class Leaf extends Node {
    /**
     * Value to store at the leaf
     */
    private final String value;

    /**
     * Leaf constructor. This sets the datatype of the decision tree, which must
     * be consistent across all leaf nodes. The datatype is determined by the
     * format of the string.
     * 
     * @param value
     * @param id
     * @throws DecisionTree.Exception 
     */
    public Leaf(String value, String id) throws DecisionTree.Exception {
      super("Leaf_" + id);
      if(value.startsWith("\"") && value.endsWith("\"")) {
        checkDataType(XSDVocabulary.STRING);
        this.value = value.substring(1, value.length() - 1);
      }
      else if(value.startsWith("<") && value.endsWith(">")) {
        checkDataType(XSDVocabulary.ANY_URI);
        this.value = value.substring(1, value.length() - 1);
      }
      else if(value.startsWith("#")) {
        checkDataType(XSDVocabulary.ANY_URI);
        this.value = URI.create(base.toString() + value).toString();
      }
      else if(value.matches("^[+-]?\\d+$")) {
        checkDataType(XSDVocabulary.INT);
        this.value = value;
      }
      else if(value.matches("^[+-]?\\d+L$")) {
        checkDataType(XSDVocabulary.LONG);
        this.value = value;
      }
      else if(value.matches("^[+-]?\\d+\\.\\d+([Ee][+-]?\\d+)")) {
        checkDataType(XSDVocabulary.DOUBLE);
        this.value = value;
      }
      else if(value.matches("^[+-]?\\d+\\.\\d+([Ee][+-]?\\d+)F")) {
        checkDataType(XSDVocabulary.FLOAT);
        this.value = value;
      }
      else {
        checkDataType(XSDVocabulary.ANY_SIMPLE_TYPE);
        this.value = value;
      }
    }

    /**
     * <!-- decide -->
     * 
     * Terminate recursion at this leaf node.
     * 
     * @see uk.ac.hutton.obiama.action.DecisionTree.Node#decide(java.net.URI)
     * @param chooserAgent
     * @return
     */
    protected Node decide(URI chooserAgent) {
      return this;
    }

    /**
     * <!-- getValue -->
     * 
     * @see uk.ac.hutton.obiama.action.DecisionTree.Node#getValue(java.net.URI)
     * @param chooserAgent
     * @return The leaf node value.
     */
    public String getValue(URI chooserAgent) {
      return value;
    }

    public void writeDOT(PrintWriter fp) {
      fp.println("  " + getID() + " [shape = \"box\", label = \"" + value + "\"];");
    }

  }

  /**
   * <!-- Decision -->
   * 
   * Class storing a decision
   * 
   * @author Gary Polhill
   */
  private class Decision {
    /**
     * Property the decision is storing
     */
    private final Var property;

    /**
     * Operator the decision is testing
     */
    private final Operator op;

    /**
     * Value the decision is testing against
     */
    private final String operand;

    /**
     * Constructor
     * 
     * @param property
     * @param op
     * @param operand
     */
    public Decision(Var property, Operator op, String operand) {
      this.property = property;
      this.op = op;
      this.operand = operand;
    }

    /**
     * <!-- decide -->
     * 
     * @param chooserAgent
     * @return the result of the decision for the <code>chooserAgent</code>
     * @throws IntegrationInconsistencyException if the agent doesn't have a
     *           value for the operator, or the property is inappropriate for
     *           the operator.
     */
    public boolean decide(URI chooserAgent) throws IntegrationInconsistencyException {
      Value<?> value = property.getExistingValueFor(chooserAgent);
      return op.decide(value, operand, process);
    }

    /**
     * <!-- toString -->
     * 
     * @see java.lang.Object#toString()
     * @return A string representation of the decision
     */
    public String toString() {
      return "#" + property.getURI().getFragment() + " " + op + " " + operand;
    }
  }

  public class Exception extends java.lang.Exception {
    public Exception(String message) {
      super(message);
    }
  }
}
