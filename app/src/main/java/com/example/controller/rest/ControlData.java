package com.example.controller.rest;

public class ControlData {
    private int speed;
    private double angle;
    private String focus;

    public ControlData(int speed, double angle, String focus) {
        this.speed = speed;
        this.angle = angle;
        this.focus = focus;
    }

    public int getSpeed() {
        return speed;
    }

    public double getAngle() {
        return angle;
    }

    public String getFocus() {
        return focus;
    }

    @Override
    public String toString() {
        return "ControlData{" +
                "speed=" + speed +
                ", angle=" + angle +
                ", focus='" + focus + '\'' +
                '}';
    }
}
