package org.cehl.commons.sftp;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public final class SshClient implements Closeable{
	
	private static final Logger logger = LoggerFactory.getLogger(SshClient.class);
	
	private String hostName = null;
	private int port = 22;
	private String sshHostName = null;
	private int sshPort = 22;
	private int localPort = 0;
	private String sshUserName = null;
	private String sshPassword = null;
	private String sshKeyFile = null;
	private String sshPassphrase = null;
	private boolean strictHostCheck = false;
	private boolean enableLoopback = true;
	
	private Session session = null;
	
	private SshClient(SshServiceBuilder builder) {
		hostName = builder.hostName;
		port = builder.port;
		sshHostName = builder.sshHostName;
		sshPort = builder.sshPort;
		localPort = builder.localPort;
		sshUserName = builder.sshUserName;
		sshPassword = builder.sshPassword;
		sshKeyFile = builder.sshKeyFile;
		sshPassphrase = builder.sshPassphrase;
		strictHostCheck = builder.strictHostCheck;
		enableLoopback= builder.enableLoopback;
	}
	
	public void connect() throws SshException {
		try {
    		final JSch jsch = new JSch();
    		if (sshKeyFile != null) {
    			if (sshPassphrase != null && !sshPassphrase.trim().equals("")) {
    				jsch.addIdentity(sshKeyFile);
    			}
    			else {
    				jsch.addIdentity(sshKeyFile, sshPassphrase);
    			}
    		}
    		
    		Properties config = new Properties();
    		if(!strictHostCheck) {
    			config.put("StrictHostKeyChecking", "no");
    		}
    			
    		session = jsch.getSession(sshUserName, sshHostName, sshPort);
    		session.setConfig(config);
    		
    		if (sshKeyFile == null) {
    			session.setPassword(sshPassword);
    		}
    		
    		session.connect();
    		
    		if(enableLoopback) {
    			session.setPortForwardingL(localPort, hostName, port);
    		}
    		
		}
		catch (Exception e) {
			if (doesPortForwardingAlreadyExist(e))
				session = null;
			else
				throw new SshException(e);
		}
	}
	
	public Session getSession() {
		 return session;
	}
	
	public Channel getChannel() throws JSchException {
		Channel channel = getSession().openChannel("sftp");
		channel.connect();
		
		return channel;
	}

	public boolean isConnected() {
		return session != null
				&& session.isConnected();
	}
	
	public void disconnect() throws SshException {
		try {
			if (session != null) {
				session.disconnect();
			}
		}
		catch (Exception e) {
			throw new SshException(e);
		}
	}
	
	@Override
	public void close() throws IOException {
		try {
			disconnect();
		} catch (SshException e) {
			logger.warn("error closing sftp session", e);
		}
	}
	
	public static boolean doesPortForwardingAlreadyExist(Throwable e) {
		String alreadyBindMessage = "Address already in use".toLowerCase();

		if (!isBlank(e.getMessage())) {
			String message = e.getMessage().toLowerCase();
			if (message.contains(alreadyBindMessage)) {
				return true;
			}
		}

		if (e.getCause() != null) {
			return doesPortForwardingAlreadyExist(e.getCause());
		}

		return false;
	}
	
	public static boolean isBlank(String str) {
		return str == null || str.trim().equals("");
	}
	


	public static class SshServiceBuilder {
		private String hostName = null;
		private int port = 22;
		private String sshHostName = null;
		private int sshPort = 22;
		private int localPort = 0;
		private String sshUserName = null;
		private String sshPassword = null;
		private String sshKeyFile = null;
		private String sshPassphrase = null;
		private boolean strictHostCheck = false;
		private boolean enableLoopback = true;
		
		private SshServiceBuilder() {			
		}
		
		public static SshServiceBuilder create() {
			return new SshServiceBuilder();
		}
		
		public SshServiceBuilder hostName(String hostName) {
			this.hostName = hostName;
			return this;
		}
		
		public SshServiceBuilder port(int port) {
			this.port = port;
			return this;
		}
		
		public SshServiceBuilder sshHostName(String sshHostName) {
			this.sshHostName = sshHostName;
			return this;
		}
		
		public SshServiceBuilder sshPort(int sshPort) {
			this.sshPort = sshPort;
			return this;
		}
		
		public SshServiceBuilder localPort(int localPort) {
			this.localPort = localPort;
			return this;
		}
		
		public SshServiceBuilder sshUserName(String sshUserName) {
			this.sshUserName = sshUserName;
			return this;
		}
		
		public SshServiceBuilder sshPassword(String sshPassword) {
			this.sshPassword = sshPassword;
			return this;
		}
		
		public SshServiceBuilder sshKeyFile(String sshKeyFile) {
			this.sshKeyFile = sshKeyFile;
			return this;
		}
		
		public SshServiceBuilder sshPassphrase(String sshPassphrase) {
			this.sshPassphrase = sshPassphrase;
			return this;
		}
		
		public SshServiceBuilder strictHostCheck(boolean strictHostCheck) {
			this.strictHostCheck = strictHostCheck;
			return this;
		}
		
		public SshServiceBuilder enableLoopback(boolean enableLoopback) {
			this.enableLoopback = enableLoopback;
			return this;
		}
		
		
		public SshClient build() {
			return new SshClient(this);
		}




	}


}