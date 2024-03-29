package com.j_ssh.view.bootstrap;

import javafx.scene.Node;

public class BootstrapColumn {

    private final Node content;

    int[] columnWidths = new int[]{
            1,
            -1,  //XS (default)
            -1, //Sm
            -1, //Md
            -1, //Lg
            -1,  //XL
            -1
    };

    public BootstrapColumn(Node content) {
        this.content = content;
    }

    public int clamp(int value, int min, int max) {

        if (max < min) throw new IllegalArgumentException("Cannot clamp when max is greater than min");

        if (value > max) {
            return max;
        } else if (value < min) {
            return min;
        } else {
            return value;
        }
    }

    public void setBreakpointColumnWidth(Breakpoint breakPoint, int width) {
        columnWidths[breakPoint.getValue()] = clamp(width, 1, 12);
    }

    public void unsetBreakPoint(Breakpoint breakPoint) {
        columnWidths[breakPoint.getValue()] = -1;
    }

    public void unsetAllBreakPoints() {
        this.columnWidths = new int[]{
                1,
                -1,  //XS (default)
                -1, //Sm
                -1, //Md
                -1, //Lg
                -1,  //XL
                -1
        };
    }

    public int getColumnWidth(Breakpoint breakPoint) {

        //Iterate through breakpoints, beginning at the specified bp, travelling down. Return first valid bp value.
        for (int i = breakPoint.getValue(); i >= 0; i--) {
            if (isValid(columnWidths[i])) return columnWidths[i];
        }

        //If none are valid, return 1
        return 1;
    }

    public Node getContent() {
        return content;
    }

    private boolean isValid(int value) {
        return value > 0 && value <= 12;
    }
}
