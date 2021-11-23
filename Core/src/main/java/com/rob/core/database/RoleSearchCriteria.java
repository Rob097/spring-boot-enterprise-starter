package com.rob.core.database;

import com.rob.core.fetch.modules.Fetch;
import com.rob.core.utils.db.Range;
import com.rob.core.utils.java.IntegerList;
import com.rob.core.utils.java.ValueObject;

public class RoleSearchCriteria extends ValueObject {

	/**Limitatore risultati*/
	private Range range;
	private boolean isCount = false;
	
	private Fetch fetch;
	
	/**Filtri*/	
	private IntegerList ids;
	private Integer userId;
	
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
		if (ids!=null && !ids.isEmpty()) {
			return true;
		}
		if (userId!=null) {
			return true;
		}
		
		return false;
	}
	
	public boolean isJoinUser() {
		if(this.userId!=null) {
			return true;
		}
		return false;
	}
	
	public IntegerList getIds() {
		return ids;
	}
	public void setIds(IntegerList ids) {
		this.ids = ids;
	}
	public void setId(Integer id) {
		if(id!=null) {
			this.ids = new IntegerList();
			this.ids.add(id);
		}
	}
	
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
}
