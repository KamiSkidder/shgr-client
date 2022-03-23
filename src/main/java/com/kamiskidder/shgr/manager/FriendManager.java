package com.kamiskidder.shgr.manager;

import com.kamiskidder.shgr.util.client.LogUtil;
import net.minecraft.entity.player.EntityPlayer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArraySet;

public class FriendManager {
    private static final CopyOnWriteArraySet<String> friends = new CopyOnWriteArraySet<>();

    public static void addFriend(String name) {
        friends.add(name.toLowerCase());
    }

    public static void removeFriend(String name) {
        friends.remove(name.toLowerCase());
    }

    public static boolean isFriend(String name) {
        return friends.contains(name.toLowerCase());
    }

    public static boolean isFriend(EntityPlayer player) {
        return friends.contains(player.getDisplayNameString().toLowerCase());
    }

    public static CopyOnWriteArraySet<String> getFriends() {
        return friends;
    }

    public static void save() {
        File parent = new File(ConfigManager.folder);
        parent.mkdirs();
        FileWriter writer;
        try {
            writer = new FileWriter(ConfigManager.folder + "friend.txt");
            writer.write(String.join(",", friends));
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        LogUtil.info("Saved friend list!");
    }

    public static void load() {
        Path path = Paths.get(ConfigManager.folder + "friend.txt");
        if (Files.exists(path)) {
            try {
                String content = readAll(path);
                friends.addAll(Arrays.asList(content.split(",")));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        LogUtil.info("Loaded friend list!");
    }

    private static String readAll(Path path) throws IOException {
        return Files.lines(path).reduce("", (prev, line) -> prev + line + System.getProperty("line.separator"));
    }
}
