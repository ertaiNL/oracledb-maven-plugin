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
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;

public class SQLPlusMojoTest {

    private static final String EXECUTABLE = "sqlplus";
    private static final String USERNAME = "username";
    private static final String HOSTNAME = "localhost";
    private static final Integer PORT = 443;
    private static final String SERVICE_NAME = "serviceName";
    private static final String DATA = "data";

    private static final File STATEMENT_FILE = new File("src/test/resources/statement.sql");

    private static final String CONNECTION_STRING = USERNAME + "@//" + HOSTNAME + ":" + PORT + "/" + SERVICE_NAME;

    @Test
    public void testGetFileNull() throws MojoExecutionException {
        SQLPlusMojo mojo = createBasicMojo();
        File sqlFile = mojo.getFile();

        Assert.assertNull(sqlFile);
    }

    @Test
    public void testGetFileSqlCommand() throws MojoExecutionException, IOException {
        SQLPlusMojo mojo = createBasicMojo();
        mojo.sqlCommand = DATA;
        File sqlFile = mojo.getFile();

        Assert.assertNotNull(sqlFile);

        String data = new String(Files.readAllBytes(sqlFile.toPath()));
        Assert.assertEquals(DATA, data);
    }

    @Test
    public void testGetFileSqlFile() throws MojoExecutionException {
        SQLPlusMojo mojo = createBasicMojo();
        mojo.sqlFile = STATEMENT_FILE;
        File sqlFile = mojo.getFile();

        Assert.assertEquals(STATEMENT_FILE, sqlFile);
    }

    @Test
    public void testGetEnvVarsNull() throws MojoExecutionException {
        SQLPlusMojo mojo = createBasicMojo();
        Map vars = mojo.getEnvVars();

        Assert.assertNull(vars);
    }

    @Test
    public void testGetEnvVarsBeforeSql() throws MojoExecutionException {
        SQLPlusMojo mojo = createBasicMojo();
        mojo.beforeSql = DATA;
        Map vars = mojo.getEnvVars();

        Assert.assertTrue(vars.containsKey("SQLPATH"));
    }

    @Test
    public void testBuildCommandlineExecutable() throws MojoExecutionException, MojoFailureException {
        SQLPlusMojo mojo = createBasicMojo();
        CommandLine cmd = mojo.buildCommandline(STATEMENT_FILE);

        Assert.assertEquals(EXECUTABLE, cmd.getExecutable());
    }

    @Test
    public void testBuildCommandlineConnectionString() throws MojoExecutionException, MojoFailureException {
        SQLPlusMojo mojo = createBasicMojo();
        CommandLine cmd = mojo.buildCommandline(STATEMENT_FILE);

        Assert.assertEquals("-L", cmd.getArguments()[0]);
        Assert.assertEquals(CONNECTION_STRING, cmd.getArguments()[1]);
    }

    @Test
    public void testBuildCommandlineFileName() throws MojoExecutionException, MojoFailureException {
        SQLPlusMojo mojo = createBasicMojo();
        CommandLine cmd = mojo.buildCommandline(STATEMENT_FILE);

        Assert.assertEquals("@" + STATEMENT_FILE.getName(), cmd.getArguments()[2]);
    }

    @Test
    public void testBuildCommandlineArguments() throws MojoExecutionException, MojoFailureException {
        SQLPlusMojo mojo = createBasicMojo();
        mojo.arguments = Arrays.asList(new String[]{DATA});
        CommandLine cmd = mojo.buildCommandline(STATEMENT_FILE);

        Assert.assertEquals(DATA, cmd.getArguments()[3]);
    }

    private SQLPlusMojo createBasicMojo() {
        SQLPlusMojo mojo = new SQLPlusMojo();
        mojo.useEasyConnect = Boolean.TRUE;
        mojo.username = USERNAME;
        mojo.hostname = HOSTNAME;
        mojo.port = PORT;
        mojo.serviceName = SERVICE_NAME;
        mojo.sqlplus = EXECUTABLE;
        return mojo;
    }
}
