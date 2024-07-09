package com.example.daiquiri.AI;

import com.example.daiquiri.Student.Student;

public abstract class BaseAI extends Thread {
    protected int speed;
    public BaseAI(int speed) {
        this.speed = speed;
    }
    public abstract void move();
    }