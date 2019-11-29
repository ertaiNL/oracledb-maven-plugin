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
import org.apache.commons.exec.LogOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;

abstract class AbstractDBMojo extends AbstractMojo {

	/**
	 * User name for your database.
	 *
	 * @parameter
	 */
	String username;

	/**
	 * Password for your database.
	 *
	 * @parameter
	 */
	String password;

	/**
	 * It is also possible to specify user name and password for the database in your settings.xml
	 * The <code>serverId</code> specified here is the reference to the server with the same <code>id</code> in your
	 * settings.xml
	 *
	 * @parameter
	 */
	String serverId;

	/**
	 * The {@link Settings} object.
	 *
	 * @parameter default-value="${settings}"
	 * @required
	 * @readonly
	 */
	Settings settings;

	/**
	 * Host name of your database server.
	 *
	 * @parameter default-value="localhost"
	 * @required
	 */
	String hostname;

	/**
	 * Port for your database server.
	 *
	 * @parameter default-value="1521"
	 * @required
	 */
	int port;

	/**
	 * The serviceName of your oracle database instance.
	 *
	 * @parameter
	 * @required
	 */
	String serviceName;

	/**
	 * The instanceName of your oracle database instance, commonly used in Oracle RAC databases with multiple instances.
	 *
	 * @parameter
	 */
	String instanceName;

	/**
	 * Specify role which should be used in the "as" clause in the connection
	 * identifier. Possible values: SYSOPER and SYSDBA. Other values are
	 * ignored.
	 *
	 * @parameter
	 */
	String asClause;

	/**
	 * Specify connection string style which should be used ("connect_identifier" can be in the form of Net Service
	 * Name or Easy Connect)
	 * options: [true | false]
	 *
	 * @parameter default-value="false"
	 */
	boolean useEasyConnect;

	AbstractDBMojo() {
		super();
	}

	Credentials getCredentials() throws MojoFailureException {
		if (!StringUtils.isEmpty(serverId)) {
			getLog().info("using credentials from serverId '" + serverId + "'");
			Server server = settings.getServer(serverId);
			if (server == null) {
				throw new MojoFailureException("serverId '" + serverId + "' not found!");
			}
			return new Credentials(server.getUsername(), server.getPassword());
		} else if (!StringUtils.isEmpty(username)) {
			return new Credentials(username, password);
		} else {
			throw new MojoFailureException("Credentials needed. Specify either username and password or serverId");
		}
	}

	/**
	 * @see http://docs.oracle.com/cd/B19306_01/server.102/b14357/toc.htm
	 *      (SQL*Plus User's Guide and Reference)
	 * 
	 * @param credentials
	 * @return
	 */
	private String getConnectionIdentifier(Credentials credentials) {
		StringBuilder connectionIdentifier = new StringBuilder();
		// fist add the username
		connectionIdentifier.append(credentials.getUsername());
		// then add the password if given
		if (!StringUtils.isEmpty(credentials.getPassword())) {
			connectionIdentifier.append("/").append(credentials.getPassword());
		}

		// now add the connect_identifier:
		if (!useEasyConnect) {
    		// To make it more robust and to not to rely on TNSNAMES we specify the
    		// full connect identifier like:
    		// (DESCRIPTION=
    		// (ADDRESS=(PROTOCOL=tcp)(HOST=host)(PORT=port) )
    		// (CONNECT_DATA=
    		// (SERVICE_NAME=service_name) ) )
    		connectionIdentifier
    				.append("@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=tcp)(HOST=")
    				.append(hostname).append(")(PORT=").append(port)
    				.append(")))(CONNECT_DATA=(SERVICE_NAME=").append(serviceName).append(")");
    				if (!StringUtils.isEmpty(instanceName)){
    					connectionIdentifier.append("(INSTANCE_NAME=").append(instanceName).append(")");
    				}
    					connectionIdentifier.append("))");
		} else {
		    // "[//]Host[:Port]/<service_name>"
            connectionIdentifier.append("@//").append(hostname).append(":").append(port).append("/").append(serviceName);
		}
		// add as clause if necessary
		if (StringUtils.equalsIgnoreCase(asClause, "SYSDBA")
				|| StringUtils.equalsIgnoreCase(asClause, "SYSOPER")) {
			connectionIdentifier.append(" AS ").append(
					StringUtils.upperCase(asClause));
		}

		return connectionIdentifier.toString();
	}

	String getConnectionIdentifier() throws MojoFailureException {
		return getConnectionIdentifier(getCredentials());
	}

	String obfuscateCredentials(CommandLine cmd, Credentials credentials) {
		String replaced = StringUtils.replaceOnce(cmd.toString(), credentials.getUsername(), "<username>");
		return StringUtils.replaceOnce(replaced, credentials.getPassword(), "<password>");
	}

	class ErrorLogOutputStream extends LogOutputStream {
		@Override
		protected void processLine(String s, int i) {
			getLog().error(s);
		}
	}

	class InfoLogOutputStream extends LogOutputStream {
		@Override
		protected void processLine(String s, int i) {
			getLog().info(s);
		}
	}


}
