/**
 * Copyright 2012 Torsten Walter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *	 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.torstenwalter.maven.plugins;

import org.apache.commons.exec.CommandLine;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Assert;
import org.junit.Test;

public class AbstractDatapumpMojoTest {

    private static final String EXECUTABLE = "Test.exe";
    private static final String USERNAME = "username";
    private static final String HOSTNAME = "localhost";
    private static final Integer PORT = 443;
    private static final String SERVICE_NAME = "serviceName";
    private static final String DATA = "argument";

    private static final String CONNECTION_STRING = "'" + USERNAME + "@//" + HOSTNAME + ":" + PORT + "/"
            + SERVICE_NAME + "'";

    @Test
    public void addCommonArgumentsCheckFirstArgumentIsConnectionString() throws MojoFailureException {
        CommandLine cmd = new CommandLine(EXECUTABLE);
        DatapumpMojo mojo = createBasicMojo();

        mojo.addCommonArguments(cmd);

        Assert.assertEquals(CONNECTION_STRING, cmd.getArguments()[0]);
    }

    @Test
    public void addCommonArgumentsCheckArgumentContent() throws MojoFailureException {
        CommandLine cmd = new CommandLine(EXECUTABLE);
        DatapumpMojo mojo = createBasicMojo();
        mojo.content = DATA;

        mojo.addCommonArguments(cmd);

        Assert.assertEquals("CONTENT=" + DATA, cmd.getArguments()[1]);
    }

    @Test
    public void addCommonArgumentsCheckArgumentDirectory() throws MojoFailureException {
        CommandLine cmd = new CommandLine(EXECUTABLE);
        DatapumpMojo mojo = createBasicMojo();
        mojo.directory = DATA;

        mojo.addCommonArguments(cmd);

        Assert.assertEquals("DIRECTORY=" + DATA, cmd.getArguments()[1]);
    }

    @Test
    public void addCommonArgumentsCheckArgumentDumpfile() throws MojoFailureException {
        CommandLine cmd = new CommandLine(EXECUTABLE);
        DatapumpMojo mojo = createBasicMojo();
        mojo.dumpfile = DATA;

        mojo.addCommonArguments(cmd);

        Assert.assertEquals("DUMPFILE=" + DATA, cmd.getArguments()[2]);
    }

    @Test
    public void addCommonArgumentsCheckArgumentExclude() throws MojoFailureException {
        CommandLine cmd = new CommandLine(EXECUTABLE);
        DatapumpMojo mojo = createBasicMojo();
        mojo.exclude = DATA;

        mojo.addCommonArguments(cmd);

        Assert.assertEquals("EXCLUDE=" + DATA, cmd.getArguments()[2]);
    }

    @Test
    public void addCommonArgumentsCheckArgumentInclude() throws MojoFailureException {
        CommandLine cmd = new CommandLine(EXECUTABLE);
        DatapumpMojo mojo = createBasicMojo();
        mojo.include = DATA;

        mojo.addCommonArguments(cmd);

        Assert.assertEquals("INCLUDE=" + DATA, cmd.getArguments()[2]);
    }

    @Test
    public void addCommonArgumentsCheckArgumentLogfile() throws MojoFailureException {
        CommandLine cmd = new CommandLine(EXECUTABLE);
        DatapumpMojo mojo = createBasicMojo();
        mojo.logfile = DATA;

        mojo.addCommonArguments(cmd);

        Assert.assertEquals("LOGFILE=" + DATA, cmd.getArguments()[2]);
    }

    @Test
    public void addCommonArgumentsCheckArgumentNetworkLink() throws MojoFailureException {
        CommandLine cmd = new CommandLine(EXECUTABLE);
        DatapumpMojo mojo = createBasicMojo();
        mojo.network_link = DATA;

        mojo.addCommonArguments(cmd);

        Assert.assertEquals("NETWORK_LINK=" + DATA, cmd.getArguments()[2]);
    }

    @Test
    public void addCommonArgumentsCheckArgumentSchemas() throws MojoFailureException {
        CommandLine cmd = new CommandLine(EXECUTABLE);
        DatapumpMojo mojo = createBasicMojo();
        mojo.schemas = DATA;

        mojo.addCommonArguments(cmd);

        Assert.assertEquals("SCHEMAS=" + DATA, cmd.getArguments()[2]);
    }

    @Test
    public void addCommonArgumentsCheckArgumentTables() throws MojoFailureException {
        CommandLine cmd = new CommandLine(EXECUTABLE);
        DatapumpMojo mojo = createBasicMojo();
        mojo.tables = DATA;

        mojo.addCommonArguments(cmd);

        Assert.assertEquals("TABLES=" + DATA, cmd.getArguments()[2]);
    }

    @Test
    public void addCommonArgumentsCheckArgumentAmount() throws MojoFailureException {
        CommandLine cmd = new CommandLine(EXECUTABLE);
        DatapumpMojo mojo = createBasicMojo();
        mojo.content = DATA;
        mojo.directory = DATA;
        mojo.dumpfile = DATA;
        mojo.exclude = DATA;
        mojo.include = DATA;
        mojo.logfile = DATA;
        mojo.network_link = DATA;
        mojo.schemas = DATA;
        mojo.tables = DATA;

        mojo.addCommonArguments(cmd);

        Assert.assertEquals(10, cmd.getArguments().length);
    }

    private DatapumpMojo createBasicMojo() {
        DatapumpMojo mojo = new DatapumpMojo();
        mojo.useEasyConnect = Boolean.TRUE;
        mojo.username = USERNAME;
        mojo.hostname = HOSTNAME;
        mojo.port = PORT;
        mojo.serviceName = SERVICE_NAME;
        return mojo;
    }

    class DatapumpMojo extends AbstractDatapumpMojo {

        public void execute() {
            //do nothing
        }
    }
}