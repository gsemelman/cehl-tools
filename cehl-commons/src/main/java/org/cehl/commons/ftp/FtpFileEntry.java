package org.cehl.commons.ftp;

public interface FtpFileEntry {
	
    public String getFilename();
    public String getLongname();
    public FtpFileAttributes getFileAttributes();

}
