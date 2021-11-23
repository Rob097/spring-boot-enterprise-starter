package com.rob.core.models.enums;

import com.rob.core.utils.java.WithID;

public enum SexEnum implements WithID<String>{
	
	M("M", "Male"),
	F("F", "Female")
	;
	
	private String id;
	private String name;
	
	SexEnum(String id, String name) {
		this.id = id;
		this.name = name;
	};
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	/**Restituisce enum dato il suo ID*/
	public static SexEnum byId(String id) {
		if (id==null) {
			return null;
		}
		for (SexEnum val : SexEnum.values() ) {
			if (id.equals(val.getId())) {
				return val;
			}
		}
		return null;
	}

	/**Restituisce enum dato il suo ID*/
	public static SexEnum byId(WithID<String> withId) {
		if (withId == null){
			return null;
		}
		return byId(withId.getId());
	}

}
