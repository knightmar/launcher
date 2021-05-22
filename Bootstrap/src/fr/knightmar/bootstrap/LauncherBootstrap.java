package fr.knightmar.bootstrap;


import fr.theshark34.openlauncherlib.LaunchException;
import fr.theshark34.openlauncherlib.external.ClasspathConstructor;
import fr.theshark34.openlauncherlib.external.ExternalLaunchProfile;
import fr.theshark34.openlauncherlib.external.ExternalLauncher;
import fr.theshark34.openlauncherlib.minecraft.util.GameDirGenerator;
import fr.theshark34.openlauncherlib.util.CrashReporter;
import fr.theshark34.openlauncherlib.util.SplashScreen;
import fr.theshark34.openlauncherlib.util.explorer.ExploredDirectory;
import fr.theshark34.openlauncherlib.util.explorer.Explorer;
import fr.theshark34.supdate.SUpdate;

import java.io.File;

import static fr.theshark34.swinger.Swinger.getResource;
import static fr.theshark34.swinger.Swinger.setResourcePath;

public class LauncherBootstrap {

    private static final File KN_B_DIR = new File(GameDirGenerator.createGameDir("Server Modu"), "Launcher");
    private static final CrashReporter KN_B_REPORTER = new CrashReporter("Modu bootstrap", KN_B_DIR);
    private static SplashScreen splash;
    private static Thread barThread;

    public static void main(String[] args) {
        setResourcePath("/fr/knightmar/bootstrap/resources/");
        displaySplash();

        try {
            doUpdate();
        } catch (Exception e) {
            KN_B_REPORTER.catchError(e, "Impossible de mettre à jour le Launcher");
            barThread.interrupt();
        }

        try {
            launchLauncher();
        } catch (LaunchException e) {
            KN_B_REPORTER.catchError(e, "Impossible de lancer le Launcher");
        }


    }

    private static void displaySplash() {
        splash = new SplashScreen("Launcher", getResource("splash.png"));
        splash.setLayout(null);

        splash.setVisible(true);
    }

    private static void doUpdate() throws Exception {
        SUpdate su = new SUpdate("http://supdate.benetnath.fr/bootstrap/", new File(KN_B_DIR, "bootstrap"));
        su.getServerRequester().setRewriteEnabled(true);

        su.start();
    }

    private static void launchLauncher() throws LaunchException {

        ClasspathConstructor constructor = new ClasspathConstructor();
        ExploredDirectory gameDir = Explorer.dir(KN_B_DIR);
        constructor.add(gameDir.sub("Libs").allRecursive().files().match("^(.*\\.((jar)$))*$"));
        constructor.add(gameDir.get("launcher.jar"));

        ExternalLaunchProfile profile = new ExternalLaunchProfile("fr.knightmar.Launcher.LauncherFrame", constructor.make());
        ExternalLauncher launcher = new ExternalLauncher(profile);

        Process p = launcher.launch();

        splash.setVisible(false);

        try {
            p.waitFor();
        } catch (InterruptedException ignored) {
        }
        System.exit(0);
    }
}
