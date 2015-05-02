package de.quellcodecounter.git;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GitInformationCommit {
	public String Checksum;
	public String Author;
	public String Mail;
	public Date Date;
	public String Message;
	
	@Override
	public String toString() {
		return Checksum + "(" + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(Date) + ")\r\n" + Author + " <" + Mail + ">\r\n" + Message;
	}
}