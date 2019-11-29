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

import nl.ertai.maven.plugins.ExpdpMojo;
import org.apache.commons.exec.CommandLine;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Assert;
import org.junit.Test;

public class ExpdpMojoTest {

    private static final String EXECUTABLE = "expdp";
    private static final String USERNAME = "username";
    private static final String HOSTNAME = "localhost";
    private static final Integer PORT = 443;
    private static final String SERVICE_NAME = "serviceName";
    private static final String DATA = "data";

    private static final String CONNECTION_STRING = "'" + USERNAME + "@//" + HOSTNAME + ":" + PORT + "/"
            + SERVICE_NAME + "'";

    @Test
    public void testBuildCommandlineExecutable() throws MojoFailureException {
        ExpdpMojo mojo = createBasicMojo();
        CommandLine cmd = mojo.buildCommandline();

        Assert.assertEquals(EXECUTABLE, cmd.getExecutable());
    }

    @Test
    public void testBuildCommandlineConnectionString() throws MojoFailureException {
        ExpdpMojo mojo = createBasicMojo();
        CommandLine cmd = mojo.buildCommandline();

        Assert.assertEquals(CONNECTION_STRING, cmd.getArguments()[0]);
    }

    @Test
    public void testBuildCommandlineCompression() throws MojoFailureException {
        ExpdpMojo mojo = createBasicMojo();
        mojo.compression = DATA;
        CommandLine cmd = mojo.buildCommandline();

        Assert.assertEquals("COMPRESSION=" + DATA, cmd.getArguments()[1]);
    }

    @Test
    public void testBuildCommandlineReuseDumpFiles() throws MojoFailureException {
        ExpdpMojo mojo = createBasicMojo();
        mojo.reuse_dump_files = true;
        CommandLine cmd = mojo.buildCommandline();

        Assert.assertEquals("REUSE_DUMPFILES=YES", cmd.getArguments()[1]);
    }

    private ExpdpMojo createBasicMojo() {
        ExpdpMojo mojo = new ExpdpMojo();
        mojo.useEasyConnect = Boolean.TRUE;
        mojo.username = USERNAME;
        mojo.hostname = HOSTNAME;
        mojo.port = PORT;
        mojo.serviceName = SERVICE_NAME;
        mojo.expdp = EXECUTABLE;
        return mojo;
    }
}
