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
package org.snipsnap.net;

import org.snipsnap.app.Application;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.user.User;
import org.snipsnap.user.UserManager;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Output the raw content of a snip
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class SnipRawServlet extends SnipSnapServlet {

  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    UserManager um = UserManager.getInstance();
    User user = Application.get().getUser();
    if (um.isAuthenticated(user)) {
      user.lastAccess();
    }

    String name = request.getPathInfo();
    if (null == name || "/".equals(name)) {
      name = "start";
    } else {
      name = name.substring(1);
    }

    name = name.replace('+', ' ');
    Snip snip = SnipSpace.getInstance().load(name);

    ServletOutputStream out = response.getOutputStream();

    response.setContentType("text/plain");
    // Snip does not exist
    if (null != snip) {
      snip.getAccess().handle(request);
      out.println(snip.getContent());
    } else {
      out.println("Snip not found.");
    }
  }
}