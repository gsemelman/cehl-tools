package org.cehl.commons.ftp;


public class FtpFileEntryImpl implements FtpFileEntry {
	
	private  String filename;
    private  String longname;
    private  FtpFileAttributes attrs;

	public FtpFileAttributes getFileAttributes() {
		return attrs;
	}

	public String getFilename() {
		return filename;
	}

	public String getLongname() {
		return longname;
	}

	public FtpFileAttributes getAttrs() {
		return attrs;
	}

	public void setAttrs(FtpFileAttributes attrs) {
		this.attrs = attrs;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void setLongname(String longname) {
		this.longname = longname;
	}
	
	public String toString()
	{
		return this.getLongname();
	}
	
	

}
