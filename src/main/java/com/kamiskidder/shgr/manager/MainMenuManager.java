package com.kamiskidder.shgr.manager;

public class MainMenuManager {
    public static String folder = ConfigManager.folder + "mainmenu/";

    /*
    public void init() {
        File dir = new File(folder);
        dir.mkdirs();

        if (!Files.exists(Paths.get(file))) {
            LogUtil.info("Downloading mainmenu video from github...");
            try {
                URL fetchWebsite = new URL("https://raw.githubusercontent.com/AzulineTeam/MainMenu/main/mainmenu.mp4");
                Path file = Files.createFile(Paths.get(MainMenuManager.file));
                try (InputStream inputStream = fetchWebsite.openStream()) {
                    Files.copy(inputStream, file, StandardCopyOption.REPLACE_EXISTING);
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        LogUtil.info("Loaded MainMenu!");
    }

     */
}
