package org.cehl.commons.ftp;

import java.util.List;

public interface FtpClient {
	
	public void setHost( String host );
	public void setPort( int serverPort );
	public void setUsername( String userName );
	public void setPassword( String password );
	public void connect();
	public void connect( String host, int port, String username, String password );
	public void connect( String host, String username, String password );
	public void disconnect();
	public void put( String src, String target );
	public void get( String src, String target );
	public void deleteFile(String target);
	public List<FtpFileEntry> listDir(String dir);

	public void mkdir(String dir);
	public void rmdir(String dir);
	
	public void put( String src, String target, FtpMonitor monitor );
	public void get( String src, String target, FtpMonitor monitor );
	
	public String getHost();
	public int getPort();
	public String getUsername();
	public String getPassword();
	
	public void quit();
	
	public void setRootDirectory( String directory );
	public String getRootDirectory();

}
