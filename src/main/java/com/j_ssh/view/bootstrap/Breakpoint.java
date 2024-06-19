package com.j_ssh.view.bootstrap;

public enum Breakpoint {
    XXSMALL(0),
    XSMALL(1),
    SMALL(2),
    MEDIUM(3),
    LARGE(4),
    XLARGE(5),
    XXLARGE(6);
    private int value;
    Breakpoint(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}
