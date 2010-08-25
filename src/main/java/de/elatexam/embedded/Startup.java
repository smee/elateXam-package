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

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

import javax.management.MBeanServer;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.deployer.WebAppDeployer;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.security.SslSocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.management.MBeanContainer;

/**
 * @author Steffen Dienst
 *
 */
public class Startup {

    public Startup() {


        try {
            Server server = new Server();

            // if (!contextPath.equals("/")) {
            // server.addHandler(new MovedContextHandler(server, "/", contextPath));
            // }

            List<Connector> conns = new ArrayList<Connector>(2);

            int httpPort = 8080;
            try {
                httpPort = Integer.valueOf(System.getProperty("jetty.port"));
                System.out.println("jetty.port property: " + httpPort);
            } catch (NumberFormatException e) { }

            if (httpPort != 0) {
                SelectChannelConnector httpConn = new SelectChannelConnector();
                httpConn.setPort(httpPort);
                // allow access to the unsecured connector from localhost only!
                httpConn.setHost("localhost");
                conns.add(httpConn);
            }

            server.setConnectors(conns.toArray(new Connector[conns.size()]));
            server.setStopAtShutdown(true);


            final WebAppDeployer wad = new WebAppDeployer();
            wad.setContexts(server);
            wad.setWebAppDir("target/wars");
            // wad.setExtract(true);
            wad.start();
            // use empty session path to make sure, all webapps share the session id
            // this is needed for data exchange via TaskModelViewDelegate
            for (org.mortbay.jetty.Handler handler : server.getHandlers())
                if (handler instanceof WebAppContext) {
                    WebAppContext context = ((WebAppContext) handler);
                    context.getSessionHandler().getSessionManager().setSessionPath("/");
                    // make sure we find extra dependencies
                    // context.setParentLoaderPriority(true);
                }

            addSSLConnector(server);
            startJMX(server);
            server.start();

            if (httpPort != 0) {
                System.out.println("Ready at http://localhost:" + httpPort);
            }
            server.join();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }




    }

    private void addSSLConnector(Server server) throws Exception {
        SslSocketConnector ssl = new SslSocketConnector();
        ssl.setPort(8443);
        ssl.setKeystore("server.keystore");
        ssl.setPassword("testtest");
        ssl.setKeyPassword("testtest");

        ssl.setNeedClientAuth(true);
        ssl.setTruststore("server.keystore");
        ssl.setTrustPassword("testtest");

        server.addConnector(ssl);

    }

    private void startJMX(Server server) {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        MBeanContainer mBeanContainer = new MBeanContainer(mBeanServer);
        server.getContainer().addEventListener(mBeanContainer);
        mBeanContainer.start();

    }

    /**
     * @param args
     */
    public static void main(final String[] args) {
        new Startup();

  }

}
