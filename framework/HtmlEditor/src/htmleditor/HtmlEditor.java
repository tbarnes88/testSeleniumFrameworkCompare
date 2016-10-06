package htmleditor;

import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JSeparator;
import javax.swing.table.*;
//import javax.swing.table.TableColumn;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author barnesto
 */
public class HtmlEditor extends JFrame {

    //Setting up column names, must be changed from here.  
    String colNames[] = {"", "<HTML>Row<BR> switch</HTML>", "Comment", "Locator Type", "Locator Text", "<HTML>Innertext</HTML>", "Action",
        "Input Value", "<HTML>Wait for<BR>Element(Secs)</HTML>", "Action Result", "Error Message:", "Output", "Result Name"};
    DefaultTableModel dtm = new DefaultTableModel(null, colNames);
    JTable table;
    private List copyList;
    private String copyCell;
    private String title;
    private File file;
    private String fileImp;
    private static final int HEADER_HEIGHT = 32;
    private int multipleFlag = 0;
    private String currentPath = "C:\\testSeleniumFramework\\src\\test\\specs\\com\\selenium\\automatedTest";
    JFrame frame;
    private int quitFlag = 0;

    public HtmlEditor() {
        setLocation(0, 0);
        setSize(1285, 750);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    exit();
                } catch (IOException ex) {
                    Logger.getLogger(HtmlEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        //Sets column 0 and 12 to be uneditable (select all column and action result)
        table = new JTable(dtm) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0 && column != 9;
            }
        };
        TableColumn actionCol = table.getColumnModel().getColumn(6);
        
        JComboBox comboBox = new JComboBox();
        comboBox.setEditable(true);
        comboBox.setEnabled(true);
        comboBox.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);

        String[] actionsL = {"callhtml", "click", "containstext", "doesnotexist",
            "endtest", "exists", "gettext", "navigate", "scrolltobottom", 
            "scrolltotop", "scrolltoelement", "sendkeys", "sendrandomemail", 
            "sendrandomemailconfirm", "set", "switchtoframebyname", "switchtoparent", "verifyhidden", "verifyvisible", "wait"};
        for (String item : actionsL) {
            comboBox.addItem(item);
        }

        actionCol.setCellEditor(new DefaultCellEditor(comboBox));

        table.getColumnModel().getColumn(0).setPreferredWidth(27);
        table.getColumnModel().getColumn(1).setPreferredWidth(50);
        table.getColumnModel().getColumn(2).setPreferredWidth(210);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(210);
        table.getColumnModel().getColumn(5).setPreferredWidth(90);
        table.getColumnModel().getColumn(6).setPreferredWidth(150);
        table.getColumnModel().getColumn(7).setPreferredWidth(160);
        table.getColumnModel().getColumn(8).setPreferredWidth(90);
        table.getColumnModel().getColumn(9).setMinWidth(0);
        table.getColumnModel().getColumn(9).setMaxWidth(0);
        table.getColumnModel().getColumn(9).setWidth(0);
        table.getColumnModel().getColumn(10).setMinWidth(0);
        table.getColumnModel().getColumn(10).setMaxWidth(0);
        table.getColumnModel().getColumn(10).setWidth(0);
        table.getColumnModel().getColumn(11).setMinWidth(0);
        table.getColumnModel().getColumn(11).setMaxWidth(0);
        table.getColumnModel().getColumn(11).setWidth(0);
        table.getColumnModel().getColumn(12).setPreferredWidth(90);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setRowSelectionAllowed(false);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setPreferredSize(new Dimension(100, HEADER_HEIGHT));
        table.getTableHeader().setFont(new Font(null, Font.BOLD, 12));
        table.getTableHeader().setBackground(Color.LIGHT_GRAY);
        table.setDragEnabled(true);
        table.getTableHeader().setPreferredSize(new Dimension(10000, 32));
        MyDragDropListener MyDragDropListener = new MyDragDropListener();
        DropTarget dropTarget = new DropTarget(table, MyDragDropListener);
        JPanel subPanel = new JPanel();
        JScrollPane sp = new JScrollPane(table);
        //set up UI and add a row
        addRow();
        initUI();
        dtm.setValueAt("PASS", 0, 9);
        table.setRowSelectionAllowed(false);
        getContentPane().add(sp, BorderLayout.CENTER);
        getContentPane().add(subPanel, BorderLayout.SOUTH);
        table.addKeyListener(new KeyAdapter() {
            @Override
            //ctrl c copy row if 0 copy cell if not
            public void keyPressed(KeyEvent e) {
                if ((e.getKeyCode() == KeyEvent.VK_C) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                    int col = table.getSelectedColumn();
                    if (col == 0) {
                        doCopy();
                    } else {
                        doCopyC();
                    }
                }
                //ctrl v, row if 0, otherwise cell
                if ((e.getKeyCode() == KeyEvent.VK_V) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                    int col = table.getSelectedColumn();
                    if (col == 0) {
                        doPaste();
                    } else {
                        doPasteC();
                    }
                }
                // ctrl s - save
                if ((e.getKeyCode() == KeyEvent.VK_S) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                    try {
                        outputFile();
                    } catch (IOException ex) {
                        Logger.getLogger(HtmlEditor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                //ctrl o - open
                if ((e.getKeyCode() == KeyEvent.VK_O) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                    try {
                        importFile();
                    } catch (IOException ex) {
                        Logger.getLogger(HtmlEditor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int col = table.columnAtPoint(e.getPoint());
                //if Col 0 then row selection is enabled, any other column disabled
                if (col < 1) {
                    table.setRowSelectionAllowed(true);
                } else {
                    table.setRowSelectionAllowed(false);
                }
            }

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

    class MyDragDropListener implements DropTargetListener {

        @Override
        public void drop(DropTargetDropEvent event) {
            // Accept copy drops
            event.acceptDrop(DnDConstants.ACTION_COPY);
            Transferable transferable = event.getTransferable();
            List<File> files;
            DataFlavor[] flavors = transferable.getTransferDataFlavors();
            for (DataFlavor flavor : flavors) {
                try {
                    // If the drop items are files
                    if (flavor.isFlavorJavaFileListType()) {
                        // Get all of the dropped files
                        files = (List) transferable.getTransferData(flavor);
                        // Loop them through      
                        fileImp = files.toString();
                        fileImp = fileImp.substring(1, fileImp.length() - 1);
                        doImport();
                    }
                } catch (UnsupportedFlavorException | IOException e) {
                }
            }
            event.dropComplete(true);
        }

        //these are needed so doesn't blow
        @Override
        public void dragEnter(DropTargetDragEvent event) {
        }

        @Override
        public void dragExit(DropTargetEvent event) {
        }

        @Override
        public void dragOver(DropTargetDragEvent event) {
        }

        @Override
        public void dropActionChanged(DropTargetDragEvent event) {
        }
    }

    public void outputFile() throws IOException {
        //Save option, sets file if title is set, if not then file chooser
        if (title == null) {
            outputFileAs();
        }
        save();
    }

    public void outputFileAs() throws IOException {
        //save option, file chooser
        final JFileChooser fc = new JFileChooser(currentPath);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter filterhtml = new FileNameExtensionFilter("HTML File (.html)", "html");
        fc.addChoosableFileFilter(filterhtml);
        fc.setFileFilter(filterhtml);
        int result1 = fc.showSaveDialog(HtmlEditor.this);
        switch (result1) {
            case JFileChooser.APPROVE_OPTION:
            file = fc.getSelectedFile();
            if (!fc.getSelectedFile().getAbsolutePath().endsWith(".html")) {
                file = new File(fc.getSelectedFile() + ".html");
            }
            if (file.exists()) {
                int result = JOptionPane.showConfirmDialog(this, "The file exists, overwrite?", "Existing file", JOptionPane.YES_NO_CANCEL_OPTION);
                switch (result) {
                    case JOptionPane.YES_OPTION:
                        break;
                    case JOptionPane.NO_OPTION:
                        outputFileAs();
                    case JOptionPane.CLOSED_OPTION:
                        return;
                    case JOptionPane.CANCEL_OPTION:
                        return;
                }
            }
            title = file.toString();
            save();
            case JFileChooser.CANCEL_OPTION:
            if (quitFlag == 1) {
                quitFlag = 0;
                exit();
            } else {
                break;
            }
        }
    }

    public void save() throws IOException {
        int finalV = 0;
        this.table.editCellAt(-1, -1);
        BufferedWriter bw = new BufferedWriter(new FileWriter(this.title));
        Throwable localThrowable2 = null;
        String title1 = title;
        String fileName = title1.substring(title1.lastIndexOf("\\") + 1, title1.length() - 5);
        String Header = "<table border=\"1\" concordion:execute=\"#result = performAction(#rowswitch,#comment,#bytype,#bytext,#byinnertext,#action,#inputvalue,#wait,#resultname)\">\n"
                + "<tr>\n"
                + "    <th concordion:set=\"#rowswitch\">Row switch</th>\n"
                + "    <th concordion:set=\"#comment\">Comment</th>\n"
                + "    <th concordion:set=\"#bytype\">Locator Type</th>\n"
                + "    <th concordion:set=\"#bytext\">Locator Text</th>\n"
                + "    <th concordion:set=\"#byinnertext\">Innertext</th>\n"
                + "    <th concordion:set=\"#action\">Action</th>\n"
                + "    <th concordion:set=\"#inputvalue\">Input value</th>\n"
                + "    <th concordion:set=\"#wait\">Wait For Element (seconds)</th>\n"
                + "    <th concordion:assertEquals=\"#result.getOutcome()\">Action Result</th>\n"
                + "    <th concordion:echo=\"#result.getErrorMessage()\">Error Message:</th>\n"
                + "    <th concordion:echo=\"#result.getOutput()\">Output:</th>\n"
                + "    <th concordion:set=\"#resultname\">Result Name (name of Script Variable)</th>\n"
                + "</tr>\n";
        try {
            bw.write("<html xmlns:concordion=\"http://www.concordion.org/2007/concordion\">\n"
                    + "<body>\n"
                    + "<h1>" + fileName + "</h1>\n");
            if (this.table.getValueAt(0, 1) == null) {
                this.table.setValueAt("", 0, 1);
            }
            if (this.table.getValueAt(0, 1).toString().equals("")) {
                bw.write(Header);
                finalV = 1;
            }
            int selRow = 0;
            //this sets comment possibility with * put in row switch
            for (int k = 0; k < this.dtm.getRowCount(); k++) {

                if (this.table.getValueAt(k, 1) == null) {
                    this.table.setValueAt("", k, 1);
                }
                if (this.table.getValueAt(k, 1).toString().startsWith("*")) {
                    if (k < 1) {
                        bw.write("<p>" + this.table.getValueAt(k, 2) + "</p>\n");
                        finalV = 1;
                    } else {
                        bw.write("</table>\n<p>" + this.table.getValueAt(k, 2) + "</p>\n");
                        finalV = 1;
                    }
                    selRow++;
                    if (table.getRowCount() > k) {
                        for (int l = k + 1; l < this.dtm.getRowCount(); l++) {
                            if (this.table.getValueAt(l, 1) == null) {
                                this.table.setValueAt("", l, 1);
                                bw.write(Header);
                                selRow++;
                                break;
                            }
                            if (dtm.getValueAt(l, 1).toString().startsWith("*")) {
                                bw.write("<p>" + dtm.getValueAt(l, 2) + "</p>\n");
                                finalV = 1;
                                selRow++;
                                k++;
                                continue;
                            } else {
                                bw.write(Header);
                            }
                            break;
                        }
                    } else {
                        bw.write(Header);
                    }
                    selRow++;
                    continue;
                }
                //process each line until end then suffix
                bw.write("    <tr>\n");
                String tValue;
                for (int i = 1; i < this.dtm.getColumnCount(); i++) {
                    if (this.table.getValueAt(k, i) == null) {
                        tValue = "";
                    } else {
                        tValue = this.table.getValueAt(k, i).toString();
                    }
                    if (i == 3 || i == 6 || i == 8 || i == 12) {
                        tValue = tValue.trim();
                    }
                    String line = "        <td>" + tValue + "</td>\n";
                    if (line.equals("        <td>null</td>\n")) {
                        line = "        <td></td>\n";
                    }
                    bw.write(line);
                }
                bw.write("    </tr>\n");
                finalV = 0;
                selRow++;
            }
            if (finalV < 1) {
                bw.write("</table>\n</body>\n</html>");
            } else {
                bw.write("</body>\n</html>");
            }
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(HtmlEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
        setTitle(this.title);
    }

    public void importFile() throws IOException {
        final JFileChooser fc = new JFileChooser(currentPath);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter filterhtml = new FileNameExtensionFilter("HTML File (.html)", "html");
        fc.addChoosableFileFilter(filterhtml);
        fc.setFileFilter(filterhtml);
        int result1 = fc.showOpenDialog(HtmlEditor.this);
        switch (result1) {
            case JFileChooser.APPROVE_OPTION:
                file = fc.getSelectedFile();
                fileImp = file.toString();
                doImport();
            case JFileChooser.CANCEL_OPTION:
                break;
        }
    }

    private void doImport() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileImp))) {
            String TD;
            int row = 0;
            int col = 1;
            //Remove all rows before import
            if (dtm.getRowCount() > 0) {
                for (int i = dtm.getRowCount() - 1; i > -1; i--) {
                    dtm.removeRow(i);
                }
            }
            dtm.addRow(",".split(","));

            //Read line by line for contains td, then trim, strip 4 (<td>) then -5 from end(</td>)
            while ((TD = br.readLine()) != null) {
                //imports comments without wait and result set
                if (TD.contains("<p>")) {
                    dtm.setValueAt("*", row, col);
                    dtm.setValueAt(TD.substring(3, TD.length() - 4), row, col + 1);
                    dtm.setValueAt("", row, 9);
                    dtm.addRow(",".split(","));
                    row++;
                }
                if (TD.contains("<td")) {
                    TD = TD.trim();
                    dtm.setValueAt(TD.substring(4, TD.length() - 5), row, col);
                    dtm.setValueAt("PASS", row, 9);
                    col++;
                    if (col > dtm.getColumnCount() - 1) {
                        dtm.addRow(",".split(","));
                        row++;
                        col = 1;
                    }
                }
            }
            int delR = dtm.getRowCount() - 1;
            dtm.removeRow(delR);
            if (dtm.getRowCount() < 1) {
                addRow();
            }
            title = fileImp;
            setTitle(title);
        }
    }

    private void addRow() {
        if (table.getSelectedRow() != -1) {
            //inserts row below the selected one
            int selRow = table.getSelectedRow();
            dtm.insertRow(selRow, ",".split(","));
            dtm.setValueAt("PASS", selRow, 9);
        } else {
            //row at bottom
            dtm.addRow(",".split(","));
            dtm.setValueAt("PASS", table.getSelectedRow() + 1, 9);
        }
    }
    
    private void addRowB() {
        if (table.getSelectedRow() != -1) {
            //inserts row below the selected one
            int selRow = table.getSelectedRow();
            dtm.insertRow(selRow + 1, ",".split(","));
            dtm.setValueAt("PASS", selRow + 1, 9);
        } else {
            //row at bottom
            dtm.addRow(",".split(","));
            dtm.setValueAt("PASS", table.getSelectedRow() + 1, 9);
        }
    }
    
    private void add5RowB() {
        if (table.getSelectedRow() != -1) {
            //inserts row below the selected one
            int selRow = table.getSelectedRow();
            for (int i = 1; i < 6; i++)
            {
                dtm.insertRow(selRow + 1, ",".split(","));
                dtm.setValueAt("PASS", selRow + 1, 9);
            }
        } else {
            //row at bottom
            dtm.addRow(",".split(","));
            dtm.setValueAt("PASS", table.getSelectedRow() + 1, 9);
        }
    }    

    public List doCopy() {
        //copy row
        table.setRowSelectionAllowed(true);
        this.copyList = new ArrayList();
        if (dtm.getRowCount() > 0 && table.getSelectedRow() != -1) {
            int[] selRow = table.getSelectedRows();
            for (int j = 0; j < selRow.length; j++) {
                for (int i = 1; i < dtm.getColumnCount(); i++) {
                    copyList.add(table.getValueAt(selRow[j], i));
                }
            }
            if (selRow.length != 1) {
                multipleFlag = 1;
            }
        }
        return copyList;
    }

    private void doPaste() {
        //paste row
        if (dtm.getRowCount() > 0 && table.getSelectedRow() != -1) {
            int selRow = table.getSelectedRow();
            int k = 0;
            for (int i = 1; i < dtm.getColumnCount(); i++) {
                dtm.setValueAt(copyList.get(k), selRow, i);
                k++;
            }
        }
    }

    private void doCopyPB() {
        //if the copy is empty then nothing should happen (bit iffy)...if not empty then pastes in a new row below right click.
        if (copyList.isEmpty()) {
        } else {
            if (table.getSelectedRow() != -1 && multipleFlag == 0) {
                int selRow = table.getSelectedRow();
                dtm.insertRow(selRow + 1, ",".split(","));
            }
            int rowL = copyList.size() / 12;
            if (table.getSelectedRow() != -1 && multipleFlag == 1) {
                int selRow = table.getSelectedRow();
                for (int i = 0; i < rowL; i++) {
                    dtm.insertRow(selRow + 1, ",".split(","));
                }
            }
            if (dtm.getRowCount() > 0 && table.getSelectedRow() != -1) {
                int selRow = table.getSelectedRow();
                int k = 0;
                for (int j = 0; j < rowL; j++) {
                    for (int i = 1; i < dtm.getColumnCount(); i++) {
                        dtm.setValueAt(copyList.get(k), selRow + 1, i);
                        k++;
                    }
                    selRow++;
                }
            }
        }
    }

    public String doCopyC() {
        //copy cell    
        this.copyCell = new String();
        if (dtm.getRowCount() > 0 && table.getSelectedRow() != -1) {
            int selRow = table.getSelectedRow();
            int selCol = table.getSelectedColumn();
            copyCell = (String) table.getValueAt(selRow, selCol);
        }
        return copyCell;
    }

    private void doPasteC() {
        //paste cell
        if (dtm.getRowCount() > 0 && table.getSelectedRow() != -1) {
            int selRow = table.getSelectedRow();
            int selCol = table.getSelectedColumn();
            if (selCol > 0) {
                dtm.setValueAt(copyCell, selRow, selCol);
            }
        }
    }

    private void doClr() {
        //clear cell
        if (dtm.getRowCount() > 0 && table.getSelectedRow() != -1) {
            int selRow = table.getSelectedRow();
            int selCol = table.getSelectedColumn();
            if (selCol == 9) {
            } else {
                dtm.setValueAt("", selRow, selCol);
            }
        }
    }

    private void doClrR() {
        //clear row
        if (dtm.getRowCount() > 0 && table.getSelectedRow() != -1) {
            int selRow = table.getSelectedRow();
            int selCol = table.getSelectedColumn();
            if (!(selCol == 9)) {
                dtm.setValueAt("", selRow, selCol);
            }
            for (int i = 1; i < dtm.getColumnCount(); i++) {
                if (i == 9) {
                    dtm.setValueAt("PASS", selRow, i);
                } else {
                    dtm.setValueAt("", selRow, i);
                }
            }
        }
    }
    
    private void delRow() {
        //If multiple select then will run through the array
        if (dtm.getRowCount() > 0 && table.getSelectedRow() != -1) {
            int[] selRow = table.getSelectedRows();
            for (int i = 0; i < selRow.length; i++) {
                dtm.removeRow(selRow[i] - i);
            }
        }
        if (dtm.getRowCount() < 1) {
            addRow();
        }
    }
    
    private void comRow() {
        //If multiple select then will run through the array
        if (dtm.getRowCount() > 0 && table.getSelectedRow() != -1) {
            int[] selRow = table.getSelectedRows();
            for (int i = 0; i < selRow.length; i++) {
                dtm.setValueAt("//", selRow[i], 1);
            }
        }
    }
    
    private void uncomRow() {
        //If multiple select then will run through the array
        if (dtm.getRowCount() > 0 && table.getSelectedRow() != -1) {
            int[] selRow = table.getSelectedRows();
            for (int i = 0; i < selRow.length; i++) {
                dtm.setValueAt("", selRow[i], 1);
            }
        }
    }

    public void exit() throws IOException {
        //exits, if yes then saves to open, if none open the filechooser. if no then saves to temp
        int exitQuestion = JOptionPane.showConfirmDialog(null, "Do you wish to save changes?\n" + "If No this session will be saved to C:\\SeleniumResults\\Temp.html", "Exit", JOptionPane.YES_NO_CANCEL_OPTION);
        if (exitQuestion == JOptionPane.YES_OPTION) {
            quitFlag = 1;
            outputFile();
            System.exit(0);
        }
        if (exitQuestion == JOptionPane.NO_OPTION) {
            title = "C:\\SeleniumResults\\temp.html";
            save();
            System.exit(0);
        }
    }

    public void newF() throws IOException {
        //new, to newY after save prompt
        int exitQuestion = JOptionPane.showConfirmDialog(null, "Do you want to save changes?\n", "New", JOptionPane.YES_NO_CANCEL_OPTION);
        if (exitQuestion == JOptionPane.YES_OPTION) {
            outputFile();
            newY();
        }
        if (exitQuestion == JOptionPane.NO_OPTION) {
            newY();
        }
    }

    public void newY() {
        //clears everything, removes all rows. Adds a row with default values
        if (dtm.getRowCount() > 0) {
            table.editCellAt(-1, -1);
            for (int i = dtm.getRowCount() - 1; i > -1; i--) {
                dtm.removeRow(i);
            }
        }
        dtm.addRow(",".split(","));
        dtm.setValueAt("PASS", 0, 9);
        setTitle(title = null);
    }
    
    public int testCount() {
        //clears everything, removes all rows. Adds a row with default values
        int totalTests = dtm.getRowCount();                   
        for (int i = 0; i < dtm.getRowCount(); i++) {
            if (table.getValueAt(i, 1)!= null && (table.getValueAt(i, 1).toString().trim().matches("\\*|//"))){
                totalTests--;
            }            
        }       
        return totalTests;
    }
    
    public void runTe() throws IOException{
        //Will run the test based on save location
        String curTitle = title;
        title = "C:\\testSeleniumFramework\\src\\test\\specs\\com\\selenium\\automatedTest\\test.html";
        save();
        setTitle(curTitle);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("C:\\testSeleniumFramework\\framework\\runSelectedTests.bat"))) {
            bw.write("Echo \"Launching tests...\n" +
            "cd C:\\testSeleniumFramework\n" +
            "mvn clean test exec:java -Dconcordion.output.dir=\"C:\\SeleniumResults\" -Dexec.args=\"test.html\"");
            Process p = Runtime.getRuntime().exec("cmd /c start C:\\testSeleniumFramework\\framework\\runSelectedTests.bat");
        }
        title = curTitle;
    }

    class PopUpDemo extends JPopupMenu {

        //Pop up menu
        JMenu submenu = new JMenu("Clear");
        JMenuItem addRA, addRB, add5RB, delR, copR, pasR, pasRB, comR, uncomR, clrR, copC, pasC, clrC;

        public PopUpDemo() {
            addRA = new JMenuItem("Add Row Above");
            addRB = new JMenuItem("Add Row Below");
            add5RB = new JMenuItem("Add 5 Rows Below");
            delR = new JMenuItem("Delete Row(s)");
            copR = new JMenuItem("Copy Row");
            pasR = new JMenuItem("Paste Row");
            pasRB = new JMenuItem("Paste Row Below");
            comR = new JMenuItem("Comment Row(s)");
            uncomR = new JMenuItem("Uncomment Row(s)");
            clrR = new JMenuItem("Clear Row");
            copC = new JMenuItem("Copy Cell");
            pasC = new JMenuItem("Paste Cell");
            clrC = new JMenuItem("Clear Cell");
            
            add(addRA);
            add(addRB);
            add(add5RB);
            add(delR);
            add(copR);
            add(pasR);
            add(pasRB);
            add(comR);
            add(uncomR);
            add(new JSeparator());
            add(submenu);
            submenu.add(clrC);
            submenu.add(clrR);
            add(new JSeparator());
            add(copC);
            add(pasC);

            addRA.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addRow();
                }
            });
            addRB.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addRowB();
                }
            });
            add5RB.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    add5RowB();
                }
            });
            
            delR.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    delRow();
                }
            });
            copR.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    table.setRowSelectionAllowed(true);
                    doCopy();
                }
            });
            pasR.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    table.setRowSelectionAllowed(false);
                    doPaste();
                }
            });
            pasRB.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    doCopyPB();
                }
            });
            comR.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    comRow();
                }
            });
            uncomR.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    uncomRow();
                }
            });
            clrR.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    doClrR();
                }
            });
            copC.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    doCopyC();
                }
            });
            pasC.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    doPasteC();
                }
            });
            clrC.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    doClr();
                }
            });
        }
    }
//these do the pop events for right click

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
        //File menu
        JMenuBar menubar = new JMenuBar();
        JMenu fileM = new JMenu("File");
        fileM.setMnemonic(KeyEvent.VK_F);
        JMenuItem newM = new JMenuItem("New");
        newM.setMnemonic(KeyEvent.VK_N);
        JMenuItem openM = new JMenuItem("Open");
        openM.setMnemonic(KeyEvent.VK_O);
        JMenuItem saveM = new JMenuItem("Save");
        saveM.setMnemonic(KeyEvent.VK_S);
        JMenuItem saveAM = new JMenuItem("Save As");
        saveAM.setMnemonic(KeyEvent.VK_A);
        JMenuItem exitM = new JMenuItem("Exit");
        exitM.setMnemonic(KeyEvent.VK_X);
        JMenu editM = new JMenu("Edit");
        editM.setMnemonic(KeyEvent.VK_E);
        JMenuItem addRw = new JMenuItem("Add Row");
        JMenuItem addRwA = new JMenuItem("Add Row Above");
        JMenuItem addRwB = new JMenuItem("Add Row Below");
        JMenuItem copRo = new JMenuItem("Copy Row");
        JMenuItem pasRo = new JMenuItem("Paste Row");
        JMenuItem delRo = new JMenuItem("Delete Row(s)");
        JMenu runM = new JMenu("Run");
        runM.setMnemonic(KeyEvent.VK_R);
        JMenuItem runT = new JMenuItem("Run current as Test");
        JMenu helpM = new JMenu("Help");
        helpM.setMnemonic(KeyEvent.VK_H);
        JMenuItem count = new JMenuItem("Test Count");
        JMenuItem about = new JMenuItem("About");
       

        newM.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    newF();
                } catch (IOException ex) {
                    Logger.getLogger(HtmlEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        openM.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    importFile();
                } catch (IOException ex) {
                    Logger.getLogger(HtmlEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        saveM.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    outputFile();
                } catch (IOException ex) {
                    Logger.getLogger(HtmlEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        saveAM.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    outputFileAs();
                } catch (IOException ex) {
                    Logger.getLogger(HtmlEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        exitM.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    exit();
                } catch (IOException ex) {
                    Logger.getLogger(HtmlEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        addRw.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                dtm.addRow(",".split(","));
                int rowNew = table.getRowCount();
                dtm.setValueAt("PASS", rowNew - 1, 9);
                table.clearSelection();
            }
        });
        addRwA.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                addRow();
                int rowNew = table.getRowCount();
                dtm.setValueAt("PASS", rowNew - 1, 9);
            }
        });
        addRwB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                addRowB();
                int rowNew = table.getRowCount();
                dtm.setValueAt("PASS", rowNew - 1, 9);
            }
        });
        copRo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                doCopy();
            }
        });
        pasRo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                doPaste();
            }
        });
        delRo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                delRow();
            }
        });
        runT.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent event )  {
                int exitQuestion = JOptionPane.showConfirmDialog(null, "This session will continue in a test file, you will need to resave your changes - proceed?\n", "Run as test file", JOptionPane.YES_NO_OPTION);
                if (exitQuestion == JOptionPane.YES_OPTION) {
                    try {
                        runTe();
                    } catch (IOException ex) {
                        Logger.getLogger(HtmlEditor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (exitQuestion == JOptionPane.NO_OPTION) {
                    
                }
            }
        });
        count.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                int testCount = testCount();
                JOptionPane.showMessageDialog(frame, "Number of Tests (excluding comments):\n\n" + testCount, "HTML Editor", JOptionPane.PLAIN_MESSAGE);
            }
        });
        about.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                JOptionPane.showMessageDialog(frame, "Version : 2.3\n\nFor support contact : thomas.ttt.barnes@gmail.com", "HTML Editor", JOptionPane.PLAIN_MESSAGE);
            }
        });
        fileM.add(newM);
        fileM.add(openM);
        fileM.add(saveM);
        fileM.add(saveAM);
        fileM.add(exitM);
        editM.add(addRw);
        editM.add(addRwA);
        editM.add(addRwB);
        editM.add(copRo);
        editM.add(pasRo);
        editM.add(delRo);
        runM.add(runT);
        helpM.add(count);
        helpM.add(about);
        menubar.add(fileM);
        menubar.add(editM);
        menubar.add(runM);
        menubar.add(helpM);
        setJMenuBar(menubar);
    }

    public static void main(String[] args) {
        new HtmlEditor().setVisible(true);
    }
}
