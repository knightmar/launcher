package fr.knightmar.launcher;


import com.azuriom.azauth.AzAuthenticator;
import fr.theshark34.openlauncherlib.LaunchException;
import fr.theshark34.openlauncherlib.minecraft.AuthInfos;
import fr.theshark34.openlauncherlib.util.Saver;
import fr.theshark34.swinger.animation.Animator;
import fr.theshark34.swinger.colored.SColoredBar;
import fr.theshark34.swinger.event.SwingerEvent;
import fr.theshark34.swinger.event.SwingerEventListener;
import fr.theshark34.swinger.textured.STexturedButton;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

import static fr.theshark34.swinger.Swinger.*;


public class LauncherPanel extends JPanel implements SwingerEventListener {

    public static AuthInfos authInfos;
    private final Image background = getResource("background.png");
    private final Saver saver = new Saver(new File(Launcher.KN_DIR, "launcher.properties"));

    private final JTextField usernameField = new JTextField(this.saver.get("username"));
    private final JPasswordField passwordField = new JPasswordField();

    private final STexturedButton playButton = new STexturedButton(getResource("play_button.png"));
    private final STexturedButton hideButton = new STexturedButton(getResource("hide_button.png"));
    private final STexturedButton quitButton = new STexturedButton(getResource("quit_button.png"));
    private final STexturedButton discordButton = new STexturedButton(getResource("discord.png"));
    private final SColoredBar progressBar = new SColoredBar(getTransparentWhite(100), getTransparentWhite(175));
    private final JLabel infobar = new JLabel("Clique sur jouer !", SwingConstants.CENTER);


    public LauncherPanel() {
        setResourcePath("/");
        this.setLayout(null);
        usernameField.setForeground(Color.white);
        usernameField.setFont(usernameField.getFont().deriveFont(20F));
        usernameField.setCaretColor(Color.WHITE);
        usernameField.setOpaque(false);
        usernameField.setBorder(null);
        usernameField.setBounds(705, 200, 262, 39);
        this.add(usernameField);

        passwordField.setForeground(Color.white);
        passwordField.setFont(usernameField.getFont());
        passwordField.setCaretColor(Color.WHITE);
        passwordField.setOpaque(false);
        passwordField.setBorder(null);
        passwordField.setBounds(705, 280, 262, 39);
        this.add(passwordField);

        playButton.setBounds(750, 370);
        playButton.setSize(306 / 4, 321 / 4);
        playButton.addEventListener(this);
        this.add(playButton);

        hideButton.setBounds(880, 18);
        hideButton.setSize(30, 30);
        hideButton.addEventListener(this);
        this.add(hideButton);

        quitButton.setBounds(923, 18);
        quitButton.setSize(30, 30);
        quitButton.addEventListener(this);
        this.add(quitButton);

        discordButton.setBounds(50, 450);
        discordButton.setSize(30, 30);
        discordButton.addEventListener(this);
        this.add(discordButton);

        progressBar.setStringPainted(true);
        progressBar.setBounds(12, 517, 937, 20);
        this.add(progressBar);

        infobar.setBounds(12, 517, 937, 20);
        infobar.setForeground(Color.white);
        infobar.setFont(usernameField.getFont());
        this.add(infobar);
    }

    public static void openWebpage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void openWebpage(URL url) {
        try {
            openWebpage(url.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEvent(SwingerEvent event) {


        if (event.getSource() == playButton) {

            setFieldEnabled(false);

            if (usernameField.getText().replaceAll(" ", "").length() == 0 || passwordField.getText().length() == 0) {
                JOptionPane.showMessageDialog(this, "Erreur, veuillez entrer un pseudo et un mot de passe valides", "Erreur", JOptionPane.ERROR_MESSAGE);
                setFieldEnabled(true);
                return;
            }

            //Launcher.auth(usernameField.getText(), passwordField.getText());

            try {
                AzAuthenticator authenticator = new AzAuthenticator("http://azuriom.benetnath.fr/");
                authInfos = authenticator.authenticate(usernameField.getText(), passwordField.getText(), AuthInfos.class);
            } catch (com.azuriom.azauth.AuthenticationException | IOException e) {

                if (Objects.equals(e.getMessage(), "Invalid credentials")) {
                    JOptionPane.showMessageDialog(LauncherPanel.this, "Erreur, impossible de se connecter : Login ou mot de passe invalides", "Erreur", JOptionPane.ERROR_MESSAGE);
                    setFieldEnabled(true);
                    return;
                }
            }



            saver.set("username", usernameField.getText());

            try {
                Launcher.update();


            } catch (Exception exception) {
                Launcher.interruptTread();
                LauncherFrame.getCrashReporter().catchError(exception, "Impossible de mettre a jour");
                setFieldEnabled(true);
                return;
            }
            try {
                Launcher.launch();
                infobar.setText("Lancement du jeu en cours");
            } catch (LaunchException launchException) {
                LauncherFrame.getCrashReporter().catchError(launchException, "Impossible de lancer le jeu");
                setFieldEnabled(true);

            }
            System.out.println("ok");
        } else if (event.getSource() == quitButton) {
            Animator.fadeOutFrame(LauncherFrame.getInstance(), Animator.FAST, () -> System.exit(0));
        } else if (event.getSource() == hideButton) {
            LauncherFrame.getInstance().setState(JFrame.ICONIFIED);

        } else if (event.getSource() == discordButton) {
            try {
                URL url = new URL("https://discord.gg/jSAb75K8rU");
                openWebpage(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        graphics.drawImage(background, 0, 0, this.getWidth(), this.getHeight(), this);
    }

    public void setFieldEnabled(boolean enabled) {
        usernameField.setEnabled(enabled);
        passwordField.setEnabled(enabled);
        playButton.setEnabled(enabled);

    }

    public SColoredBar getProgressBar() {
        return progressBar;
    }

    public void setInfoText(String text) {
        infobar.setText(text);
    }

    public static AuthInfos getAuthInfos(){
        return authInfos;
    }
}
