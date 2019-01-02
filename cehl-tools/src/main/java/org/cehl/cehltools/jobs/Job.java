package org.cehl.cehltools.jobs;

import java.util.ArrayList;
import java.util.List;

public interface Job {
	 default public List<String> additionalPreValidation(){
		 return new ArrayList<String>();
	 }
	 
	 default public void additionalBackupSteps(){
		
	 }
}
