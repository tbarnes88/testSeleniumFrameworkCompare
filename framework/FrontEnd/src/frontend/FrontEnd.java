package frontend;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTextField;
import javax.swing.JLabel;

/**
 *
 * @author barnesto
 */
class FrontEnd extends JFrame {

    //col names must be changed here.
    String colNames[] = {"Item",
        "Location"};
    DefaultTableModel dtm = new DefaultTableModel(null, colNames);
    JTable table;
    private static final int HEADER_HEIGHT = 32;
    JFrame frame;
    public String pullS;
    public String placeS = "C:\\SeleniumResults";
    private File[] file;
    private File dir;
    private String fileImp;
    private String dirImp;
    private int curRow = 0;
    //says it's not used. But it is. Don't remove.
    private List copyList;
    private String currentPath = "C:\\testSeleniumFramework\\src\\test\\specs\\com\\selenium\\automatedTest";
    private final JCheckBox duplC;
    private final JButton openF, openFo, selFo, canF, Launch, foSel;
    private int selFoFl = 0;
    private JTextField resBox;
    private int headC = 1;
    private final JLabel resLab;
    private String lineC = "";
    private int flagF = 1;

    public FrontEnd() {
        setLocation(0, 0);
        setSize(1285, 750);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        table = new JTable(dtm) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0 && column != 1;
            }
        };
        final JComboBox comboBoxRun = new JComboBox();
        comboBoxRun.setEditable(true);
        comboBoxRun.addItem("Run all tests");
        comboBoxRun.addItem("Run all tests and then rerun Failed tests");
        comboBoxRun.addItem("Only run Failed tests");
        table.getColumnModel().getColumn(0).setPreferredWidth(422);
        table.getColumnModel().getColumn(1).setPreferredWidth(460);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setRowSelectionAllowed(false);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setPreferredSize(new Dimension(100, HEADER_HEIGHT));
        table.getTableHeader().setFont(new Font(null, Font.BOLD, 12));
        table.getTableHeader().setBackground(Color.LIGHT_GRAY);
        table.getTableHeader().setPreferredSize(new Dimension(10000, 32));
        JScrollPane sp = new JScrollPane(table);
        JPanel subPanel = new JPanel();
        JPanel subPanel1 = new JPanel();
        openF = new JButton("Choose File");
        openFo = new JButton("Choose Folder");
        selFo = new JButton("Select Single Folder");
        canF = new JButton("Cancel");
        canF.setEnabled(false);
        resLab = new JLabel("Results Path");
        Launch = new JButton("Launch");
        foSel = new JButton("...");
        foSel.setPreferredSize(new Dimension(18, 18));
        resBox = new JTextField(placeS);
        resBox.setPreferredSize(new Dimension(500, 24));
        resBox.setEditable(false);
        duplC = new JCheckBox("Remove Duplicates");
        duplC.setSelected(true);
        subPanel.add(openF);
        subPanel.add(openFo);
        subPanel.add(selFo);
        subPanel.add(canF);
        subPanel.add(Launch);
        subPanel.add(duplC);
        subPanel.add(comboBoxRun);
        subPanel1.add(resLab);
        subPanel1.add(resBox);
        subPanel1.add(foSel);

        initUI();
        getContentPane().add(sp, BorderLayout.CENTER);
        getContentPane().add(subPanel, BorderLayout.SOUTH);
        getContentPane().add(subPanel1, BorderLayout.NORTH);
        comboBoxRun.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (comboBoxRun.getSelectedIndex() == 0) {
                    flagF = 1;
                }
                if (comboBoxRun.getSelectedIndex() == 1) {
                    flagF = 2;
                }
                if (comboBoxRun.getSelectedIndex() == 2) {
                    flagF = 3;
                }
            }
        });
        openF.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                openFile();
            }
        });
        openFo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                openFolder();
            }
        });
        selFo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                selFolder();
            }
        });
        canF.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                cancelSel();
            }
        });
        foSel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                resFol();
                resBox.setText(placeS);
            }
        });
        Launch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    doLaunch();
                } catch (IOException ex) {
                    Logger.getLogger(FrontEnd.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    //Bring up pop up on row/col of right click
                    JTable source = (JTable) e.getSource();
                    int row = source.rowAtPoint(e.getPoint());
                    int column = source.columnAtPoint(e.getPoint());
                    if (!source.isRowSelected(row) || !source.isColumnSelected(column)) {
                        source.changeSelection(row, column, false, false);
                    }
                    doPop(e);
                }
            }
        });
    }

    private void addRow() {
        if (table.getSelectedRow() != -1) {
            //inserts row below the selected one else bottom
            int numRow = table.getRowCount();
            dtm.insertRow(numRow + 1, ",".split(","));
        } else {
            dtm.addRow(",".split(","));
        }
    }

    private void openFile() {
        //opens a single file chooser (can select multiple), does not traverse folders.
        this.copyList = new ArrayList();
        final JFileChooser fc = new JFileChooser(currentPath);
        fc.setMultiSelectionEnabled(true);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter filterhtml = new FileNameExtensionFilter("HTML File (.html)", "html");
        fc.addChoosableFileFilter(filterhtml);
        fc.setFileFilter(filterhtml);
        int result = fc.showOpenDialog(FrontEnd.this);
        dir = fc.getCurrentDirectory();
        dirImp = dir.toString();
        int dirLen = dirImp.length();
        switch (result) {
            case JFileChooser.APPROVE_OPTION:
                for (File file1 : fc.getSelectedFiles()) {
                    fileImp = file1.toString();
                    boolean exists = false;
                    for (int i = 0; i < table.getRowCount(); i++) {
                        dir = fc.getCurrentDirectory();
                        dirImp = dir.toString();
                        String copyC = dtm.getValueAt(i, 0).toString();
                        if (duplC.isSelected()) {
                            if (fileImp.endsWith(copyC)) {
                                exists = true;
                                break;
                            }
                        }
                    }
                    if (!exists) {
                        addRow();
                        if (dirLen < 68) {
                            dtm.removeRow(curRow);
                            JOptionPane.showMessageDialog(frame, "Incorrect file location - please select from within a subfolder of automatedTest", "Location error", JOptionPane.PLAIN_MESSAGE);
                            openFile();
                        } else {
                            dtm.setValueAt(fileImp.substring(67), curRow, 0);
                            dtm.setValueAt(dirImp.substring(67), curRow, 1);
                            curRow++;
                            if (headC == 1) {
                                if (fileImp.substring(67).endsWith(dirImp.substring(67) + ".html")) {
                                    curRow--;
                                    dtm.removeRow(curRow);
                                }
                            }
                        }
                    }
                }
            case JFileChooser.CANCEL_OPTION:
                break;
        }
    }

    private void openFolder() {
        //opens all htmls in a folder other than the one with same name as folder
        final JFileChooser fc = new JFileChooser(currentPath);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fc.showOpenDialog(FrontEnd.this);
        file = fc.getSelectedFiles();
        dir = fc.getSelectedFile();
        switch (result) {
            case JFileChooser.APPROVE_OPTION:
                dirImp = dir.toString();
                int dirLen = dirImp.length();
                if (dirLen < 67) {
                    JOptionPane.showMessageDialog(frame, "Incorrect folder location - please select a subfolder of automatedTest", "Location error", JOptionPane.PLAIN_MESSAGE);
                    openFolder();
                } else {
                    File[] filesInDirectory = dir.listFiles();
                    for (File file1 : filesInDirectory) {
                        String fileS = file1.toString();
                        fileS.substring(fileS.lastIndexOf('.') + 1);
                        if (fileS.contains("html")) {
                            fileImp = file1.toString();
                            boolean exists = false;
                            for (int i = 0; i < table.getRowCount(); i++) {
                                String copyC = dtm.getValueAt(i, 0).toString();
                                if (duplC.isSelected()) {
                                    if (fileImp.contains(copyC)) {
                                        exists = true;
                                        break;
                                    }
                                }
                            }
                            if (!exists) {
                                addRow();
                                dtm.setValueAt(fileImp.substring(67), curRow, 0);
                                dtm.setValueAt(dirImp.substring(67), curRow, 1);
                                curRow++;
                                if (headC == 1) {
                                    if (fileImp.substring(67).endsWith(dirImp.substring(67) + ".html")) {
                                        curRow--;
                                        dtm.removeRow(curRow);
                                    }
                                }
                            }
                        }
                    }
                }
            case JFileChooser.CANCEL_OPTION:
                break;
        }
    }

    private void selFolder() {
        //selects a single folder, then makes table uneditable other than launch, sel res folder and cancel, gui table different, just shows folder
        final JFileChooser fc = new JFileChooser(currentPath);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fc.showOpenDialog(FrontEnd.this);
        dir = fc.getSelectedFile();
        switch (result) {
            case JFileChooser.APPROVE_OPTION:
                dirImp = dir.toString();
                int dirLen = dirImp.length();
                dtm.getDataVector().removeAllElements();
                dtm.fireTableDataChanged();
                curRow = 0;
                if (dirLen < 67) {
                    JOptionPane.showMessageDialog(frame, "Incorrect folder location - please select a subfolder of automatedTest", "Location error", JOptionPane.PLAIN_MESSAGE);
                    selFolder();
                } else {
                    addRow();
                    dtm.setValueAt("You have chosen the folder '" + dirImp.substring(67) + "' and all of its subfolders.", 0, 0);
                    dtm.setValueAt(dirImp.substring(67), 0, 1);
                    if (table.getRowCount() > 0) {
                        openF.setEnabled(false);
                        openFo.setEnabled(false);
                        selFo.setEnabled(false);
                        canF.setEnabled(true);
                    }
                    selFoFl = 1;
                }
            case JFileChooser.CANCEL_OPTION:
                break;
        }
    }

    private void cancelSel() {
        //resets launch changes.
        dtm.getDataVector().removeAllElements();
        dtm.fireTableDataChanged();
        openF.setEnabled(true);
        openFo.setEnabled(true);
        selFo.setEnabled(true);
        canF.setEnabled(false);
        selFoFl = 0;
        curRow = 0;
    }

    private void doLaunch() throws IOException {
        //kicks off the launch after confirm and check if not null, two possibilities. Select Folder sets flag and does folder. All others just makes a list.
        int reply = JOptionPane.showConfirmDialog(null, "Are you sure you want to launch?", "Launch?", JOptionPane.YES_NO_OPTION);
        if (reply == JOptionPane.YES_OPTION) {
            int numRow = table.getRowCount();
            if (numRow < 1) {
                JOptionPane.showMessageDialog(frame, "You need to select tests to launch.");
                return;
            }
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("C:\\testSeleniumFramework\\framework\\runSelectedTests.bat"))) {
                if (selFoFl == 0) {
                    if (numRow < 2) {
                        pullS = table.getValueAt(0, 0).toString();
                    }
                    if (numRow > 1) {
                        for (int i = 0; i < numRow; i++) {
                            Object o = table.getValueAt(i, 0);
                            pullS += ",";
                            pullS += o.toString();
                        }
                    }
                    if (pullS.contains("null,")) {
                        pullS = pullS.substring(5);
                    }
                    if (pullS.startsWith(",")) {
                        pullS = pullS.substring(1);
                    }
                }
                if (flagF == 1) {
                    if (selFoFl == 0) {
                        lineC = "mvn clean test exec:java -Dconcordion.output.dir=\"" + placeS + "\" -Dexec.args=\"" + pullS + "\"";
                    } else if (selFoFl == 1) {
                        lineC = "mvn clean test exec:java -Dconcordion.output.dir=\"" + placeS + "\" -Dexec.args=\"" + dtm.getValueAt(0, 1).toString() + "\\\"";
                    }
                }
                if (flagF == 2) {
                    if (selFoFl == 0) {
                        lineC = "mvn clean test exec:java -Dconcordion.output.dir=\"" + placeS + "\" -Dexec.args=\"" + pullS + " Y\"";
                    } else if (selFoFl == 1) {
                        lineC = "mvn clean test exec:java -Dconcordion.output.dir=\"" + placeS + "\" -Dexec.args=\"" + dtm.getValueAt(0, 1).toString() + "\\ Y\"";
                    }
                }
                if (flagF == 3) {
                    if (selFoFl == 0) {
                        lineC = "mvn clean test exec:java -Dconcordion.output.dir=\"" + placeS + "\" -Dexec.args=\"" + pullS + " ONLYFAIL\"";

                    } else if (selFoFl == 1) {
                        lineC = "mvn clean test exec:java -Dconcordion.output.dir=\"" + placeS + "\" -Dexec.args=\"" + dtm.getValueAt(0, 1).toString() + "\\ ONLYFAIL\"";
                    }
                }
                bw.write("Echo \"Launching tests..." + "\r\ncd C:\\testSeleniumFramework\r\n" + lineC + "\r\n");
            }
            pullS = "";
            //System.out.println(lineC);
            Process p = Runtime.getRuntime().exec("cmd /c start C:\\testSeleniumFramework\\framework\\runSelectedTests.bat");
        } else {
        }
    }

    public void resFol() {
        final JFileChooser fc = new JFileChooser(placeS);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fc.showOpenDialog(FrontEnd.this);
        dir = fc.getSelectedFile();
        switch (result) {
            case JFileChooser.APPROVE_OPTION:
                dirImp = dir.toString();
                placeS = dirImp;
        }
    }

    class PopUpDemo extends JPopupMenu {

        //right click Delete Row option only
        JMenuItem delR;

        public PopUpDemo() {
            delR = new JMenuItem("Delete Row(s)");
            add(delR);
            delR.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (dtm.getRowCount() > 0 && table.getSelectedRow() != -1) {
                        int[] selRow = table.getSelectedRows();
                        for (int i = 0; i < selRow.length; i++) {
                            dtm.removeRow(selRow[i] - i);
                            curRow--;
                        }
                    }
                }
            });
        }
    }

    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) {
            doPop(e);
        }
    }

    private void doPop(MouseEvent e) {
        PopUpDemo menu = new PopUpDemo();
        menu.show(e.getComponent(), e.getX(), e.getY());
        table.editCellAt(-1, -1);
    }

    public final void initUI() {
        //File Exit only
        JMenuBar menubar = new JMenuBar();
        JMenu fileM = new JMenu("File");
        fileM.setMnemonic(KeyEvent.VK_F);
        JMenuItem exitM = new JMenuItem("Exit");
        exitM.setMnemonic(KeyEvent.VK_X);
        JMenu helpM = new JMenu("Help");
        helpM.setMnemonic(KeyEvent.VK_H);
        JMenuItem about = new JMenuItem("About");
        exitM.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });
        about.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                JOptionPane.showMessageDialog(frame, "Version : 1.2\n\nFor support contact : thomas.ttt.barnes@gmail.com", "Front End", JOptionPane.PLAIN_MESSAGE);
            }
        });
        fileM.add(exitM);
        helpM.add(about);
        menubar.add(fileM);
        menubar.add(helpM);
        setJMenuBar(menubar);
    }

    public static void main(String[] args) {
        FrontEnd mainFrame = new FrontEnd();
        mainFrame.setVisible(true);
    }
}
