package com.icebear.speechnote.model;

public class SignatureImage {
    public int id;
    public byte[] imgbitmap;

    public SignatureImage() {
        this.id = -1;
        this.imgbitmap = new byte[0];
    }

    public SignatureImage(int id, byte[] imgbitmap) {
        this.id = id;
        this.imgbitmap = imgbitmap;
    }
}
