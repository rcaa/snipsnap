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
/*
 * Class that finds snippets like
 * {link|neotis|http://www.neotis.de} ---> <elink ....>
 * {neotis} -> include neotis wiki
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */

package org.snipsnap.snip.filter;

import org.apache.oro.text.regex.MatchResult;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.snip.filter.macro.*;
import org.snipsnap.snip.filter.regex.RegexTokenFilter;
import org.snipsnap.app.Application;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class MacroFilter extends RegexTokenFilter {

  private static Map macros;

  public MacroFilter() {
    super("\\{([^:}]*):?(.*?)\\}(.*?)\\{(\\1)\\}", SINGLELINE);
    addRegex("\\{([^:}]*):?(.*?)\\}", "", MULTILINE);

    synchronized (this) {
      if (null == macros) {
        macros = new HashMap();
        add(new FieldMacro());
        add(new LinkMacro());
        add(new AnnotationMacro());
        add(new CodeMacro());
        add(new IsbnMacro());
        add(new ApiMacro());
        add(new TableMacro());
        add(new UserSnipMacro());
        add(new RecentSnipMacro());
        add(new UserMacro());
        add(new SearchMacro());
        add(new WeblogMacro());
        add(new IndexSnipMacro());
        add(new ImageMacro());
        add(new LastLoginMacro());
        add(new HotSnipMacro());
      }
    }
  }

  public void add(Macro macro) {
    macros.put(macro.getName(), macro);
  }

  /**
   * Splits a String on a delimiter to a List. The function works like
   * the perl-function split.
   *
   * @param aString    a String to split
   * @param delimiter  a delimiter dividing the entries
   * @return           a Array of splittet Strings
   */

  public static String[] split(String aString, String delimiter, Map params) {
    StringTokenizer st = new StringTokenizer(aString, delimiter);
    String[] result = new String[st.countTokens()];
    int i = 0;

    while (st.hasMoreTokens()) {
      String value = st.nextToken();
      if (value.startsWith("$")) {
        value = value.substring(1);
        if (params.containsKey(value)) {
          result[i++] = (String) params.get(value);
        } else {
          result[i++] = "";
        }
      } else {
        result[i++] = value;
      }
    }

    return result;
  }

  public void handleMatch(StringBuffer buffer, MatchResult result, Snip snip) {
    String[] params = null;
    String content = null;
    String command = result.group(1);

    // System.out.println("Parameter block:" + Application.get().getParameters() );

    // {$peng} are variables not macros.
    if (!command.startsWith("$")) {
//    for (int i=0; i<result.groups(); i++) {
//      System.err.println(i+" "+result.group(i));
//    }

      Map paramMap = Application.get().getParameters();
      // {tag} ... {tag}
      if (result.group(1).equals(result.group(result.groups() - 1))) {
        // {tag:1|2} ... {tag}
        if (!"".equals(result.group(2))) {
          params = split(result.group(2), "|", paramMap);
        }
        content = result.group(3);
      } else {
        // {tag}
        if (result.groups() > 1) {
          params = split(result.group(2), "|", paramMap);
        }
      }

      // @DANGER: recursive calls may replace macros in included source code
      try {
        if (macros.containsKey(command)) {
          Macro macro = (Macro) macros.get(command);
          // recursively filter macros within macros
          if (null != content) {
            content = filter(content, snip);
          }
          macro.execute(buffer, params, content, snip);
        } else if (command.startsWith("!")) {
          // @TODO including of other snips
          Snip includeSnip = SnipSpace.getInstance().load(command.substring(1));
          if (null != includeSnip) {
            String included = includeSnip.getContent();
            Filter paramFilter = new ParamFilter(params);
            buffer.append(paramFilter.filter(included, null));
          } else {
            buffer.append(command.substring(1) + " not found.");
          }
          return;
        } else {
          buffer.append(result.group(0));
          return;
        }
      } catch (Exception e) {
        System.err.println("unable to format macro: " + result.group(1));
        e.printStackTrace();
        buffer.append("?" + command + (result.length() > 1 ? ":" + result.group(2) : "") + "?");
        return;

      }
    } else {
      buffer.append("<");
      buffer.append(command.substring(1));
      buffer.append(">");
    }
  }
}
