package org.cehl.cehltools;

public class JobException extends RuntimeException {

	/**
	 * Construct a {@link JobException} with a generic message.
	 * @param msg the message
	 */
	public JobException(String msg) {
		super(msg);
	}

	/**
	 * Construct a {@link JobException} with a generic message and a
	 * cause.
	 * 
	 * @param msg the message
	 * @param cause the cause of the exception
	 */
	public JobException(String msg, Throwable cause) {
		super(msg, cause);
	}
}