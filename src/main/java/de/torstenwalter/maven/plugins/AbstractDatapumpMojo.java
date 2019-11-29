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

package de.torstenwalter.maven.plugins;

import org.apache.commons.exec.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.IOException;

abstract class AbstractDatapumpMojo extends AbstractDBMojo {

	/**
	 * Enables you to filter what is loaded during the import operation.
	 * options: [ALL | DATA_ONLY | METADATA_ONLY]
	 *
	 * @parameter default-value="ALL"
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
	 *
	 * @parameter default-value="NONE"
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
		exec.setStreamHandler(new PumpStreamHandler(System.out, System.err));

		getLog().debug("Executing command line: " + obfuscateCredentials(commandLine.toString(), getCredentials()));
		try {
			exec.execute(commandLine);
		} catch (ExecuteException e) {
			throw new MojoExecutionException("program exited with exitCode: " + e.getExitValue(), e);
		} catch (IOException e) {
			throw new MojoExecutionException("Command execution failed.", e);
		}
	}

	abstract CommandLine buildCommandline() throws MojoFailureException;

	void addCommonArguments(CommandLine commandLine)
			throws MojoFailureException {
		commandLine.addArgument("'" + getConnectionIdentifier() + "'", false);

		if (StringUtils.isNotEmpty(content)) {
			commandLine.addArgument("CONTENT=" + content);
		}

		if (StringUtils.isNotEmpty(directory)) {
			commandLine.addArgument("DIRECTORY=" + directory);
		}

		if (StringUtils.isNotEmpty(dumpfile)) {
			commandLine.addArgument("DUMPFILE=" + dumpfile);
		}

		if (StringUtils.isNotEmpty(exclude)) {
			commandLine.addArgument("EXCLUDE=" + exclude);
		}

		if (StringUtils.isNotEmpty(include)) {
			commandLine.addArgument("INCLUDE=" + include);
		}

		if (StringUtils.isNotEmpty(logfile)) {
			commandLine.addArgument("LOGFILE=" + logfile);
		}

		if (StringUtils.isNotEmpty(logtime)) {
			commandLine.addArgument("LOGTIME=" + logtime);
		}

		if (StringUtils.isNotEmpty(network_link)) {
			commandLine.addArgument("NETWORK_LINK=" + network_link);
		}

		if (StringUtils.isNotEmpty(schemas)) {
			commandLine.addArgument("SCHEMAS=" + schemas);
		}

		if (StringUtils.isNotEmpty(tables)) {
			commandLine.addArgument("TABLES=" + tables);
		}
	}

}
