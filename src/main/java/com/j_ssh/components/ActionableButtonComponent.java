package com.j_ssh.components;

import com.j_ssh.model.objects.ActionData;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import lombok.Getter;

public class ActionableButtonComponent extends Button {
    @Getter
    private ActionData actionData;
    
    public ActionableButtonComponent(ActionData actionData) {
        super(actionData.getName());
        this.actionData = actionData;
        
        // Set up the button styling and tooltip
        this.getStyleClass().add("action-button");
        this.setMaxWidth(Double.MAX_VALUE);
        this.setPrefHeight(40);
        
        // Create tooltip showing the commands
        StringBuilder tooltipText = new StringBuilder("Commands:\n");
        for (int i = 0; i < actionData.getCommands().size(); i++) {
            tooltipText.append((i + 1)).append(". ").append(actionData.getCommands().get(i)).append("\n");
        }
        this.setTooltip(new Tooltip(tooltipText.toString()));
        
        // Set up click handler
        this.setOnAction(event -> {
            // This will be handled by the parent controller
            fireEvent(new ActionButtonClickEvent(this));
        });
    }
    
    /**
     * Custom event for when an action button is clicked
     */
    public static class ActionButtonClickEvent extends javafx.event.Event {
        public static final javafx.event.EventType<ActionButtonClickEvent> ACTION_BUTTON_CLICKED = 
            new javafx.event.EventType<>(javafx.event.Event.ANY, "ACTION_BUTTON_CLICKED");
        
        private final ActionableButtonComponent source;
        
        public ActionButtonClickEvent(ActionableButtonComponent source) {
            super(ACTION_BUTTON_CLICKED);
            this.source = source;
        }
        
        public ActionableButtonComponent getSource() {
            return source;
        }
    }
}
