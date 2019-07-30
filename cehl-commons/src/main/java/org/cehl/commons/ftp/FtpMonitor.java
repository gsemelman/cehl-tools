package org.cehl.commons.ftp;

import com.jcraft.jsch.SftpProgressMonitor;

public interface FtpMonitor extends SftpProgressMonitor {
	
	public long getCount();

}
