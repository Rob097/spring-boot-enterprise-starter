package com.rob.core.utils.db;

import java.io.Serializable;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * Classe per configurare un range di risultati.<br>
 * Il range va considerato 0 based,
 * perciò start=0 e end=9 significa richiedere i primi 10 elementi
 *
 */
public class Range implements Serializable{
	private static final long serialVersionUID = -5043055161267368521L;
	
	public static final String REG_EXP_VALUE = "^\\S+=\\d*-\\d*$";
    public static final Pattern REG_EXP_PATTERN = Pattern.compile(REG_EXP_VALUE);
    public static final String ROWS = "rows";
    
    private String unit;
    private int start;
    private int end;

    /**Limitatore massimo risultati 
     * @param end */
    public Range(int end) {
		this.unit = ROWS;
		this.start = 0;
		if (end >= 0 && end < this.start){
			throw new IllegalArgumentException("Non negative end cannot be less than start");
		}
		this.end = end;
    }
    
    /**Costruttore Range
     * 
     * @param unit unità di misura(es: ""Item"")
     * @param start indice iniziale
     * @param end indice finale
     */
    public Range(String unit, int start, int end) {
        if (unit == null){
            throw new IllegalArgumentException("Unit argument cannot be null");
        }
        if (end >= 0 && end < start){
            throw new IllegalArgumentException("Non negative end cannot be less than start",new IllegalArgumentException(Range.toLoggableString(unit, start, end, null)));
        }
        this.unit = unit;
        this.start = (start >= 0 ? start : -1);
        this.end = (end >= 0 ? end : -1);
    }

    public Range() {
    	
    }

    /**
     * Crea un oggetto range parsando la stringa in ingresso.<br>
     * Il range può essere definito secondo questa sintassi:
     * <br>
     * <code>{unit}={start}-{end}</code>
     * <br>
     * <code>{start}-{end}</code>
     * <br><br>
     * es. items=0-9
     * @param range La stringa da parsare
     */
    public Range(String range) {
        if (range == null){
            throw new IllegalArgumentException("range string cannot be null");
        }
        if (!REG_EXP_PATTERN.matcher(range).matches()){
            throw new IllegalArgumentException("range string is malformed");
        }

        int eqpos = range.indexOf("=");

        unit = range.substring(0, eqpos);
        range = range.substring(eqpos + 1);

        if (range.equals("-")){
            throw new IllegalArgumentException("range string is malformed");
        }

        int minpos = range.indexOf("-");
        start = -1;
        if (minpos > 0) {
            start = Integer.parseInt(range.substring(0, minpos));
        }
        end = -1;
        if (minpos < range.length() - 1) {
            end = Integer.parseInt(range.substring(minpos + 1));
        }
    }

    /**
     * L'unità di misura del range
     * @return String
     */
    public String getUnit() {
        return unit;
    }

    /**
     * L'indice di inizio
     * @return L'indice di inizio. (default -1)
     */
    public int getStart() {
        return start;
    }

    /**
     * L'indice di fine
     * @return L'indice di fine. (default -1)
     */
    public int getEnd() {
        return end;
    }
    
    
    /**
     * Rappresenta un oggetto range<br>
     * Il range può essere definito secondo questa sintassi:
     * <br>
     * <code>{unit}={start}-{end}</code>
     * <br>
     * <code>{start}-{end}</code>
     * <br><br>
     * es. items=0-9
     */
    @Override
    public String toString() {
    	return unit + "=" + start + "-" + end;
    }
    
    /**Crea una stringa contenente i valori del range fornito
     * Destinata ai logs
     * @param unit 
     * @param start 
     * @param end 
     * @param count 
     * @return 
     */
    public static String toLoggableString(String unit, Integer start, Integer end, Integer count) {
    	StringBuilder sb = new StringBuilder(1024);
    	if (StringUtils.isNotBlank(unit)) {
        	sb.append("[UNIT=").append(unit).append("]");
      	}
    	if (start!=null) {
    		sb.append("[START=").append(String.valueOf(start)).append("]");
    	}
    	if (end!=null) {
    		sb.append("[END=").append(String.valueOf(end)).append("]");
    	}
    	if (count!=null) {
    		sb.append("[COUNT=").append(String.valueOf(count)).append("]");
    	}
    	return sb.toString();
    }
}