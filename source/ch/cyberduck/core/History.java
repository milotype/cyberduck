package ch.cyberduck.core;

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

import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
* Keeps track of recently connected hosts.
 * The hosts are stored in a hashmap where host.getName() is the key
 * @see ch.cyberduck.core.Host
 * @version $Id$
 */
public abstract class History {
    private static Logger log = Logger.getLogger(History.class);
	
    private Map data = new HashMap();
	
    public History() {
		this.load();
    }
	
    /**
		* Write data to file.
     */
    public abstract void save();
	
    /**
		* Read from file into memory.
     */
    public abstract void load();
	
    public void addItem(Host h) {
		log.debug("addItem:"+h);
		this.data.put(h.getHostname(), h);
    }
	
    public void removeItem(String url) {
		log.debug("removeItem:"+url);
		this.data.remove(url);
    }
	
    /**
		* @param name the Key the host is stored with (ususally host.toString())
     */
    public Host getItem(String name) {
		Host result =  (Host)this.data.get(name);
		if(null == result) {
			log.info("Host "+name+" not found in History.");
			// throw new IllegalArgumentException("Host "+name+" not found in History.");
		}
		return result;
    }
	
    public Collection values() {
		return data.values();
    }
	
    public Iterator getIterator() {
		return data.values().iterator();
    }
}
