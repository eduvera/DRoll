package com.viedmapp.timeddiceroller;

import java.util.Random;

public class Dice {
    private int FACES;
    private int VALUE;
    private Random random = new Random();

    public Dice(){
        this.FACES = 6;
    }
    Dice(int faces){
        this.FACES = faces;
    }

    void roll(){
        VALUE = random.nextInt(FACES) + 1;
    }

    int getFACES() {
        return FACES;
    }

    public void setFACES(int FACES) {
        this.FACES = FACES;
    }

    int getVALUE() {
        return VALUE;
    }

    public void setVALUE(int VALUE) {
        this.VALUE = VALUE;
    }
}
