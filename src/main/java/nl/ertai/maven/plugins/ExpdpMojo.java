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

import org.apache.commons.exec.CommandLine;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @goal expdp
 */
public class ExpdpMojo extends AbstractDatapumpMojo {

	/**
	 * The expdp command to execute. Optional. If not specified, expdp will be used.
	 *
	 * @parameter default-value="expdp"
	 */
	String expdp;

	/**
	 * Specifies whether to compress metadata before writing to the dump file set.
	 * options: [METADATA_ONLY | NONE]
	 * oracle-default: METADATA_ONLY
	 *
	 * @parameter
	 */
	String compression;

	/**
	 * Overwrite destination dump file if it exists
	 * options: [true | false]
	 * default: false
	 *
	 * @parameter
	 */
	boolean reuseDumpFiles;

	@Override
	CommandLine buildCommandline() throws MojoFailureException {
		CommandLine commandLine = new CommandLine(expdp);
		addCommonArguments(commandLine);

		addStringArgument(commandLine, "COMPRESSION", compression);
		addBooleanArgument(commandLine, "REUSE_DUMPFILES", reuseDumpFiles);
		return commandLine;
	}

}
