package com.github.wargulwb.pixelcross;

import java.awt.image.BufferedImage;

import com.github.wargulwb.pixelcross.model.ImageModel;

public interface Painter {

    public BufferedImage paint(final ImageModel imageModel);

}
