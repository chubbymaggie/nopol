package fr.inria.lille.commons.utils.library;

import static java.util.Arrays.asList;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedList;
import java.util.List;

public class JavaLibrary {

	public static String systemClasspath() {
		return property("java.class.path");
	}

	public static URL[] systemURLsClasspath() {
		return classpathFrom(systemClasspath());
	}
	
	public static void extendSystemClasspathWith(URL[] classpaths) {
		StringBuilder newClasspath = new StringBuilder(systemClasspath());
		for (URL classpath : classpaths) {
			newClasspath.append(classpathSeparator() +  classpath.getPath()); 
		}
		setClasspath(newClasspath.toString());
	}
	
	public static URL[] extendClasspathWith(String classpath, URL[] destination) {
		List<URL> extended = new LinkedList<URL>(asList(destination));
		extended.addAll(asList(classpathFrom(classpath)));
		return extended.toArray(new URL[extended.size()]);
	}
	
	public static URL[] classpathFrom(String classpath) {
		List<String> folderNames = StringLibrary.split(classpath, classpathSeparator());
		URL[] folders = new URL[folderNames.size()];
		int index = 0;
		for (String folderName : folderNames) {
			folders[index] = FileLibrary.urlFrom(folderName);
			index += 1;
		}
		return folders;
	}
	
	public static void setClasspath(String newClasspath) {
		setProperty("java.class.path", newClasspath);
	}
	
	public static void extendSystemClassLoaderClasspathWith(URL[] classpaths) {
		extendClassLoaderClasspathWith((URLClassLoader) ClassLoader.getSystemClassLoader(), classpaths);
	}
	
	public static void extendClassLoaderClasspathWith(URLClassLoader classLoader, URL[] classpaths) {
		Method method = methodFrom(URLClassLoader.class, "addURL", URL.class);
		if (method != null) {
			method.setAccessible(true);
			try {
				for (URL classpath : classpaths) {
					method.invoke(classLoader, classpath);
				}
			} catch (Exception e) {
				throw new RuntimeException("Failed to extend classpath on class loader with " + classpaths);
			} finally {
				method.setAccessible(false);
			}
		}
	}
	
	public static Method methodFrom(Class<?> aClass, String methodName, Class<?>... argumentClasses) {
		Method method = null;
		try {
			method = aClass.getDeclaredMethod(methodName, argumentClasses);
		} catch (NoSuchMethodException nsme) {
			nsme.printStackTrace();
		}
		return method;
	}
	
	public static Character classpathSeparator() {
		if (javaPathSeparator == null) {
			javaPathSeparator = File.pathSeparatorChar;
		}
		return javaPathSeparator;
	}
	
	public static Character folderPathSeparator() {
		if (javaFolderSeparator == null) {
			javaFolderSeparator = File.separatorChar;
		}
		return javaFolderSeparator;
	}
	
	public static String lineSeparator() {
		if (javaLineSeparator == null) {
			javaLineSeparator = property("line.separator");
		}
		return javaLineSeparator;
	}
	
	private static String property(String key) {
		return System.getProperty(key);
	}
	
	private static void setProperty(String key, String value) {
		System.setProperty(key, value);
	}
	
	private static String javaLineSeparator;
	private static Character javaPathSeparator;
	private static Character javaFolderSeparator;
}