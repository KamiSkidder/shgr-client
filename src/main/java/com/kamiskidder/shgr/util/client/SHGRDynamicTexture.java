package com.kamiskidder.shgr.util.client;

import net.minecraft.client.renderer.texture.DynamicTexture;

public class SHGRDynamicTexture {
    public DynamicTexture texture;
    public int width, height;

    public SHGRDynamicTexture(DynamicTexture texture, int width, int height) {
        this.texture = texture;
        this.width = width;
        this.height = height;
    }
}
