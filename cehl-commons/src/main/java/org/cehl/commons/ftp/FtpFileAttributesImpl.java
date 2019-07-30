package org.cehl.commons.ftp;

import java.util.Date;

public class FtpFileAttributesImpl implements FtpFileAttributes {

	int flags=0;
	long size;
	int uid;
	int gid;
	int permissions;
	int atime;
	int mtime;
	String[] extended=null;
	
	/*
	 * The atime--access time--is the time when the data of a file was last accessed. 
	 * Displaying the contents of a file or executing a shell script will update a file's atime, for example.
	 * 
	 * @Return the number of elapsed seconds since 1970-01-01 00:00 (the epoch)
	 * (non-Javadoc)
	 * @see com.openlead.io.ftp.client.FtpFileAttributes#getATime()
	 */
	public int getATime() {
		return atime;
	}

	public String[] getExtended() {
		return extended;
	}

	public int getFlags() {
		return flags;
	}

	public int getGId() {
		return gid;
	}

	/*
	 * The mtime--modify time--is the time when the actual contents of a file was last modified. 
	 * This is the time displayed in a long directoring listing
	 * 
	 * @Return the number of elapsed seconds since 1970-01-01 00:00 (the epoch)
	 * 
	 * (non-Javadoc)
	 * @see com.openlead.io.ftp.client.FtpFileAttributes#getMTime()
	 */
	public int getMTime() {
		return mtime;
	}

	public int getPermissions() {
		return permissions;
	}

	public long getSize() {
		return size;
	}

	public int getUId() {
		return uid;
	}

	public void setFlags(int flags) {
		this.flags = flags;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public void setUId(int uid) {
		this.uid = uid;
	}

	public void setGId(int gid) {
		this.gid = gid;
	}

	public void setPermissions(int permissions) {
		this.permissions = permissions;
	}

	public void setATime(int atime) {
		this.atime = atime;
	}

	public void setMTime(int mtime) {
		this.mtime = mtime;
	}

	public void setExtended(String[] extended) {
		this.extended = extended;
	}

	public Date getATimeAsDate() {
		return new Date( getATime() * 1000L );
	}

	public Date getMTimeAsDate() {
		return new Date( getMTime() * 1000L );
	}
	
	

}
