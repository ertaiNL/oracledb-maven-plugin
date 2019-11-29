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

package de.torstenwalter.maven.plugins;

import org.apache.commons.exec.CommandLine;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Assert;
import org.junit.Test;

public class AbstractDatapumpMojoTest {

    private static final String USERNAME = "username";
    private static final String HOSTNAME = "localhost";
    private static final Integer PORT = 443;
    private static final String SERVICE_NAME = "serviceName";
    private static final String DATA = "argument";

    private static final String CONNECTION_STRING = "'" + USERNAME + "@//" + HOSTNAME + ":" + PORT + "/"
            + SERVICE_NAME + "'";

    @Test
    public void testAddCommonArgumentsConnectionString() throws MojoFailureException {
        DatapumpMojo mojo = createBasicMojo();

        CommandLine cmd = mojo.buildCommandline();

        Assert.assertEquals(CONNECTION_STRING, cmd.getArguments()[0]);
    }

    @Test
    public void testAddCommonArgumentsContent() throws MojoFailureException {
        DatapumpMojo mojo = createBasicMojo();
        mojo.content = DATA;

        CommandLine cmd = mojo.buildCommandline();

        Assert.assertEquals("CONTENT=" + DATA, cmd.getArguments()[1]);
    }

    @Test
    public void testAddCommonArgumentsDirectory() throws MojoFailureException {
        DatapumpMojo mojo = createBasicMojo();
        mojo.directory = DATA;

        CommandLine cmd = mojo.buildCommandline();

        Assert.assertEquals("DIRECTORY=" + DATA, cmd.getArguments()[1]);
    }

    @Test
    public void testAddCommonArgumentsDumpfile() throws MojoFailureException {
        DatapumpMojo mojo = createBasicMojo();
        mojo.dumpfile = DATA;

        CommandLine cmd = mojo.buildCommandline();

        Assert.assertEquals("DUMPFILE=" + DATA, cmd.getArguments()[1]);
    }

    @Test
    public void testAddCommonArgumentsExclude() throws MojoFailureException {
        DatapumpMojo mojo = createBasicMojo();
        mojo.exclude = DATA;

        CommandLine cmd = mojo.buildCommandline();

        Assert.assertEquals("EXCLUDE=" + DATA, cmd.getArguments()[1]);
    }

    @Test
    public void testAddCommonArgumentsInclude() throws MojoFailureException {
        DatapumpMojo mojo = createBasicMojo();
        mojo.include = DATA;

        CommandLine cmd = mojo.buildCommandline();

        Assert.assertEquals("INCLUDE=" + DATA, cmd.getArguments()[1]);
    }

    @Test
    public void testAddCommonArgumentsLogfile() throws MojoFailureException {
        DatapumpMojo mojo = createBasicMojo();
        mojo.logfile = DATA;

        CommandLine cmd = mojo.buildCommandline();

        Assert.assertEquals("LOGFILE=" + DATA, cmd.getArguments()[1]);
    }

    @Test
    public void testAddCommonArgumentsLogtime() throws MojoFailureException {
        DatapumpMojo mojo = createBasicMojo();
        mojo.logtime = DATA;

        CommandLine cmd = mojo.buildCommandline();

        Assert.assertEquals("LOGTIME=" + DATA, cmd.getArguments()[1]);
    }

    @Test
    public void testAddCommonArgumentsNetworkLink() throws MojoFailureException {
        DatapumpMojo mojo = createBasicMojo();
        mojo.network_link = DATA;

        CommandLine cmd = mojo.buildCommandline();

        Assert.assertEquals("NETWORK_LINK=" + DATA, cmd.getArguments()[1]);
    }

    @Test
    public void testAddCommonArgumentsSchemas() throws MojoFailureException {
        DatapumpMojo mojo = createBasicMojo();
        mojo.schemas = DATA;

        CommandLine cmd = mojo.buildCommandline();

        Assert.assertEquals("SCHEMAS=" + DATA, cmd.getArguments()[1]);
    }

    @Test
    public void testAddCommonArgumentsTables() throws MojoFailureException {
        DatapumpMojo mojo = createBasicMojo();
        mojo.tables = DATA;

        CommandLine cmd = mojo.buildCommandline();

        Assert.assertEquals("TABLES=" + DATA, cmd.getArguments()[1]);
    }

    @Test
    public void testAddCommonArgumentsAmount() throws MojoFailureException {
        DatapumpMojo mojo = createBasicMojo();
        mojo.content = DATA;
        mojo.directory = DATA;
        mojo.dumpfile = DATA;
        mojo.exclude = DATA;
        mojo.include = DATA;
        mojo.logfile = DATA;
        mojo.logtime = DATA;
        mojo.network_link = DATA;
        mojo.schemas = DATA;
        mojo.tables = DATA;

        CommandLine cmd = mojo.buildCommandline();

        Assert.assertEquals(11, cmd.getArguments().length);
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

    static class DatapumpMojo extends AbstractDatapumpMojo {

        CommandLine buildCommandline() throws MojoFailureException {
            CommandLine commandLine = new CommandLine("test");
            addCommonArguments(commandLine);

            return commandLine;
        }
    }
}
