package de.danielprinz.ProjectGUI.gui;

import de.danielprinz.ProjectGUI.Main;
import de.danielprinz.ProjectGUI.drawing.DrawHelper;
import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class MouseListener {

    private static double prevX, prevY;

    private final ImageView node;
    private final int MOVEMENT_RADIUS;
    private final double LOSS;

    private int CROSSHAIR_POSITION_X;
    private int CROSSHAIR_POSITION_Y;
    private int cursorPositionX, cursorPositionY;

    public void reset() {
        cursorPositionX = 0;
        cursorPositionY = 0;
    }


    /**
     * Creates the listener wrapper
     * @param node The ImageView which should be able to be moved
     * @param movementRadius The radius in which the node should be allowed to move
     * @param loss The percent of the loss of the mouse movement (0..1)
     */
    public MouseListener(ImageView node, int movementRadius, double loss) {
        this.node = node;
        MOVEMENT_RADIUS = movementRadius;
        LOSS = loss;
    }

    public class MouseListenerPress implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent e) {
            if(Main.isUIDisabled) return;

            prevX = e.getSceneX();
            prevY = e.getSceneY();

            DrawHelper.runMovementChecker(node);
        }
    }

    public class MouseListenerDrag implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent e) {

            if(Main.isUIDisabled) return;

            double dx = e.getSceneX() - prevX;
            double dy = e.getSceneY() - prevY;

            double newX = node.getX() + dx * LOSS;
            double newY = node.getY() + dy * LOSS;

            double distance = distance(0, 0, newX, newY);
            if(distance < MOVEMENT_RADIUS) {
                // we are inside the circle
                node.setX(newX);
                node.setY(newY);
            }

            prevX = e.getSceneX();
            prevY = e.getSceneY();
        }

        /**
         * Uses pythagoras to calculate the distance between two points
         * @param x1 Point 1
         * @param y1 Point 1
         * @param x2 Point 2
         * @param y2 Point 2
         * @return The distance
         */
        private double distance(double x1, double y1, double x2, double y2) {
            double dx = x1 - x2;
            double dy = y1 - y2;
            return distance(dx, dy);
        }
        private double distance(double a, double b) {
            return Math.sqrt(a * a + b * b);
        }
    }

    public class MouseListenerReleased implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent e) {
            node.setX(0);
            node.setY(0);

            DrawHelper.stopMovementChecker();
        }
    }

}
