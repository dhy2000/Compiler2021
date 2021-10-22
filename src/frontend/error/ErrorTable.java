package frontend.error;

import exception.FrontendException;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

public class ErrorTable implements Iterable<Error> {

    @Override
    public Iterator<Error> iterator() {
        return errors.iterator();
    }

    private static class InstanceHolder {
        private static final ErrorTable instance = new ErrorTable();
    }

    private ErrorTable() {}

    public static ErrorTable getInstance() {
        return InstanceHolder.instance;
    }

    private final TreeSet<Error> errors = new TreeSet<>();

    public void add(Error e) {
        errors.add(e);
    }

    public boolean isEmpty() {
        return errors.isEmpty();
    }
}
