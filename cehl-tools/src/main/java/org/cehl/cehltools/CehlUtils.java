package org.cehl.cehltools;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

import org.cehl.commons.SimFileType;

public class CehlUtils {
	public static File[] getLeagueFiles(File simLocation, String leaguePrefix){
		File[] leagueFiles = simLocation.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.toLowerCase().startsWith(leaguePrefix.toLowerCase() + ".");
		    }
		});

		return leagueFiles;
	}
	
	public static File getLeagueFileByType(File simLocation, String leaguePrefix, SimFileType fileType){
		for(File file : getLeagueFiles(simLocation,leaguePrefix)){
			if(file.getName().endsWith(fileType.getExtension())){
				return file;
			}
		}
		
		return null;
	}

	
	public static String getFileNameStringByType(String leaguePrefix, SimFileType fileType){
		return leaguePrefix + "." + fileType.getExtension();
	}
	
	static <T> T[] append(T[] arr, T element) {
	    final int N = arr.length;
	    arr = Arrays.copyOf(arr, N + 1);
	    arr[N] = element;
	    return arr;
	}
}
