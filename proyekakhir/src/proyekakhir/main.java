/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyekakhir;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.xml.sax.SAXException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;

/**
 *
 * @author exphuda
 */
class AutoResizeTable {

    public void sesuaikanKolom(JTable t) {

        TableColumnModel modelKolom = t.getColumnModel();

        for (int kol = 0; kol < modelKolom.getColumnCount(); kol++) {
            int lebarKolomMax = 0;
            for (int baris = 0; baris < t.getRowCount(); baris++) {
                TableCellRenderer rend = t.getCellRenderer(baris, kol);
                Object nilaiTablel = t.getValueAt(baris, kol);
                Component comp = rend.getTableCellRendererComponent(t, nilaiTablel, false, false, baris, kol);
                lebarKolomMax = Math.max(comp.getPreferredSize().width, lebarKolomMax);
            }
            TableColumn kolom = modelKolom.getColumn(kol);
            kolom.setPreferredWidth(lebarKolomMax);
        }
    }
}

public class main extends javax.swing.JFrame {

    /**
     * Creates new form main
     */
    private config Config = new config();
    private boolean statusProgress = false;
    private int indexProgress = 0, maksimum = 0, fileUji = 0;
//    private removeEtc removeETC = null;
//    private Stemming Stem = null;
//    private StringTokenizer stringToken = null;

    public main() throws ClassNotFoundException {
        initComponents();
        this.setExtendedState(MAXIMIZED_BOTH);
        this.setLocationRelativeTo(this);
        if (Config.isConnected() == true) {
            connectionLabel.setText("Connected");
        } else {
            connectionLabel.setText("Not Connected");
        }
    }

    public void setpBar(int banyak) {
        statusProgress = true;
        maksimum = banyak - 1;
        pbar1.setMinimum(0);
        pbar1.setMaximum(maksimum);
    }

    public void prosesPbar(ActionEvent evt, int value) {

        indexProgress = value;
        listenerProgress.actionPerformed(evt);
    }

    private void viewFile() {
        tableFile.setModel(Config.viewFile());
        new AutoResizeTable().sesuaikanKolom(tableFile);
        tableFile.revalidate();
    }

    private void viewKorpus(int cbx) {
        tableKorpus.setModel(Config.viewKorpus(cbx));
        new AutoResizeTable().sesuaikanKolom(tableKorpus);
        tableKorpus.revalidate();
    }

    private String ekstrak(String PathFile) {
        String value = "";
        Parser parser = new AutoDetectParser();
        InputStream inStream = null;
        BodyContentHandler handler = null;

        try {
            parser = new AutoDetectParser();
            inStream = new FileInputStream(PathFile);
            handler = new BodyContentHandler();
            parser.parse(inStream, handler, new Metadata(), new ParseContext());
            value = handler.toString();
        } catch (IOException | SAXException | TikaException e) {
            e.printStackTrace();
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException ex) {

                }
            }
        }

        return value;
    }

    private void pembobotan2() {
        int nKorpus = Config.banyakKorpus();
        int banyakfile = Config.banyakFile();
        Vector<Double> pvektor = null;
        pvektor = new Vector<Double>();
        for (int i = 1; i <= nKorpus; i++) {
            String value = "";
            
            double kk = Config.getKK(i);
            double idf = Math.pow(Config.getIdf(i), 2);
            String TF = Config.getTF(i);
            value = value.concat(String.valueOf(kk*idf) +" ");
            
            int index = 0;
            for (String tfString : TF.split("\\s")) {
                int tf = Integer.valueOf(tfString);
                double temp = kk * tf;
                pvektor.set(index, pvektor.get(index) + Math.pow(temp, 2));
                if (temp == 0.0) {
                    value = value.concat("0 ");
                } else {
                    value = value.concat(temp + " ");
                }
                index++;
            }
            Config.updatetfidfKK(i, value);
            
        }

    }

    private void pembobotan(ActionEvent evt) {

        int nKorpus = Config.banyakKorpus();
        setpBar(nKorpus);
        int banyakfile = Config.banyakFile();
        Vector<Double> pvektor = null;
        double idf = 0.0;
        pvektor = new Vector<Double>();
        for (int i = 1; i <= nKorpus; i++) {
            String Korpus = Config.getKorpus(i);
            String TF = "";
            double DF = 0;
            for (int j = 1; j <= banyakfile; j++) {
                if (Config.isTF(Korpus, j) == true) {
                    TF = TF.concat(Config.getjumlahTF(Korpus, j) + " ");
                    DF++;
                } else {
                    TF = TF.concat("0 ");
                }
            }

            double dd = Math.log10(banyakfile / DF);
            String dds = String.valueOf(dd);

            String value1 = "";
            String value2 = "";

            for (int b = 0; b < banyakfile; b++) {
                pvektor.add(0.0);
            }

            int index = 0;
            for (String tfString : TF.split("\\s")) {
                int tf = Integer.valueOf(tfString);
                double temp = dd * tf;
                pvektor.set(index, pvektor.get(index) + Math.pow(temp, 2));
                if (temp == 0.0) {
                    value1 = value1.concat("0 ");
                } else {
                    value1 = value1.concat(temp + " ");
                }
                index++;
            }
            int ddf = (int) DF;
            Config.updateTf(i, TF, ddf, dds, value1);
            prosesPbar(evt, i);

        }
        for (int l = 0; l < banyakfile; l++) {
            Config.updatepvektor(l + 1, String.valueOf(pvektor.get(l)));
        }

    }

    private void preprocessing(ActionEvent evt, int cbx) {
        removeEtc removeETC = new removeEtc();
        Stemming Stem = new Stemming();
        StringTokenizer stringToken;
        setpBar(Config.banyakFile());
        int index = 1, indexdetail = 1;
        for (int i = 1; i <= Config.banyakFile(); i++) {
            String Line = Config.getValueFile(i);
            Line = removeETC.replace(Line);
            stringToken = new StringTokenizer(Line);

            while (stringToken.hasMoreTokens()) {
                String kt = stringToken.nextToken();
                kt = kt.toLowerCase();
                if (Config.isStoplist(kt) != true) {
                    if (cbx == 1) {
                        kt = kt;
                    } else if (cbx == 2) {
                        kt = Stem.proses(kt);
                    }

                    if (Config.isdetailKorpus(i, kt) != true) {
                        Config.adddetailKorpus(indexdetail, i, kt, 1);
                    } else {
                        Config.setdetailKorpus(i, kt);
                    }
                    indexdetail++;

                    if (Config.cekKorpus(kt) != true) {
                        Config.addKorpus(index, kt);
                        index++;
                    } else {
                        Config.setKorpus(kt);
                    }
                }
            }
            prosesPbar(evt, i);
            stringToken = null;

        }
        pembobotan(evt);
        stringToken = null;
        removeETC = null;
        Stem = null;
        viewKorpus(cbx);

    }

    private void importFile(ActionEvent evt) {
        String folder;
        File LokasiFolder = null;
        File listFilenya[];
        int banyakFile = 0;
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("choosertitle");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            LokasiFolder = chooser.getSelectedFile();
            pathFileTxt.setText("Path : " + LokasiFolder.toString());
            folder = LokasiFolder.toString();
            listFilenya = LokasiFolder.listFiles();
            banyakFile = listFilenya.length;
            setpBar(listFilenya.length);
            for (int i = 0; i < listFilenya.length; i++) {
                Config.setlistFile((i + 1), listFilenya[i].getName(), listFilenya[i].getPath(), ekstrak(LokasiFolder + "\\" + listFilenya[i].getName()));
                prosesPbar(evt, i);
            }
        }

        viewFile();
    }

    private void ujiFile(int cbx) {
        removeEtc removeETC = new removeEtc();
        Stemming Stem = new Stemming();
        int index = 1, indexdetail = 1;
        StringTokenizer stringToken;
        if (fileUji == 1) {
            String Line = Config.getValueFileKK(fileUji);
            Line = removeETC.replace(Line);
            stringToken = new StringTokenizer(Line);

            while (stringToken.hasMoreTokens()) {
                String kt = stringToken.nextToken();
                kt = kt.toLowerCase();
                if (Config.isStoplist(kt) != true) {
                    if (cbx == 1) {
                        kt = kt;
                    } else if (cbx == 2) {
                        kt = Stem.proses(kt);
                    }
                    if (Config.cekKorpus(kt) == true) {
                        Config.setKK(kt);
                    }
//                    if(Config.cektermKK(kt) != true){
//                       Config.addKorpusKK(index, kt);
//                       index++; 
//                    } else {
//                        Config.setKorpusKK(kt);
//                    }
                }
            }
            stringToken = null;
        }
        stringToken = null;
        removeETC = null;
        Stem = null;

//        for(int i = 1; i <= Config.banyakKorpusKK(); i++){
//            String term = Config.getKorpusKK(i);
//            int tf = Config.gettfKK(index);
//            if(Config.cekKorpus(term) != true){
//                Config.updatetfKK(term, tf);
//            }
//        }
        pembobotan2();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        pbar1 = new javax.swing.JProgressBar();
        notifTxt = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        connectionLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel6 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tableFile = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        pathFileTxt = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tableKorpus = new javax.swing.JTable();
        preprocessingBtn = new javax.swing.JButton();
        opsiCbx = new javax.swing.JComboBox<>();
        jPanel8 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        pathFileUji = new javax.swing.JLabel();
        browseFile = new javax.swing.JButton();
        prosesUji = new javax.swing.JButton();
        opsiCbx1 = new javax.swing.JComboBox<>();
        jPanel10 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Plagapp");
        setMinimumSize(new java.awt.Dimension(800, 500));

        jPanel1.setBackground(new java.awt.Color(153, 153, 153));

        pbar1.setStringPainted(true);

        notifTxt.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        notifTxt.setForeground(new java.awt.Color(255, 255, 255));
        notifTxt.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Status");

        connectionLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        connectionLabel.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(connectionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(notifTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(pbar1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pbar1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(notifTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(connectionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10))
        );

        jPanel2.setBackground(new java.awt.Color(153, 153, 153));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("SELAMAT DATANG");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbedPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jPanel6.setBackground(new java.awt.Color(153, 153, 153));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 825, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 460, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Main Utama", jPanel6);

        jPanel5.setBackground(new java.awt.Color(153, 153, 153));

        tableFile.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "No", "Nama File", "Path File"
            }
        ));
        jScrollPane4.setViewportView(tableFile);

        jButton1.setText("Import Dokumen + Ektrak");
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        pathFileTxt.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        pathFileTxt.setForeground(new java.awt.Color(255, 255, 255));
        pathFileTxt.setText("Path : ");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 805, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(pathFileTxt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pathFileTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Dokumen Training", jPanel5);

        jPanel7.setBackground(new java.awt.Color(153, 153, 153));

        tableKorpus.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "No", "Term", "Stem", "Freq"
            }
        ));
        jScrollPane5.setViewportView(tableKorpus);

        preprocessingBtn.setText("Preprocessing");
        preprocessingBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        preprocessingBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                preprocessingBtnActionPerformed(evt);
            }
        });

        opsiCbx.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-- Pilih --", "Term", "Stemm" }));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 805, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(preprocessingBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(opsiCbx, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(preprocessingBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(opsiCbx, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Korpus Training", jPanel7);

        jPanel8.setBackground(new java.awt.Color(153, 153, 153));

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Uji Dokumen", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP));

        browseFile.setText("Browse");
        browseFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseFileActionPerformed(evt);
            }
        });

        prosesUji.setText("Proses");
        prosesUji.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prosesUjiActionPerformed(evt);
            }
        });

        opsiCbx1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-- Pilih --", "Term", "Stemm" }));

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pathFileUji, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                    .addComponent(browseFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(prosesUji, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(opsiCbx1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(30, 30, 30))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(pathFileUji, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(browseFile, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(opsiCbx1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(prosesUji, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(196, Short.MAX_VALUE))
        );

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Hasil", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP));

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 471, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(10, 10, 10))
        );

        jTabbedPane1.addTab("Uji Dokumen", jPanel8);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        Config.removedFile();
        if (Config.isConnected() == true) {
            importFile(evt);
        } else {
            JOptionPane.showMessageDialog(rootPane, "Database is Not Connected");
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void preprocessingBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_preprocessingBtnActionPerformed
        // TODO add your handling code here:
        Config.removedDetKorpus();
        Config.removekorpus();
        preprocessing(evt, opsiCbx.getSelectedIndex());
    }//GEN-LAST:event_preprocessingBtnActionPerformed

    private void browseFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseFileActionPerformed
        // TODO add your handling code here:
        Config.removedFileUji();
        Config.removekorpusKK();
        String folder;
        File LokasiFolder = null;
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("choosertitle");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            LokasiFolder = chooser.getSelectedFile();
            pathFileUji.setText("Path : " + LokasiFolder.toString());
            folder = LokasiFolder.toString();
            fileUji++;
            Config.setFileKK(fileUji, LokasiFolder.getName(), LokasiFolder.getPath(), ekstrak(LokasiFolder.getPath()));
        }
    }//GEN-LAST:event_browseFileActionPerformed

    private void prosesUjiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prosesUjiActionPerformed
        // TODO add your handling code here:

        ujiFile(opsiCbx1.getSelectedIndex());
    }//GEN-LAST:event_prosesUjiActionPerformed

    ActionListener listenerProgress = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (statusProgress != false && indexProgress < maksimum) {
                    Thread.sleep(50);
                    pbar1.paintImmediately(0, 0, 200, 25);
                    pbar1.setValue(indexProgress);
                } else {
                    Thread.sleep(50);
                    pbar1.paintImmediately(0, 0, 200, 25);
                    pbar1.setValue(0);
                }

            } catch (InterruptedException ex) {

            }
        }

    };

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
            java.util.logging.Logger.getLogger(main.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(main.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(main.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(main.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new main().setVisible(true);

                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(main.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseFile;
    private javax.swing.JLabel connectionLabel;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel notifTxt;
    private javax.swing.JComboBox<String> opsiCbx;
    private javax.swing.JComboBox<String> opsiCbx1;
    private javax.swing.JLabel pathFileTxt;
    private javax.swing.JLabel pathFileUji;
    private javax.swing.JProgressBar pbar1;
    private javax.swing.JButton preprocessingBtn;
    private javax.swing.JButton prosesUji;
    private javax.swing.JTable tableFile;
    private javax.swing.JTable tableKorpus;
    // End of variables declaration//GEN-END:variables
}
