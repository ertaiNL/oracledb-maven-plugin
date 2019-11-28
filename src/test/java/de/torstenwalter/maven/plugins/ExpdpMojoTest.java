package de.torstenwalter.maven.plugins;

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
    public void addCommonArgumentsCheckExecutable() throws MojoFailureException {
        ExpdpMojo mojo = createBasicMojo();
        CommandLine cmd = mojo.buildCommandline();

        Assert.assertEquals(EXECUTABLE, cmd.getExecutable());
    }

    @Test
    public void addCommonArgumentsCheckFirstArgumentIsConnectionString() throws MojoFailureException {
        ExpdpMojo mojo = createBasicMojo();
        CommandLine cmd = mojo.buildCommandline();

        Assert.assertEquals(CONNECTION_STRING, cmd.getArguments()[0]);
    }

    @Test
    public void addCommonArgumentsCheckCompression() throws MojoFailureException {
        ExpdpMojo mojo = createBasicMojo();
        mojo.compression = DATA;
        CommandLine cmd = mojo.buildCommandline();

        Assert.assertEquals("COMPRESSION=" + DATA, cmd.getArguments()[2]);
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
