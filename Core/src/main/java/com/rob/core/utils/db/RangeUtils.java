package com.rob.core.utils.db;

import java.util.List;

import org.apache.tomcat.util.http.parser.ContentRange;

public class RangeUtils {
	public static Range next(Range range){
        if (range == null || range.getEnd() < 0) {
            return null;
        }

        Integer size = rangeToSize(range);
        return new Range(range.getUnit(), range.getStart() + size, range.getEnd() + size);
    }

    /**
     * Calcola il numero di risultati massimi previsti da questo range.
     * @param range
     * @return il numero di risultati massimi previsti dal range, null se il range è null o invalido per tale calcolo (end < start o end < 0)
     */
    public static Integer rangeToSize(Range range) {
        if (range == null || range.getEnd() < 0) {
            return null;
        }

        int start = (range.getStart() >= 0 ? range.getStart() : 0);
        if (range.getEnd() < start){
            return null;
        }

        return range.getEnd() - start + 1;
    }

    /**
     * Calcola il numero di risultati massimi previsti da questo range.
     * Di fatto delega a {@link #rangeToSize(Range, int, int)} passando max
     * sia come parametro max che come parametro def
     * @param range il range da elaborare
     * @param max il valore massimo o di default da restituire
     * @return il numero di risultati massimi previsti dal range (mai null)
     */
    public static Integer rangeToSize(Range range, int max) {
        return rangeToSize(range, max, max);
    }

    /**
     * Calcola il numero di risultati massimi previsti da questo range.
     * Di fatto invoca a {@link #rangeToSize(Range)}. Se il risultato è null
     * torna def (se def <= max), se il risultato è maggiore di max torna max
     * @param range il range da elaborare
     * @param max il valore massimo da restituire
     * @param def il valore di default da resituire se il range e nullo o invalido (e che dovrebbe essere minore di max)
     * @return il numero di risultati massimi previsti dal range (mai null)
     */
    public static Integer rangeToSize(Range range, int max, int def) {
        Integer size = rangeToSize(range);
        if (size == null) {
            size = def;
        }
        if (size > max) {
            size = max;
        }
        return size;
    }

    /**
     * Equivale a {@link #extend(Range, int)} con amount = 1
     * @param range
     * @return
     */
    public static Range extend(Range range){
        return extend(range, 1);
    }

    /**
     * Incrementa la dimensione del range di <code>amount</code> unità.
     * In sostanza torna un nuovo range con <b>start</b> uguale a <code>range.getStart()</code>
     * e con <b>end</b> uguale a <code>range.getEnd() + amount</code>
     *
     * @param range
     * @param amount
     * @return null se range è null
     * @throws IllegalArgumentException se amount è <= 0
     */
    public static Range extend(Range range, int amount){
        if (range == null){
            return null;
        }
        if (amount <= 0){
            throw new IllegalArgumentException("Amount must be grater than 0");
        }

        Range output = new Range(range.getUnit(), range.getStart(), range.getEnd() + amount);

        return output;
    }

    /**
     * Crea un nuovo {@link Range} a partire da quello passato come parametro.
     * Il nuovo range avrà lo stesso <b>start</b> del range passato in input e un <b>end</b> uguale
     * al minor valore tra <code>range.getEnd()</code> e <code>range.getStart() + max - 1</code>.
     *
     * Se range è null verrà creato un range con start = 0 e end = def -1
     * @param range
     * @param max
     * @param def
     * @param unit
     * @return
     */
    public static Range limit(Range range, int max, int def, String unit) {

        if (range == null){
            return new Range(unit, 0, def - 1);
        }

        Integer rangeSize = rangeToSize(range);
        if (rangeSize!=null && rangeSize > max){
            return new Range(range.getUnit(), range.getStart(), range.getStart() + max - 1);
        }

        return new Range(range.getUnit(), range.getStart(), range.getEnd());
    }

    /**
     * Equivale ad invocare {@link #countlessContentRange(Range, List, boolean)} con reduceResults = true
     * @param requestedRange
     * @param augmentedResults
     * @return
     */
    public static ContentRange countlessContentRange(Range requestedRange, List<?> augmentedResults){
        return countlessContentRange(requestedRange, augmentedResults, true);
    }

    /**
     * Crea un oggetto content range a fronte di una ricerca che non contempla la count preventiva
     * per il calcolo del numero totali di risultati possibili.
     * Il content range avrà lo start uguale al range in input e l'end uguale al minore tra
     * <code>requestedRange.getEnd()</code> e <code>start + augmentedResults.size()</code>.
     *
     * Se augmentedResults.size() e maggiore di quanto richiesto dal range, la count sarà -1 altrimenti
     * coninciderà con <code>requestedRange.getEnd() + 1</code>
     *
     * Questo significa che, per utilizzare correttamente questo metodo, l'utilizzatore deve cercare di estrarre
     * almeno un record in più di quelli esplicitati dal range, in modo da permettere a questo metodo di stabilire
     * se esiterebbero potenziali altri risultati (count -1) o no.
     *
     * Passando reduceRsults = true, il metodo si occupa anche di eliminare dalla lista dei risultati i risultati
     * estratti in eccesso rispetto al range
     *
     * @param requestedRange Il range di risultati richiesto
     * @param augmentedResults I risultati 'aumentati' rispetto al range
     * @param reduceResults flag per ridurre i risultati in eccesso
     * @return null se non viene passato nessun range o nessun risultato di ricerca
     */
    public static ContentRange countlessContentRange(Range requestedRange, List<?> augmentedResults, boolean reduceResults){
        if (requestedRange == null || augmentedResults == null || augmentedResults.isEmpty()){
            return null;
        }

        String unit = requestedRange.getUnit();
        int start = requestedRange.getStart();
        int end = requestedRange.getEnd();
        Integer rangeSize = rangeToSize(requestedRange);
        if (augmentedResults.size() <= rangeSize){
            end = start + augmentedResults.size() - 1;
        }

        int count = end + 1;
        if (augmentedResults.size() > rangeSize){
            count = -1;
            if (reduceResults){
                for (int i = augmentedResults.size(); i > rangeSize; i--){
                    augmentedResults.remove(i -1);
                }
            }
        }

        return new ContentRange(unit, start, end, count);
    }

    /**
     * Crea un oggetto content range a fronte di una ricerca che contempli la count preventiva
     * dei risultati totali possibili
     *
     * Il content range avrà l'attributo start uguale a <code>requestedRange.getStart()</code>, end uguale a
     * <code>start + results.size()</code> e count uguale a <code>totalCount</code>
     * @param requestedRange Il range di risultati richiesto
     * @param results I risultati estratti
     * @param totalCount La count totale dei risultati che soddisfano i criteri di ricerca a prescindere dal range
     * @return null se non viene passato nessun range o nessun risultato di ricerca
     * @throws IllegalArgumentException se <code>results.size()</code> e maggiore del range richiesto
     */
    public static ContentRange contentRange(Range requestedRange, List<?> results, int totalCount){
        if (requestedRange == null || results == null || results.isEmpty()){
            return null;
        }

        Integer rangeSize = rangeToSize(requestedRange);
        if (results.size() > rangeSize) {
            throw new IllegalArgumentException("Results size does not respect requested range");
        }

        String unit = requestedRange.getUnit();
        int start = requestedRange.getStart();
        if (start<0) {
        	start = 0;
        }
        int end = start + results.size() -1;

        return new ContentRange(unit, start, end, totalCount);
    }
    
    /**Applica range a lista di risultati
    public static <T extends ValueObject> List<T> applyRange(Range range, List<T> input) {
    	if (input==null || input.isEmpty()) {
    		return input;
    	}
    	if (range==null) {
    		return input;
    	}
    	int start = range.getStart();
    	if (start<0) {
    		start = 0;
    	}
    	int end = range.getEnd();
       	if (end<0 || end>input.size()) {
       		end = input.size();
    	}
       	
       	//Ciclo ed estraggo risultati
       	List<T> outPut = new ArrayList<>();
       	int i = start;
		while (i<input.size() && i<=end) {
			outPut.add(input.get(i));
			i++;
		}
    	return outPut;
    }*/
}
