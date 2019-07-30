package org.cehl.commons.ftp;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * The Class JschFtpClientImpl.
 */
public class JschFtpClientImpl implements FtpClient{
	private static final Logger logger = LoggerFactory.getLogger(JschFtpClientImpl.class);
	
	JschFtpClientSyncModeImpl client = new JschFtpClientSyncModeImpl();
	private long operationTimeout = 5400000; //for put/get

	/* (non-Javadoc)
	 * @see com.openlead.io.ftp.client.FtpClient#connect()
	 */
	public void connect() {
		client.connect();
		
	}

	/* (non-Javadoc)
	 * @see com.openlead.io.ftp.client.FtpClient#connect(java.lang.String, int, java.lang.String, java.lang.String)
	 */
	public void connect(String host, int port, String username, String password) {
		client.connect(host,port,username,password);
		
	}

	/* (non-Javadoc)
	 * @see com.openlead.io.ftp.client.FtpClient#connect(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void connect(String host, String username, String password) {
		client.connect(host,username,password);
		
	}

	/* (non-Javadoc)
	 * @see com.openlead.io.ftp.client.FtpClient#deleteFile(java.lang.String)
	 */
	public void deleteFile(String target) {
		client.deleteFile(target);
		
	}

	/* (non-Javadoc)
	 * @see com.openlead.io.ftp.client.FtpClient#disconnect()
	 */
	public void disconnect() {
		client.disconnect();
		
	}

	/* (non-Javadoc)
	 * @see com.openlead.io.ftp.client.FtpClient#get(java.lang.String, java.lang.String)
	 */
	public void get(String src, String dst) {
		client.get(src, dst);
		
	}

	/* (non-Javadoc)
	 * @see com.openlead.io.ftp.client.FtpClient#get(java.lang.String, java.lang.String, com.openlead.io.ftp.client.FtpMonitor)
	 */
	public void get(String src, String dst, FtpMonitor monitor) {
		AbstractTransferWorker w = new AbstractTransferWorker(src,dst,monitor){
			@Override
			public void work() {
				client.get(this.src, this.dst, this.monitor);
			}
			
		};
		_work(w);
	
	}
	
	/* (non-Javadoc)
	 * @see com.openlead.io.ftp.client.FtpClient#put(java.lang.String, java.lang.String, com.openlead.io.ftp.client.FtpMonitor)
	 */
	public void put(String src, String dst, FtpMonitor monitor) {
		AbstractTransferWorker w = new AbstractTransferWorker(src,dst,monitor){
				@Override
				public void work() {
					client.put(this.src, this.dst, this.monitor);
				}
				
				
		};

		_work(w);

	}
	
	/**
	 * _work.
	 *
	 * @param prototypeWorker the prototype worker
	 */
	protected synchronized void _work(AbstractTransferWorker prototypeWorker){
		int oldRetries = client.getRetries();
		try{
			client.setRetries(0);  //turn off retry

			int retries = 0;
			boolean complete = false;
			
			while(!complete){
				GetPutBase delegateWorker = new GetPutBase(prototypeWorker);
				delegateWorker.start();
				try {
					delegateWorker.join(operationTimeout);
				} catch (InterruptedException e) {
				}
				complete = delegateWorker.completed;
				if(!complete){
					delegateWorker.printStack();
					
					client.quit();
					retries++;
	
					if(retries <=2){
						JschFtpClientSyncModeImpl.logger.debug("GET/PUT timed out, retrying [" + retries +"]...");
						client.sleep( client.getRetryWaitTime() );
					}
					else{
						throw new RuntimeException("sftp error, GET/PUT timed out.");
					}
				}
			}
		}finally{
			client.setRetries(oldRetries);
		}
	}

	/* (non-Javadoc)
	 * @see com.openlead.io.ftp.client.FtpClient#getHost()
	 */
	public String getHost() {
		return client.getHost();
	}

	/* (non-Javadoc)
	 * @see com.openlead.io.ftp.client.FtpClient#getPassword()
	 */
	public String getPassword() {
		return client.getPassword();
	}

	/* (non-Javadoc)
	 * @see com.openlead.io.ftp.client.FtpClient#getPort()
	 */
	public int getPort() {
		return client.getPort();
	}

	/* (non-Javadoc)
	 * @see com.openlead.io.ftp.client.FtpClient#getUsername()
	 */
	public String getUsername() {
		return client.getUsername();
	}

	/* (non-Javadoc)
	 * @see com.openlead.io.ftp.client.FtpClient#listDir(java.lang.String)
	 */
	public List<FtpFileEntry> listDir(String dir) {
		return client.listDir(dir);
	}

	/* (non-Javadoc)
	 * @see com.openlead.io.ftp.client.FtpClient#mkdir(java.lang.String)
	 */
	public void mkdir(String dir) {
 
		client.mkdir(dir);
	}

	/* (non-Javadoc)
	 * @see com.openlead.io.ftp.client.FtpClient#put(java.lang.String, java.lang.String)
	 */
	public void put(String src, String dst) {
		client.put(src, dst);
		
	}


	/* (non-Javadoc)
	 * @see com.openlead.io.ftp.client.FtpClient#quit()
	 */
	public void quit() {
		client.quit();
	}

	/* (non-Javadoc)
	 * @see com.openlead.io.ftp.client.FtpClient#rmdir(java.lang.String)
	 */
	public void rmdir(String dir) {
		client.rmdir(dir);
	}

	/* (non-Javadoc)
	 * @see com.openlead.io.ftp.client.FtpClient#setHost(java.lang.String)
	 */
	public void setHost(String host) {
		client.setHost(host);
	}

	/* (non-Javadoc)
	 * @see com.openlead.io.ftp.client.FtpClient#setPassword(java.lang.String)
	 */
	public void setPassword(String password) {
		client.setPassword(password);
	}

	/* (non-Javadoc)
	 * @see com.openlead.io.ftp.client.FtpClient#setPort(int)
	 */
	public void setPort(int serverPort) {
		client.setPort(serverPort);
	}

	/* (non-Javadoc)
	 * @see com.openlead.io.ftp.client.FtpClient#setUsername(java.lang.String)
	 */
	public void setUsername(String userName) {
		client.setUsername(userName);
	
	}
	
	/**
	 * Sets the retries.
	 *
	 * @param retries the new retries
	 */
	public void setRetries(int retries)
	{
		client.setRetries(retries);
	}
	
	/**
	 * Gets the retries.
	 *
	 * @return the retries
	 */
	public int getRetries()
	{
		return client.getRetries();
	}
	
	/**
	 * The Class AbstractTransferWorker.
	 */
	abstract class AbstractTransferWorker {
		String src;
		String dst;
		FtpMonitor monitor;
		
		/**
		 * Instantiates a new abstract transfer worker.
		 *
		 * @param src the src
		 * @param dst the dst
		 * @param monitor the monitor
		 */
		public AbstractTransferWorker(String src, String dst, FtpMonitor monitor){
			this.src = src;
			this.dst = dst;
			this.monitor = monitor;
		}
		
		/**
		 * Work.
		 */
		public abstract void work();
	}
	
	/**
	 * The Class GetPutBase.
	 */
	class GetPutBase extends Thread{
		boolean completed = false;
		AbstractTransferWorker worker;
		
		/**
		 * Instantiates a new gets the put base.
		 *
		 * @param worker the worker
		 */
		public GetPutBase(AbstractTransferWorker worker){
			this.worker = worker;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run(){
			worker.work();
			completed = true;
		}

		/**
		 * Prints the stack.
		 */
		public void printStack(){
			StringBuilder sb = new StringBuilder("Sftp thread timed out , current stack --- ");
			for(StackTraceElement ste : getStackTrace()){
				sb.append("\n      ").append(ste.getClassName()).append(":").append(ste.getMethodName()).append(":").append(ste.getLineNumber()); 
			}
			JschFtpClientSyncModeImpl.logger.debug(sb.toString());
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Thread#clone()
		 */
		public GetPutBase clone(){
			return new GetPutBase(worker);
		}

	}

	/* (non-Javadoc)
	 * @see com.openlead.io.ftp.client.FtpClient#setRootDirectory(java.lang.String)
	 */
	@Override
	public void setRootDirectory(String directory) {
		// Not used in this implementation
	}

	/* (non-Javadoc)
	 * @see com.openlead.io.ftp.client.FtpClient#getRootDirectory()
	 */
	@Override
	public String getRootDirectory() {
		// Not used in this implementation
		return null;
	}
}

class JschFtpClientSyncModeImpl implements FtpClient{
	//static final Logger logger = Logger.getLogger(JschFtpClientImpl.class);
	static final Logger logger = LoggerFactory.getLogger(JschFtpClientImpl.class);
	
	String host;
	String username;
	int port = 22; // default
	int sessionTimeout = 30000; // timeout
	int retries = 3; // default
	int retryWaitTime = 30000; // default
	String password;
	boolean strictHostChecking = false; // default
	
	private ChannelSftp channel;
	
	
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isStrictHostChecking() {
		return strictHostChecking;
	}

	public void setStrictHostChecking(boolean strictHostChecking) {
		this.strictHostChecking = strictHostChecking;
	}
	
	public int getSessionTimeout() {
		return sessionTimeout;
	}

	public void setSessionTimeout(int sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

	public int getRetries() {
		return retries;
	}

	public void setRetries(int retries) {
		this.retries = retries;
	}

	public int getRetryWaitTime() {
		return retryWaitTime;
	}

	public void setRetryWaitTime(int retryWaitTime) {
		this.retryWaitTime = retryWaitTime;
	}

	public void connect( String host, String username, String password )
	{
		connect( host, this.port, username, password );
	}
	
	public void connect( String host, int port, String username, String password )
	{
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		
		connect();
	}
	
	public void connect()
	{
		boolean connected = false;
		int retries = 0;
		
		while( !connected )
		{
			try
			{
				_connect();
				connected = true;
			}
			catch( Throwable e )
			{
				retries++;
				logger.error("A connection error occurred", e );
				
				if( retries <= getRetries() )
				{				
					logger.debug("Waiting [" + getRetryWaitTime() + "] milliseconds before retry..." );
					sleep( getRetryWaitTime() );
					logger.debug("Retry starting...");
				}
				else
				{
					throw new RuntimeException("Connection Error", e );
				}
			}
		}
		
	}
	
	public void _connect() throws JSchException
	{

		  logger.debug("Connecting to [" + this.host + ":" + this.port + "]...");
    	  JSch jsch=new JSch();

	      Session session=jsch.getSession(this.username, this.host, this.port);

	      // username and password will be given via UserInfo interface.
	      //UserInfo ui=new MyUserInfo();
	      //session.setUserInfo(ui);
	      session.setPassword(this.password);
	      if( !isStrictHostChecking() )
	      {
	    	  session.setConfig( getConfigProperties( "StrictHostKeyChecking", "no"));
	      }
	      session.setTimeout( getSessionTimeout() );
	      session.connect();

	      Channel c=session.openChannel("sftp");
	      c.connect();
	      channel=(ChannelSftp)c;
	      logger.debug("Connected");
	}
	
	public void disconnect()
	{
		
		logger.debug("Disconnecting from [" + this.host + ":" + this.port + "]...");
		
		if( channel != null )
		{
			channel.disconnect();
			try {
				channel.getSession().disconnect();
			} catch (JSchException e) {
                logger.error("error",e);
                throw new RuntimeException("failed to disconnect:" + e);
			}
		}
	}
	
	public void put( String src, String dst, FtpMonitor monitor )
	{
		int retries = 0;
		boolean complete = false;
		int transferMode;
		
		while( !complete )
		{
			logger.debug("sftp put [ src:" + src + ", dest:" + dst + "]");
			try 
			{
				if( channel == null || channel.isClosed() )
				{
					connect();
				}
				
				if( monitor.getCount() > 0 )
				{
					logger.debug("Resuming transfer...");
					transferMode = ChannelSftp.RESUME;
				}
				else
				{
					transferMode = ChannelSftp.OVERWRITE;
				}
				
				channel.put( cleanPath(src), cleanPath(dst), monitor, transferMode );
				
				complete = true;
			} 
			catch ( Throwable e )
			{
				logger.error("An sftp error occurred", e );
				retries++;
				
				if( retries <= getRetries() )
				{
					
					logger.debug("Waiting [" + getRetryWaitTime() + "] milliseconds before retry..." );
					sleep( getRetryWaitTime() );
					logger.debug("Retry starting...");
				}
				else
				{
					throw new RuntimeException("sftp error", e);
				}
			}
		}

	}
	
	public void put( String src, String dst )
	{
		put( src, dst,  new DefaultFtpMonitor() );
	}
	
	public void get( String src, String dst, FtpMonitor monitor )
	{
		int retries = 0;
		boolean complete = false;
		int transferMode;
		
		while( !complete )
		{	
			logger.debug("sftp get [ src:" + src + ", dest:" + dst + " ]");
			
			try
			{
				
				if( channel == null || channel.isClosed() )
				{
					connect();
				}
				
				if( monitor.getCount() > 0 )
				{
					logger.debug("Resuming transfer...");
					transferMode = ChannelSftp.RESUME;
				}
				else
				{
					transferMode = ChannelSftp.OVERWRITE;
				}
				
				channel.get( cleanPath(src), cleanPath(dst), monitor, transferMode );
				
				complete = true;
			}
			catch ( SftpException e )
			{
				logger.error("An sftp error occurred", e );
				retries++;
				
				if( retries <= getRetries()  )
				{
					logger.debug("Waiting [" + getRetryWaitTime() + "] milliseconds before retry..." );
					sleep( getRetryWaitTime() );
					logger.debug("Retry starting...");
				}
				else
				{
					throw new RuntimeException("sftp error", e);
				}
			}
		}
	}
	
	public void get( String src, String dst )
	{
		get( src, dst, new DefaultFtpMonitor() );
	}
	
	private Properties getConfigProperties(Object key, Object value)
	{
		Properties configProperties = new Properties();
		configProperties.put(key, value);
		return configProperties;
	}
	
	private String cleanPath( String path )
	{
		return path.replace("\\", "/");
	}
	
	protected void sleep( long sleepTimeMillis )
	{
		try
		{
			Thread.currentThread().sleep(sleepTimeMillis);
		}
		catch( Throwable e )
		{
			// Nothing to do
		}
	}
	
	class DefaultFtpMonitor implements FtpMonitor{

		int count;
		
		public long getCount() {
			return this.count;
		}

		public boolean count(long count) {
			this.count += count;
			return true;
		}

		public void end() {
			//nothing to do
		}

		public void init(int op, String src, String dest, long max) {
			this.count=0;
		}
		
		
	}

	public void deleteFile(String target) {
		
		int retries = 0;
		boolean complete = false;
		int transferMode;
		
		while( !complete )
		{	
			logger.debug("sftp rm [ target:" + target + "]");
			
			try
			{
				
				if( channel == null || channel.isClosed() )
				{
					connect();
				}
				
				channel.rm( cleanPath(target));
				
				complete = true;
				
			}
			catch ( SftpException e )
			{
				logger.error("An sftp error occurred", e );
				retries++;
				
				if( retries <= getRetries()  )
				{
					logger.debug("Waiting [" + getRetryWaitTime() + "] milliseconds before retry..." );
					sleep( getRetryWaitTime() );
					logger.debug("Retry starting...");
				}
				else
				{
					throw new RuntimeException("sftp error", e);
				}
			}
		}
		
	}

	public void mkdir(String dir) {
		
		logger.debug("sftp mkdir [ dir:" + dir + "]");
		
		try {
			
			if( channel == null || channel.isClosed() ) {
				connect();
			}
			
			channel.mkdir( cleanPath(dir));
			
		}
		catch ( SftpException e ) {
			logger.error("An sftp error occurred", e );
			throw new RuntimeException("sftp error", e);
		}
		
	}

	public void rmdir(String dir) {

		logger.debug("sftp rmdir [ dir:" + dir + "]");
		
		try {
			
			if( channel == null || channel.isClosed() ) {
				connect();
			}
			
			channel.rmdir( cleanPath(dir));
			
		}
		catch ( SftpException e ) {
			//don't throw an exception if the directory did not exist in first place
			if(2 != e.id) {
				logger.error("An sftp error occurred", e );
				throw new RuntimeException("sftp error", e);
			}
		}
		
	}
	
	public List<FtpFileEntry> listDir(String dir)
	{
		logger.debug("sftp ls [ dir:" + dir + "]");
		List<FtpFileEntry> fileEntryList = new ArrayList<FtpFileEntry>();
		
		try {
			
			if( channel == null || channel.isClosed() ) {
				connect();
			}
			
			Vector items =  channel.ls( cleanPath(dir));
			
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
	
	public void quit() {
		if(channel != null) {
			channel.disconnect();
			try {
				channel.getSession().disconnect();
			} catch (JSchException e) {
                logger.error("error",e);
                throw new RuntimeException("failed to disconnect:" + e);
			}finally{
			    channel.quit();
			}
		}
	}

	@Override
	public void setRootDirectory(String directory) {
		// Not used on this implementation
	}

	@Override
	public String getRootDirectory() {
		// Not used on this implementation
		return null;
	}
	
}
