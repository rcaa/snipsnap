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
package org.snipsnap.user;

import org.snipsnap.util.Nameable;
import org.snipsnap.app.Application;
import org.snipsnap.config.AppConfiguration;

import java.sql.Timestamp;

/**
 * User class.
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class User implements Nameable {

  private String login;
  private String passwd;
  private String email;
  private String status;
  private Roles roles;
  private Timestamp cTime, mTime, lastLogin, lastAccess, lastLogout;

  private boolean guest = false;
  private boolean nonUser = false;

  public User(String login, String passwd, String email) {
    this.login = login;
    this.passwd = passwd;
    this.email = email;
  }

  public Timestamp getCTime() {
    return cTime;
  }

  public void setCTime(Timestamp cTime) {
    this.cTime = cTime;
  }

  public Timestamp getMTime() {
    return mTime;
  }

  public void setMTime(Timestamp mTime) {
    this.mTime = mTime;
  }

  public void lastAccess() {
    this.lastAccess = new Timestamp(new java.util.Date().getTime());
    //System.err.println(this.login+" hashcode: "+((Object) this).hashCode());
    //System.err.println("Set lastAccess() "+this.login+" "+lastAccess);
  }

  public Timestamp getLastLogout() {
    return lastLogout;
  }

  public void setLastLogout(Timestamp lastLogout) {
    //System.err.println(this.login+" hashcode: "+((Object) this).hashCode());
    //System.err.println("Set LastLogout() "+this.login+" "+lastLogout+" old: "+this.lastLogout);
    this.lastLogout = lastLogout;
  }

  public Timestamp getLastAccess() {
    return this.lastAccess;
  }

  public void setLastAccess(Timestamp lastAccess) {
    this.lastAccess = lastAccess;
  }

  public Timestamp getLastLogin() {
    return lastLogin;
  }

  public void lastLogin() {
    setLastLogin(new Timestamp(new java.util.Date().getTime()));
  }

  public void setLastLogin(Timestamp lastLogin) {
    this.lastLogin = lastLogin;
  }

  public void setStatus(String status) {
    this.status = status;
    return;
  }

  public String getStatus() {
    if (null == status) {
      status = "not set";
    }
    return status;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getEmail() {
    return email;
  }

  // Set passwd of user. Takes unecrypted
  // passwd and then sets an encrypted version
  public void setPasswd(String passwd) {
    this.passwd = Digest.getDigest(passwd);
  }

  public String getPasswd() {
    return passwd;
  }

  public String getName() {
    return getLogin();
  }

  public String getLogin() {
    return login;
  }

  public void setRoles(Roles roles) {
    this.roles = roles;
    return;
  }

  public Roles getRoles() {
    if (null == roles) {
      roles = new Roles();
    }
    return roles;
  }

  public boolean isAdmin() {
    Application app = Application.get();
    AppConfiguration config = app.getConfiguration();
    return config.getAdminLogin().equals(login);
  }

  public void setGuest(boolean guest) {
    this.guest = guest;
  }

  public boolean isGuest() {
    return guest;
  }

  public void setNonUser(boolean nonUser) {
    this.nonUser = nonUser;
  }

  public boolean isNonUser() {
    return nonUser;
  }

  public boolean equals(Object obj) {
    if(obj instanceof User && obj != null && this.getName() != null) {
      this.getName().equals(((User)obj).getName());
    }
    return super.equals(obj);
  }

  public String toString() {
    return "User["+login+","+(passwd != null ? "pass set" : "no pass") +","+email+","+status+","+roles+"]";
  }
}