package com.parallel.framework;

public class Level implements Comparable<Level> {

    private int level;

    private int increase;

    private boolean hasExec;

    public Level() {
        level = 100;
        increase = 10;
        hasExec = false;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void increaseLevel() {
        this.level = level + increase;
    }

    public boolean hasExec() {
        return hasExec;
    }

    public void exec() {
        this.hasExec = true;
    }

    @Override
    public int compareTo(Level o) {
        return level - o.level;
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj);
    }
}
