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
package com.neotis.config;

import com.neotis.app.Application;
import com.neotis.snip.HomePage;
import com.neotis.snip.Snip;
import com.neotis.snip.SnipSpace;
import com.neotis.user.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Create initial database and example snips.
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class CreateDB {

  public static void main(String[] args) {
    createDB("funzel", "funzel", "stephan@mud.de");
    insertData("funzel", "funzel", "stephan@mud.de");
  }

  // The username/password for the database.  This will be the username/
  // password for the user that has full control over the database.
  public static void createDB(String username, String password, String email) {
    // Register the Mckoi JDBC Driver
    try {
      Class.forName("com.mckoi.JDBCDriver").newInstance();
    } catch (Exception e) {
      System.out.println(
        "Unable to register the JDBC Driver.\n" +
        "Make sure the classpath is correct.\n" +
        "For example on Win32;  java -cp ../../mckoidb.jar;. SimpleApplicationDemo\n" +
        "On Unix;  java -cp ../../mckoidb.jar:. SimpleApplicationDemo");
      return;
    }

    // This URL specifies we are creating a local database.  The
    // configuration file for the database is found at './ExampleDB.conf'
    // The 'create=true' argument means we want to create the database.  If
    // the database already exists, it can not be created.
    String url = "jdbc:mckoi:local://./conf/db.conf?create=true";

    // Make a connection with the database.  This will create the database
    // and log into the newly created database.
    Connection connection;
    try {
      connection = DriverManager.getConnection(url, username, password);
    } catch (SQLException e) {
      System.out.println(
        "Unable to create the database.\n" +
        "The reason: " + e.getMessage());
      return;
    }

    // --- Set up the database ---

    try {
      // Create a Statement object to execute the queries on,
      Statement statement = connection.createStatement();

      System.out.println("CreateDB: Creating Tables");

      // Create a Person table,
      statement.executeQuery(
        "    CREATE TABLE Snip ( " +
        "       name      VARCHAR(100) NOT NULL, " +
        "       content   TEXT, " +
        "       cTime     TIMESTAMP, " +
        "       mTime     TIMESTAMP, " +
        "       cUser     VARCHAR(55), " +
        "       mUser     VARCHAR(55), " +
        "       parentSnip VARCHAR(100), " +
        "       commentSnip VARCHAR(100), " +
        "       permissions VARCHAR(200) )");

      statement.executeQuery(
        "    CREATE TABLE User ( " +
        "       login    VARCHAR(100) NOT NULL, " +
        "       passwd   VARCHAR(20), " +
        "       email    VARCHAR(100)," +
        "       status   VARCHAR(50), " +
        "       roles    VARCHAR(200) )");


      // Close the statement and the connection.
      statement.close();
      connection.close();

    } catch (SQLException e) {
      System.out.println(
        "An error occured\n" +
        "The SQLException message is: " + e.getMessage());
    }
    // Close the the connection.
    try {
      connection.close();
    } catch (SQLException e2) {
      e2.printStackTrace(System.err);
    }

  }

  public static void insertData(String username, String password, String email) {

    System.out.println("CreateDB: Inserting Data");

    User admin = UserManager.getInstance().create(username, password, email);
    admin.getRoles().add(Roles.EDITOR);
    UserManager.getInstance().store(admin);

    Application app = Application.get();
    app.setUser(admin);

    System.out.println("Creating admin homepage");
    HomePage.create(username);

    SnipSpace space = SnipSpace.getInstance();
    Snip snip = null;
    System.out.print("Creating about...");
    snip = space.create("about", "[SnipSnap] is a [Weblog] and [Wiki] tool by [funzel] und [arte]");
    if (snip != null) {
      System.out.println("ok.");
    } else {
      System.out.println("failed.");
    }

    snip = space.create("start", "{weblog}");
    snip.addPermission(Permissions.EDIT, Roles.EDITOR);
    space.store(snip);

    snip = space.create("snipsnap-index", "{index}");
    snip.addPermission(Permissions.EDIT, Roles.EDITOR);
    space.store(snip);

    String rolling = "__Blogrolling:__\\\\ \n" +
      "{link:Langreiter|http://www.langreiter.com}\\\\ \n" +
      "{link:Earl|http://earl.strain.at}\\\\ \n" +
      "{link:henso|http://www.henso.com}\\\\ \n" +
      "{link:Lambda|http://lambda.weblogs.com}\\\\ \n" +
      "{link:e7l3|http://www.e7l3.com}\\\\ \n";
    snip = space.create("snipsnap-blogrolling", rolling);
    snip.addPermission(Permissions.EDIT, Roles.EDITOR);
    space.store(snip);

    space.post("Welcome to [SnipSnap]. You can now login and add/edit your first post");
    System.out.println("CreateDB: Complete");
  }
}
