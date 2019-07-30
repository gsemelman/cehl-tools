package org.cehl.cehltools.jobs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
	private String username = "root";
	private String password = "";
	
	private String baseRemoteLocation = "/var/www/test.canadianelitehockeyleague.ca/public_html";
	private String transferDir = "transfer";
	private String filesDir = "gmo/files";

	public UploadJob() {
		super(JobType.UPLOAD_FILES);
	}

	@Override
	public void _run() {
		
		JschFtpClientImpl ftpClient = new JschFtpClientImpl();

		try {

			ftpClient.connect(host, username, password);
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
		Map<String, FtpFileEntry> fileMap = 
				ftpClient.listDir(baseRemoteLocation + "/" + transferDir)
				.stream().collect(Collectors.toMap(FtpFileEntry::getFilename, Function.identity()));
		
		File leagueTransfer = new File(super.getSimLocation(),transferDir);

		Files.list(Paths.get(leagueTransfer.getPath()))
        .filter(Files::isRegularFile)
        .forEach(path -> {
        	File file = path.toFile();
        	
        	if(file.isDirectory()) return;
        	
        	if(fileMap.containsKey(file.getName())){
        		Date localLastModified  = new Date(file.lastModified());
        		FtpFileEntry remoteFile = fileMap.get(file.getName());
        		
        		if(localLastModified.after(remoteFile.getFileAttributes().getMTimeAsDate())) {
        			logger.info("upload file: " + baseRemoteLocation + "/" + transferDir + "/" + file.getName());
        			ftpClient.put(file.getAbsolutePath(), baseRemoteLocation + "/" + transferDir + "/" + file.getName());
        		}
        		
        	}else {
        		logger.info("upload file: " + baseRemoteLocation + "/" + transferDir + "/" + file.getName());
        		ftpClient.put(file.getAbsolutePath(), baseRemoteLocation + "/" + transferDir + "/" + file.getName());
        	}
        	
        	
        });
	}
	
	private void uploadLeagueFiles(FtpClient ftpClient) {
		Map<String, FtpFileEntry> fileMap = 
				ftpClient.listDir(baseRemoteLocation + "/" + filesDir)
				.stream().collect(Collectors.toMap(FtpFileEntry::getFilename, Function.identity()));
		
		Arrays.stream(super.getLeagueFiles()).forEach(file -> {

        	if(fileMap.containsKey(file.getName())){
        		Date localLastModified  = new Date(file.lastModified());
        		FtpFileEntry remoteFile = fileMap.get(file.getName());
        		
        		if(localLastModified.after(remoteFile.getFileAttributes().getMTimeAsDate())) {
        			logger.info("upload file: " + baseRemoteLocation + "/" + filesDir + "/" + file.getName());
        			ftpClient.put(file.getAbsolutePath(), baseRemoteLocation + "/" + filesDir + "/" + file.getName());
        		}
        		
        	}else {
        		logger.info("upload file: " + baseRemoteLocation + "/" + filesDir + "/" + file.getName());
        		ftpClient.put(file.getAbsolutePath(), baseRemoteLocation + "/" + filesDir + "/" + file.getName());
        	}
		});
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
