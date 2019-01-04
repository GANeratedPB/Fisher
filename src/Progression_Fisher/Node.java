package Progression_Fisher;

public abstract class Node {
    protected final Fighter_main c;

    public Node(Fighter_main main){
        this.c = main;
    }
    public abstract boolean validate();
    public abstract int execute();
}
