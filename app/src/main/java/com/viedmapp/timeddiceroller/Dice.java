package com.viedmapp.timeddiceroller;

import java.util.Random;

public class Dice {
    private int faces;
    private int value;


    private int modifier;
    private static Random random = new Random();

    Dice(int faces) {
        this.faces = faces;
    }

    Dice(int faces, int value) {
        this.faces = faces;
        this.value = value;
    }

    void roll() {
        random.setSeed(random.nextLong());
        value = random.nextInt(faces) + 1;
    }

    int getFaces() {
        return faces;
    }

    public void setFaces(int faces) {
        this.faces = faces;
    }

    int getValue() {
        return value + getModifier();
    }

    public void setValue(int value) {
        this.value = value;
    }

    int getModifier() {
        return modifier;
    }

    public void setModifier(int modifier) {
        this.modifier = modifier;
    }

}