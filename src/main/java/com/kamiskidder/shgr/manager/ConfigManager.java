package com.kamiskidder.shgr.manager;

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.kamiskidder.shgr.SHGR;
import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;
import com.kamiskidder.shgr.module.Setting;
import com.kamiskidder.shgr.util.client.LogUtil;

public class ConfigManager {
    public static final String folder = SHGR.DIR_NAME + "/";

    public static void save() {
        File parent = new File(folder);
        parent.mkdirs();

        saveConfig();
        LogUtil.info("Saved Configs!");
    }

    public static void load() {
        loadConfig();
        LogUtil.info("Loaded Configs!");
    }

    private static void saveConfig() {
        for (Category c : Category.values()) {
            File category = new File(folder + c.name().toLowerCase());
            category.mkdirs();

            List<Module> modules = ModuleManager.getInstance().getModulesByCategory(c);
            for (Module module : modules) {
                Gson gson = new Gson();
                Map<String, Object> mappedSettings = new HashMap<>();
                for (Setting s : module.getSettings()) {
                    if (s.getValue() instanceof Color) {
                        Color color = (Color) s.getValue();
                        mappedSettings.put(s.getName(), color.getRed() + "," + color.getGreen() + "," + color.getBlue()
                                + "," + color.getAlpha());
                        continue;
                    }
                    mappedSettings.put(s.getName(), s.getValue());
                }

                Map<String, Object> moduleConfig = new HashMap<>();
                moduleConfig.put("enable", module.isToggled());
                moduleConfig.put("bind", module.getBind());
                moduleConfig.put("setting", mappedSettings);
                String json = gson.toJson(moduleConfig);
                File config = new File(folder + module.getCategory().name().toLowerCase() + "/"
                        + module.getName().toLowerCase() + ".json");
                FileWriter writer;
                try {
                    writer = new FileWriter(config);
                    writer.write(json);
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void loadConfig() {
        List<Module> modules = ModuleManager.getInstance().getModule();
        for (Module module : modules) {
            try {
                Path path = Paths.get(folder + module.getCategory().name().toLowerCase() + "/"
                        + module.getName().toLowerCase() + ".json");
                if (!Files.exists(path))
                    continue;
                String context = readAll(path);
                Gson gson = new Gson();
                Map<String, Object> mappedSettings = gson.fromJson(context, Map.class);
                mappedSettings.forEach((name, value) -> {
                    if (name.equalsIgnoreCase("enable") && (Boolean) value) {
                        module.enable();
                    }
                    if (name.equalsIgnoreCase("bind")) {
                        module.setBind(((Double) value).intValue());
                    }
                    if (name.equalsIgnoreCase("setting")) {
                        Map<String, Object> setting = (Map<String, Object>) value;
                        setting.forEach((_name, _value) -> {
                            for (Setting s : module.getSettings()) {
                                if (s.getName().equalsIgnoreCase(_name)) {
                                    if (s.getValue() instanceof Float) {
                                        s.setValue(((Double) _value).floatValue());
                                    } else if (s.getValue() instanceof Integer) {
                                        s.setValue(((Double) _value).intValue());
                                    } else if (s.getValue() instanceof Color) {
                                        String[] values = _value.toString().split(",");
                                        s.setValue(new Color(Integer.parseInt(values[0]), Integer.parseInt(values[1]),
                                                Integer.parseInt(values[2]), Integer.parseInt(values[3])));
                                    } else {
                                        s.setValue(_value);
                                    }
                                }
                            }
                        });
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String readAll(Path path) throws IOException {
        return Files.lines(path).reduce("", (prev, line) -> prev + line + System.getProperty("line.separator"));
    }
}
