/*
 * uk.ac.hutton.obiama.model: RepastModel.java
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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyStorageException;
import org.semanticweb.owl.model.UnknownOWLOntologyException;

import uchicago.src.reflector.DescriptorContainer;
import uchicago.src.sim.engine.ActionGroup;
import uchicago.src.sim.engine.BasicAction;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimEvent;
import uchicago.src.sim.engine.SimEventListener;
import uchicago.src.sim.engine.SimEventProducer;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.engine.SimModel;
import uchicago.src.sim.engine.SimModelImpl;
import uk.ac.hutton.obiama.action.Action;
import uk.ac.hutton.obiama.action.ActionFactory;
import uk.ac.hutton.obiama.action.ActionParameter;
import uk.ac.hutton.obiama.action.GetSpacesQuery;
import uk.ac.hutton.obiama.action.ModelSetUpAction;
import uk.ac.hutton.obiama.action.Process;
import uk.ac.hutton.obiama.exception.Bug;
import uk.ac.hutton.obiama.exception.ErrorHandler;
import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.exception.ModelStructureOntologyException;
import uk.ac.hutton.obiama.exception.NoSuchProcessImplementationException;
import uk.ac.hutton.obiama.exception.NoSuchConceptException;
import uk.ac.hutton.obiama.exception.OntologyConfigurationException;
import uk.ac.hutton.obiama.exception.Panic;
import uk.ac.hutton.obiama.exception.ScheduleException;
import uk.ac.hutton.obiama.exception.StateOntologyHasTBoxAxiomsException;
import uk.ac.hutton.obiama.exception.UninitialisedParameterException;
import uk.ac.hutton.obiama.exception.UninitialisedValueException;
import uk.ac.hutton.obiama.exception.UsageException;
import uk.ac.hutton.obiama.msb.Concept;
import uk.ac.hutton.obiama.msb.Instance;
import uk.ac.hutton.obiama.msb.ModelStateBroker;
import uk.ac.hutton.obiama.msb.ModelStateBrokerFactory;
import uk.ac.hutton.obiama.msb.ObiamaSetUp;
import uk.ac.hutton.obiama.msb.OntologyIOHelper;
import uk.ac.hutton.obiama.msb.Var;
import uk.ac.hutton.obiama.random.RNGFactory;
import uk.ac.hutton.util.InterfaceCreator;

/**
 * <!-- RepastModel -->
 * 
 * Repast interface to OBIAMA. Repast expects models to implement get and set
 * methods for parameters. Obviously this is not known in advance for OBIAMA, so
 * the effect is achieved by creating a proxy instance that pretends to
 * implement the get and set methods, which are then implemented via an
 * invocation handler.
 * 
 * @author Gary Polhill
 */
public class RepastModel extends SimModelImpl implements InvocationHandler, Model {

  /**
   * Repast schedule object
   */
  private Schedule schedule;
  // May need to be protected or 'default' if display
  // actions have to be scheduled

  /**
   * The model state broker
   */
  protected ModelStateBroker msb;

  /**
   * The schedule ontology
   */
  private ScheduleOntology scheduleOntology;

  /**
   * The main schedule
   */
  private ObiamaSchedule mainSchedule;

  /**
   * The default length of time elapsed in one time step
   */
  private double clockTick;

  /**
   * The initial schedule
   */
  private ObiamaSchedule initialSchedule;

  /**
   * List of actions
   */
  private LinkedList<AbstractScheduledAction> mainActionList;

  /**
   * Parameters taken by actions
   */
  private Map<String, ActionParameter> actionParameters;

  /**
   * Constructor
   * 
   * @param scheduleABoxURI The ontology containing the schedule to use (which
   *          schedule instances to use will be obtained from command-line
   *          arguments)
   */
  public RepastModel(URI scheduleABoxURI) {

    actionParameters = new HashMap<String, ActionParameter>();

    // Create the model state broker

    try {
      msb = ModelStateBrokerFactory.getModelStateBroker(this);
    }
    catch(ModelStructureOntologyException e) {
      ErrorHandler.redo(e, "building the Model State Broker");
    }

    // Create the schedule ontology

    try {
      scheduleOntology = new ScheduleOntologyInstance(scheduleABoxURI);
    }
    catch(OWLOntologyCreationException e) {
      System.err.println(e);
      e.printStackTrace();
      throw new Bug();
    }
    catch(OWLOntologyChangeException e) {
      throw new Bug();
    }
    catch(UnknownOWLOntologyException e) {
      throw new Bug();
    }

    // Create the initial and main OBIAMA schedules

    String argSchedule = ObiamaSetUp.getMainScheduleInstanceURI();
    String argInitialSchedule = ObiamaSetUp.getInitialScheduleInstanceURI();

    mainSchedule = null;
    initialSchedule = null;

    if(argSchedule != null) {
      try {
        mainSchedule = scheduleOntology.getSchedule(new URI(argSchedule), msb);
      }
      catch(NoSuchProcessImplementationException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      catch(IntegrationInconsistencyException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      catch(ScheduleException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      catch(OntologyConfigurationException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      catch(URISyntaxException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      if(!mainSchedule.isTimed()) {
        // TODO throw exception (?)--there's not really a reason to object...
        // The schedule could just be run through, step by step.
      }
    }

    if(argInitialSchedule != null) {
      try {
        initialSchedule = scheduleOntology.getSchedule(new URI(argInitialSchedule), msb);
      }
      catch(NoSuchProcessImplementationException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      catch(IntegrationInconsistencyException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      catch(ScheduleException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      catch(OntologyConfigurationException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      catch(URISyntaxException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      if(initialSchedule.isTimed()) {
        // TODO throw exception: initial schedule must not be timed.
      }
    }

    if(mainSchedule == null || initialSchedule == null) {
      try {
        Set<ObiamaSchedule> allSchedules = scheduleOntology.getAllSchedules(msb);
        Set<ObiamaSchedule> timedSchedules = new HashSet<ObiamaSchedule>();
        Set<ObiamaSchedule> nonTimedSchedules = new HashSet<ObiamaSchedule>();

        for(ObiamaSchedule obiSchedule: allSchedules) {
          if(obiSchedule.isTimed()) timedSchedules.add(obiSchedule);
          else
            nonTimedSchedules.add(obiSchedule);
        }

        if(mainSchedule == null) {
          if(timedSchedules.size() == 0) {
            // TODO exception: no schedules
          }
          else if(timedSchedules.size() == 1) {
            mainSchedule = timedSchedules.iterator().next();
          }
          else {
            // TODO exception: ambiguous schedule
          }
        }

        if(initialSchedule == null) {
          if(nonTimedSchedules.size() == 0 && ObiamaSetUp.getStateOntologyURI() == null) {
            // TODO ditto, unless there's an argument loading in a prior state
          }
          else if(nonTimedSchedules.size() == 1) {
            initialSchedule = nonTimedSchedules.iterator().next();
          }
          else {
            // TODO ditto
          }
        }
      }
      catch(NoSuchProcessImplementationException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      catch(IntegrationInconsistencyException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      catch(ScheduleException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      catch(OntologyConfigurationException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    clockTick = mainSchedule.getClockTick() == null ? 1.0 : mainSchedule.getClockTick();
    mainActionList = null;
  }

  /**
   * <!-- begin -->
   * 
   * @see uchicago.src.sim.engine.SimModel#begin()
   */
  public void begin() {
    // Initialise the model for the start of a run. The three build methods are
    // called here, and any displays are displayed. begin() is called whenever
    // the start button (or step button if the run has not yet started) is
    // clicked. Any objects to be created that depend on parameter values can be
    // created here.
    buildModel();
    buildDisplay();
    buildSchedule();
  }

  /**
   * <!-- getInitParam -->
   * 
   * @see uchicago.src.sim.engine.SimModel#getInitParam()
   */
  public String[] getInitParam() {
    // This method should return a string array of the initial model parameters
    // that are to be displayable and manipulable
    return actionParameters.keySet().toArray(new String[0]);
  }

  /**
   * <!-- getName -->
   * 
   * @see uchicago.src.sim.engine.SimModel#getName()
   */
  public String getName() {
    // Returns the name of the model--displayed as the title of the tool bar.
    return msb.getBaseURI().getPath();
  }

  /**
   * <!-- getSchedule -->
   * 
   * @see uchicago.src.sim.engine.SimModel#getSchedule()
   */
  public Schedule getSchedule() {
    // Returns the schedule associated with the model--typically just returns
    // the schedule variable
    return schedule;
  }

  /**
   * <!-- getTimeStep -->
   * 
   * @see uk.ac.hutton.obiama.model.Model#getTimeStep()
   * @return The time step we are currently at in the model
   */
  public double getTimeStep() {
    return schedule.getCurrentTime();
  }

  /**
   * <!-- setup -->
   * 
   * @see uchicago.src.sim.engine.SimModel#setup()
   */
  public void setup() {
    // Tear down the model in preparation for a call to begin(). setup() is
    // called when the model is loaded either through passing the model name as
    // an argument to SimInit, or through the load model dialog, and more
    // frequently, whenever the setup button is clicked. Setup should set any
    // objects that are created over the course of the run to null, and dispose
    // of any DisplaySurfaces, graphs and Schedules. While not strictly
    // necessary this should help prevent memory leaks and insure a clean
    // startup (calling System.gc() helps too).

    // The initial model parameters should be set to whatever defaults the user
    // wants to see initially, and a Schedule should be created here (i.e.
    // schedule = new Schedule(1);) and if the model is a GUI model a
    // DisplaySurface should be created here as well (i.e. displaySurface = new
    // DisplaySurface(this, "Heat Bugs Display");).

    // Objects that rely on parameter values should not be created or setup
    // here.

    schedule = null;
    mainActionList = null;

    System.gc();

    schedule = new Schedule(clockTick);
    msb.reset();
  }

  /**
   * <!-- buildModel -->
   * 
   * Build the model--either load in an initial state or run the initial
   * schedule
   */
  private void buildModel() {
    String initialStateURI = ObiamaSetUp.getStateOntologyURI();
    if(initialStateURI == null) {
      try {
        // Run the initial schedule
        // TODO might want to run an initial schedule even after loading in a
        // state...
        msb.createState();
        Log.startInitialSchedule(initialSchedule.getURI());
        initialSchedule.run();
        Log.stopInitialSchedule(initialSchedule.getURI());
      }
      catch(IntegrationInconsistencyException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      catch(ScheduleException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    else {
      // Load in the initial state
      try {
        msb.loadState(initialStateURI);
      }
      catch(URISyntaxException e) {
        ErrorHandler.redo(e, "Loading state ontology from " + initialStateURI);
      }
      catch(StateOntologyHasTBoxAxiomsException e) {
        ErrorHandler.redo(e, "Loading state ontology from " + initialStateURI);
      }
    }

    // Construct spatial objects that will act as an interface between the
    // ontology and the modelling library.

    /*
     * try {
     * 
     * GetSpacesQuery query = new GetSpacesQuery(); ModelSetUpAction action =
     * new ModelSetUpAction();
     * 
     * action.initialise(msb);
     * 
     * query.initialise(action);
     * 
     * Set<URI> spaces = query.ask(ObiamaOntology.GLOBAL_AGENT_URI,
     * ObiamaOntology.EXOGENOUS_AGENT_URI);
     * 
     * 
     * // OK, so now we know the URIs of all the spaces, they need to be
     * created. // Not a lot will happen to them here--spaces are only really
     * useful for // displaying } catch(NoSuchConceptException e) { // ignore
     * it: there are no spaces } catch(IntegrationInconsistencyException e) {
     * e.printStackTrace(); } catch(ScheduleException e) { // TODO
     * Auto-generated catch block e.printStackTrace(); }
     */
  }

  /**
   * <!-- buildDisplay -->
   * 
   * Build the display
   */
  private void buildDisplay() {
    // Do nothing--the GUI will be created by the observer
  }

  /**
   * <!-- buildSchedule -->
   * 
   * Build the schedule. This is implemented recursively according to the type
   * of action(group). This is the top level schedule building method.
   */
  private void buildSchedule() {
    if(mainSchedule.isTimed()) {
      buildSchedule(mainSchedule.getActionGroup());

      Double stopTime = mainSchedule.getStopTime();
      if(stopTime == null) {
        // TODO throw exception if we're not in GUI mode.
      }
      else {
        if(ObiamaSetUp.getGUIMode()) schedule.scheduleActionAt(stopTime, this, "pause");
        else
          schedule.scheduleActionAt(stopTime, this, "stop");
      }
    }
    else {
      try {
        mainActionList = mainSchedule.getActionList();
        schedule.scheduleActionAtInterval(clockTick, this, "step");
        schedule.scheduleActionAt((double)mainActionList.size(), this, "stop");
      }
      catch(ScheduleException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  /**
   * <!-- buildSchedule -->
   * 
   * @param action The action to schedule
   */
  private void buildSchedule(AbstractScheduledAction action) {
    if(action instanceof RecurrentActionGroup) {
      buildSchedule((RecurrentActionGroup)action);
    }
    else if(action instanceof SequentialActionGroup) {
      buildSchedule((SequentialActionGroup)action);
    }
    else {
      schedule.scheduleActionAt(action.getTime(), action, "step");
    }
  }

  /**
   * <!-- buildSchedule -->
   * 
   * Build the (sub)schedule for a recurrent action group
   * 
   * @param actionGroup A repeated action group
   */
  private void buildSchedule(RecurrentActionGroup actionGroup) {
    AbstractScheduledAction repeatedAction = actionGroup.getRepeatedAction();
    double interval = actionGroup.getInterval();
    double time = actionGroup.getTime();
    if(repeatedAction.isTimed()) {
      buildSchedule(repeatedAction, time, interval);
    }
    else {
      ActionGroup group = new ActionGroup(ActionGroup.SEQUENTIAL);
      if(repeatedAction instanceof SequentialActionGroup) {
        for(AbstractScheduledAction action: ((SequentialActionGroup)repeatedAction).getActionSequence()) {
          group.addAction(new RepastAction(action));
        }
      }
      if(time == 0.0) {
        schedule.scheduleActionAtInterval(interval, group);
      }
      else if(interval == clockTick) {
        schedule.scheduleActionBeginning(time, group);
      }
      else {
        // TODO throw exception
      }
    }
  }

  /**
   * <!-- buildSchedule -->
   * 
   * Build the (sub)schedule for a sequence
   * 
   * @param actionGroup
   */
  private void buildSchedule(SequentialActionGroup actionGroup) {
    double time = actionGroup.getTime();
    double increment = actionGroup.getIncrement();
    for(AbstractScheduledAction action: actionGroup.getActionSequence()) {
      schedule.scheduleActionAt(time, action, "step");
      time += increment;
    }
  }

  /**
   * <!-- buildSchedule -->
   * 
   * Build a timed action
   * 
   * @param action
   * @param time
   * @param interval
   */
  private void buildSchedule(AbstractScheduledAction action, double time, double interval) {
    if(action instanceof SequentialActionGroup) {
      buildSchedule((SequentialActionGroup)action, time, interval);
    }
    else {
      if(time == 0.0) {
        schedule.scheduleActionAtInterval(interval, action, "step");
      }
      else if(interval == clockTick) {
        schedule.scheduleActionBeginning(time, action, "step");
      }
      else {
        // TODO throw exception
      }
    }
  }

  /**
   * <!-- buildSchedule -->
   * 
   * Build a timed sequence
   * 
   * @param actionGroup
   * @param time
   * @param interval
   */
  private void buildSchedule(SequentialActionGroup actionGroup, double time, double interval) {
    for(AbstractScheduledAction action: actionGroup.getActionSequence()) {
      buildSchedule(action, time, interval);
    }
  }

  /**
   * <!-- step -->
   * 
   * If running a non-timed schedule, this method will step through it.
   */
  public void step() {
    try {
      mainActionList.removeFirst().step();
    }
    catch(IntegrationInconsistencyException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch(ScheduleException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * <!-- stop -->
   * 
   * Stop the model and save the state.
   * 
   * @see uchicago.src.sim.engine.SimModelImpl#stop()
   */
  @Override
  public void stop() {
    super.stop();
    msb.saveState(ObiamaSetUp.getSaveLast());
    Log.stopMainSchedule(mainSchedule.getURI());
  }

  /**
   * <!-- pause -->
   * 
   * Save the state at the paused point.
   * 
   * @see uchicago.src.sim.engine.SimModelImpl#pause()
   */
  @Override
  public void pause() {
    super.pause();
    msb.saveState(ObiamaSetUp.getSaveLast());
  }

  /**
   * <!-- createModelParameters -->
   * 
   * @return A proxy instance implementing get and set methods for the
   *         parameters
   */
  Object createModelParameters() {
    Map<String, Class<?>> parameters = new HashMap<String, Class<?>>();
    if(initialSchedule != null) addParameters(initialSchedule, parameters);
    addParameters(mainSchedule, parameters);

    for(ObiamaSchedule creatorSchedule: msb.getCreators()) {
      addParameters(creatorSchedule, parameters);
    }

    String modelName = "OBIAMAModel";
    InterfaceCreator creator = new InterfaceCreator(modelName, parameters);
    try {
      Class<?> iface = creator.findClass(modelName);
      return Proxy.newProxyInstance(creator, new Class[] { iface, SimModel.class, DescriptorContainer.class,
        SimEventProducer.class }, this);
    }
    catch(ClassNotFoundException e) {
      throw new Bug();
    }
  }

  /**
   * <!-- addParameters -->
   * 
   * Add the parameters from the schedule to the parameter map
   * 
   * @param obiamaSchedule schedule object
   * @param parameters map of parameter name to parameter datatype
   */
  private void addParameters(ObiamaSchedule obiamaSchedule, Map<String, Class<?>> parameters) {
    for(Action action: obiamaSchedule.getActionSet()) {
      Set<ActionParameter> params = action.getParameters();
      for(ActionParameter param: params) {
        String parameterName = action.getURI().getFragment() + "$" + param.getParameterName();
        parameters.put(parameterName, param.getType());
        actionParameters.put(parameterName, param);
      }
    }

  }

  /**
   * <!-- setRngSeed -->
   * 
   * Override the seed setting methods to interface with OBIAMA's RNG
   * 
   * @see uchicago.src.sim.engine.SimModelImpl#setRngSeed(long)
   * @param seed
   */
  @Override
  public void setRngSeed(long seed) {
    RNGFactory.getRNG().setSeed(seed);
  }

  /**
   * <!-- getRngSeed -->
   * 
   * Override the seed method to interface with OBIAMA's RNG
   * 
   * @see uchicago.src.sim.engine.SimModelImpl#getRngSeed()
   * @return the seed
   */
  @Override
  public long getRngSeed() {
    return RNGFactory.getRNG().getSeed();
  }

  /**
   * <!-- main -->
   * 
   * Main method. This adds one command line argument to the default list for
   * OBIAMA, containing a parameter file.
   * 
   * @param args
   */
  public static void main(String[] args) {
    try {
      ObiamaSetUp.addArgument(new CommandLineArgument("--parameters", "-p", "parameter file",
          "parameter file to load for initial /default parameter values"));
      Map<String, String> parsedArgs = ObiamaSetUp.getObiamaOptions(RepastModel.class.getName(), args);
      Map<String, String> nonObiamaArgs = ObiamaSetUp.removeObiamaOptions(parsedArgs);
      String parameterFile = null;

      SimInit init = new SimInit();
      RepastModel model = new RepastModel(new URI(ObiamaSetUp.getScheduleURI()));

      for(String arg: nonObiamaArgs.keySet()) {
        if(arg.equals("parameters")) {
          parameterFile = nonObiamaArgs.get("parameters");
        }
        else {
          throw new UsageException(arg, "not recognised", ObiamaSetUp.usage(model.getClass().getName()));
        }
      }

      Object proxy = model.createModelParameters();
      if(ObiamaSetUp.getGUIMode()) {
        init.loadModel((SimModel)proxy, parameterFile, false);
      }
      else {
        init.loadModel((SimModel)proxy, parameterFile, true);
      }
      // This line is never reached
    }
    catch(UsageException e) {
      ErrorHandler.fatal(e, "processing command line arguments");
      throw new Panic();
    }
    catch(URISyntaxException e) {
      ErrorHandler.fatal(e, "creating URI from supplied schedule URI: " + ObiamaSetUp.getScheduleURI());
      throw new Panic();
    }
  }

  /**
   * <!-- invoke -->
   * 
   * Implements the get and set methods for the parameters on the proxy object.
   * Other methods are just passed on to this object.
   * 
   * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
   *      java.lang.reflect.Method, java.lang.Object[])
   * @param proxy The object pretending to implement the get and set methods for
   *          the parameters
   * @param method Method to call
   * @param args Arguments for the method
   * @return Anything returned by the method
   * @throws Throwable
   */
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if(method.getName().startsWith("get") && !method.getName().equals("getRngSeed")) {
      String paramName = getParamName(method.getName());
      if(actionParameters.containsKey(paramName)) {
        ActionParameter parameter = actionParameters.get(paramName);
        if(!parameter.parameterSet()) {
          throw new UninitialisedParameterException(paramName, parameter);
        }
        if(method.getReturnType() == Double.class) {
          return parameter.getDoubleParameter();
        }
        else if(method.getReturnType() == Integer.class) {
          return parameter.getIntParameter();
        }
        else if(method.getReturnType() == Long.class) {
          return parameter.getLongParameter();
        }
        else if(method.getReturnType() == Boolean.class) {
          return parameter.getBooleanParameter();
        }
        else
          return parameter.getParameter();
      }
      // else fall through and call the method as default
    }
    else if(method.getName().startsWith("set") && !method.getName().equals("setRngSeed")) {
      String paramName = getParamName(method.getName());
      if(actionParameters.containsKey(paramName)) {
        ActionParameter parameter = actionParameters.get(paramName);
        parameter.set(args);
        return null;
      }
      // else fall through and call the method as default
    }
    return method.invoke(this, args);
  }

  /**
   * <!-- getParamName -->
   * 
   * @param methodName
   * @return The parameter name from the get/set method name
   */
  private String getParamName(String methodName) {
    String noGetSet = methodName.substring(3);
    return noGetSet.substring(0, 1).toLowerCase() + noGetSet.substring(1);
  }

}
