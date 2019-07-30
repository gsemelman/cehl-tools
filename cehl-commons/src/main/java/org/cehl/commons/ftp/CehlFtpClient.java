package org.cehl.commons.ftp;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CehlFtpClient implements Closeable{

	private static final Logger logger = LoggerFactory.getLogger(CehlFtpClient.class);
	 
    private String server;
    private int port;
    private String user;
    private String password;
    private FTPSClient ftp;
    
    private CehlFtpClient() {
    	
    }

    public CehlFtpClient(String server, int port, String user, String password) {
		super();
		this.server = server;
		this.port = port;
		this.user = user;
		this.password = password;
	}

 
    public void open() throws IOException {
        ftp = new FTPSClient();
 
        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
 
        ftp.connect(server, port);
        int reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            throw new IOException("Exception in connecting to FTP Server");
        }
 
        ftp.login(user, password);
        
    }
    
    public void changeWorkingDir(String path) throws IOException {
        ftp.changeWorkingDirectory(path);
    }

	public void close() {
    	if(ftp!= null) {
            try {
				ftp.disconnect();
			} catch (IOException e) {
				logger.warn("Unable to disconnect from FTP", e);
			}
    	}
    }
    
    private void checkState() {
    	if(!ftp.isConnected()) {
    		throw new IllegalStateException("FTP is not connected, unable to complete operation");
    	}
    }
    
    public Collection<String> listFiles(String path) throws IOException {
    	checkState();
    
        FTPFile[] files = ftp.listFiles(path);
        return Arrays.stream(files)
          .map(FTPFile::getName)
          .collect(Collectors.toList());
    }
    
    public Collection<FTPFile> listFiles2(String path) throws IOException {
    	checkState();
    
    	FTPFile[] files = ftp.listFiles(path);
    	
        return Arrays.stream(files).collect(Collectors.toList());
    }

    
    public boolean downloadFile(String source, String destination) throws IOException {
    	checkState();
    	
        FileOutputStream out = new FileOutputStream(destination);
        
        return ftp.retrieveFile(source, out);

    }
    
	public void downloadDirectory(String parentDir, String currentDir, String saveDir) throws IOException {
	    String dirToList = parentDir;
	    if (!currentDir.equals("")) {
	        dirToList += "/" + currentDir;
	    }
	 
	    FTPFile[] subFiles = ftp.listFiles(dirToList);
	 
	    if (subFiles != null && subFiles.length > 0) {
	        for (FTPFile aFile : subFiles) {
	            String currentFileName = aFile.getName();
	            if (currentFileName.equals(".") || currentFileName.equals("..")) {
	                // skip parent directory and the directory itself
	                continue;
	            }
	            String filePath = parentDir + "/" + currentDir + "/"
	                    + currentFileName;
	            if (currentDir.equals("")) {
	                filePath = parentDir + "/" + currentFileName;
	            }
	 
	            String newDirPath = saveDir + parentDir + File.separator
	                    + currentDir + File.separator + currentFileName;
	            if (currentDir.equals("")) {
	                newDirPath = saveDir + parentDir + File.separator
	                          + currentFileName;
	            }
	 
	            if (aFile.isDirectory()) {
	                // create the directory in saveDir
	                File newDir = new File(newDirPath);
	                boolean created = newDir.mkdirs();
	                if (created) {
	                    System.out.println("CREATED the directory: " + newDirPath);
	                } else {
	                	throw new IOException(String.format("Could not create local directory. File Path: %s", newDirPath));
	                }
	 
	                // download the sub directory (via recursion)
	                downloadDirectory(dirToList, currentFileName, saveDir);
	            } else {
	                // download the file
	                boolean success = downloadFile(filePath,
	                        newDirPath);
	                if (success) {
	                    System.out.println("DOWNLOADED the file: " + filePath);
	                } else {
	                	throw new IOException(String.format("Unable to download file. Remote File Path: %s", filePath));
	                }
	            }
	        }
	    }
	}
    
    void putFileToPath(File file, String path) throws IOException {
    	checkState();
    	
        ftp.storeFile(path, new FileInputStream(file));
    }


	public String getServer() {
		return server;
	}


	public void setServer(String server) {
		this.server = server;
	}


	public int getPort() {
		return port;
	}


	public void setPort(int port) {
		this.port = port;
	}


	public String getUser() {
		return user;
	}


	public void setUser(String user) {
		this.user = user;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


    
}
