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
package com.neotis.test.snip;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import com.neotis.snip.Snip;

import java.sql.Date;

public class SnipTest extends TestCase {
  public SnipTest(String name) {
    super(name);
  }

  public static Test suite() {
    TestSuite s = new TestSuite();
    s.addTestSuite(SnipTest.class);
    return s;
  }

  public void testName() {
    Snip snip1 = new Snip("A", "A Content");
    assertEquals(snip1.getName(), "A");
  }

  public void testContent() {
    Snip snip1 = new Snip("A", "A Content");
    assertEquals(snip1.getContent(), "A Content");
  }

  public void testDateName() {
    assertEquals( Snip.toName(new Date(new java.util.Date("01 Jan 2002").getTime())) , "2002-01-01");
  }

}
