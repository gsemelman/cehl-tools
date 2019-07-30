package org.cehl.commons.sftp;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.cehl.commons.ftp.FtpFileAttributesImpl;
import org.cehl.commons.ftp.FtpFileEntry;
import org.cehl.commons.ftp.FtpFileEntryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public class SftpService implements Closeable{
	
	private static final Logger logger = LoggerFactory.getLogger(SftpService.class);
	
	
	private SshClient sshService;
	
	private ChannelSftp channel;
	
	public SftpService(SshClient sshService) throws SshException, JSchException {
		this.sshService = sshService;
		
		try {
			connect();	
		}catch( SshException | JSchException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	private void connect() throws SshException, JSchException {
		if(!sshService.isConnected()) {
			sshService.connect();
		}
		
		if(getChannel() == null || !channel.isConnected()) {
			Channel _channel = sshService.getSession().openChannel("sftp");
			_channel.connect();
			
			this.channel = (ChannelSftp) _channel;
		}
	}
	
	public ChannelSftp getChannel() {
		return channel;
	}
	
	public void cd(String path) throws URISyntaxException {
		URI uri = new URI(path);
		
		try {
			getChannel().cd(uri.getPath());
		} catch (SftpException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	public void listFiles(String path) throws URISyntaxException  {
		URI uri = new URI(path);
		
		try {
			getChannel().cd(path);
	
			Vector<LsEntry> directoryEntries = getChannel().ls(uri.getPath());
			for (LsEntry file : directoryEntries) {
				System.out.println(String.format("File - %s", file.getFilename()));
			}
		} catch (SftpException e) {
			throw new RuntimeException(e);
		}
		

	}
	
	public List<FtpFileEntry> listDir(String dir)
	{
		logger.debug("sftp ls [ dir:" + dir + "]");
		List<FtpFileEntry> fileEntryList = new ArrayList<FtpFileEntry>();
		
		try {
			
			if( channel == null || channel.isClosed() ) {
				try {
					connect();
				} catch (SshException | JSchException e) {
					throw new RuntimeException(e);
				}
			}
			
			Vector items =  channel.ls( dir);
			
		    if(items!=null){
			      for(int ii=0; ii<items.size(); ii++){

		                Object obj=items.elementAt(ii);
		                if(obj instanceof com.jcraft.jsch.ChannelSftp.LsEntry){
		                	
		                	ChannelSftp.LsEntry entry = ((com.jcraft.jsch.ChannelSftp.LsEntry)obj);
		                	FtpFileEntryImpl fileEntry = new FtpFileEntryImpl();
		                	fileEntry.setFilename( entry.getFilename() );
		                	fileEntry.setLongname( entry.getLongname() );
		                	
		                	FtpFileAttributesImpl fileAttributes = new FtpFileAttributesImpl();
		                	if( entry.getAttrs() != null )
		                	{
			                	fileAttributes.setATime( entry.getAttrs().getATime() );
			                	fileAttributes.setFlags( entry.getAttrs().getFlags() );
			                	fileAttributes.setGId( entry.getAttrs().getGId() );
			                	fileAttributes.setMTime( entry.getAttrs().getMTime() );
			                	fileAttributes.setPermissions( entry.getAttrs().getPermissions() );
			                	fileAttributes.setSize( entry.getAttrs().getSize() );
			                	fileAttributes.setUId( entry.getAttrs().getUId() );
			                	fileAttributes.setExtended( entry.getAttrs().getExtended() );
		                	
			                	fileEntry.setAttrs( fileAttributes );
		                	}
		                	logger.debug("File [" + fileEntry.toString() + "]" );
		                	//logger.debug("File ATime [" + fileEntry.getFileAttributes().getATimeAsDate() + "]" );
		                	//logger.debug("File MTime [" +  fileEntry.getFileAttributes().getMTimeAsDate() + "]" );
		                	
		                	
		                	fileEntryList.add( fileEntry );     	
		                }

			      }
		    }   
			
		}
		catch ( SftpException e ) {
			//don't throw an exception if the directory did not exist in first place
			if(2 != e.id) {
				logger.error("An sftp error occurred", e );
				throw new RuntimeException("sftp error", e);
			}
		}
		return fileEntryList;
	}
	
	
	
    /**
     * Uploads a file to the sftp server
     * @param sourceFile String path to sourceFile
     * @param destinationFile String path on the remote server
     * @throws InfinItException if connection and channel are not available or if an error occurs during upload.
     */
    public void uploadFile(String sourceFile, String destinationFile) {
        if (getChannel() == null || sshService.getSession() == null || !sshService.getSession().isConnected() || !getChannel().isConnected()) {
            //throw new InfinItException("Connection to server is closed. Open it first.");
        }
        
        try {
            logger.debug("Uploading file to server");
            getChannel().put(sourceFile, destinationFile);
            logger.info("Upload successfull.");
        } catch (SftpException e) {
          //  throw new InfinItException(e);
        }
    }

    /**
     * Retrieves a file from the sftp server
     * @param destinationFile String path to the remote file on the server
     * @param sourceFile String path on the local fileSystem
     * @throws InfinItException if connection and channel are not available or if an error occurs during download.
     */
    public void retrieveFile(String sourceFile, String destinationFile){
        if (getChannel() == null || sshService.getSession() == null || !sshService.getSession().isConnected() || !getChannel().isConnected()) {
            //throw new InfinItException("Connection to server is closed. Open it first.");
        }

        try {
            logger.debug("Downloading file to server");
            getChannel().get(sourceFile, destinationFile);
            logger.info("Download successfull.");
        } catch (SftpException e) {
           // throw new InfinItException(e.getMessage(), e);
        }
    }

	@Override
	public void close() throws IOException {
		if(channel!= null) {
			channel.disconnect();
		}
	}
	
}
