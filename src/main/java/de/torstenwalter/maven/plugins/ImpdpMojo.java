/**
 * Copyright 2012 Torsten Walter
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

import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @goal impdp
 */
public class ImpdpMojo extends AbstractDatapumpMojo {

	/**
	 * The impdp command to execute. Optional. If not specified, impdp will be used.
	 *
	 * @parameter default-value="impdp"
	 */
	String impdp;

	/**
	 * Remaps all objects selected for import with persistent data in the source tablespace to be created in the target
	 * tablespace.
	 *
	 * @parameter
	 */
	String remap_tablespace;

	/**
	 * Loads all objects from the source schema into a target schema.
	 *
	 * @parameter
	 */
	String remap_schema;

	/**
	 * Tells Import what to do if the table it is trying to create already exists.
	 * options: [SKIP | APPEND | TRUNCATE | REPLACE]
	 *
	 * @parameter default-value="SKIP (Note that if CONTENT=DATA_ONLY is specified, the default is APPEND, not SKIP.)"
	 */
	String table_exists_action;

	public void execute() throws MojoExecutionException, MojoFailureException {
		CommandLine commandLine = buildCommandline();

		Executor exec = new DefaultExecutor();
		exec.setStreamHandler(new PumpStreamHandler(System.out, System.err));
		try {
			exec.execute(commandLine);
		} catch (IOException e) {
			throw new MojoExecutionException("Command execution failed.", e);
		}
	}

	CommandLine buildCommandline() throws MojoFailureException {
		CommandLine commandLine = new CommandLine(impdp);
		addCommonArguments(commandLine);

		if (StringUtils.isNotEmpty(remap_tablespace)) {
			commandLine.addArgument("REMAP_TABLESPACE=" + remap_tablespace);
		}

		if (StringUtils.isNotEmpty(remap_schema)) {
			commandLine.addArgument("REMAP_SCHEMA=" + remap_schema);
		}

		if (StringUtils.isNotEmpty(table_exists_action)) {
			commandLine.addArgument("TABLE_EXISTS_ACTION=" + table_exists_action);
		}

		getLog().info("Executing command line: " + obfuscateCredentials(commandLine.toString(), getCredentials()));
		return commandLine;
	}

}
