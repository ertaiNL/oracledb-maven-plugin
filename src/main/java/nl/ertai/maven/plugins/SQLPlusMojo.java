/**
 * Copyright 2019 Torsten Walter, Rob Snelders
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

package nl.ertai.maven.plugins;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.cli.CommandLineUtils;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Executes scripts or code snippet using SQL*Plus command line utility
 * 
 * @goal sqlplus
 */
public class SQLPlusMojo extends AbstractDBMojo {

	/**
	 * The sqlplus command to execute. If sqlplus is not in you PATH you can
	 * simply specify the full path to the sqlplus command here
	 *
	 * @parameter default-value="sqlplus"
	 */
	String sqlplus;

	/**
	 * These statements are executed before the statements in sqlCommand or
	 * sqlFile. They should make sure the build fails if an error occurs.
	 *
	 * Technically speaking these statements are written to a login.sql file and
	 * executed directly after sqlplus starts.
	 *
	 * @parameter default-value= "WHENEVER SQLERROR EXIT FAILURE ROLLBACK;\nWHENEVER OSERROR EXIT FAILURE ROLLBACK;"
	 * @required
	 */
	String beforeSql;

	/**
	 * Specify commands which SQL*Plus should executed.
	 *
	 * @parameter
	 */
	String sqlCommand;

	/**
	 * File containing commands which SQL*Plus should execute.
	 *
	 * @parameter
	 */
	File sqlFile;

	/**
	 * A list of arguments passed to the {@code executable}, which should be of type <code>&lt;argument&gt;</code>
	 *
	 * @parameter
	 */
	List<String> arguments;

    public void execute() throws MojoExecutionException, MojoFailureException {
    	File file = getFile();
    	if (file != null) {
    		CommandLine cmd = buildCommandline(file);

			Executor exec = new DefaultExecutor();
			exec.setWorkingDirectory(file.getParentFile());
			exec.setStreamHandler(new PumpStreamHandler(new InfoLogOutputStream(), new ErrorLogOutputStream()));
			try {
				exec.execute(cmd, getEnvVars());
			} catch (ExecuteException e) {
				throw new MojoExecutionException("program exited with exitCode: " + e.getExitValue(), e);
			} catch (IOException e) {
				throw new MojoExecutionException("Command execution failed.", e);
			}
		}
	}

	File getFile() throws MojoExecutionException {
		if (!StringUtils.isEmpty(sqlCommand)) {
			return printToTempFile(sqlCommand);
		} else {
			return sqlFile;
		}
	}

	CommandLine buildCommandline(File file) throws MojoFailureException {
		CommandLine commandLine = new CommandLine(sqlplus);
		// logon only once, else it would prompt for credentials after failure
		commandLine.addArgument("-L");
		StringTokenizer stringTokenizer = new StringTokenizer(getConnectionIdentifier());
		while (stringTokenizer.hasMoreTokens()) {
			commandLine.addArgument(stringTokenizer.nextToken());
		}
		commandLine.addArgument("@" + file.getName());
		addSqlArguments(commandLine);
		
		getLog().info("Executing command line: " + obfuscateCredentials(commandLine, getCredentials()));

		return commandLine;
	}

	private void addSqlArguments(CommandLine commandLine) {
		if (arguments != null) {
			for (String argument : arguments) {
				commandLine.addArgument(argument);
			}
		}
	}

	Map getEnvVars() throws MojoExecutionException {
		if (beforeSql != null) {
			Map<Object, Object> envVars = copySystemEnvVars();
			File tmpSqlFile = printToTempFile(beforeSql);
			envVars.put("SQLPATH", tmpSqlFile.getParent());
			return envVars;
		} else {
			return null;
		}

	}

	private static Map<Object, Object> copySystemEnvVars() throws MojoExecutionException {
		try {
			return new HashMap<>(CommandLineUtils.getSystemEnvVars());
		} catch (IOException e) {
			throw new MojoExecutionException("Could not copy system environment variables.", e);
		}
	}

	private static File printToTempFile(String data) throws MojoExecutionException {
		File tmpSqlFile;
		try {
			tmpSqlFile = File.createTempFile("statement-", ".sql");
			try (OutputStreamWriter p = new OutputStreamWriter(new FileOutputStream(tmpSqlFile), UTF_8)) {
				p.write(data);
			}
		} catch (IOException e) {
			throw new MojoExecutionException("Could not write sql statements to file", e);
		}
		return tmpSqlFile;
	}

}
