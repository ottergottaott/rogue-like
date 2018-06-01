package core.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import core.Configuration;
import core.EventRouter;
import core.MainRouter;
import core.RoguelikeAppAdapter;

import static core.DefaultConfigurationKt.loadConfiguration;

/** Launches the desktop (LWJGL) application. */
public class DesktopLauncher {

    public static void main(String[] args) {
        createApplication();
    }

    private static LwjglApplication createApplication() {

        Configuration config = loadConfiguration();
        EventRouter mainRouter = new MainRouter();
        return new LwjglApplication(new RoguelikeAppAdapter(config, mainRouter), getApplicationConfiguration(config));
    }

    private static LwjglApplicationConfiguration getApplicationConfiguration(Configuration defaultConfig) {
        LwjglApplicationConfiguration configuration = new LwjglApplicationConfiguration();
        configuration.title = "rog";

        configuration.width = defaultConfig.getAppWidth();
        configuration.height = defaultConfig.getAppHeight();
        configuration.foregroundFPS = 0;
        for (int size : new int[] { 128, 64, 32, 16 }) {
            configuration.addIcon("libgdx" + size + ".png", FileType.Internal);
        }
        return configuration;
    }
}