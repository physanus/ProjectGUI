package de.danielprinz.ProjectGUI.gui;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class MouseDraggedListener implements EventHandler<MouseEvent> {

    private int movementRadius;
    private final double X_LAYOUT, Y_LAYOUT;
    private final double WIDTH, HEIGHT;
    private final double SPEED;

    public MouseDraggedListener(Node node, int movementRadius, double width, double height, double speed) {
        this.movementRadius = movementRadius;
        this.X_LAYOUT = node.getLayoutX();
        this.Y_LAYOUT = node.getLayoutY();
        this.WIDTH = width;
        this.HEIGHT = height;
        this.SPEED = speed;
    }


    @Override
    public void handle(MouseEvent e) {

        if(
            !e.getEventType().equals(MouseEvent.MOUSE_DRAGGED) ||
            !e.getButton().equals(MouseButton.PRIMARY) && !e.getButton().equals(MouseButton.SECONDARY)
        ) {
            return;
        }


        MouseButton mouseButton = e.getButton();
        double sceneX = e.getSceneX();
        double sceneY = e.getSceneY();

        // get direction vector from center
        double dx = sceneX - (X_LAYOUT + WIDTH / 2);
        double dy = sceneY - (Y_LAYOUT + HEIGHT / 2);


        Node node = (Node) e.getSource();
        double posX = node.getLayoutX();
        double posY = node.getLayoutY();

        node.setLayoutX(posX + SPEED * dx);
        node.setLayoutY(posY + SPEED * dy);
    }
}
