package io.plotnik.entran;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

/**
 * Утилита для поиска соответствий между английским оригиналом и русским переводом.
 *
 * Для работы с JSON можно использовать GSON:
 * - https://github.com/google/gson
 * - https://mvnrepository.com/artifact/com.google.code.gson/gson
 *
 * Вместо TagSoup можно использовать JSoup:
 * - https://jsoup.org/
 * - https://mvnrepository.com/artifact/org.jsoup/jsoup
 * - https://mvnrepository.com/artifact/org.ccil.cowan.tagsoup/tagsoup
 *
 * В качестве базы данных можно использовать SQLite:
 * - https://mvnrepository.com/artifact/org.xerial/sqlite-jdbc
 */
public class EnTranFrame extends javax.swing.JFrame {

    TranEngine tranEngine;

    private final static String GOOGLE_FONT = "EBGaramond-VariableFont.ttf";
    
    private final static boolean verbose = Main.verbose;

    public EnTranFrame() throws FontFormatException, IOException {
        Font googleFont = addFont("/" + GOOGLE_FONT, 32);
        initComponents();

        tranTextArea.setFont(googleFont);
        enText.setFont(googleFont);
                
        fixCopyPasteKeysOnMac(enText);
        fixCopyPasteKeysOnMac(tranTextArea);

        getContentPane().setBackground(Settings.background);
        enText.setBackground(Settings.background);

        setLocationRelativeTo(null);
    }

    void fixCopyPasteKeysOnMac(JTextArea textArea) {
        // Get the InputMap of the JTextArea when it is in focused window
        InputMap inputMap = textArea.getInputMap(JComponent.WHEN_FOCUSED);
        // Get the ActionMap of the JTextArea
        ActionMap actionMap = textArea.getActionMap();

        // Define the Command+C keystroke for copy
        KeyStroke copyKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx());
        // Map the Command+C keystroke to the "copy" action in the JTextArea
        inputMap.put(copyKeyStroke, "copy");

        // Likewise, define the Command+V keystroke for paste
        KeyStroke pasteKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx());
        // Map the Command+V keystroke to the "paste" action in the JTextArea
        inputMap.put(pasteKeyStroke, "paste");
    }

    Font addFont(String ttfFile, float fontSize) throws FontFormatException, IOException {
        // Load the font
        InputStream is = getClass().getResourceAsStream(ttfFile);
        Font font = Font.createFont(Font.TRUETYPE_FONT, is);
        // Scale the font
        Font scaledFont = font.deriveFont(Font.PLAIN, fontSize);

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(scaledFont);
        return scaledFont;
    } 

    void updateScreenText(boolean updateTextArea) {
        Sentence enSent = tranEngine.findParagraphEn();
        Sentence ruSent = tranEngine.findParagraphRu();
        //String ruText = tranEngine.getRuText();
        
        if (verbose) {
            enText.setText(
                    "EN." + tranEngine.getEnPos() + 
                    ".RU." + tranEngine.getRuPos() +
                    ".DB." + tranEngine.getDbPos() +
                    ": " + enSent.getS());
        } else {
            enText.setText(enSent.getS());
        }

        if (updateTextArea) {
            tranTextArea.setText(ruSent.getS());  
        } else {
            tranTextArea.setText(tranTextArea.getText() + "\n" + ruSent.getS());  
        }
    }

    public void setTranEngine(TranEngine tranEngine) {
        this.tranEngine = tranEngine;
        updateScreenText(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonPanel = new javax.swing.JPanel();
        prevRuButton = new javax.swing.JButton();
        nextRuButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        addButton = new javax.swing.JButton();
        addNextButton = new javax.swing.JButton();
        prevButton = new javax.swing.JButton();
        aboutButton = new javax.swing.JButton();
        splitPane = new javax.swing.JSplitPane();
        editScrollPane = new javax.swing.JScrollPane();
        tranTextArea = new javax.swing.JTextArea();
        jScrollPane1 = new javax.swing.JScrollPane();
        enText = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("EN TRAN");

        buttonPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        prevRuButton.setText("Prev RU");

        nextRuButton.setText("Next RU");

        nextButton.setText("Next");

        addButton.setText("Add");

        addNextButton.setText("Add Next");

        prevButton.setText("Prev");

        aboutButton.setText("About");

        javax.swing.GroupLayout buttonPanelLayout = new javax.swing.GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(aboutButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 643, Short.MAX_VALUE)
                .addComponent(prevButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nextButton)
                .addGap(18, 18, 18)
                .addComponent(prevRuButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nextRuButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addNextButton)
                .addContainerGap())
        );
        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(prevRuButton)
                    .addComponent(nextRuButton)
                    .addComponent(nextButton)
                    .addComponent(addButton)
                    .addComponent(addNextButton)
                    .addComponent(prevButton)
                    .addComponent(aboutButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        splitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(1.0);

        tranTextArea.setColumns(20);
        tranTextArea.setLineWrap(true);
        tranTextArea.setRows(5);
        tranTextArea.setWrapStyleWord(true);
        editScrollPane.setViewportView(tranTextArea);

        splitPane.setBottomComponent(editScrollPane);

        enText.setEditable(false);
        enText.setColumns(20);
        enText.setLineWrap(true);
        enText.setRows(5);
        enText.setWrapStyleWord(true);
        jScrollPane1.setViewportView(enText);

        splitPane.setTopComponent(jScrollPane1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(splitPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1057, Short.MAX_VALUE)
                    .addComponent(buttonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(splitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 660, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(EnTranFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EnTranFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EnTranFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EnTranFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new EnTranFrame().setVisible(true);
                } catch (FontFormatException ex) {
                    Logger.getLogger(EnTranFrame.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(EnTranFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    public static void setLookAndFeel(String lfname) {
        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if (lfname.equals(info.getName())) {
                try {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(EnTranFrame.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InstantiationException ex) {
                    Logger.getLogger(EnTranFrame.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(EnTranFrame.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedLookAndFeelException ex) {
                    Logger.getLogger(EnTranFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton aboutButton;
    public javax.swing.JButton addButton;
    public javax.swing.JButton addNextButton;
    private javax.swing.JPanel buttonPanel;
    public javax.swing.JScrollPane editScrollPane;
    private javax.swing.JTextArea enText;
    private javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JButton nextButton;
    public javax.swing.JButton nextRuButton;
    public javax.swing.JButton prevButton;
    public javax.swing.JButton prevRuButton;
    public javax.swing.JSplitPane splitPane;
    public javax.swing.JTextArea tranTextArea;
    // End of variables declaration//GEN-END:variables
}
