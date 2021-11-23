package com.rob.core.fetch.modules;

public abstract class AbstractFetchHandler<T> implements FetchHandler<T> {

	@Override
    public final boolean supportStrategy(Fetch.Option option) {
        if (supportKey(option) && option.getStrategy() == Fetch.Strategy.DEFAULT){
            return true;
        }

        return supportNonDefaultStrategy(option);
    }

    /**
     * Viene invocato per valutare se la strategy della option è supportata.
     * Il metodo viene invocato solo le la strategy non è DEFAULT in quanto
     * DEFAULT è sempre supportata
     * @param option
     * @return
     */
    protected abstract boolean supportNonDefaultStrategy (Fetch.Option option);

    @Override
    public final boolean supportMapping(Fetch.Option option) {
        if (supportKey(option) && option.getMapping() == Fetch.Mapping.DEFAULT){
            return true;
        }

        return supportNonDefaultMapping(option);
    }

    /**
     * Viene invocato per valutare se il mapping della option è supportato.
     * Il metodo viene invocato solo le il mapping non è DEFAULT in quanto
     * DEFAULT è sempre supportato
     * @param option
     * @return
     */
    protected abstract boolean supportNonDefaultMapping (Fetch.Option option);

}
