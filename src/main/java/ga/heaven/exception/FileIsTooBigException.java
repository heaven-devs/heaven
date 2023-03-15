package ga.heaven.exception;

public class FileIsTooBigException extends RuntimeException{

    private int sizeLimit;

    public FileIsTooBigException(int sizeLimit) {
        this.sizeLimit = sizeLimit;
    }

    public int getSizeLimit() {
        return sizeLimit;
    }
}
