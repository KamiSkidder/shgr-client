package com.kamiskidder.shgr.util.render;

import com.kamiskidder.shgr.mixin.mixins.AccessorSplashProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.FileResourcePack;
import net.minecraft.client.resources.FolderResourcePack;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.SplashProgress;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.asm.FMLSanityChecker;
import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.util.Iterator;

import static net.minecraftforge.fml.client.SplashProgress.checkGLError;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_BGRA;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_INT_8_8_8_8_REV;

public class SplashTexture {
    private final int name;
    private final int width;
    private final int height;
    private final int frames;
    private final int size;
    private static final IntBuffer buf = BufferUtils.createIntBuffer(4 * 1024 * 1024);
    private static final IResourcePack mcPack = Minecraft.getMinecraft().defaultResourcePack;
    private static final IResourcePack fmlPack = createResourcePack(FMLSanityChecker.fmlLocation);

    public SplashTexture(String location) {
        IResourcePack miscPack = AccessorSplashProgress.getMiscPack();
        InputStream s = null;
        try {
            s = open(location);
            ImageInputStream stream = ImageIO.createImageInputStream(s);
            Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);
            if (!readers.hasNext()) throw new IOException("No suitable reader found for image" + location);
            ImageReader reader = readers.next();
            reader.setInput(stream);
            int frames = reader.getNumImages(true);
            BufferedImage[] images = new BufferedImage[frames];
            FMLLog.log.info("Found {} frames in {}", frames, location);
            for (int i = 0; i < frames; i++) {
                images[i] = reader.read(i);
            }
            reader.dispose();
            width = images[0].getWidth();
            int height = images[0].getHeight();
            // Animation strip
            if (height > width && height % width == 0) {
                frames = height / width;
                BufferedImage original = images[0];
                height = width;
                images = new BufferedImage[frames];
                for (int i = 0; i < frames; i++) {
                    images[i] = original.getSubimage(0, i * height, width, height);
                }
            }
            this.frames = frames;
            this.height = height;
            int size = 1;
            while ((size / width) * (size / height) < frames) size *= 2;
            this.size = size;
            glEnable(GL_TEXTURE_2D);
            synchronized (SplashProgress.class) {
                name = glGenTextures();
                glBindTexture(GL_TEXTURE_2D, name);
            }
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, size, size, 0, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, (IntBuffer) null);
            checkGLError("Texture creation");
            for (int i = 0; i * (size / width) < frames; i++) {
                for (int j = 0; i * (size / width) + j < frames && j < size / width; j++) {
                    buf.clear();
                    BufferedImage image = images[i * (size / width) + j];
                    for (int k = 0; k < height; k++) {
                        for (int l = 0; l < width; l++) {
                            buf.put(image.getRGB(l, k));
                        }
                    }
                    buf.position(0).limit(width * height);
                    glTexSubImage2D(GL_TEXTURE_2D, 0, j * width, i * height, width, height, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, buf);
                    checkGLError("Texture uploading");
                }
            }
            glBindTexture(GL_TEXTURE_2D, 0);
            glDisable(GL_TEXTURE_2D);
        } catch (Exception e) {
            FMLLog.log.error("Error reading texture from file: {}", location, e);
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(s);
        }
    }

    public int getName() {
        return name;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getFrames() {
        return frames;
    }

    public int getSize() {
        return size;
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, name);
    }

    public void delete() {
        glDeleteTextures(name);
    }

    public float getU(int frame, float u) {
        return width * (frame % (size / width) + u) / size;
    }

    public float getV(int frame, float v) {
        return height * (frame / (size / width) + v) / size;
    }

    public void texCoord(int frame, float u, float v) {
        glTexCoord2f(getU(frame, u), getV(frame, v));
    }

    private static InputStream open(String loc) throws IOException {
        return new FileInputStream(loc);
    }

    private static IResourcePack createResourcePack(File file) {
        if (file.isDirectory()) {
            return new FolderResourcePack(file);
        } else {
            return new FileResourcePack(file);
        }
    }

}