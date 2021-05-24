package fr.knightmar.launcher;

import com.sun.awt.AWTUtilities;
import fr.theshark34.openlauncherlib.util.CrashReporter;
import fr.theshark34.swinger.animation.Animator;
import fr.theshark34.swinger.util.WindowMover;

import javax.swing.*;

import static fr.theshark34.swinger.Swinger.*;

public class LauncherFrame extends JFrame {


    private static LauncherFrame instance;
    private static CrashReporter crashReporter;
    private LauncherPanel launcherPanel;


    public LauncherFrame() {
        this.setTitle("launcher du server Modu");
        this.setSize(975, 625);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setUndecorated(true);
        this.setIconImage(getResource("icon.png"));
        this.setContentPane(launcherPanel = new LauncherPanel());
        AWTUtilities.setWindowOpacity(this, 0.0F);

        WindowMover mover = new WindowMover(this);
        this.addMouseListener(mover);
        this.addMouseMotionListener(mover);


        this.setVisible(true);

        Animator.fadeInFrame(this, Animator.FAST);

    }


    public static void main(String[] args) {
        setSystemLookNFeel();
        setResourcePath("/");
        Launcher.KN_CRASH_DIR.mkdirs();
        crashReporter = new CrashReporter("Modu launcher", Launcher.KN_CRASH_DIR);

        instance = new LauncherFrame();
    }

    public static LauncherFrame getInstance() {
        return instance;
    }

    public static CrashReporter getCrashReporter() {
        return crashReporter;
    }


    public LauncherPanel getLauncherPanel() {
        return this.launcherPanel;
    }


}
