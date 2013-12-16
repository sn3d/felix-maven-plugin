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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.*;
import java.util.Map;
import java.util.Properties;

/**
 * The 'assembly' goal impl.
 *
 * @aggregator
 * @goal assembly
 * @author sn3d (vrabel.zdenko@gmail.com)
 */
public class AssemblyOsgiMojo extends AbstractOsgiMojo {

	/**
	 * @parameter default-value="${project.build.directory}/felix"
	 */
	protected File assemblyOutput;


	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		File felixPath = resolveBundleAsFile("maven:org.apache.felix:org.apache.felix.main:4.2.1");

		//create 'lib'
		File libDir = new File(assemblyOutput, "lib");
		this.copyFile(felixPath, new File(libDir, "felix.jar"));

		//create 'bundle'
		StringBuilder autoInstall = new StringBuilder(" ");
		File bundleDir = new File(assemblyOutput, "bundle");
		for (String bundle : this.bundles) {
			File bundlePath = resolveBundleAsFile(bundle);
			String bundleFileName = bundlePath.getName();
			copyFile(bundlePath, new File(bundleDir, bundleFileName));
			autoInstall.append(" \\\n file:bundle/" + bundleFileName);
		}

		//create configuration
		Properties felixConfig = new Properties();
		for (Map.Entry<String,String> param : this.osgiProperties.entrySet()) {
			felixConfig.put(param.getKey(), param.getValue());
		}

		felixConfig.put("felix.log.level", "1");
		felixConfig.put("felix.auto.deploy.action", "install,start");
		felixConfig.put("felix.auto.install.1", autoInstall.toString());
		felixConfig.put("felix.auto.start.1", autoInstall.toString());

		saveConfiguration(felixConfig, new File(assemblyOutput, "conf" + File.separator + "config.properties"));
	}


	private void saveConfiguration(Properties configuration, File outputFile) {
		OutputStream os = null;
		try {
			if (!outputFile.getParentFile().exists()) {
				outputFile.getParentFile().mkdirs();
			}

			if (!outputFile.exists()) {
				outputFile.createNewFile();
			}

			os = new FileOutputStream(outputFile);
			PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(os)));
			for (Map.Entry<Object, Object> e : configuration.entrySet()) {
				String key = e.getKey().toString();
				String value = e.getValue().toString();
				writer.print(key + "=" + value + "\n");
			}
			writer.flush();

		} catch (IOException e) {
			System.err.println("error create file " + outputFile.toString());
			e.printStackTrace();
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
