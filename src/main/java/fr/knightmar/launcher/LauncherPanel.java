package fr.knightmar.launcher;


import fr.litarvan.openauth.AuthenticationException;
import fr.theshark34.openlauncherlib.LaunchException;
import fr.theshark34.openlauncherlib.util.Saver;
import fr.theshark34.swinger.animation.Animator;
import fr.theshark34.swinger.colored.SColoredBar;
import fr.theshark34.swinger.event.SwingerEvent;
import fr.theshark34.swinger.event.SwingerEventListener;
import fr.theshark34.swinger.textured.STexturedButton;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import static fr.theshark34.swinger.Swinger.*;


public class LauncherPanel extends JPanel implements SwingerEventListener {

    private final Image background = getResource("background.png");



    private final Saver saver = new Saver(new File(Launcher.KN_DIR, "launcher.properties"));

    private final JTextField usernameField = new JTextField(this.saver.get("username"));
    private final JPasswordField passwordField = new JPasswordField();

    private final STexturedButton playButton = new STexturedButton(getResource("play_button.png"));
    private final STexturedButton hideButton = new STexturedButton(getResource("hide_button.png"));
    private final STexturedButton quitButton = new STexturedButton(getResource("quit_button.png"));
    private final SColoredBar progressBar = new SColoredBar(getTransparentWhite(100),getTransparentWhite(175));
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

        playButton.setBounds(341, 400);
        playButton.setSize(350, 118);
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

        progressBar.setStringPainted(true);
        progressBar.setBounds(12, 600, 950, 20);
        this.add(progressBar);

        infobar.setBounds(12, 600, 950, 20);
        infobar.setForeground(Color.white);
        infobar.setFont(usernameField.getFont());
        this.add(infobar);



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

            Thread t = new Thread() {
                @Override
                public void run() {

                    try {
                        Launcher.auth(usernameField.getText(), passwordField.getText());
                    } catch (AuthenticationException e) {
                        JOptionPane.showMessageDialog(LauncherPanel.this, "Erreur, impossible de se connecter" + e.getErrorModel().getErrorMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                        setFieldEnabled(true);
                        return;
                    }
                    


                    saver.set("username", usernameField.getText());


                    try {
                        Launcher.update();
                    } catch (Exception e) {
                        Launcher.interruptTread();
                        LauncherFrame.getCrashReporter().catchError(e, "Impossible de mettre a jour");
                        setFieldEnabled(true);
                        return;
                    }
                    try {
                        Launcher.launch();
                    } catch (LaunchException e) {
                        LauncherFrame.getCrashReporter().catchError(e, "Impossible de lancer le jeu");
                        setFieldEnabled(true);

                    }


                    System.out.println("ok");
                }
            };
            t.start();

        } else if (event.getSource() == quitButton) {
            Animator.fadeOutFrame(LauncherFrame.getInstance(), Animator.FAST, new Runnable() {
                @Override
                public void run() {
                    System.exit(0);
                }
            });
        } else if (event.getSource() == hideButton) {
            LauncherFrame.getInstance().setState(JFrame.ICONIFIED);
        }
    }

    @Override
    public void paintComponent(Graphics graphics) {
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


}
