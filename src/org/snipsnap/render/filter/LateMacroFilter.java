/*
 * This file is part of "SnipSnap Wiki/Weblog".
 *
 * Copyright (c) 2002 Stephan J. Schmidt, Matthias L. Jugel
 * All Rights Reserved.
 *
 * Please visit http://snipsnap.org/ for updates and contact.
 *
 * --LICENSE NOTICE--
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 * --LICENSE NOTICE--
 */

package org.snipsnap.render.filter;

import org.radeox.api.engine.IncludeRenderEngine;
import org.radeox.api.engine.RenderEngine;
import org.radeox.filter.regex.RegexTokenFilter;
import org.radeox.filter.regex.MatchResult;
import org.radeox.filter.Filter;
import org.radeox.filter.context.FilterContext;
import org.radeox.macro.Macro;
import org.radeox.macro.parameter.MacroParameter;
import org.radeox.util.StringBufferWriter;
import org.radeox.util.logging.Logger;
import org.snipsnap.render.macro.WeblogMacro;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/*
 * Macro filter that is called later than MacroFilter.
 * Only used to call {weblog} macro because we do not
 * want to call filters on already filtered content
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */

public class LateMacroFilter extends RegexTokenFilter {

  private static LateMacroFilter instance;

  private Map macros;
  private static Object monitor = new Object();

  public LateMacroFilter() {
    super("\\{([^:}]+):?(.*?)\\}(.*?)\\{(\\1)\\}", SINGLELINE);
    addRegex("\\{([^:}]+):?(.*?)\\}", "", MULTILINE);

    macros = new HashMap();

    add(new WeblogMacro());
  }

  public static Filter getInstance() {
    synchronized (monitor) {
      if (null == instance) {
        instance = new LateMacroFilter();
      }
    }
    return instance;
  }

  public void add(Macro macro) {
    macros.put(macro.getName(), macro);
  }

  public void handleMatch(StringBuffer buffer, MatchResult result, FilterContext context) {
    String command = result.group(1);

// Logger.debug("Parameter block:" + Application.get().getParameters() );

// {$peng} are variables not macros.
    if (!command.startsWith("$")) {
//      for (int i=0; i<result.groups(); i++) {
//        Logger.debug(i+" "+result.group(i));
//      }

      MacroParameter mParams = context.getMacroParameter();

// {tag} ... {tag}
      if (result.group(1).equals(result.group(result.groups() - 1))) {
// {tag:1|2} ... {tag}
        if (!"".equals(result.group(2))) {
          mParams.setParams(result.group(2));
        }
        mParams.setContent(result.group(3));
      } else {
// {tag}
        if (result.groups() > 1) {
// {tag:1|2}
          mParams.setParams(result.group(2));
        }
      }

// @DANGER: recursive calls may replace macros in included source code
      try {
        if (macros.containsKey(command)) {
          Macro macro = (Macro) macros.get(command);
// recursively filter macros within macros
          if (null != mParams.getContent()) {
            mParams.setContent(filter(mParams.getContent(), context));
          }
          Writer writer = new StringBufferWriter(buffer);
          macro.execute(writer, mParams);
        } else if (command.startsWith("!")) {
// @TODO including of other snips
          RenderEngine engine = context.getRenderContext().getRenderEngine();
          if (engine instanceof IncludeRenderEngine) {
            String include = ((IncludeRenderEngine) engine).include(command.substring(1));
            if (null != include) {
              //Filter paramFilter = new ParamFilter(params);
              //buffer.append(paramFilter.filter(included, null));
              buffer.append(include);
            } else {
              buffer.append(command.substring(1) + " not found.");
            }
          }
          return;
        } else {
          buffer.append(result.group(0));
          return;
        }
      } catch (IllegalArgumentException e) {
        buffer.append("<div class=\"error\">" + command + ": " + e.getMessage() + "</div>");
      } catch (Exception e) {
        Logger.warn("unable to format macro: " + result.group(1), e);
        buffer.append("<div class=\"error\">" + command + "</div>");
        return;
      }
    } else {
      buffer.append("<");
      buffer.append(command.substring(1));
      buffer.append(">");
    }
  }
}
