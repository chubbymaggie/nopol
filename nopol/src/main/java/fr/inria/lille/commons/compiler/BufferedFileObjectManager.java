package fr.inria.lille.commons.compiler;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardLocation;

import fr.inria.lille.commons.collections.IterableLibrary;
import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.io.FileHandler;

public class BufferedFileObjectManager extends ForwardingJavaFileManager<JavaFileManager> {

	protected BufferedFileObjectManager(DynamicallyCompiledClassLoader classLoader, JavaFileManager fileManager) {
		super(fileManager);
		this.classLoader = classLoader;
		sourceFiles = MapLibrary.newHashMap();
	}
	
	@Override
	public FileObject getFileForInput(Location location, String packageName, String relativeName) throws IOException {
		URI fileURI = uriFor(location, packageName, relativeName);
		if (containsSourceFileFor(fileURI)) {
			return sourceFile(fileURI);
		}
		return super.getFileForInput(location, packageName, relativeName);
	}
	
	@Override
	public JavaFileObject getJavaFileForOutput(Location location, String qualifiedName, Kind kind, FileObject outputFile) throws IOException {
		BufferedClassFileObject classFile = new BufferedClassFileObject(qualifiedName, kind);
		classLoader().addClassFileObject(qualifiedName, classFile);
		return classFile;
	}
	
	@Override
	public String inferBinaryName(Location loc, JavaFileObject file) {
		if (BufferedSourceFileObject.class.isInstance(file) || BufferedClassFileObject.class.isInstance(file)) {
			return file.getName();
		}
		return super.inferBinaryName(loc, file);
	}
	
	@Override
	public Iterable<JavaFileObject> list(Location location, String packageName, Set<Kind> kinds, boolean recurse) throws IOException {
		Iterable<JavaFileObject> result = super.list(location, packageName, kinds, recurse);
		List<JavaFileObject> files = ListLibrary.newLinkedList();
		if (location == StandardLocation.CLASS_PATH && kinds.contains(JavaFileObject.Kind.CLASS)) {
			for (JavaFileObject file : sourceFiles().values()) {
				if (file.getKind() == Kind.CLASS && file.getName().startsWith(packageName)) {
					files.add(file);
				}
			}
			files.addAll(classLoader().compiledClasses());
		} else if (location == StandardLocation.SOURCE_PATH && kinds.contains(JavaFileObject.Kind.SOURCE)) {
			for (JavaFileObject file : sourceFiles().values()) {
				if (file.getKind() == Kind.SOURCE && file.getName().startsWith(packageName)) {
					files.add(file);
				}
			}
		}
		IterableLibrary.addTo(files, result);
		return files;
	}

	public void addSourceFile(Location location, String packageName, String simpleClassName, BufferedSourceFileObject sourceFile) {
		URI fileURI = uriFor(location, packageName, simpleClassName);
		sourceFiles().put(fileURI, sourceFile);
	}
	
	public boolean containsSourceFileFor(URI fileURI) {
		return sourceFiles().containsKey(fileURI);
	}
	
	public BufferedSourceFileObject sourceFile(URI fileURI) {
		return sourceFiles().get(fileURI);
	}

	public DynamicallyCompiledClassLoader classLoader() {
		return classLoader;
	}
	
	private URI uriFor(Location location, String packageName, String simpleClassName) {
		String uriScheme = location.getName() + '/' + packageName + '/' + simpleClassName + ".java";
		return FileHandler.uriFrom(uriScheme);
	}
	
	private Map<URI, BufferedSourceFileObject> sourceFiles() {
		return sourceFiles;
	}
	
	private DynamicallyCompiledClassLoader classLoader;
	private Map<URI, BufferedSourceFileObject> sourceFiles;
}
