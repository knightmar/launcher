package fr.knightmar.launcher;

import com.azuriom.azauth.*;
import fr.flowarg.openlauncherlib.NewForgeVersionDiscriminator;
import fr.litarvan.openauth.AuthenticationException;
import fr.theshark34.openlauncherlib.LaunchException;
import fr.theshark34.openlauncherlib.external.ExternalLaunchProfile;
import fr.theshark34.openlauncherlib.external.ExternalLauncher;
import fr.theshark34.openlauncherlib.minecraft.*;
import fr.theshark34.supdate.BarAPI;
import fr.theshark34.supdate.SUpdate;
import fr.theshark34.supdate.application.integrated.FileDeleter;

import javax.swing.*;
import java.io.File;
<<<<<<< HEAD
import java.nio.file.Path;
=======
import java.io.IOException;
>>>>>>> parent of b5227c6 (Add error management for Azuriom Auth)

import static fr.theshark34.swinger.Swinger.percentage;

public class Launcher {

    public static NewForgeVersionDiscriminator discriminator = new NewForgeVersionDiscriminator("36.1.0", "1.16.5","20210115.111550");
    public static final GameVersion KN_VERSION = new GameVersion("1.16.5", GameType.V1_13_HIGHER_FORGE.setNewForgeVersionDiscriminator(discriminator));
    public static final GameInfos KN_INFOS = new GameInfos("Mod knightmar", KN_VERSION, new GameTweak[]{});
    public static final File KN_DIR = KN_INFOS.getGameDir().toFile();
    public static final File KN_CRASH_DIR = new File(KN_DIR, "crash");

    private static Thread updateThread;


    public static void auth(String username, String password) throws AuthenticationException {
        AzAuthenticator authenticator = new AzAuthenticator("http://azuriom.benetnath.fr/");
        try {
            authInfos = authenticator.authenticate(username, password, AuthInfos.class);
        } catch (com.azuriom.azauth.AuthenticationException | IOException e) {
            e.printStackTrace();

        }

    }

    public static void update() throws Exception {
        SUpdate su = new SUpdate("http://supdate.benetnath.fr/", KN_DIR);
        su.getServerRequester().setRewriteEnabled(true);

        su.addApplication(new FileDeleter());



        updateThread = new Thread() {
            private int val;
            private int max;

            @Override
            public void run() {
                System.out.println("ok");
                while (!this.isInterrupted()) {
                    val = (int) BarAPI.getNumberOfTotalDownloadedBytes() / 1000;
                    max = (int) BarAPI.getNumberOfTotalBytesToDownload() / 1000;

                    if (BarAPI.getNumberOfFileToDownload() == 0) {
                        LauncherFrame.getInstance().getLauncherPanel().setInfoText("Verification des fichiers  ");
                        continue;
                    }


                    LauncherFrame.getInstance().getLauncherPanel().getProgressBar().setMaximum(max);
                    LauncherFrame.getInstance().getLauncherPanel().getProgressBar().setValue(val);

                    LauncherFrame.getInstance().getLauncherPanel().setInfoText("Telechargement des fichiers "
                            + BarAPI.getNumberOfDownloadedFiles()
                            + " /"
                            + BarAPI.getNumberOfFileToDownload()
                            + " "
                            + percentage(val, max) + "%");

                }
            }
        };
        updateThread.start();
        su.start();
        updateThread.interrupt();
    }

    public static void launch() throws LaunchException {
<<<<<<< HEAD
        AuthInfos authInfos = LauncherPanel.getAuthInfos();
        ExternalLaunchProfile profile = MinecraftLauncher.createExternalProfile(KN_INFOS, GameFolder.BASIC, authInfos);
=======

        ExternalLaunchProfile profile = MinecraftLauncher.createExternalProfile(KN_INFOS,GameFolder.BASIC,authInfos);
>>>>>>> parent of b5227c6 (Add error management for Azuriom Auth)

        ExternalLauncher launcher = new ExternalLauncher(profile);

        LauncherFrame.getInstance().setVisible(false);

        launcher.launch();
       System.exit(0);


    }

    public static void interruptTread() {
        updateThread.interrupt();
    }


}

