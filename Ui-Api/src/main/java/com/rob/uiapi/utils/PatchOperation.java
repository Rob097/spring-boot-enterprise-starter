package com.rob.uiapi.utils;


/**
 * Classe modello per le operazioni PATCH secondo lo standard <a href="https://tools.ietf.org/html/rfc6902">RFC 6902</a>
 */
public class PatchOperation {
    public enum Op {

        add,
        remove,
        replace,
        move,
        copy,
        test
    }
    private Op op;
    private String path;
    private JsonRaw value;
    private String from;

    /**
     * L'operazione da effettuare
     * @return
     */
    public Op getOp() {
        return op;
    }

    public void setOp(Op op) {
        this.op = op;
    }

    /**
     * Il path (json pointer secondo lo standard <a href="https://tools.ietf.org/html/rfc6901">RFC 6901</a>) su cui applicare l'operazione.
     * @return
     */
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Il valore da utilizzare per l'operazione richiesta
     * @return
     */
    public JsonRaw getValue() {
        return value;
    }

    public void setValue(JsonRaw value) {
        this.value = value;
    }

    /**
     * Il path (json pointer secondo lo standard <a href="https://tools.ietf.org/html/rfc6901">RFC 6901</a>) da copiare/spostare
     * in caso di operazioni di tipo {@link Op#copy} o {@link Op#move}
     * @return
     */
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
