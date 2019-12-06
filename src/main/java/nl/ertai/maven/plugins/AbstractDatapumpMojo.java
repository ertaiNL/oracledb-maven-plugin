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

import org.apache.commons.exec.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.IOException;

abstract class AbstractDatapumpMojo extends AbstractDBMojo {

	/**
	 * Enables you to filter what is loaded during the import operation.
	 * options: [ALL | DATA_ONLY | METADATA_ONLY]
	 * oracle-default: ALL
	 *
	 * @parameter
	 */
	String content;

	/**
	 * Specifies the default location in which the import job can find the dump file set and where it should create
	 * log and SQL files.
	 *
	 * @parameter
	 */
	String directory;

	/**
	 * Specifies the names and optionally, the directory objects of the dump file set that was created by Export.
	 *
	 * @parameter
	 */
	String dumpfile;

	/**
	 * Enables you to filter the metadata that is imported by specifying objects and object types to exclude from the
	 * import job.
	 *
	 * @parameter
	 */
	String exclude;

	/**
	 * Enables you to filter the metadata that is imported by specifying objects and object types for the current
	 * import mode.
	 *
	 * @parameter
	 */
	String include;

	/**
	 * Specifies the name, and optionally, a directory object, for the log file of the import job.
	 *
	 * @parameter
	 */
	String logfile;

	/**
	 * Specifies that messages displayed during import operations be timestamped. You can use the timestamps to figure
	 * out the elapsed time between different phases of a Data Pump operation. Such information can be helpful in
	 * diagnosing performance problems and estimating the timing of future similar operations.
	 * options: [NONE | STATUS | LOGFILE | ALL]
	 * oracle-default: NONE
	 *
	 * @parameter
	 */
	String logtime;

	/**
	 * Enables an import from a (source) database identified by a valid database link. The data from the source
	 * database instance is written directly back to the connected database instance.
	 *
	 * @parameter
	 */
	String network_link;

	/**
	 * Specifies that a schema-mode import is to be performed.
	 *
	 * @parameter
	 */
	String schemas;

	/**
	 * Specifies that you want to perform a table-mode import.
	 *
	 * @parameter
	 */
	String tables;

	AbstractDatapumpMojo() {
		super();
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		CommandLine commandLine = buildCommandline();

		Executor exec = new DefaultExecutor();
		exec.setStreamHandler(new PumpStreamHandler(new InfoLogOutputStream(), new ErrorLogOutputStream()));

		getLog().debug("Executing command line: " + obfuscateCredentials(commandLine, getCredentials()));
		try {
			exec.execute(commandLine);
		} catch (ExecuteException e) {
			if (failOnError) {
				throw new MojoExecutionException("program exited with exitCode: " + e.getExitValue(), e);
			} else {
				getLog().warn("program exited with exitCode: " + e.getExitValue());
			}
		} catch (IOException e) {
			throw new MojoExecutionException("Command execution failed.", e);
		}
	}

	abstract CommandLine buildCommandline() throws MojoFailureException;

	void addCommonArguments(CommandLine commandLine)
			throws MojoFailureException {
		commandLine.addArgument("'" + getConnectionIdentifier() + "'", false);

		addStringArgument(commandLine, "CONTENT", content);
		addStringArgument(commandLine, "DIRECTORY", directory);
		addStringArgument(commandLine, "DUMPFILE", dumpfile);
		addStringArgument(commandLine, "EXCLUDE", exclude);
		addStringArgument(commandLine, "INCLUDE", include);
		addStringArgument(commandLine, "LOGFILE", logfile);
		addStringArgument(commandLine, "LOGTIME", logtime);
		addStringArgument(commandLine, "NETWORK_LINK", network_link);
		addStringArgument(commandLine, "SCHEMAS", schemas);
		addStringArgument(commandLine, "TABLES", tables);
	}

	void addStringArgument(CommandLine commandLine, String argumentName, String argumentValue) {
		if (StringUtils.isNotEmpty(argumentValue)) {
			commandLine.addArgument(argumentName + "=" + argumentValue);
		}
	}

	void addBooleanArgument(CommandLine commandLine, String argumentName, boolean argumentValue) {
		if (argumentValue) {
			commandLine.addArgument(argumentName + "=YES");
		}
	}

}
