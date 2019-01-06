package Progression_Fisher;

public abstract class Node {
    protected final Fisher_Main c;

    public Node(Fisher_Main main){
        this.c = main;
    }
    public abstract boolean validate();
    public abstract int execute();
}
