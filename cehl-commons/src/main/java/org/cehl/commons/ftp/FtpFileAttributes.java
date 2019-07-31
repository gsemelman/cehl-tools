package org.cehl.commons.ftp;

import java.util.Date;

public interface FtpFileAttributes {
	
	  public int getFlags();
	  public long getSize();
	  public int getUId();
	  public int getGId();
	  public int getPermissions();
	  
	  /*
	  * The atime--access time--is the time when the data of a file was last accessed. 
	  * Displaying the contents of a file or executing a shell script will update a file's atime, for example.
	  * 
	  * @Return the number of elapsed seconds since 1970-01-01 00:00 (the epoch)
	  */
	  public int getATime();
	  
	  /*
	   * The mtime--modify time--is the time when the actual contents of a file was last modified. 
	   * This is the time displayed in a long directoring listing
	   * 
	   * @Return the number of elapsed seconds since 1970-01-01 00:00 (the epoch)
	   */
	  public int getMTime();
	  
	  public String[] getExtended();
	  
	  /*
	   * @Return return the atime as a java.util.Date
	   */
	  public Date getATimeAsDate();
	  
	  /*
	   * @Return return the mtime as a java.util.Date
	   */
	  public Date getMTimeAsDate();
	  
	  public boolean isDirectory();

}
