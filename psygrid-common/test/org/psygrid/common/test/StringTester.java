package org.psygrid.common.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;


class JavaFilter implements FilenameFilter {
    public boolean accept(File dir, String name) {
        return (name.endsWith(".java"));
    }
}

class DirectoryFilter implements FileFilter {

	public boolean accept(File pathname) {
		// TODO Auto-generated method stub
		return pathname.isDirectory();
	}
	
}

class JavaFileLoader{
	
	public class Directory{
		
		private File[] files;
		
		public Directory(File[] files){
			this.files = files;
		}

		public File[] getFiles() {
			return files;
		}
	}

	String searchDirectory;
	boolean loadRecursively;
	
	public JavaFileLoader(String directory, boolean loadRecursively){
		searchDirectory = directory;
		this.loadRecursively = loadRecursively; 
	}
	
	public List<Directory> getJavaFiles(){
		List<Directory> directoriesWithJavaFiles = new ArrayList<Directory>();
		return getJavaFiles(searchDirectory, directoriesWithJavaFiles);
	}
	
	private List<Directory> getJavaFiles(String searchDirectory, List<Directory> directoryList){
		
		if(loadRecursively){
			
			//Find out if this folder has any sub-folders. If it does, call the
			//method recursively for each folder.
			File f1 = new File(searchDirectory);
			FileFilter dirFilter = new DirectoryFilter();
			File[] files = f1.listFiles(dirFilter);
			
			if(files != null){
				for(int i = 0; i < files.length; i++){
					String newSearchPath = files[i].getAbsolutePath();
					getJavaFiles(newSearchPath, directoryList);
				}
			}

		}
		
		//If there are any java files in here, 
		//create a Directory object for this directory, and then add
		//this to the list.
		
		File f1 = new File(searchDirectory);
	    FilenameFilter filter = new JavaFilter();
	    File[] files = f1.listFiles(filter);
	    
	    if(files != null && files.length > 0){
	    	directoryList.add(new Directory(files));
	    }
	    
	    return directoryList;
		
	}
}


public class StringTester {
	
	private StringReaders readers = new StringReaders();
	
	public StringTester(){
	}
	
	
	/**
	 * This is a collection of string readers (of type MethodInvoker).
	 * This follows the OpenCDMS string convention that a package usually has its own 'local' reader,
	 * which loads strings from a message.properties' file' also local. BUT on occasion a package may use
	 * a reader from another package.
	 * 
	 * So this class stores multiple readers. When 'readString' is called, the strategy is to call
	 * try to read the token from each specified reader, in the order they were specified.
	 * 
	 * @author williamvance
	 *
	 */
	private class StringReaders{
		
		private List<MethodInvoker> invokers = null;
		
		public StringReaders(){
			invokers = new ArrayList<MethodInvoker>();
		}
		
		public StringReaders(List<MethodInvoker> stringReaders){
			this.invokers = stringReaders;
		}
		
		public void addStringReader(MethodInvoker stringReader){
			invokers.add(stringReader);
		}
		
		/**
		 * Tries to read the string property from the string readers encapsulated by this class.
		 * Reads them in the order in which they are stored in the list, starting at index 0.
		 * Return the string as soon as it is found.
		 * 
		 * If the string is not found by any of the readers, it throws an InvocationTargetException
		 * 
		 * @param stringProperty
		 * @return the string located by one of the readers.
		 * @throws InvocationTargetException 
		 */
		public String readString(String stringProperty) throws InvocationTargetException {
				
			for(MethodInvoker inv: invokers){
				try {
					Object returnObj = inv.callMethod(null, new Object[] {stringProperty});
					return (String) returnObj;
					
				} catch (IllegalArgumentException e) {
					
				} catch (IllegalAccessException e) {
					
				} catch (InvocationTargetException e) {
					
				}
			}
			
			throw new InvocationTargetException(null, "Property: " + stringProperty + " was not found.");
		}
	};
	

	/**
	 * @param args
	 * @throws IOException 
	 */
	
	/**
	 * The first argument specifies the directory in which the .java files
	 * that need string testing reside. This is the BASE directory in which all
	 * packages for the project are stored. It should end with a forward slash.
	 * (e.g. '/home/williamvance/workspace2/psygrid-data-collection/src/')
	 * 
	 * The second argument specifies the package name of the java files we want to inspect.
	 * (If '*' is specified, it means to inspect ALL).
	 * 
	 * The third argument is the method name to call on the string loader class.
	 * 
	 * The fourth argument is the name of the method called in the java files being searched.
	 * 
	 * The fourth argument specifies the class of the EXTERNAL string loader 
	 * (one located in another package) to try and use.
	 * This can be null if one is not used. 
	 * 
	 * NOTE: It is assumed that the default loader is
	 * called 'Messages' and resides within the package being tested.
	 * (e.g. 'org.psygrid.collection.entry.EntryMessages'). 
	 * 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		boolean testAllFolders = false;
		
		String dir = args[0];
		String defaultStringReaderPackage = args[1];
		String methodName = args[2];
		String javaFileSearchString = args[3];
		String externalLoader = args[4];
		if(defaultStringReaderPackage.equals("*")){
			testAllFolders = true;
		}
		
		String directoryToExplore;
		if(!testAllFolders){
			String stringReaderPath = defaultStringReaderPackage.replace(".", "/");
			directoryToExplore = dir.concat(stringReaderPath);
		}else{
			directoryToExplore = dir;
		}
		
		JavaFileLoader fileLoader = new JavaFileLoader(directoryToExplore, testAllFolders);
		List<JavaFileLoader.Directory> javaFiles = fileLoader.getJavaFiles();
		
		int problemCount = 0;
		for(JavaFileLoader.Directory directory : javaFiles){
			
			File[] files = directory.getFiles();
			String absolutePath = files[0].getAbsolutePath();
			String fileName = files[0].getName();
			absolutePath.lastIndexOf(fileName);
			absolutePath = absolutePath.substring(0, absolutePath.lastIndexOf(fileName)-1);
			
			String packageName = getPackageName(dir, absolutePath);
			
			StringTester.StringReaders stringReaders = initStringReaders(packageName, externalLoader, methodName);
		    problemCount += testJavaFiles(files, stringReaders, javaFileSearchString);
		}
		
		Integer probCountInt = new Integer(problemCount);
		System.out.println(probCountInt + " strings were encountered that could not be loaded.");
	    
	}
	
	/**
	 * 
	 * @param baseDirectory
	 * @param subDirectory
	 * @return
	 */
	public static String getPackageName(String baseDirectory, String subDirectory){
		String packageName = subDirectory.substring(baseDirectory.length());
		packageName = packageName.replace("/", ".");
		return packageName;
	}
	
	/**
	 * Initializes the string readers that will be used to read the string properties extracted from the .java files.
	 * @param defaultStringReaderPackage - the package of the default string resolver (name is assumed to be 'Messages')
	 * @param externalLoader - the fully qualified name of the external string resolver
	 * @param methodName - the method to call on the external string resolver class
	 * @return
	 */
	public static StringTester.StringReaders initStringReaders(String defaultStringReaderPackage, String externalLoader, String methodName){
		
	    //Now try loading the string loader classes.
	    boolean localLoaderLoaded = false;
	    boolean remoteLoaderLoaded = false;
	    Class[] arguments = new Class[1];
		try {
			arguments[0] = Class.forName("java.lang.String");
		} catch (ClassNotFoundException e1) {
			//This will not happen.
		}
		MethodInvoker localMethodInvoker = null, remoteMethodInvoker = null;
		StringReaders stringReaders = null;
	    
	    try{
		    Class subject = Class.forName(defaultStringReaderPackage + ".Messages");
			localMethodInvoker = new MethodInvoker(subject, methodName, arguments);
			localLoaderLoaded = true;
	    }catch (ClassNotFoundException e){
	    	System.out.println("Cannot test strings loaded by: " + defaultStringReaderPackage + ".Messages" + " - class not found.");
	    }catch (IllegalArgumentException e){
	    	System.out.println("Cannot test strings loaded by: " + defaultStringReaderPackage + ".Messages" + " - method: " + methodName + " not found.");
	    }
	    
	    if(externalLoader != null){
	    	try{
	    		Class subject = Class.forName(externalLoader);
				remoteMethodInvoker = new MethodInvoker(subject, methodName, arguments);
				remoteLoaderLoaded = true;
	    	}catch (ClassNotFoundException e){
	    		System.out.println("Cannot test strings loaded by: " + externalLoader + " - class not found.");
	    	}catch (IllegalArgumentException e){
	    		System.out.println("Cannot test strings loaded by: " + externalLoader + " - method: " + methodName + " not found.");
	    	}
	    	
	    }
	    
	    if(!remoteLoaderLoaded && !localLoaderLoaded){
	    	System.out.println("Test failed. No String loaders found.");
	    	return null;
	    }else{
	    	ArrayList<MethodInvoker> stringLoaderList = new ArrayList<MethodInvoker>();
	    	if(localMethodInvoker != null){
	    		stringLoaderList.add(localMethodInvoker);
	    	}
	    	
	    	if(remoteMethodInvoker != null){
	    		stringLoaderList.add(remoteMethodInvoker);
	    	}
	    	
	    	StringTester tester = new StringTester();
	    	stringReaders = tester.getReaders();
	    	
	    	for(MethodInvoker reader: stringLoaderList){
	    		stringReaders.addStringReader(reader);
	    	}
	    }
	    
	    return stringReaders;
		
	}
	
	public static int testJavaFiles(File[] javaFiles, final StringTester.StringReaders readers, String javaFileSearchString) throws IOException{
		int problemCount = 0;
	    for(int k = 0; k < javaFiles.length; k++){
	    	int lineCount = 0;
	    	String stringToken = null;
	    	 
			BufferedReader jamOnIt = new BufferedReader(new FileReader(javaFiles[k]));
			String line = jamOnIt.readLine();
			
			while(line != null){
			
			//we are looking for getString.
			String startToken = javaFileSearchString + "(\"";
			String endToken = "\")";
			
			int startTokenLocation = line.indexOf(startToken);
			int endTokenLocation; 
			
			if(startTokenLocation >=0){
				endTokenLocation = line.indexOf(endToken, startTokenLocation);
				stringToken = line.substring(startTokenLocation + javaFileSearchString.length() + 2, endTokenLocation);
				
				try{
					Object theString = readers.readString(stringToken);
					//System.out.println("Success finding string for " + stringToken);
				}catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}catch (InvocationTargetException e) {
					problemCount++;
					printProblem(javaFiles[k].getName(), lineCount+1, stringToken);
				} 
				
			}
				
			line = jamOnIt.readLine();
			lineCount++;
			}
	     }
	    
	    return problemCount;
		
	}
	

	
	static void printProblem(String fileName, Integer lineNumber, String stringToken){
		System.out.println("Could not locate '" + stringToken + "' on line " + lineNumber.toString() + " of file: " + fileName + ".");
	}

	public StringReaders getReaders() {
		return readers;
	}

}
