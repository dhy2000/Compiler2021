package frontend.error;

import java.util.Iterator;
import java.util.TreeSet;

public class ErrorTable implements Iterable<Error> {

    @Override
    public Iterator<Error> iterator() {
        return errors.iterator();
    }

    public ErrorTable() {}

    private final TreeSet<Error> errors = new TreeSet<>();

    public void add(Error e) {
        errors.add(e);
    }

    public boolean isEmpty() {
        return errors.isEmpty();
    }
}
