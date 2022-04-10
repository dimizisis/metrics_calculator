package gui;

import calculator.MetricsCalculator;
import infrastructure.entities.Project;
import output.ResultPrinter;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.Dialog.ModalExclusionType;
import java.io.File;
import java.util.Objects;

public class GUI extends JFrame{

    private JFileChooser dirChooser;
    private JButton selectInDirBtn;
    private JButton selectOutDirBtn;
    private JButton calculateBtn;
    private JTextField inputDirTextField;
    private JTextField outputDirTextField;
    private JPanel loadingSpinnerPanel;

    /**
     * Create the frame.
     */
    public GUI(){
        init();
    }

    void init(){
        setModalExclusionType(ModalExclusionType.TOOLKIT_EXCLUDE);
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        GroupLayout gl_contentPane = new GroupLayout(contentPane);
        gl_contentPane.setHorizontalGroup(
                gl_contentPane.createParallelGroup(Alignment.LEADING)
                        .addGap(0, 424, Short.MAX_VALUE)
        );
        gl_contentPane.setVerticalGroup(
                gl_contentPane.createParallelGroup(Alignment.LEADING)
                        .addGap(0, 251, Short.MAX_VALUE)
        );
        contentPane.setLayout(gl_contentPane);

        JPanel panel = new JPanel();
        JPanel panelUp = new JPanel();
        JPanel panelDown = new JPanel();
        loadingSpinnerPanel = new JPanel();
        panelUp.setLayout(new GridLayout(2,2,10,10));
        selectInDirBtn = new JButton("...");
        selectInDirBtn.setHorizontalAlignment(JTextField.CENTER);
        selectInDirBtn.setVerticalAlignment(JTextField.CENTER);
        selectOutDirBtn = new JButton("...");
        selectOutDirBtn.setHorizontalAlignment(JTextField.CENTER);
        selectOutDirBtn.setVerticalAlignment(JTextField.CENTER);
        dirChooser = new JFileChooser();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 150, 5));
        Font ioFont = new Font("Courier", Font.PLAIN,10);
        inputDirTextField = new JTextField("Project's Root Directory Path...");
        inputDirTextField.setFont(ioFont);
        outputDirTextField = new JTextField("Output Directory Path...");
        outputDirTextField.setFont(ioFont);
        panelUp.add(inputDirTextField);
        panelUp.add(selectInDirBtn);
        panelUp.add(outputDirTextField);
        panelUp.add(selectOutDirBtn);
        panelUp.setMaximumSize(panelUp.getSize());
        panelDown.setLayout(new CardLayout(0, 0));
        loadingSpinnerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        loadingSpinnerPanel.setVisible(false);
        calculateBtn = new JButton("Calculate");
        panelDown.add(calculateBtn, "name_50649954272336");
        ImageIcon loading = new ImageIcon(Objects.requireNonNull(getClass().getResource("/ajax-loader.gif")));
        JLabel waitingSpinner = new JLabel("", loading, JLabel.CENTER);
        panel.add(panelUp);
        loadingSpinnerPanel.add(waitingSpinner);
        panel.add(panelDown);
        panel.add(loadingSpinnerPanel);
        this.setContentPane(panel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(370, 145));
        this.setVisible(true);
        setTitle("Metrics Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setBackground(new Color(227));

        calculateBtn.addActionListener(e -> EventQueue.invokeLater(() -> new CalculationThread().start()));

        selectInDirBtn.addActionListener(arg0 -> {
            dirChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            dirChooser.setDialogTitle("Choose Input Directory");
            dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (dirChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
                inputDirTextField.setText(dirChooser.getSelectedFile().getAbsolutePath());
        });

        selectOutDirBtn.addActionListener(arg0 -> {
            dirChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            dirChooser.setDialogTitle("Choose Output Directory");
            dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = dirChooser.showOpenDialog(null);
            if(returnVal == JFileChooser.APPROVE_OPTION)
                outputDirTextField.setText(dirChooser.getSelectedFile().getAbsolutePath());
        });
    }

    private class CalculationThread extends Thread{
        @Override
        public void run() {
            super.run();
            try {
                if (inputDirTextField.getText().isEmpty() || !new File(inputDirTextField.getText()).isDirectory()) {
                    JOptionPane.showMessageDialog(null, "No input directory is set.", "I/O Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (outputDirTextField.getText().isEmpty() || !(new File(outputDirTextField.getText()).isDirectory())) {
                    JOptionPane.showMessageDialog(null, "No output directory is set.", "I/O Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                calculateBtn.setEnabled(false);
                selectInDirBtn.setEnabled(false);
                selectOutDirBtn.setEnabled(false);
                inputDirTextField.setEnabled(false);
                outputDirTextField.setEnabled(false);
                loadingSpinnerPanel.setVisible(true);
                setSize(new Dimension(370, 185));
                Project project = new Project(inputDirTextField.getText().replace("\\", "/"));
                MetricsCalculator mc = new MetricsCalculator(project);
                if (mc.start() == -1) {
                    JOptionPane.showMessageDialog(null, "No classes could be identified!", "Parsing Error", JOptionPane.ERROR_MESSAGE);
                    init();
                    return;
                }
                ResultPrinter.printCSV(mc, outputDirTextField.getText() + "/analysis_" + System.currentTimeMillis() / 1000 + ".csv");
                JOptionPane.showMessageDialog(null, "Calculation complete! File with calculations saved in "+ outputDirTextField.getText(), "Success!", JOptionPane.INFORMATION_MESSAGE);
                init();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

}