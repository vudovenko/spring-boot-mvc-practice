package dev.vudovenko.springbootmvcpractice.managingID;

public abstract class IDManager {

    private Long idCounter;

    public IDManager() {
        this.idCounter = 0L;
    }

    protected Long getNextId() {
        return ++idCounter;
    }
}
