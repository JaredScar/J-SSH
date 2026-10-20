package com.j_ssh.view;

import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/** Small building blocks shared by every page so they look and behave the same. */
public final class UiKit {
    public static final double COLUMN_WIDTH = 860;

    public static final String SEARCH = "\uE721";
    public static final String PLUS = "\uE710";
    public static final String MORE = "\uE712";
    public static final String COPY = "\uE8C8";
    public static final String PLAY = "\uE768";
    public static final String BOLT = "\uE945";
    public static final String CLOSE = "\uE711";
    public static final String EDIT = "\uE70F";
    public static final String RENAME = "\uE8AC";
    public static final String DELETE = "\uE74D";
    public static final String ERASER = "\uE75C";
    public static final String POWER = "\uE7E8";
    public static final String CHEVRON = "\uE76C";
    public static final String MONITOR = "\uE7F4";
    public static final String PROMPT = "\uE756";

    private UiKit() {
    }

    public static Label icon(String glyph) {
        Label label = new Label(glyph);
        label.getStyleClass().add("glyph");
        return label;
    }

    public static Button button(String text, String glyph, String... styleClasses) {
        Button button = new Button(text);
        button.getStyleClass().add("btn");
        button.getStyleClass().addAll(styleClasses);
        if (glyph != null) {
            button.setGraphic(icon(glyph));
            button.setGraphicTextGap(7);
        }
        return button;
    }

    public static Button iconButton(String glyph, String tooltip) {
        Button button = new Button(glyph);
        button.getStyleClass().addAll("btn", "btn-icon", "glyph");
        if (tooltip != null) {
            button.setTooltip(new Tooltip(tooltip));
        }
        return button;
    }

    /** A "..." button that opens a small menu of actions. */
    public static Button moreButton(MenuItem... items) {
        Button button = iconButton(MORE, "More");
        ContextMenu menu = new ContextMenu(items);
        button.setOnAction(event -> menu.show(button, Side.BOTTOM, 0, 4));
        return button;
    }

    public static MenuItem menuItem(String text, Runnable action) {
        MenuItem item = new MenuItem(text);
        item.setOnAction(event -> action.run());
        return item;
    }

    public static HBox searchBox(TextField field, String prompt) {
        field.setPromptText(prompt);
        field.getStyleClass().add("search-field");
        HBox box = new HBox(8, icon(SEARCH), field);
        box.getStyleClass().add("search-box");
        box.setAlignment(Pos.CENTER_LEFT);
        field.focusedProperty().addListener((obs, was, focused) -> {
            if (focused) {
                if (!box.getStyleClass().contains("search-focused")) {
                    box.getStyleClass().add("search-focused");
                }
            } else {
                box.getStyleClass().remove("search-focused");
            }
        });
        return box;
    }

    public static HBox pageHeader(String title, String subtitle, Node... trailing) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("header-title");
        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.getStyleClass().add("header-subtitle");
        VBox titles = new VBox(3, titleLabel, subtitleLabel);
        titles.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(10);
        header.getStyleClass().add("page-header");
        header.setAlignment(Pos.CENTER_LEFT);
        header.getChildren().addAll(titles, spacer);
        header.getChildren().addAll(trailing);
        return header;
    }

    /** Scrolls a centered column so every page lines up the same way. */
    public static ScrollPane scrollPage(Region content) {
        content.setMaxWidth(COLUMN_WIDTH);
        StackPane holder = new StackPane(content);
        holder.getStyleClass().add("page-body");
        holder.setAlignment(Pos.TOP_CENTER);

        ScrollPane scroll = new ScrollPane(holder);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("page-scroll");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        return scroll;
    }

    public static VBox emptyState(String glyph, String title, String text, Node action) {
        Label iconLabel = icon(glyph);
        iconLabel.getStyleClass().add("empty-icon");
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("empty-title");
        Label textLabel = new Label(text);
        textLabel.getStyleClass().add("empty-text");
        textLabel.setWrapText(true);

        VBox box = new VBox(10, iconLabel, titleLabel, textLabel);
        if (action != null) {
            box.getChildren().add(action);
        }
        box.getStyleClass().add("empty-state");
        box.setAlignment(Pos.CENTER);
        return box;
    }
}
