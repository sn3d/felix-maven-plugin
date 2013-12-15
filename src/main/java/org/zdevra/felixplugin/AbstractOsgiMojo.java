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
 *
 */
public abstract class AbstractOsgiMojo extends AbstractMojo {

	/**
	 * @parameter
	 */
	protected Map<String, String> systemProperties;

	/**
	 * @parameter
	 */
	protected Map<String, String> osgiProperties;

	/**
	 * @parameter
	 */
	protected Resource configFile;

	/**
	 * @parameter
	 */
	protected String[] bundles;

	/**
	 * @parameter default-value="${project.build.directory}/osgi-cache"
	 */
	protected String cache;


	/**
	 * method resolve 'maven:' bundles via maven resolver mechanism and
	 * returns path to file in local repository.
	 *
	 * @param mavenDependency
	 * @return
	 */
	protected String resolveBundle(String mavenDependency)
	{
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
	protected File resolveBundleAsFile(String mavenDependency)
	{
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
