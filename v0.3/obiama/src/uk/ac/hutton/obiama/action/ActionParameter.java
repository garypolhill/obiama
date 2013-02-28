/*
 * uk.ac.hutton.obiama.action: ActionParameter.java 
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

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import uk.ac.hutton.obiama.exception.Bug;
import uk.ac.hutton.obiama.msb.XSDHelper;
import uk.ac.hutton.util.Reflection;

/**
 * ActionParameter
 * 
 * 
 * 
 * @author Gary Polhill
 */
public class ActionParameter {
  String parameter;
  String parameterName;
  String comment;
  Class<?> type;
  private boolean parameterSet;
  private boolean defaultParameterSet;

  ActionParameter(String parameterName, String comment) {
    this.parameterName = parameterName;
    this.comment = comment;
    parameter = null;
    parameterSet = false;
    defaultParameterSet = false;
    type = null;
  }

  public ActionParameter(String parameterName, Class<?> type, String comment) {
    this(parameterName, comment);
    if(type.isPrimitive()) {
      throw new Bug("Parameter \"" + parameterName + "\" must not be defined with primitive type \"" + type + "\"");
    }
    this.type = type;
  }

  public ActionParameter(String parameterName, Class<?> type, String parameter, String comment) {
    this(parameterName, type, comment);
    this.parameter = parameter;
    defaultParameterSet = true;
  }

  
  ActionParameter(String parameterName, String parameter, String comment) {
    this(parameterName, comment);
    this.parameter = parameter;
    parameterSet = true;
  }

  public boolean parameterSet() {
    return parameterSet || defaultParameterSet;
  }

  public boolean parameterSetByUser() {
    return parameterSet;
  }

  public boolean parameterSetByDefault() {
    return defaultParameterSet;
  }

  public String getComment() {
    return comment;
  }

  public String getParameterName() {
    return parameterName;
  }

  public String getParameter() {
    if(!(parameterSet || defaultParameterSet)) {
      // TODO throw exception
    }
    return parameter;
  }

  public Class<?> getType() {
    return type;
  }

  public void setParameter(String value) {
    if(type != null) {
      if(type == Double.class) {
        try {
          setDoubleParameter(Double.parseDouble(value));
          return;
        }
        catch(NumberFormatException e) {
          // TODO
        }
      }
      else if(type == Integer.class) {
        try {
          setIntParameter(Integer.parseInt(value));
          return;
        }
        catch(NumberFormatException e) {
          // TODO
        }
      }
      else if(type == Long.class) {
        try {
          setLongParameter(Long.parseLong(value));
          return;
        }
        catch(NumberFormatException e) {
          // TODO
        }
      }
      else if(type == Boolean.class) {
        setBooleanParameter(Boolean.parseBoolean(value));
        return;
      }
      else if(type == URI.class) {
        try {
          setURIParameter(new URI(value));
          return;
        }
        catch(URISyntaxException e) {
          // TODO Auto-generated catch block
        }
      }
    }
    parameter = value;
    parameterSet = true;
  }

  public double getDoubleParameter() {
    return Double.parseDouble(parameter);
  }

  public void setDoubleParameter(double value) {
    parameter = Double.toString(value);
    parameterSet = true;
  }

  public int getIntParameter() {
    return Integer.parseInt(parameter);
  }

  public void setIntParameter(int value) {
    parameter = Integer.toString(value);
    parameterSet = true;
  }

  public long getLongParameter() {
    return Long.parseLong(parameter);
  }

  public void setLongParameter(long value) {
    parameter = Long.toString(value);
    parameterSet = true;
  }

  public boolean getBooleanParameter() {
    return Boolean.parseBoolean(parameter);
  }

  public void setBooleanParameter(boolean value) {
    parameter = Boolean.toString(value);
    parameterSet = true;
  }

  public URI getURIParameter() {
    return URI.create(parameter);
  }

  public void setURIParameter(URI value) {
    parameter = value.toString();
    parameterSet = true;
  }

  public void setURIParameter(String value) {
    parameter = value;
    parameterSet = true;
  }

  public Properties getPropertiesParameter() {
    StringReader reader = new StringReader(parameter);
    Properties properties = new Properties();
    try {
      properties.load(reader);
    }
    catch(IOException e) {
      throw new Bug();
    }
    return properties;
  }

  /**
   * <!-- set -->
   * 
   * Called from RepastModel enabling the object to act as a parameter to set in
   * the Repast GUI.
   * 
   * @param args A one-element array containing the value to set this parameter
   *          to
   */
  public void set(Object[] args) {
    if(args.length != 1) throw new IllegalArgumentException();
    if(!(type == URI.class && args[0].getClass() == String.class) && !Reflection.subType(args[0].getClass(), type))
      throw new IllegalArgumentException();
    if(type == Double.class) {
      setDoubleParameter((Double)args[0]);
    }
    else if(type == Integer.class) {
      setIntParameter((Integer)args[0]);
    }
    else if(type == Long.class) {
      setLongParameter((Long)args[0]);
    }
    else if(type == Boolean.class) {
      setBooleanParameter((Boolean)args[0]);
    }
    else if(type == URI.class) {
      if(args[0] instanceof String) setURIParameter((String)args[0]);
      else
        setURIParameter((URI)args[0]);
    }
    else {
      setParameter(args[0].toString());
    }
  }

  /**
   * <!-- set -->
   * 
   * Set this parameter value from another one, which must have the same name
   * and a compatible type. This parameter value <i>must</i> have a type.
   * 
   * @param actionParameter
   */
  public void set(ActionParameter input) {
    if(input.parameterName != null && !input.parameterName.equals(parameterName)) throw new Bug();
    if(type == null) throw new Bug();
    if(input.type != null
      && !XSDHelper.datatypeCompatible(XSDHelper.getTypeFor(type), input.parameter, XSDHelper.getTypeFor(input.type))) {
      // TODO throw exception
    }
    setParameter(input.getParameter());
  }
}
