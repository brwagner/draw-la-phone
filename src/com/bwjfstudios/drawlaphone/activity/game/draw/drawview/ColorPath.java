package com.bwjfstudios.drawlaphone.activity.game.draw.drawview;

import android.graphics.Path;

// Color stuff! Benno did this, he'll tell me about it later but everything looks like default stuff
class ColorPath {

    private int size;
    private int color;
    private Path path;

    public ColorPath(int size, int color, Path path) {
        this.size = size;
        this.color = color;
        this.path = path;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }
}