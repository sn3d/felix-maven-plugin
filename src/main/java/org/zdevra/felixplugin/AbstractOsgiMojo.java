/*****************************************************************************
 * Copyright 2013 Zdenko Vrabel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *****************************************************************************/
package org.zdevra.felixplugin;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Map;

/**
 * This is the common class that provide you access to maven common parameters. The
 * felix plugin goals are extending this class.
 *
 * @author sn3d (vrabel.zdenko@gmail.com)
 */
public abstract class AbstractOsgiMojo extends AbstractMojo {

	//------------------------------------------------------------------------------------------------------------------
	// Parameters
	//------------------------------------------------------------------------------------------------------------------

	/**
	 * System propeties
	 * @parameter
	 */
	protected Map<String, String> systemProperties;

	/**
	 * Apache Felix OSGI propeties
	 * @parameter
	 */
	protected Map<String, String> osgiProperties;

	/**
	 * Apache Felix configuration file. The properties could be overriden by properties
	 * you specified in 'osgiProperties'.
	 * @parameter
	 */
	protected Resource configFile;

	/**
	 * list of bundle URIs. It's same URI you type in felix when you're doing install.
	 * The scheme 'maven' is special case when bundle is resolved via maven.
	 * @parameter
	 */
	protected String[] bundles;

	/**
	 * Apache felix cache folder
	 * @parameter default-value="${project.build.directory}/osgi-cache"
	 */
	protected String cache;


	//------------------------------------------------------------------------------------------------------------------
	// methods
	//------------------------------------------------------------------------------------------------------------------

	/**
	 * method resolve 'maven:' bundles via maven resolver mechanism and
	 * returns path to file in local repository.
	 *
	 * @param mavenDependency
	 * @return
	 */
	protected String resolveBundle(String mavenDependency) {
		String dependency = mavenDependency.substring("maven:".length());
		File[] file = Maven.resolver().resolve(dependency).withoutTransitivity().asFile();
		return "file:" + file[0].getAbsolutePath();
	}


	/**
	 * method resolve 'maven:' bundles via maven resolver mechanism and
	 * returns path to file in local repository.
	 *
	 * @param mavenDependency
	 * @return
	 */
	protected File resolveBundleAsFile(String mavenDependency) {
		String dependency = mavenDependency.substring("maven:".length());
		File[] file = Maven.resolver().resolve(dependency).withoutTransitivity().asFile();
		return file[0];
	}


	/**
	 * Copy file from source to destination
	 */
	protected void copyFile(File source, File dest) {
		FileChannel sourceCh = null;
		FileChannel destCh = null;
		try {
			if (!dest.getParentFile().isDirectory()) {
				dest.getParentFile().mkdirs();
			}
			dest.createNewFile();
			sourceCh = new FileInputStream(source).getChannel();
			destCh = new FileOutputStream(dest).getChannel();
			destCh.transferFrom(sourceCh, 0, sourceCh.size());
		} catch (IOException e) {
			throw new IllegalStateException("Cannot copy " + source.getAbsolutePath() + " to " + dest.getAbsolutePath());
		} finally {
			try {
				if (sourceCh != null) {
					sourceCh.close();
				}
				if (destCh != null) {
					destCh.close();
				}
			} catch (IOException e2) {
				throw new IllegalStateException("Cannot close the channel", e2);
			}
		}
	}

}
