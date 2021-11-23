package com.rob.core.utils.db;

import org.apache.commons.lang3.StringUtils;

public enum SortEnum {

	ASC("ASC", 1) 
	,DESC("DESC", -1) 	
	;
	
  private String value;
  private int code;
  
  SortEnum(String value, int code) {
      this.value = value;
      this.code = code;
  }

  public String getValue() {
      return value;
  }

  public int getCode() {
      return code;
  }
  
	public static SortEnum getByValue(String value) {
		for (SortEnum val : SortEnum.values() ) {
			if (StringUtils.isNotEmpty(value) && val.getValue().equals(value)) {
				return val;
			}
		}
		return getDefault();
	}
	
	public static SortEnum getDefault() {
		return SortEnum.ASC;
	}
	
}
