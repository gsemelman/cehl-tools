package org.cehl.cehltools.jobs;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.cehl.cehltools.JobType;
import org.cehl.commons.SimFileType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.google.common.collect.Lists;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;



public abstract class AbstractJob implements Job {
	
	private static final Logger logger = Logger.getLogger(AbstractJob.class);
	
	@Autowired
	@Qualifier("simLocation")
	private File simLocation;
	
	@Autowired
	@Qualifier("backupLocation")
	private File backupLocation;
	
	@Autowired
	@Qualifier("baseInputLocation")
	private File baseInputLocation;
	
	@Autowired
	@Qualifier("baseOutputLocation")
	private File baseOutputLocation;
	
	@Autowired
	@Qualifier("leaguePrefix")
	private String leaguePrefix;
	
	private File[] leagueFiles;
	private List<String> errorMessages = new ArrayList<String>();
	private JobType jobType;
	
	private boolean backupLeagueFiles = true;
	
	public abstract void _run();
	public abstract String getJobInfo();
	
	private boolean jobSuccess = false;
	private boolean filesOutputted = false;
	
	
	
	public AbstractJob(JobType jobType){
		this.jobType = jobType;
	}
	
	public void runJob(){
		logger.info("------------------------------------------------------");
		logger.info("Job: " + jobType);
		logger.info("------------------------------------------------------");
		logger.info("Job Description: " + getJobInfo());
		logger.info("------------------------------------------------------");
		
		try{
			getLeagueFiles();
			
			logger.info("Pre Validating Job..");
			List<String> validationMessages = preValidation();
			if(!validationMessages.isEmpty()){
				errorMessages.addAll(validationMessages);
				return;
			}
			
			logger.info("Clearing output directory..");
			this.clearOldFiles();
			
			if(backupLeagueFiles){
				logger.info("Backing up league files to [ " + backupLocation.getAbsolutePath() +"]");
				this.backupLeagueFiles() ;
			}else{
				logger.info("League files backup skipped [backupLeagueFiles=false]");
			}
			
			logger.info("Running job [" + jobType + "]");
			this._run();
			jobSuccess = true;
			
		} catch (ZipException e) {
			logger.error("Job failed due to zip error. Unable to backup files.");
			throw new RuntimeException(e);
		} catch (IOException e) {
			logger.error("Job failed, unable to cleanup old files.");
			throw new RuntimeException(e);
		}catch(Exception e){
			logger.error("Unknown error running job");
			throw new RuntimeException(e);
		}finally{
			logger.info("------------------------------------------------------");
			if(errorMessages.isEmpty() && jobSuccess){
				logger.info("Job: " + this.getClass().getSimpleName() + " Complete");
						
			}else{
				logger.error("Job: "+ this.getClass().getSimpleName() + " Failed");
				logger.error("There was one or more errors while running job:");
				for(String message : errorMessages){	
					logger.error(message);
					
				}
			}
			logger.info("------------------------------------------------------");
		}
	
	}
	
	
	List<String> preValidation(){
		List<String> messages = new ArrayList<String>();
		
		if(!simLocation.exists()){
			messages.add("Cannot find simulator at location:[" + simLocation.getAbsolutePath() + "]" );
			return messages;
		}
		
		if(getLeagueFiles().length == 0){
			messages.add("Cannot find league files with prefix:" + "[" + leaguePrefix +"]" 
					+ " at location: [" + simLocation.getAbsolutePath() + "]" );
		}else if(getLeagueFiles().length != 14){
			//messages.add("Leagues files not complete. expected 15, got " + getLeagueFiles().length);
		}

		messages.addAll(additionalPreValidation());
		
		return messages;
	}
	
	public void clearOldFiles() throws IOException{
		try{
			FileUtils.cleanDirectory(baseOutputLocation);
		}catch(IOException e){
			this.addMessage("There was an error cleaning the ouput directory at [" + baseOutputLocation + "] ");
			this.addMessage("Please close any applications using files in this location");
			throw e;
		}
		
	};
	
	void init(){
		
	}
	
	void backupLeagueFiles() throws ZipException{
		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		File fileToOutput = new File(backupLocation.getAbsolutePath(),"cehl-files-" + timeStamp + ".zip");
		ZipFile zipFile = new ZipFile(fileToOutput.getAbsolutePath());
		
		ZipParameters parameters = new ZipParameters();
		
		zipFile.createZipFile(Lists.newArrayList(leagueFiles), parameters);
		
		additionalBackupSteps();
		
	}
	
	public File[] getLeagueFiles(){
		if(leagueFiles == null){
			
			File[] leagueFiles = simLocation.listFiles(new FilenameFilter() {
			    public boolean accept(File dir, String name) {
			    	logger.debug("file:" +  name.toLowerCase());
			        return name.toLowerCase().startsWith(leaguePrefix.toLowerCase() + ".");
			    }
			});
			
			this.leagueFiles = leagueFiles;
		}

		return this.leagueFiles;
	}
	
	public File getLeagueFileByType(SimFileType fileType){
		for(File file : leagueFiles){
			if(file.getName().endsWith(fileType.getExtension())){
				return file;
			}
		}
		
		return null;
	}
	
	public String getFileNameStringByType(SimFileType fileType){
		return leaguePrefix + "." + fileType.getExtension();
	}
	
	public void addMessage(String message){
		errorMessages.add(message);
	}
	
	public void addMessages(Collection<String> messages){
		for(String message : messages){
			addMessage(message);
		}
	}
	
	public boolean isJobSuccess(){
		return this.jobSuccess;
	}
	public boolean jobContainsErrors(){
		return !this.errorMessages.isEmpty();
	}


	public File getSimLocation() {
		return simLocation;
	}


	public void setSimLocation(File simLocation) {
		this.simLocation = simLocation;
	}


	public File getBackupLocation() {
		return backupLocation;
	}


	public void setBackupLocation(File backupLocation) {
		this.backupLocation = backupLocation;
	}


	public File getBaseInputLocation() {
		return baseInputLocation;
	}


	public void setBaseInputLocation(File baseInputLocation) {
		this.baseInputLocation = baseInputLocation;
	}


	public String getLeaguePrefix() {
		return leaguePrefix;
	}


	public void setLeaguePrefix(String leaguePrefix) {
		this.leaguePrefix = leaguePrefix;
	}


	public void setLeagueFiles(File[] leagueFiles) {
		this.leagueFiles = leagueFiles;
	}


	public File getBaseOutputLocation() {
		return baseOutputLocation;
	}


	public void setBaseOutputLocation(File baseOutputLocation) {
		this.baseOutputLocation = baseOutputLocation;
	}
	public boolean isFilesOutputted() {
		return filesOutputted;
	}
	public void setFilesOutputted(boolean filesOutputted) {
		this.filesOutputted = filesOutputted;
	}
	public boolean isBackupLeagueFiles() {
		return backupLeagueFiles;
	}
	public void setBackupLeagueFiles(boolean backupLeagueFiles) {
		this.backupLeagueFiles = backupLeagueFiles;
	}

	
}
