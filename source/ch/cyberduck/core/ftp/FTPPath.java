package ch.cyberduck.core.ftp;

/*
 *  Copyright (c) 2003 David Kocher. All rights reserved.
 *  http://cyberduck.ch/
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  Bug fixes, suggestions and comments should be sent to:
 *  dkocher@cyberduck.ch
 */

import ch.cyberduck.core.*;
import com.enterprisedt.net.ftp.FTPException;
import com.enterprisedt.net.ftp.FTPTransferType;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;

/**
* @version $Id$
 */
public class FTPPath extends Path {
    private static Logger log = Logger.getLogger(FTPPath.class);
	
    private FTPSession session;
	
    /**
		* @param session The connection to work with for regular file operations
     * @param parent The parent directory relative to this file
     * @param name The filename of this path
     */
    public FTPPath(FTPSession session, String parent, String name) {
		super(parent, name);
		this.session = session;
    }
	
    public FTPPath(FTPSession session, String path) {
		super(path);
		this.session = session;
    }
	
    /**
		* @param session The connection to work with for regular file operations
     * @param parent The parent directory relative to this file
	 * @param file The corresponding local file to the remote path
     */
    public FTPPath(FTPSession session, String parent, java.io.File file) {
		super(parent, file);
		this.session = session;
    }
	
    public Path copy(Session s) {
		FTPPath copy = new FTPPath((FTPSession)s, this.getParent().getAbsolute(), this.getLocal());
		copy.attributes = this.attributes;
		//	copy.status = this.status;
		return copy;
    }
    
    public Path getParent() {
		String abs = this.getAbsolute();
		if((null == parent)) {
			int index = abs.lastIndexOf('/');
			String dirname = abs;
			if(index > 0)
				dirname = abs.substring(0, index);
			else if(index == 0) //parent is root
				dirname = "/";
			else if(index < 0)
				dirname = session.workdir().getAbsolute();
			parent = new FTPPath(session, dirname);
		}
		log.debug("getParent:"+parent);
		return parent;
    }
	
    public List list() {
		return this.list(true, Preferences.instance().getProperty("browser.showHidden").equals("true"));
    }
	
    public Session getSession() {
		return this.session;
    }
    
    public List list(boolean notifyobservers, boolean showHidden) {
		session.log("Listing "+this.getAbsolute(), Message.PROGRESS);
		session.addPathToHistory(this);
		try {
			session.check();
			session.FTP.setTransferType(FTPTransferType.ASCII);
			session.FTP.chdir(FTPPath.this.getAbsolute());
			this.setCache(new FTPParser().parseList(this.getAbsolute(), session.FTP.dir(), showHidden));
			if(notifyobservers) {
				session.callObservers(this);
			}
		}
		catch(FTPException e) {
			session.log("FTP Error: "+e.getMessage(), Message.ERROR);
		}
		catch(IOException e) {
			session.log("IO Error: "+e.getMessage(), Message.ERROR);
		}
		finally {
			session.log("Idle", Message.STOP);
		}
		return this.cache();
    }
	
    public void delete() {
		log.debug("delete:"+this.toString());
		try {
			session.check();
			if(this.isDirectory()) {
				session.FTP.chdir(this.getAbsolute());
				List files = this.list(false, true);
				java.util.Iterator iterator = files.iterator();
				Path file = null;
				while(iterator.hasNext()) {
					file = (Path)iterator.next();
					if(file.isDirectory()) {
						file.delete();
					}
					if(file.isFile()) {
						session.log("Deleting "+this.getName(), Message.PROGRESS);
						session.FTP.delete(file.getName());
					}
				}
				session.FTP.cdup();
				session.log("Deleting "+this.getName(), Message.PROGRESS);
				session.FTP.rmdir(this.getName());
			}
			if(this.isFile()) {
				session.log("Deleting "+this.getName(), Message.PROGRESS);
				session.FTP.delete(this.getName());
			}
		}
		catch(FTPException e) {
			session.log("FTP Error: "+e.getMessage(), Message.ERROR);
		}
		catch(IOException e) {
			session.log("IO Error: "+e.getMessage(), Message.ERROR);
		}
		finally {
			session.log("Idle", Message.STOP);
		}
    }
	
    public void rename(String filename) {
		log.debug("rename:"+filename);
		try {
			session.check();
			session.FTP.chdir(this.getParent().getAbsolute());
			session.log("Renaming "+this.getName()+" to "+filename, Message.PROGRESS);
			session.FTP.rename(this.getName(), filename);
			this.setPath(this.getParent().getAbsolute(), filename);
			this.getParent().list();
		}
		catch(FTPException e) {
			session.log("FTP Error: "+e.getMessage(), Message.ERROR);
		}
		catch(IOException e) {
			session.log("IO Error: "+e.getMessage(), Message.ERROR);
		}
		finally {
			session.log("Idle", Message.STOP);
		}
    }
	
    public Path mkdir(String name) {
		log.debug("mkdir:"+name);
		try {
			session.check();
			session.log("Make directory "+name, Message.PROGRESS);
			//	    session.FTP.mkdir(this.getAbsolute());
			session.FTP.mkdir(name);
			this.list();
		}
		catch(FTPException e) {
			session.log("FTP Error: "+e.getMessage(), Message.ERROR);
		}
		catch(IOException e) {
			session.log("IO Error: "+e.getMessage(), Message.ERROR);
		}
		return new FTPPath(session, this.getAbsolute(), name);
    }
	
    public void changePermissions(int permissions) {
		log.debug("changePermissions:"+permissions);
		try {
			session.check();
			session.FTP.site("chmod "+permissions+" "+this.getAbsolute());
		}
		catch(FTPException e) {
			session.log("FTP Error: "+e.getMessage(), Message.ERROR);
		}
		catch(IOException e) {
			session.log("IO Error: "+e.getMessage(), Message.ERROR);
		}
		finally {
			session.log("Idle", Message.STOP);
		}
    }
	
	//  public void changeOwner(String owner) {
 //	log.debug("changeOwner");
 //	try {
 //	    session.check();
 //	    session.FTP.site("chown "+owner+" "+this.getAbsolute());
 //	}
 //	catch(FTPException e) {
 //	    session.log("FTP Error: "+e.getMessage(), Message.ERROR);
 //	}
 //	catch(IOException e) {
 //	    session.log("IO Error: "+e.getMessage(), Message.ERROR);
 //	}
 //    }
	
	//    public void changeGroup(String group) {
 //	log.debug("changeGroup");
 //	try {
 //	    session.check();
 //	    session.FTP.site("chown :"+group+" "+this.getAbsolute());
 //	}
 //	catch(FTPException e) {
 //	    session.log("FTP Error: "+e.getMessage(), Message.ERROR);
 //	}
 //	catch(IOException e) {
 //	    session.log("IO Error: "+e.getMessage(), Message.ERROR);
 //	}
 //  }
	
    public void fillQueue(List queue, int kind) {
		log.debug("fillQueue:"+kind+","+kind);
		try {
			this.session.check();
			switch(kind) {
				case Queue.KIND_DOWNLOAD:
					this.fillDownloadQueue(queue);
					break;
				case Queue.KIND_UPLOAD:
					this.fillUploadQueue(queue);
					break;
			}
		}
		catch(FTPException e) {
			session.log("FTP Error: "+e.getMessage(), Message.ERROR);
		}
		catch(IOException e) {
			session.log("IO Error: "+e.getMessage(), Message.ERROR);
		}
    }
	
    private void fillDownloadQueue(List queue)  throws IOException {
		if(this.isDirectory()) {
			List files = this.list(false, true);
			java.util.Iterator i = files.iterator();
			while(i.hasNext()) {
				FTPPath p = (FTPPath)i.next();
				p.setLocal(new File(this.getLocal(), p.getName()));
				p.fillDownloadQueue(queue);
			}
		}
		else if(this.isFile()) {
			this.status.setSize(this.session.FTP.size(this.getAbsolute()));
			queue.add(this);
		}
		else
			throw new IOException("Cannot determine file type");
    }
	
    public void download() {
		try {
			log.debug("download:"+this.toString());
			if(!this.isFile())
				throw new IOException("Download must be a file.");
			this.session.check();
			if(Preferences.instance().getProperty("ftp.transfermode").equals("binary")) {
				//		this.session.log("Setting transfer mode to BINARY", Message.PROGRESS);
				this.session.FTP.setTransferType(FTPTransferType.BINARY);
				this.getLocal().getParentFile().mkdirs();
				OutputStream out = new FileOutputStream(this.getLocal(), this.status.isResume());
				if(out == null) {
					throw new IOException("Unable to buffer data");
				}
				//		this.session.log("Opening data stream...", Message.PROGRESS);
				java.io.InputStream in = this.session.FTP.getBinary(this.getAbsolute(), this.status.isResume() ? this.getLocal().length() : 0);
				if(in == null) {
					throw new IOException("Unable opening data stream");
				}
				//this.session.log("Downloading "+this.getName(), Message.PROGRESS);
				this.download(in, out);
				if(this.status.isComplete())
					this.session.FTP.validateTransfer();
			}
			else if(Preferences.instance().getProperty("ftp.transfermode").equals("ascii")) {
				//		this.session.log("Setting transfer type to ASCII", Message.PROGRESS);
				this.session.FTP.setTransferType(FTPTransferType.ASCII);
				this.getLocal().getParentFile().mkdir();
				java.io.Writer out = new FileWriter(this.getLocal(), this.status.isResume());
				if(out == null) {
					throw new IOException("Unable to buffer data");
				}
				//		this.session.log("Opening data stream...", Message.PROGRESS);
				java.io.Reader in = this.session.FTP.getASCII(this.getName(), this.status.isResume() ? this.getLocal().length() : 0);
				//		java.io.Reader in = this.session.FTP.getASCII(this.getName(), this.status.isResume() ? this.status.getCurrent() : 0);
				if(in == null) {
					throw new IOException("Unable opening data stream");
				}
				//this.session.log("Downloading "+this.getName(), Message.PROGRESS);
				this.download(in, out);
				if(this.status.isComplete())
					this.session.FTP.validateTransfer();
			}
			else {
				throw new FTPException("Transfer type not set");
			}
		}
		catch(FTPException e) {
			this.session.log("FTP Error: "+e.getMessage(), Message.ERROR);
		}
		catch(IOException e) {
			this.session.log("IO Error: "+e.getMessage(), Message.ERROR);
		}
		finally {
			session.log("Idle", Message.STOP);
		}
    }
	
    private void fillUploadQueue(List queue) throws IOException {
		if(this.getLocal().isDirectory()) {
			session.FTP.mkdir(this.getAbsolute());//@todo do it here rather than in upload() ?
			File[] files = this.getLocal().listFiles();
			for(int i = 0; i < files.length; i++) {
				FTPPath p = new FTPPath(this.session, this.getAbsolute(), files[i]);
				p.fillUploadQueue(queue);
			}
		}
		else if(this.getLocal().isFile()) {
			this.status.setSize(this.getLocal().length());
			queue.add(this);
		}
		else
			throw new IOException("Cannot determine file type");
    }
	
    public void upload() {
		try {
			log.debug("upload:"+this.toString());
			this.session.check();
			if(Preferences.instance().getProperty("ftp.transfermode").equals("binary")) {
				//		this.session.log("Setting transfer mode to BINARY.", Message.PROGRESS);
				this.session.FTP.setTransferType(FTPTransferType.BINARY);
				
				java.io.InputStream in = new FileInputStream(this.getLocal());
				if(in == null) {
					throw new IOException("Unable to buffer data");
				}
				
				//		this.session.log("Opening data stream...", Message.PROGRESS);
				java.io.OutputStream out = this.session.FTP.putBinary(this.getAbsolute(), false);
				//		java.io.OutputStream out = this.session.FTP.putBinary(this.getAbsolute(), this.status.isResume());
				if(out == null) {
					throw new IOException("Unable opening data stream");
				}
				//session.log("Uploading "+this.getName(), Message.PROGRESS);
				this.upload(out, in);
				this.session.FTP.validateTransfer();
			}
			else if(Preferences.instance().getProperty("ftp.transfermode").equals("ascii")) {
				//		this.session.log("Setting transfer type to ASCII.", Message.PROGRESS);
				this.session.FTP.setTransferType(FTPTransferType.ASCII);
				
				java.io.Reader in = new FileReader(this.getLocal());
				if(in == null) {
					throw new IOException("Unable to buffer data");
				}
				
				//		this.session.log("Opening data stream...", Message.PROGRESS);
				java.io.Writer out = this.session.FTP.putASCII(this.getAbsolute(), false);
				//		java.io.Writer out = this.session.FTP.putASCII(this.getAbsolute(), this.status.isResume());
				if(out == null) {
					throw new IOException("Unable opening data stream");
				}
				//this.session.log("Uploading "+this.getName(), Message.PROGRESS);
				this.upload(out, in);
				this.session.FTP.validateTransfer();
			}
			else {
				throw new FTPException("Transfer mode not set");
			}
		}
		catch(FTPException e) {
			this.session.log("FTP Error: "+e.getMessage(), Message.ERROR);
		}
		catch(IOException e) {
			this.session.log("IO Error: "+e.getMessage(), Message.ERROR);
		}
		finally {
			session.log("Idle", Message.STOP);
		}
    }
    
	
    // ----------------------------------------------------------
    // FTPParser
    // ----------------------------------------------------------
    
    class FTPParser {
		private final String months[] = {
			"JAN", "FEB", "MAR",
			"APR", "MAY", "JUN",
			"JUL", "AUG", "SEP",
			"OCT", "NOV", "DEC"
		};
		
		public List parseList(String parent, String[] list, boolean showHidden) throws FTPException {
			//        log.debug("[FTPParser] parseList(" + parent + "," + list + ")");
			List parsedList = new ArrayList();
			//	    boolean showHidden = Preferences.instance().getProperty("browser.showHidden").equals("true");
			for(int i = 0; i < list.length; i++) {
				int index = 0;
				String line = list[i].trim();
				if(isValidLine(line)) {
					Path p = parseListLine(parent, line);
					String filename = p.getName();
					if(!(filename.equals(".") || filename.equals(".."))) {
						if(!showHidden && filename.charAt(0) == '.') {
							p.attributes.setVisible(false);
						}
						parsedList.add(p);
					}
				}
			}
			return parsedList;
		}
		
		
		/**
			If the file name is a link, it may include a pointer to the original, in which case it is in the form "name -> link"
		 */
		public String parseLink(String link) {
			if(!isValidLink(link)) {
				return null;
			}
			return link.substring(jumpWhiteSpace(link, link.indexOf("->")) + 3).trim();
		}
		
		public boolean isFile(String c) {
			return c.charAt(0) == '-';
		}
		
		public boolean isLink(String c) {
			return c.charAt(0) == 'l';
		}
		
		public boolean isDirectory(String c) {
			//        log.debug("[FTPParser] isDirectory(" + c + ")");
			return c.charAt(0) == 'd';
		}
		
		private Path parseListLine(String parent, String line) throws FTPException {
			//        log.debug("[FTPParser] parseListLine("+ parent+","+line+")");
   // unix list format never strarts with number
			if("0123456789".indexOf(line.charAt(0)) < 0) {
				return parseUnixListLine(parent, line);
			}
			// windows list format always starts with number
			else {
				return parseWinListLine(parent, line);
			}
		}
		
		
		private Path parseWinListLine(String path, String line) throws FTPException {
			//        log.debug("[FTPParser] parseWinListLine("+ path+","+line+")");
			
			// 10-16-01  11:35PM                 1479 file
   // 10-16-01  11:37PM       <DIR>          awt  *
			Path p = null;
			try {
				StringTokenizer toker = new StringTokenizer(line);
				long date = parseWinListDate (toker.nextToken(),  toker.nextToken());// time
					String size2dir  = toker.nextToken();  // size or dir
					String access;
					int size = 0;
					if(size2dir.equals("<DIR>")) {
						access = "d?????????";
					}
					else {
						access = "-?????????";
					}
					String name = toker.nextToken("").trim();
					String owner = "";
					String group = "";
					
					if(isDirectory(access) && !(name.charAt(name.length()-1) == '/')) {
						name = name + "/";
					}
					p = new FTPPath(session, path, name);
					p.attributes.setOwner(owner);
					p.attributes.setModified(date);
					p.attributes.setMode(access);
					p.attributes.setPermission(new Permission(access));
					p.status.setSize(size);
					return p;
			}
			catch(NumberFormatException e) {
				throw new FTPException("Invalid server response : "+e.getMessage());
			}
			catch(StringIndexOutOfBoundsException e) {
				throw new FTPException("Invalid server response : "+e.getMessage());
			}
		}
		
		private long parseWinListDate(String date, String time) throws NumberFormatException {
			//10-16-01    11:35PM
   //10-16-2001  11:35PM
			Calendar c = Calendar.getInstance();
			StringTokenizer toker = new StringTokenizer(date,"-");
			int m = Integer.parseInt(toker.nextToken()),
				d = Integer.parseInt(toker.nextToken()),
				y = Integer.parseInt(toker.nextToken());
			if(y >= 70) y += 1900; else y += 2000;
			toker = new StringTokenizer(time,":APM");
			c.set(y,m,d,(time.endsWith("PM")?12:0)+
		 Integer.parseInt(toker.nextToken()),
		 Integer.parseInt(toker.nextToken()));
			return c.getTime().getTime();
		}
		
		private Path parseUnixListLine(String path, String line) throws FTPException{
			//        log.debug("[FTPParser] parseUnixListLine("+ path+","+line+")");
			
			//drwxr-xr-x  33 root     wheel       1078 Mar 15 16:18 bin
   //lrwxrwxr-t   1 root     admin         13 Mar 16 13:38 cores -> private/cores
   //dr-xr-xr-x   2 root     wheel        512 Mar 16 02:38 dev
   //lrwxrwxr-t   1 root     admin         11 Mar 16 13:38 etc -> private/etc
   //lrwxrwxr-t   1 root     admin          9 Mar 16 13:38 mach -> /mach.sym
   //-r--r--r--   1 root     admin     563812 Mar 16 02:38 mach.sym
   //-rw-r--r--   1 root     wheel    3156580 Jan 25 07:06 mach_kernel
   //drwxr-xr-x   7 root     wheel        264 Jul 10  2001 private
   //drwxr-xr-x  59 root     wheel       1962 Mar 15 16:18 sbin
   //lrwxrwxr-t   1 root     admin         11 Mar 16 13:38 tmp -> private/tmp
   //drwxr-xr-x  11 root     wheel        330 Jan 31 08:15 usr
   //lrwxrwxr-t   1 root     admin         11 Mar 16 13:38 var -> private/var
			
			Path p = null;
			try {
				String link = null;
				if(isLink(line)) {
					link = parseLink(line);
					line = line.substring(0, line.indexOf("->")).trim();
				}
				StringTokenizer toker = new StringTokenizer(line);
				String access = toker.nextToken();  // access
				toker.nextToken();  // links
				String owner = toker.nextToken();  // owner
				String group = toker.nextToken();  // group
				String size = toker.nextToken();  // size
				if(size.endsWith(","))
					size = size.substring(0,size.indexOf(","));
				String uu = size;
				if(access.startsWith("c"))
					uu = toker.nextToken();             // device
										 // if uu.charAt(0) is not digit try uu_file format
				if("0123456789".indexOf(uu.charAt(0)) < 0) {
					size = group;
					group = "";
				}
				long date = parseUnixListDate(("0123456789".indexOf(uu.charAt(0)) < 0 ?uu
																		  :toker.nextToken()), // month
								  toker.nextToken(),  // day
								  toker.nextToken()); // time or year
				String name = toker.nextToken("").trim(); // name
				
				p = new FTPPath(session, path, name);
				p.attributes.setOwner(owner);
				p.attributes.setGroup(group);
				p.attributes.setModified(date);
				p.attributes.setMode(access);
				p.attributes.setPermission(new Permission(access));
				p.status.setSize(Long.parseLong(size));
				return p;
			}
			catch(NoSuchElementException e) {
				throw new FTPException("Invalid server response : "+e.getMessage());
			}
			catch(StringIndexOutOfBoundsException e) {
				throw new FTPException("Invalid server response : "+e.getMessage());
			}
		}
		
		
		private long parseUnixListDate(String month, String day, String year2time) throws NumberFormatException {
			
			// Nov  9  1998
   // Nov 12 13:51
			Calendar c = Calendar.getInstance();
			month = month.toUpperCase();
			for(int m=0;m<12;m++) {
				if(month.equals(months[m])) {
					if(year2time.indexOf(':')!= -1) {
						// current year
						c.setTime(new Date(System.currentTimeMillis()));
						StringTokenizer toker = new StringTokenizer(year2time,":");
						// date and time
						c.set(c.get(Calendar.YEAR), m,
			Integer.parseInt(day),
			Integer.parseInt(toker.nextToken()),
			Integer.parseInt(toker.nextToken()));
					}
					else {
						// date
						c.set(Integer.parseInt(year2time), m, Integer.parseInt(day),0,0);
					}
					break;
				}
			}
			return c.getTime().getTime();
		}
		
		// UTILITY METHODS
		
		private boolean isValidLink(String link) {
			return link.indexOf("->") != -1;
		}
		
		private boolean isValidLine(String l) {
			String line = l.trim();
			if(line.equals("")) {
				return false;
			}
			/* When decoding, it is important to note that many implementations include a line at the start like "total <number>". Clients should ignore any lines that don't match the described format.
				*/
			if( line.indexOf("total") != -1 ) {
				try {
					Integer.parseInt(line.substring(line.lastIndexOf(' ') + 1));
					return false;
				}
				catch(NumberFormatException e) {
					// return true // total must be name of real file
				}
			}
			return true;
		}
		
		private int jumpWhiteSpace(String line, int index) {
			while(line.substring(index, index + 1).equals(" ")) {
				index++;
			}
			return index;
		}
    }
}
