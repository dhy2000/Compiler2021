package frontend.error.exception;

public interface InstantiatedException<T> {
    void thrower();

    InstantiatedException<T> catcher();
}
