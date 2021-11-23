package com.rob.core.database;

import org.apache.commons.lang3.StringUtils;

import com.rob.core.fetch.modules.Fetch;
import com.rob.core.utils.db.Range;
import com.rob.core.utils.java.ValueObject;

public class UserSearchCriteria extends ValueObject {

	/**Limitatore risultati*/
	private Range range;
	private boolean isCount = false;
	
	private Fetch fetch;
	
	/**Filtri*/	
	private String id;
	private String username;
	private String email;
	
	
	
	public Range getRange() {
		return range;
	}
	public void setRange(Range range) {
		this.range = range;
	}
	public void setMaxRows(Integer maxRows) {
		if (maxRows==null || maxRows <= 0) {
			this.range = null;
		} else {
			this.range = new Range(Range.ROWS, 0, maxRows - 1);
		}
	}
	
	public Fetch getFetch() {
		return fetch;
	}
	public void setFetch(Fetch fetch) {
		this.fetch = fetch;
	}
	
	public boolean isCount() {
		return isCount;
	}
	public void setCount(boolean isCount) {
		this.isCount = isCount;
	}
	
	public boolean isValidCriteria() {
		if (StringUtils.isNotBlank(id)) {
			return true;
		}
		if (StringUtils.isNotBlank(username)) {
			return true;
		}
		if (StringUtils.isNotBlank(email)) {
			return true;
		}
		
		return false;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}	
	
}
