package ga.heaven.exception;

public class PetNotFoundException  extends RuntimeException{

    private Long id;

    public PetNotFoundException(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
