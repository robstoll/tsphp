package ch.tsphp.demo;

import ch.tsphp.HardCodedCompilerInitialiser;
import ch.tsphp.common.ICompiler;
import ch.tsphp.common.ICompilerListener;
import ch.tsphp.common.IErrorLogger;
import ch.tsphp.common.exceptions.TSPHPException;
import java.awt.event.KeyEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Executors;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.antlr.runtime.RecognitionException;

public class CompilerDemo extends javax.swing.JFrame implements ICompilerListener, IErrorLogger
{

    private final ICompiler compiler;
    private DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);

    /**
     * Creates new form CompilerDemo
     */
    public CompilerDemo() {
        initComponents();
        this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        String path = "./bin/tsphp.png";
        setIconImage(new ImageIcon(path).getImage());

        txtTSPHP.setTabSize(4);
        TextLineNumber tln = new TextLineNumber(txtTSPHP);
        scrollTSPHP.setRowHeaderView(tln);

        compiler = new HardCodedCompilerInitialiser().create(Executors.newSingleThreadExecutor());
        compiler.registerCompilerListener(this);
        compiler.registerErrorLogger(this);
    }

    /**
     * This method is called from within the constructor to initialise the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane2 = new javax.swing.JSplitPane();
        jSplitPane1 = new javax.swing.JSplitPane();
        scrollPHP = new javax.swing.JScrollPane();
        txtPHP =new javax.swing.JTextArea();
        scrollTSPHP = new javax.swing.JScrollPane();
        txtTSPHP = new UndoableTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtOutput = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("TSPHP Demonstration");
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                formKeyReleased(evt);
            }
        });
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        jSplitPane2.setDividerLocation(500);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane2.setResizeWeight(0.5);
        jSplitPane2.setPreferredSize(new java.awt.Dimension(800, 600));
        jSplitPane2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jSplitPane2KeyReleased(evt);
            }
        });

        jSplitPane1.setDividerLocation(200);
        jSplitPane1.setResizeWeight(0.5);
        jSplitPane1.setPreferredSize(new java.awt.Dimension(339, 500));

        txtPHP.setColumns(20);
        txtPHP.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtPHP.setRows(5);
        scrollPHP.setViewportView(txtPHP);

        jSplitPane1.setRightComponent(scrollPHP);

        txtTSPHP.setColumns(20);
        txtTSPHP.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtTSPHP.setRows(5);
        txtTSPHP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTSPHPKeyReleased(evt);
            }
        });
        scrollTSPHP.setViewportView(txtTSPHP);

        jSplitPane1.setLeftComponent(scrollTSPHP);

        jSplitPane2.setLeftComponent(jSplitPane1);

        txtOutput.setColumns(20);
        txtOutput.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtOutput.setRows(5);
        jScrollPane2.setViewportView(txtOutput);

        jSplitPane2.setRightComponent(jScrollPane2);

        getContentPane().add(jSplitPane2);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtTSPHPKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTSPHPKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_G && ((evt.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            if (!compiler.isCompiling()) {
                String tsphp = txtTSPHP.getText();
                if (!tsphp.isEmpty()) {
                    compiler.reset();
                    txtOutput.setText("");
                    txtPHP.setText("");
                    compiler.addCompilationUnit("demo", tsphp);
                    compiler.compile();
                } else {
                    JOptionPane.showMessageDialog(this, "Please provide some code, otherwise it is quite boring ;-)");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please be patient, compilation is still ongoing");
            }
        }
    }//GEN-LAST:event_txtTSPHPKeyReleased

    private void jSplitPane2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jSplitPane2KeyReleased
        txtTSPHPKeyReleased(evt);
    }//GEN-LAST:event_jSplitPane2KeyReleased

    private void formKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyReleased
        txtTSPHPKeyReleased(evt);
    }//GEN-LAST:event_formKeyReleased

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
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CompilerDemo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run() {
                new CompilerDemo().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JScrollPane scrollPHP;
    private javax.swing.JScrollPane scrollTSPHP;
    private javax.swing.JTextArea txtOutput;
    private javax.swing.JTextArea txtPHP;
    private UndoableTextArea txtTSPHP;
    // End of variables declaration//GEN-END:variables

    @Override
    public void afterParsingAndDefinitionPhaseCompleted() {
        txtOutput.append(
                dateFormat.format(new Date()) + ": Parsing and Definition phase completed\n"
                + "----------------------------------------------------------------------\n");
        txtOutput.setCaretPosition(txtOutput.getDocument().getLength());
    }

    @Override
    public void afterReferencePhaseCompleted() {
        txtOutput.append(
                "\n" + dateFormat.format(new Date()) + ": Reference phase completed\n"
                + "----------------------------------------------------------------------\n");
        txtOutput.setCaretPosition(txtOutput.getDocument().getLength());
    }

    @Override
    public void afterTypecheckingCompleted() {
        txtOutput.append(
                "\n" + dateFormat.format(new Date()) + ": Type checking completed\n"
                + "----------------------------------------------------------------------\n");
        txtOutput.setCaretPosition(txtOutput.getDocument().getLength());
    }

    @Override
    public void afterCompilingCompleted() {
        txtOutput.append("\n" + dateFormat.format(new Date()) + ": Compilation completed\n");

        Map<String, String> translations = compiler.getTranslations();
        txtPHP.setText(translations.get("demo"));
        txtOutput.setCaretPosition(txtOutput.getDocument().getLength());
    }

    @Override
    public void log(TSPHPException exception) {
        txtOutput.append(dateFormat.format(new Date()) + ": " + exception.getMessage() + "\n");
        Throwable throwable = exception.getCause();
        if (throwable != null && !(throwable instanceof RecognitionException)) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            throwable.printStackTrace(printWriter);
            txtOutput.append(stringWriter.toString());
        }
        txtOutput.setCaretPosition(txtOutput.getDocument().getLength());
    }
}