package org.cehl.cehltools.jobs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.cehl.cehltools.JobType;
import org.cehl.commons.ftp.FtpClient;
import org.cehl.commons.ftp.FtpFileEntry;
import org.cehl.commons.ftp.JschFtpClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UploadJob extends AbstractJob{
	
	private static final Logger logger = LoggerFactory.getLogger(UploadJob.class);
	
	private String host = "canadianelitehockeyleague.ca";
	private int port = 22;
	private String username = "";
	private String password = "";
	
	private String baseRemoteLocation = "/var/www/canadianelitehockeyleague.ca/public_html/";
	private String transferDir = "transfer/";
	private String filesDir = "gmo/files/";
	private String backFilesDir = "gmo/files/backup/";


	public UploadJob() {
		super(JobType.UPLOAD_FILES);
	}

	@Override
	public void _run() {
		
		JschFtpClientImpl ftpClient = new JschFtpClientImpl();

		try {

			ftpClient.connect(host, port, username, password);
			_doWork(ftpClient);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}finally {
			ftpClient.disconnect();
		}
	
		
	}
	
	private void _doWork(FtpClient ftpClient) throws IOException{

		uploadLeagueResults(ftpClient);
		uploadLeagueFiles(ftpClient);
		
	}
	
	private void uploadLeagueResults(FtpClient ftpClient) throws IOException {
		
		String remoteBaseLocation = baseRemoteLocation +  transferDir;
		
		Map<String, FtpFileEntry> fileMap = 
				ftpClient.listDir(baseRemoteLocation + transferDir)
				.stream().collect(Collectors.toMap(FtpFileEntry::getFilename, Function.identity()));
		
		File leagueTransfer = new File(super.getSimLocation(),transferDir);
		
		logger.info("--------------------------------------");
		logger.info("copying league transfer files to remote");
		logger.info("--------------------------------------");
		
		Files.list(Paths.get(leagueTransfer.getPath()))
        .filter(Files::isRegularFile)
        .forEach(path -> {
        	File file = path.toFile();
        	
        	if(file.isDirectory()) return;
        	
        	if(fileMap.containsKey(file.getName())){
        		Date localLastModified  = new Date(file.lastModified());
        		FtpFileEntry remoteFile = fileMap.get(file.getName());
        		
        		if(localLastModified.after(remoteFile.getFileAttributes().getMTimeAsDate())) {
        			logger.info("uploading file: " + file.getAbsolutePath());
        			logger.info("upload file: " + remoteBaseLocation + file.getName());
        			ftpClient.put(file.getAbsolutePath(), remoteBaseLocation + file.getName());
        		}
        		
        	}else {
        		logger.info("uploading file: " + file.getAbsolutePath());
        		logger.info("to remote path: " + remoteBaseLocation + file.getName());
        		ftpClient.put(file.getAbsolutePath(), remoteBaseLocation + file.getName());
        	}
        	
        	
        });
		
		logger.info("--------------------------------------");
		logger.info("copying league transfer files complete");
		logger.info("--------------------------------------");
	}
	
	private void uploadLeagueFiles(FtpClient ftpClient) throws IOException {
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = format.format(new Date());
		
		String remoteBaseLocation = baseRemoteLocation + filesDir;
		String remoteBackupLocation = baseRemoteLocation + backFilesDir  + dateString + "/";
		
		logger.info("upload league files: listing remote file data");
		Map<String, FtpFileEntry> fileMap = 
				ftpClient.listDir(baseRemoteLocation + filesDir)
				.stream().collect(Collectors.toMap(FtpFileEntry::getFilename, Function.identity()));
		
		//check if files have been modified. 
		//backup all league files if they have and copy new ones.
		boolean filesModified = false;
		for(File leaguefile : super.getLeagueFiles()) {
		  	if(fileMap.containsKey(leaguefile.getName())){
		  		
        		Date localLastModified  = new Date(leaguefile.lastModified());
        		FtpFileEntry remoteFile = fileMap.get(leaguefile.getName());
        		
        		if(localLastModified.after(remoteFile.getFileAttributes().getMTimeAsDate())) {
        			filesModified = true;
        			break;
        		}
        		
        	}
		}

		if(filesModified) {
			
			logger.info("--------------------------------------");
			logger.info("backing up league files on remote");
			logger.info("--------------------------------------");
			
			for(FtpFileEntry remoteFile : fileMap.values()) {

			    if( remoteFile.getFileAttributes().isDirectory()) {
			    	continue;
			    }

				logger.info("backup up file: " + remoteBaseLocation + remoteFile.getFilename());
				logger.info("to: " + remoteBackupLocation + remoteFile.getFilename());
				
				ftpClient.remoteToRemoteCopy(
						remoteBaseLocation + remoteFile.getFilename(), 
						remoteBackupLocation + remoteFile.getFilename());
			}
			
			logger.info("--------------------------------------");
			logger.info("backup complete");
			logger.info("--------------------------------------");
			
			
			logger.info("--------------------------------------");
			logger.info("copying league files to remote");
			logger.info("--------------------------------------");
				
			Arrays.stream(super.getLeagueFiles()).forEach(file -> {

	        	if(fileMap.containsKey(file.getName())){
	        		Date localLastModified  = new Date(file.lastModified());
	        		FtpFileEntry remoteFile = fileMap.get(file.getName());
	        		
	        		if(localLastModified.after(remoteFile.getFileAttributes().getMTimeAsDate())) {
	        			logger.info("uploading file: " + file.getAbsolutePath());
	        			logger.info("upload file: " + remoteBaseLocation + file.getName());
	        			ftpClient.put(file.getAbsolutePath(), remoteBaseLocation + file.getName());
	        		}
	        		
	        	}else {
	        		logger.info("uploading file: " + file.getAbsolutePath());
	        		logger.info("upload file: " + remoteBaseLocation + file.getName());
	        		ftpClient.put(file.getAbsolutePath(), remoteBaseLocation + file.getName());
	        	}
			});
		}else {
			logger.info("--------------------------------------");
			logger.info("Files not modified, back not required");
			logger.info("--------------------------------------");
		}

		logger.info("--------------------------------------");
		logger.info("copying league files complete");
		logger.info("--------------------------------------");
	}
	
	private void uploadLegacyFiles(FtpClient ftpClient) throws IOException {
		
	}

	@Override
	public String getJobInfo() {
		return "Upload league files";
	}
	
	@Override
	public boolean isBackupLeagueFiles() {
		return false;
	}

}
