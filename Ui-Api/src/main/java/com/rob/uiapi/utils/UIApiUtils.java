package com.rob.uiapi.utils;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.apache.tomcat.util.http.parser.ContentRange;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.rob.core.utils.java.messages.IMessage;
import com.rob.core.utils.java.messages.MessageResources;
import com.rob.core.utils.db.Range;
import com.rob.core.utils.db.RangeUtils;

public class UIApiUtils {
	/**
     * Equivale a {@link #buildRangeAwareSuccessResponse(List, Range, Integer)} con count = null
     * @param results
     * @param range
     * @param <T>
     * @return
     */
    public static <T> ResponseEntity<MessageResources<T>> buildRangeAwareSuccessResponse(List<T> results,
                                                                                         Range range) {
        return buildRangeAwareSuccessResponse(results, range, (Integer)null);
    }

    /**
     * Equivale a {@link #buildRangeAwareSuccessResponse(List, Range, Range, Integer)} con range = range e orginial = range
     * @param results
     * @param range
     * @param count
     * @param <T>
     * @return
     */
    public static <T> ResponseEntity<MessageResources<T>> buildRangeAwareSuccessResponse(List<T> results,
                                                                                         Range range,
                                                                                         Integer count) {
        return buildRangeAwareSuccessResponse(results, range, range, count, null);
    }

    /**
     * Equivale a {@link #buildRangeAwareSuccessResponse(List, Range, Range, Integer)} con count = null
     * @param results
     * @param range
     * @param original
     * @param <T>
     * @return
     */
    public static <T> ResponseEntity<MessageResources<T>> buildRangeAwareSuccessResponse(List<T> results,
                                                                                  Range range,
                                                                                  Range original) {
        return buildRangeAwareSuccessResponse(results, range, original, null, null);
    }

    /**
     * Costriusce la response per un servizio che implementa la gesitione del range.
     * La risposta conterrà sempre l'header {@link HttpHeaders#ACCEPT_RANGES}, e lo status
     * http sarà {@link HttpStatus#PARTIAL_CONTENT} qualora  la lista di risultati contenga
     * valori ed il client abbia fornito un range header nella request
     * @param results L'elenco dei risultati di ricerca
     * @param range Il range realmente applicato (ad esempio per la limitazione di range troppo ampi)
     * @param original Il range richiesto dal client
     * @param count Il conteggio degli elementi che soddisfano i criteri
     * @param <T> Il tipo di risorsa
     * @return ResponseEntity
     */
    public static <T> ResponseEntity<MessageResources<T>> buildRangeAwareSuccessResponse(List<T> results,
																			            Range range,
																			            Range original,
																			            Integer count) {
    	return buildRangeAwareSuccessResponse(results, range, original, count, null);
    }
    
    /**
     * Costriusce la response per un servizio che implementa la gesitione del range.
     * La risposta conterrà sempre l'header {@link HttpHeaders#ACCEPT_RANGES}, e lo status
     * http sarà {@link HttpStatus#PARTIAL_CONTENT} qualora  la lista di risultati contenga
     * valori ed il client abbia fornito un range header nella request
     * @param results L'elenco dei risultati di ricerca
     * @param range Il range realmente applicato (ad esempio per la limitazione di range troppo ampi)
     * @param original Il range richiesto dal client
     * @param count Il conteggio degli elementi che soddisfano i criteri
     * @param messages : lista messaggi da restituire nella MessageResources della risposta
     * @param <T> Il tipo di risorsa
     * @return ResponseEntity
     */
    public static <T> ResponseEntity<MessageResources<T>> buildRangeAwareSuccessResponse(List<T> results,
                                                      Range range,
                                                      Range original,
                                                      Integer count,
                                                      Iterable<? extends IMessage> messages) {

        HttpHeaders headers = new HttpHeaders();
        headers.put(HttpHeaders.ACCEPT_RANGES, Arrays.asList(UIApiConstants.RANGE_ITEMS_UNIT));

        HttpStatus status = HttpStatus.OK;
        MessageResources<T> result = new MessageResources<>(results, messages);
        ContentRange contentRange;
        
        if (range != null && results != null && !results.isEmpty()) {
            if (count == null) {
                contentRange = RangeUtils.countlessContentRange(range, results);
            }else{
                contentRange = RangeUtils.contentRange(range, results, count);
            }

            //RangeUtils potrebbe modificara i risultati, perciò il result viene ricreato per
            //correttezza formale anche se non sarebbe necessario
            result = new MessageResources<>(results, messages);

            if (original != null) {
                status = HttpStatus.PARTIAL_CONTENT;
                if (range.getEnd() < original.getEnd()) {
                    //result.add(new Message(UIApiConstants.RANGE_REDUCED_MESSAGE, IMessage.Level.WARNING));
                }
            }

            headers.put(HttpHeaders.CONTENT_RANGE, Arrays.asList(contentRange.toString()));
        } else {
        	//SOLO CONTEGGIO
        	if (count!=null && count>0) {
        		contentRange = new ContentRange(Range.ROWS, count, count, count);
        		headers.put(HttpHeaders.CONTENT_RANGE, Arrays.asList(contentRange.toString()));
        	}
        }

        return new ResponseEntity<>(result, headers, status);
    }

    public static ResponseEntity<byte[]> buildPDFResponse(byte[] pdfContent) {
        return buildPDFResponse(pdfContent, null);
    }
    
    public static ResponseEntity<byte[]> buildPDFResponse(byte[] pdfContent, String filename) {
        return buildPDFResponse(pdfContent, filename, null);
    }

    public static ResponseEntity<byte[]> buildPDFResponse(byte[] pdfContent,
            String filename, MediaType mediaType) {
        if (pdfContent == null || pdfContent.length == 0){
            return ResponseEntity.notFound().build();
        }
        if (filename == null){
            filename = Instant.now().getEpochSecond() + ".pdf";
        }
        HttpHeaders headers = new HttpHeaders();
        ContentDisposition cd = ContentDisposition.builder("inline").filename(filename).build();
        headers.setContentDisposition(cd);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        if(mediaType!=null) {
        	headers.setContentType(mediaType);
        }
        ResponseEntity<byte[]> response = new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
        return response;
    }
    
    public static ResponseEntity<byte[]> buildZIPResponse(byte[] zippedContent,
            String filename) {
        if (zippedContent == null || zippedContent.length == 0){
            return ResponseEntity.notFound().build();
        }
        if (filename == null){
            filename = Instant.now().getEpochSecond() + ".zip";
        }
        HttpHeaders headers = new HttpHeaders();
        ContentDisposition cd = ContentDisposition.builder("inline").filename(filename).build();
        headers.setContentDisposition(cd);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ResponseEntity<byte[]> response = new ResponseEntity<>(zippedContent, headers, HttpStatus.OK);
        return response;

    }

    public static ResponseEntity<byte[]> buildXLSResponse(byte[] xlsContent,
            String filename) {
        if (xlsContent == null || xlsContent.length == 0){
            return ResponseEntity.notFound().build();
        }
        if (filename == null){
            filename = Instant.now().getEpochSecond() + ".xls";
        }
        HttpHeaders headers = new HttpHeaders();
        ContentDisposition cd = ContentDisposition.builder("inline").filename(filename).build();
        headers.setContentDisposition(cd);
        ResponseEntity<byte[]> response = new ResponseEntity<>(xlsContent, headers, HttpStatus.OK);
        return response;
    }
    

    public static String uriToModule(String uri){
        if (uri == null){
            return null;
        }
        int i = uri.indexOf(UIApiConstants.API_URI+"/");
        if (i < 0) {
            return null;
        }
        int startIndex = i + UIApiConstants.API_URI.length() + 1;
        int endIndex = uri.indexOf("/", startIndex);
        if (endIndex==-1) {
        	endIndex = uri.length();
        }
        return uri.substring(startIndex, endIndex);
    }
}