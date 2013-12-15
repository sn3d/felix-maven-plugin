package org.zdevra.felixplugin;

import org.apache.commons.io.FileUtils;
import org.apache.felix.framework.FrameworkFactory;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;

import java.io.*;
import java.util.Map;
import java.util.Properties;

/**
 * @aggregator
 * @goal run
 */
public class RunOsgiMojo extends AbstractOsgiMojo {


	@Override
	public void execute() throws MojoExecutionException, MojoFailureException
	{
		try {
			//setup system properties
			for (Map.Entry<String, String> property : this.systemProperties.entrySet()) {
				System.setProperty(property.getKey(), property.getValue());
			}

			//load configuration
			Properties config = createConfiguration();
			cleanCache();

			//create OSGi framework
			FrameworkFactory frameworkFactory = getOsgiFrameworkFactory();
			Framework framework = frameworkFactory.newFramework(config);
			framework.start();

			//install and start bundles
			BundleContext context = framework.getBundleContext();
			for (String bundle : this.bundles) {
				if (bundle.startsWith("maven:")) {
					bundle = resolveBundle(bundle);
				}
				context.installBundle(bundle).start();
			}

			//start and go into infinite loop
			framework.waitForStop(0);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * create Felix configuration from plugin 'configuration'
	 *
	 * @return 'felix' properties
	 */
	private Properties createConfiguration() throws IOException
	{
		//configuration
		Properties config = new Properties();

		if (this.configFile != null) {
			InputStream is = new FileInputStream(this.configFile.getTargetPath());
			config.load(is);
		}

		config.setProperty(Constants.FRAMEWORK_STORAGE, this.cache);

		if (this.osgiProperties != null) {
			for (Map.Entry<String,String> param : this.osgiProperties.entrySet()) {
				config.setProperty(param.getKey(), param.getValue());
			}
		}

		return config;
	}


	/**
	 * method remove Felix cache
	 *
	 * @throws IOException
	 */
	private void cleanCache() throws IOException
	{
		File cacheDir = new File(this.cache);
		if (cacheDir.exists()) {
			FileUtils.deleteDirectory(cacheDir);
		}
	}


	/**
	 * method returns OSGi framework factory needed for start
	 *
	 */
	private FrameworkFactory getOsgiFrameworkFactory() throws Exception
	{
		java.net.URL url =
				this.getClass().getClassLoader().getResource("META-INF/services/org.osgi.framework.launch.FrameworkFactory");

		if (url == null) {
			throw new Exception("Could not find framework factory.");
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
		try {
			for (String s = br.readLine(); s != null; s = br.readLine()) {
				s = s.trim();
				if ((s.length() > 0) && (s.charAt(0) != '#')) {
					return (FrameworkFactory) Class.forName(s).newInstance();
				}
			}
		} finally {
			if (br != null) br.close();
		}

		throw new Exception("Could not find framework factory.");
	}
}
