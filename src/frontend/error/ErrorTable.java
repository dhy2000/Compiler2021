package frontend.error;

import frontend.error.exception.FrontendException;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class ErrorTable implements Iterable<FrontendException> {

    @Override
    public Iterator<FrontendException> iterator() {
        return frontendExceptions.iterator();
    }

    private static class InstanceHolder {
        private static final ErrorTable instance = new ErrorTable();
    }

    private ErrorTable() {}

    public static ErrorTable getInstance() {
        return InstanceHolder.instance;
    }

    private final Collection<FrontendException> frontendExceptions = new LinkedList<>();

    public void add(FrontendException e) {
        this.frontendExceptions.add(e);
    }

    public boolean contains(FrontendException e) {
        return frontendExceptions.contains(e);
    }

}
