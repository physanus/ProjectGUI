package de.danielprinz.ProjectGUI.gui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class DrawingArea extends Canvas {

    private ArrayList<Line> lines = new ArrayList<>();

    public DrawingArea(double width, double height) {
        super(width, height);
    }


    public void add(Line line) {
        this.lines.add(line);
    }


    public void add(int xOldTL, int yOldTL, int xTL, int yTL) {
        this.lines.add(new Line(xOldTL, yOldTL, xTL, yTL));
    }


    public void draw() {
        GraphicsContext graphicsContext = this.getGraphicsContext2D();
        graphicsContext.setFill(Color.BLACK);
        for(Line line : this.lines) {
            graphicsContext.strokeLine(line.getX1(), line.getY1(), line.getX2(), line.getY2());
        }
    }
}
