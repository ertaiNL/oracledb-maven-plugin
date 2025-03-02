/**
 * Copyright 2019 Torsten Walter, Rob Snelders
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.ertai.maven.plugins;

import nl.ertai.maven.plugins.AbstractDBMojo;
import nl.ertai.maven.plugins.Credentials;
import org.apache.commons.exec.CommandLine;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;

import org.apache.maven.plugin.MojoFailureException;
import org.junit.Assert;
import org.junit.Test;

public class AbstractDBMojoTest {

    private static final String EXECUTABLE = "exe";
    private static final String SERVER_ID = "serverId";
    private static final String USERNAME = "username";
    private static final String USERNAME_SERVER = "usernameServer";
    private static final String PASSWORD = "Password";
    private static final String HOSTNAME = "localhost";
    private static final Integer PORT = 443;
    private static final String SERVICE_NAME = "serviceName";
    private static final String INSTANCE_NAME = "instanceName";
    private static final String SYSDBA = "SYSDBA";
    private static final String SYSOPER = "SYSOPER";

    private static final Credentials CREDENTIALS = new Credentials(USERNAME, PASSWORD);

    @Test(expected = MojoFailureException.class)
    public void testGetCredentialsEmpty() throws MojoFailureException {
        DBMojo mojo = new DBMojo();
        mojo.getCredentials();
    }

    @Test(expected = MojoFailureException.class)
    public void testGetCredentialsServerIdNoServerSettings() throws MojoFailureException {
        Settings settings = new Settings();
        DBMojo mojo = new DBMojo();
        mojo.serverId = SERVER_ID;
        mojo.settings = settings;

        mojo.getCredentials();
    }

    @Test
    public void testGetCredentialsServerIdWithServerSettings() throws MojoFailureException {
        Settings settings = new Settings();
        settings.addServer(createServerObject());
        DBMojo mojo = new DBMojo();
        mojo.serverId = SERVER_ID;
        mojo.settings = settings;

        Credentials cred = mojo.getCredentials();

        Assert.assertEquals(USERNAME_SERVER, cred.getUsername());
        Assert.assertEquals(PASSWORD, cred.getPassword());
    }

    @Test
    public void testGetCredentialsCredentials() throws MojoFailureException {
        DBMojo mojo = new DBMojo();
        mojo.username = USERNAME;
        mojo.password = PASSWORD;

        Credentials cred = mojo.getCredentials();

        Assert.assertEquals(USERNAME, cred.getUsername());
        Assert.assertEquals(PASSWORD, cred.getPassword());
    }

    @Test
    public void testGetCredentialsCredentialsWithCredentialsAndSettings() throws MojoFailureException {
        Settings settings = new Settings();
        settings.addServer(createServerObject());
        DBMojo mojo = new DBMojo();
        mojo.serverId = SERVER_ID;
        mojo.settings = settings;
        mojo.username = USERNAME;
        mojo.password = PASSWORD;

        Credentials cred = mojo.getCredentials();

        Assert.assertEquals(USERNAME_SERVER, cred.getUsername());
        Assert.assertEquals(PASSWORD, cred.getPassword());
    }

    @Test
    public void testGetConnectionIdentifierBasicNonEasyConnect() throws MojoFailureException {
        DBMojo mojo = new DBMojo();
        mojo.username = USERNAME;
        mojo.hostname = HOSTNAME;
        mojo.port = PORT;
        mojo.serviceName = SERVICE_NAME;

        String connectionId = mojo.getConnectionIdentifier();
        String expectedId = USERNAME
                + "@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=tcp)"
                + "(HOST=" + HOSTNAME + ")(PORT=" + PORT
                + ")))(CONNECT_DATA=(SERVICE_NAME=" + SERVICE_NAME + ")))";
        Assert.assertEquals(expectedId, connectionId);
    }

    @Test
    public void testGetConnectionIdentifierAllNonEasyConnect() throws MojoFailureException {
        DBMojo mojo = new DBMojo();
        mojo.username = USERNAME;
        mojo.password = PASSWORD;
        mojo.hostname = HOSTNAME;
        mojo.port = PORT;
        mojo.serviceName = SERVICE_NAME;
        mojo.instanceName = INSTANCE_NAME;
        mojo.asClause = SYSDBA;

        String connectionId = mojo.getConnectionIdentifier();
        String expectedId = USERNAME + "/" + PASSWORD
                + "@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=tcp)"
                + "(HOST=" + HOSTNAME + ")(PORT=" + PORT
                + ")))(CONNECT_DATA=(SERVICE_NAME=" + SERVICE_NAME + ")"
                + "(INSTANCE_NAME=" + INSTANCE_NAME + ")))"
                + " AS " + SYSDBA;
        Assert.assertEquals(expectedId, connectionId);
    }

    @Test
    public void testGetConnectionIdentifierBasicEasyConnect() throws MojoFailureException {
        DBMojo mojo = new DBMojo();
        mojo.useEasyConnect = Boolean.TRUE;
        mojo.username = USERNAME;
        mojo.hostname = HOSTNAME;
        mojo.port = PORT;
        mojo.serviceName = SERVICE_NAME;

        String connectionId = mojo.getConnectionIdentifier();
        String expectedId = USERNAME + "@//" + HOSTNAME + ":" + PORT + "/" + SERVICE_NAME;
        Assert.assertEquals(expectedId, connectionId);
    }

    @Test
    public void testGetConnectionIdentifierAllEasyConnect() throws MojoFailureException {
        DBMojo mojo = new DBMojo();
        mojo.useEasyConnect = Boolean.TRUE;
        mojo.username = USERNAME;
        mojo.password = PASSWORD;
        mojo.hostname = HOSTNAME;
        mojo.port = PORT;
        mojo.serviceName = SERVICE_NAME;
        mojo.asClause = SYSOPER;

        String connectionId = mojo.getConnectionIdentifier();
        String expectedId = USERNAME + "/" + PASSWORD + "@//" + HOSTNAME + ":" + PORT
                + "/" + SERVICE_NAME + " AS " + SYSOPER;
        Assert.assertEquals(expectedId, connectionId);
    }

    @Test
    public void testObfuscateCredentials() {
        DBMojo mojo = new DBMojo();
        String text = "Test " + USERNAME + " " + PASSWORD;
        CommandLine cmd = new CommandLine(EXECUTABLE);
        cmd.addArgument(text);

        String result = mojo.obfuscateCredentials(cmd, CREDENTIALS);

        Assert.assertEquals("[exe, \"Test <username> <password>\"]", result);
    }

    private Server createServerObject() {
        Server server = new Server();
        server.setId(SERVER_ID);
        server.setUsername(USERNAME_SERVER);
        server.setPassword(PASSWORD);
        return server;
    }

    static class DBMojo extends AbstractDBMojo {

        public void execute() {
            //do nothing
        }
    }
}
