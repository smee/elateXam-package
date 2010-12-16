/*

Copyright (C) 2010 Steffen Dienst

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package de.elatexam.embedded;

import java.io.File;
import java.io.FileReader;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.management.MBeanServer;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.security.SslSocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.management.MBeanContainer;

/**
 * @author Steffen Dienst
 *
 */
public class Startup {

  public Startup() throws Exception {

    Properties prop = new Properties();
    prop.load(new FileReader("sslsettings.properties"));

    final int httpPort = Integer.parseInt(prop.getProperty("port.http", "8080"));
    Server server = initializeServer(prop, httpPort);
    initializeWebapps(prop, server);
    startJMX(server);
    server.start();

    System.out.println("###########################################################");
    System.out.println("#");
    System.out.printf("#  Ready at http://localhost%s/%s\n", (httpPort != 0 ? ":" + httpPort : ""), prop.getProperty("elatexam.name", "examServer"));
    System.out.println("#");
    System.out.println("###########################################################");
    server.join();
  }

  /**
   * @param prop
   * @param httpPort
   * @return
   * @throws Exception
   */
  protected Server initializeServer(Properties prop, int httpPort) throws Exception {
    Server server = new Server();

    // if (!contextPath.equals("/")) {
    // server.addHandler(new MovedContextHandler(server, "/", contextPath));
    // }

    List<Connector> conns = new ArrayList<Connector>(2);

    SelectChannelConnector httpConn = new SelectChannelConnector();
    httpConn.setPort(httpPort);
    // allow access to the unsecured connector from localhost only!
    conns.add(httpConn);
    if (addSSLConnector(server, prop) == true) {
      // whenn ssl is enabled disallow non-ssl access from all ips but localhost
      httpConn.setHost("localhost");
    }

    server.setConnectors(conns.toArray(new Connector[conns.size()]));
    server.setStopAtShutdown(true);
    return server;
  }

  /**
   * @param prop
   * @param elatexamContextPath
   * @param server
   * @throws Exception
   */
  protected void initializeWebapps(Properties prop, Server server) throws Exception {
    // set name of the elatexam context path
    final String elatexamContextPath = prop.getProperty("elatexam.name", "examServer");
    System.setProperty("elatexam.name", elatexamContextPath);

    // initialize taskmodel-core-view webapp
    WebAppContext tcv = new WebAppContext(prop.getProperty("war.directory", "wars") + "/taskmodel-core-view.war", "/taskmodel-core-view");
    tcv.setExtractWAR(true);
    server.addHandler(tcv);
    // initialize examserver webapp, use configured context path
    WebAppContext scv = new WebAppContext(prop.getProperty("war.directory", "wars") + "/examServer.war", "/"
        + elatexamContextPath);
    scv.setExtractWAR(true);
    server.addHandler(scv);
    // use empty session path to make sure, all webapps share the session id
    // this is needed for data exchange via TaskModelViewDelegate
    for (org.mortbay.jetty.Handler handler : server.getHandlers())
      if (handler instanceof WebAppContext) {
        WebAppContext context = ((WebAppContext) handler);
        context.getSessionHandler().getSessionManager().setSessionPath("/");
      }
  }

  private boolean addSSLConnector(Server server, Properties prop) throws Exception {
    final String keystoreFile = prop.getProperty("keystore.filename", "server.keystore");
    final String password = prop.getProperty("keystore.password", "testtest");
    String httpsPort = prop.getProperty("port.https", "8443");

    if (new File(keystoreFile).exists()) {
      SslSocketConnector ssl = new SslSocketConnector();
      ssl.setPort(Integer.parseInt(httpsPort));
      ssl.setKeystore(keystoreFile);
      ssl.setPassword(password);
      ssl.setKeyPassword(password);

      ssl.setNeedClientAuth(true);
      ssl.setTruststore(keystoreFile);
      ssl.setTrustPassword(password);

      server.addConnector(ssl);
      return true;
    } else {
      System.out.println("\n" +
                    "#######################################################################\n" +
                    "# There seems to be no keystore available. Please generate one using \n" +
                    "#  for Linux: createCertificates.sh \n" +
                    "#  for Windows: createCertificates.bat \n\n" +
                    "# and place it in this directory with name \"server.keystore\". \n" +
                    "# \n" +
                    "# WARNING: SSL will not be available! \n" +
                    "# \n" +
                    "#######################################################################");
      return false;
    }

  }

  private void startJMX(Server server) {
    MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
    MBeanContainer mBeanContainer = new MBeanContainer(mBeanServer);
    server.getContainer().addEventListener(mBeanContainer);
    mBeanContainer.start();

  }

  /**
   * @param args
   * @throws Exception
   */
  public static void main(final String[] args) throws Exception {
    new Startup();

  }

}
