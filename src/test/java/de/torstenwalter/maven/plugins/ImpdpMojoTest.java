package de.torstenwalter.maven.plugins;

import org.apache.commons.exec.CommandLine;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Assert;
import org.junit.Test;

public class ImpdpMojoTest {

    private static final String EXECUTABLE = "impdp";
    private static final String USERNAME = "username";
    private static final String HOSTNAME = "localhost";
    private static final Integer PORT = 443;
    private static final String SERVICE_NAME = "serviceName";
    private static final String DATA = "data";

    private static final String CONNECTION_STRING = "'" + USERNAME + "@//" + HOSTNAME + ":" + PORT + "/"
            + SERVICE_NAME + "'";

    @Test
    public void addCommonArgumentsCheckExecutable() throws MojoFailureException {
        ImpdpMojo mojo = createBasicMojo();
        CommandLine cmd = mojo.buildCommandline();

        Assert.assertEquals(EXECUTABLE, cmd.getExecutable());
    }

    @Test
    public void addCommonArgumentsCheckFirstArgumentIsConnectionString() throws MojoFailureException {
        ImpdpMojo mojo = createBasicMojo();
        CommandLine cmd = mojo.buildCommandline();

        Assert.assertEquals(CONNECTION_STRING, cmd.getArguments()[0]);
    }

    @Test
    public void addCommonArgumentsCheckRemapTablespace() throws MojoFailureException {
        ImpdpMojo mojo = createBasicMojo();
        mojo.remap_tablespace = DATA;
        CommandLine cmd = mojo.buildCommandline();

        Assert.assertEquals("REMAP_TABLESPACE=" + DATA, cmd.getArguments()[2]);
    }

    @Test
    public void addCommonArgumentsCheckRemapSchema() throws MojoFailureException {
        ImpdpMojo mojo = createBasicMojo();
        mojo.remap_schema = DATA;
        CommandLine cmd = mojo.buildCommandline();

        Assert.assertEquals("REMAP_SCHEMA=" + DATA, cmd.getArguments()[2]);
    }

    @Test
    public void addCommonArgumentsCheckTableExistsAction() throws MojoFailureException {
        ImpdpMojo mojo = createBasicMojo();
        mojo.table_exists_action = DATA;
        CommandLine cmd = mojo.buildCommandline();

        Assert.assertEquals("TABLE_EXISTS_ACTION=" + DATA, cmd.getArguments()[2]);
    }

    private ImpdpMojo createBasicMojo() {
        ImpdpMojo mojo = new ImpdpMojo();
        mojo.useEasyConnect = Boolean.TRUE;
        mojo.username = USERNAME;
        mojo.hostname = HOSTNAME;
        mojo.port = PORT;
        mojo.serviceName = SERVICE_NAME;
        mojo.impdp = EXECUTABLE;
        return mojo;
    }
}
