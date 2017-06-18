/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyekakhir;

import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;
import java.sql.ResultSet;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author exphuda
 */


public class config {

    private final connnection Conn;
    private int exbanyak;
    private Statement s;
    private PreparedStatement preparedStmt;
    private ResultSet rs = null;
    private String query;
    DefaultTableModel model;

    public config() throws ClassNotFoundException {
        Conn = new connnection();
    }

    public boolean isConnected() {
        return Conn.Status();
    }

    public Integer banyakFile() {
        int value = 0;

        try {
            this.query = "select count(id) from tb_listFile";
            preparedStmt = (PreparedStatement) Conn.getConn().prepareStatement(this.query);
            rs = preparedStmt.executeQuery();
            while (rs.next()) {
                value = rs.getInt("count(id)");
            }
            rs.close();
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
        }
        return value;
    }

    public String getValueFile() {
        String value = "";

        try {
            this.query = "select valueFile from tb_listFile";
            preparedStmt = (PreparedStatement) Conn.getConn().prepareStatement(this.query);
            rs = preparedStmt.executeQuery();
            while (rs.next()) {
                value = value.concat(rs.getString("valueFile") + "\n");
            }
            rs.close();
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
        }
        return value;
    }

    public boolean isTF(String Korpus, int idFile) {
        boolean value = false;

        try {
            this.query = "select id from tb_detailkorpus where korpus = ? AND id_file = ?";
            preparedStmt = (PreparedStatement) Conn.getConn().prepareStatement(this.query);
            preparedStmt.setString(1, Korpus);
            preparedStmt.setInt(2, idFile);
            rs = preparedStmt.executeQuery();
            while (rs.next()) {
                if (rs.getString("id") != null) {
                    value = true;
                }
            }
            rs.close();
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
        }
        return value;
    }

    public String getjumlahTF(String Korpus, int idFile) {
        String value = "";

        try {
            this.query = "select jumlah from tb_detailkorpus where korpus = ? AND id_file = ?";
            preparedStmt = (PreparedStatement) Conn.getConn().prepareStatement(this.query);
            preparedStmt.setString(1, Korpus);
            preparedStmt.setInt(2, idFile);
            rs = preparedStmt.executeQuery();
            while (rs.next()) {
                if (rs.getInt("jumlah") > 0 || rs.getInt("jumlah") != 0) {
                    value = String.valueOf(rs.getInt("jumlah"));
                }
            }
            rs.close();
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
        }
        return value;
    }

    public boolean isStoplist(String token) {
        boolean value = false;

        try {
            this.query = "select stoplist from tb_stoplist where stoplist=?";
            preparedStmt = (PreparedStatement) Conn.getConn().prepareStatement(this.query);
            preparedStmt.setString(1, token);
            rs = preparedStmt.executeQuery();
            while (rs.next()) {
                if (rs.getString("stoplist") != null) {
                    value = true;
                }
            }
            rs.close();
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
        }
        return value;
    }

    public boolean isdetailKorpus(int idDoc, String term) {
        boolean value = false;

        try {
            this.query = "select * from tb_detailKorpus where korpus=? AND id_file=?";
            preparedStmt = (PreparedStatement) Conn.getConn().prepareStatement(this.query);
            preparedStmt.setString(1, term);
            preparedStmt.setInt(2, idDoc);
            rs = preparedStmt.executeQuery();
            while (rs.next()) {
                if (rs.getString("jumlah") != null) {
                    value = true;
                }
            }
            rs.close();
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
        }
        return value;
    }

    public String getValueFile(int index) {
        String value = "";

        try {
            this.query = "select valueFile from tb_listFile where id=?";
            preparedStmt = (PreparedStatement) Conn.getConn().prepareStatement(this.query);
            preparedStmt.setInt(1, index);
            rs = preparedStmt.executeQuery();
            while (rs.next()) {
                value = value.concat(rs.getString("valueFile"));
            }
            rs.close();
        } catch (Exception ex) {
            System.out.println("getValueFile Error: " + ex);
        }
        return value;
    }

    public String getValueFileKK(int index) {
        String value = "";

        try {
            this.query = "select valueFileKK from tb_FileKK where idKK=?";
            preparedStmt = (PreparedStatement) Conn.getConn().prepareStatement(this.query);
            preparedStmt.setInt(1, index);
            rs = preparedStmt.executeQuery();
            while (rs.next()) {
                value = value.concat(rs.getString("valueFileKK"));
            }
            rs.close();
        } catch (Exception ex) {
            System.out.println("getValueFileKK Error: " + ex);
        }
        return value;
    }

    public TableModel viewFile() {
        model = null;
        model = new DefaultTableModel();
        String[] o = new String[3];
        model.addColumn("No");
        model.addColumn("Nama File");
        model.addColumn("Path File");

        try {
            this.query = "select Id,namaFile,pathFile from tb_listFile";
            preparedStmt = (PreparedStatement) Conn.getConn().prepareStatement(this.query);
            rs = preparedStmt.executeQuery();
            while (rs.next()) {
                o[0] = rs.getString("Id");
                o[1] = rs.getString("namaFile");
                o[2] = rs.getString("pathFile");
                model.addRow(o);
            }
            rs.close();
        } catch (Exception ex) {
            System.out.println("getlistFile Error: " + ex);
        }
        return model;
    }

    public Vector getdetailKorpus(String korpus) {
        Vector value = new Vector();
        Integer[] o = new Integer[2];
        try {

            this.query = "select id_file, jumlah from tb_detailkorpus where korpus=?";
            preparedStmt = (PreparedStatement) Conn.getConn().prepareStatement(this.query);
            preparedStmt.setString(1, korpus);
            rs = preparedStmt.executeQuery();

            while (rs.next()) {
                o[0] = rs.getInt("id_file");
                o[1] = rs.getInt("jumlah");
                value.add(o);
            }
            rs.close();
        } catch (Exception ex) {
            System.out.println("getlistFile Error: " + ex);
        }
        return value;
    }

    public void removedFile() {
        try {
            this.query = "truncate table tb_listfile";
            preparedStmt = (PreparedStatement) Conn.getConn().prepareStatement(this.query);
            preparedStmt.execute();
        } catch (Exception ex) {
            System.out.println("removedFile Error: " + ex);
        }
    }

    public void removedDetKorpus() {
        try {
            this.query = "truncate table tb_detailKorpus";
            preparedStmt = (PreparedStatement) Conn.getConn().prepareStatement(this.query);
            preparedStmt.execute();
        } catch (Exception ex) {
            System.out.println("removedFile Error: " + ex);
        }
    }

    public void removedFileUji() {
        try {
            this.query = "truncate table tb_filekk";
            preparedStmt = (PreparedStatement) Conn.getConn().prepareStatement(this.query);
            preparedStmt.execute();
        } catch (Exception ex) {
            System.out.println("removedFileUji Error: " + ex);
        }
    }

    public void setlistFile(int No, String namaFile, String pathFile, String value) {
        try {
            this.query = " insert into tb_listFile (id, namaFile, pathFile, valueFile) values ( ?, ?, ?, ?)";
            preparedStmt = (PreparedStatement) Conn.getConn().prepareStatement(this.query);
            preparedStmt.setInt(1, No);
            preparedStmt.setString(2, namaFile);
            preparedStmt.setString(3, pathFile);
            preparedStmt.setString(4, value);
            preparedStmt.execute();
        } catch (Exception ex) {
            System.out.println("setlistFile Error: " + ex);
        }
    }

    public void setFileKK(int No, String namaFile, String pathFile, String value) {
        try {
            this.query = " insert into tb_filekk (idKK, namaFileKK, pathFileKK, valueFileKK) values ( ?, ?, ?, ?)";
            preparedStmt = (PreparedStatement) Conn.getConn().prepareStatement(this.query);
            preparedStmt.setInt(1, No);
            preparedStmt.setString(2, namaFile);
            preparedStmt.setString(3, pathFile);
            preparedStmt.setString(4, value);
            preparedStmt.execute();
        } catch (Exception ex) {
            System.out.println(" setFileKK Error: " + ex);
        }
    }

    public void adddetailKorpus(int index, int idDoc, String term, int jumlah) {
        try {
            this.query = " insert into tb_detailKorpus (id, id_file, korpus, jumlah) values (?, ?, ?, ?)";
            preparedStmt = (PreparedStatement) Conn.getConn().prepareStatement(this.query);
            preparedStmt.setInt(1, index);
            preparedStmt.setInt(2, idDoc);
            preparedStmt.setString(3, term);
            preparedStmt.setInt(4, jumlah);
            preparedStmt.execute();
        } catch (Exception ex) {
            System.out.println("addKorpus Error: " + ex);
        }
    }

    public void setdetailKorpus(int doc, String term) {
        try {
            this.query = "update tb_detailKorpus SET jumlah=jumlah+1 where korpus=? AND id_file=?";

            preparedStmt = (PreparedStatement) Conn.getConn().prepareStatement(this.query);
            preparedStmt.setString(1, term);
            preparedStmt.setInt(2, doc);
            preparedStmt.execute();
        } catch (Exception ex) {
            System.out.println("setKorpus Error: " + ex);
        }
    }

    public void removekorpus() {
        try {
            this.query = "truncate table tb_korpustraining";
            preparedStmt = (PreparedStatement) Conn.getConn().prepareStatement(this.query);
            preparedStmt.execute();
        } catch (Exception ex) {
            System.out.println("rekorpustraining Error: " + ex);
        }
    }

    public void removekorpusKK() {
        try {
            this.query = "truncate table tb_korpusKK";
            preparedStmt = (PreparedStatement) Conn.getConn().prepareStatement(this.query);
            preparedStmt.execute();
        } catch (Exception ex) {
            System.out.println("removekorpusKK Error: " + ex);
        }
    }

    public boolean cekKorpus(String korpus) {
        boolean value = false;

        try {
            this.query = "select * from tb_korpustraining where korpus = ?";
            preparedStmt = (PreparedStatement) Conn.getConn().prepareStatement(this.query);
            preparedStmt.setString(1, korpus);
            rs = preparedStmt.executeQuery();
            while (rs.next()) {
                if (rs.getString("korpus") != null) {
                    value = true;
                }
            }
            rs.close();
        } catch (Exception ex) {
            System.out.println("cekKorpus Error : " + ex);
        }
        return value;
    }

    public boolean cektermKK(String korpus) {
        boolean value = false;

        try {
            this.query = "select * from tb_korpuskk where termKK = ?";
            preparedStmt = (PreparedStatement) Conn.getConn().prepareStatement(this.query);
            preparedStmt.setString(1, korpus);
            rs = preparedStmt.executeQuery();
            while (rs.next()) {
                if (rs.getString("termKK") != null) {
                    value = true;
                }
            }
            rs.close();
        } catch (Exception ex) {
            System.out.println("cektermKK Error : " + ex);
        }
        return value;
    }

    public void addKorpus(int index, String korpus) {
        try {
            this.query = " insert into tb_korpustraining (id_korpus, korpus, freq) values (?, ?, ?)";
            preparedStmt = (PreparedStatement) Conn.getConn().prepareStatement(this.query);
            preparedStmt.setInt(1, index);
            preparedStmt.setString(2, korpus);
            preparedStmt.setInt(3, 1);
            preparedStmt.execute();
        } catch (Exception ex) {
            System.out.println("addKorpusT Error : " + ex);
        }
    }

    public void addKorpusKK(int index, String korpus) {
        try {
            this.query = " insert into tb_korpusKK (id_korpusKK, termKK, tfKK) values (?, ?, ?)";
            preparedStmt = (PreparedStatement) Conn.getConn().prepareStatement(this.query);
            preparedStmt.setInt(1, index);
            preparedStmt.setString(2, korpus);
            preparedStmt.setInt(3, 1);
            preparedStmt.execute();
        } catch (Exception ex) {
            System.out.println("addKorpusKK Error : " + ex);
        }
    }

    public void setKorpus(String korpus) {
        try {
            this.query = "update tb_korpustraining SET freq=freq+1 where korpus = ?";

            preparedStmt = (PreparedStatement) Conn.getConn().prepareStatement(this.query);
            preparedStmt.setString(1, korpus);
            preparedStmt.execute();
        } catch (Exception ex) {
            System.out.println("setKorpus Error: " + ex);
        }
    }

    public void setKK(String korpus) {
        try {
            this.query = "update tb_korpustraining SET kk=kk+1 where korpus = ?";

            preparedStmt = (PreparedStatement) Conn.getConn().prepareStatement(this.query);
            preparedStmt.setString(1, korpus);
            preparedStmt.execute();
        } catch (Exception ex) {
            System.out.println("setKK Error: " + ex);
        }
    }

    public void setKorpusKK(String korpus) {
        try {
            this.query = "update tb_korpusKK SET tfKK=tfKK+1 where termKK = ?";

            preparedStmt = (PreparedStatement) Conn.getConn().prepareStatement(this.query);
            preparedStmt.setString(1, korpus);
            preparedStmt.execute();
        } catch (Exception ex) {
            System.out.println("setKorpusKK Error: " + ex);
        }
    }

    public void updateTf(int index, String Tf, int df, String idf, String tfidf) {
        try {
            this.query = "update tb_korpustraining SET tf= ?, df = ?, idf = ?, tfidf = ? where id_korpus = ?";

            preparedStmt = (PreparedStatement) Conn.getConn().prepareStatement(this.query);
            preparedStmt.setString(1, Tf);
            preparedStmt.setInt(2, df);
            preparedStmt.setString(3, idf);
            preparedStmt.setString(4, tfidf);
            preparedStmt.setInt(5, index);
            preparedStmt.execute();
        } catch (Exception ex) {
            System.out.println("UpdateTF Error: " + ex);
        }
    }

    public void updatepvektor(int index, String pVektor) {
        try {
            this.query = "update tb_listfile SET panjangVektor= ? where id = ?";

            preparedStmt = (PreparedStatement) Conn.getConn().prepareStatement(this.query);
            preparedStmt.setString(1, pVektor);
            preparedStmt.setInt(2, index);
            preparedStmt.execute();
        } catch (Exception ex) {
            System.out.println("updatepvektor Error: " + ex);
        }
    }

    public void updatetfKK(String term, int kk) {
        try {
            this.query = "update tb_korpustraining SET kk=kk+? where korpus = ?";

            preparedStmt = (PreparedStatement) Conn.getConn().prepareStatement(this.query);
            preparedStmt.setInt(1, kk);
            preparedStmt.setString(2, term);
            preparedStmt.execute();
        } catch (Exception ex) {
            System.out.println("updatetfKK Error: " + ex);
        }
    }
    
    public void updatetfidfKK(int id, String value) {
        try {
            this.query = "update tb_korpustraining SET tfidf2=? where id_korpus = ?";

            preparedStmt = (PreparedStatement) Conn.getConn().prepareStatement(this.query);
            preparedStmt.setString(1, value);
            preparedStmt.setInt(2, id);
            preparedStmt.execute();
        } catch (Exception ex) {
            System.out.println("updatetfidfKK Error: " + ex);
        }
    }

    public Integer banyakKorpus() {
        int value = 0;

        try {
            this.query = "select count(id_korpus) from tb_korpustraining";
            preparedStmt = (PreparedStatement) Conn.getConn().prepareStatement(this.query);
            rs = preparedStmt.executeQuery();
            while (rs.next()) {
                value = rs.getInt("count(id_korpus)");
            }
            rs.close();
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
        }
        return value;
    }

    public Integer banyakKorpusKK() {
        int value = 0;

        try {
            this.query = "select count(id_korpusKK) from tb_korpusKK";
            preparedStmt = (PreparedStatement) Conn.getConn().prepareStatement(this.query);
            rs = preparedStmt.executeQuery();
            while (rs.next()) {
                value = rs.getInt("count(id_korpusKK)");
            }
            rs.close();
        } catch (Exception ex) {
            System.out.println("banyakKorpusKK Error: " + ex);
        }
        return value;
    }

    public String getKorpus(int index) {
        String value = null;

        try {
            this.query = "select korpus from tb_korpustraining where id_korpus=?";
            preparedStmt = (PreparedStatement) Conn.getConn().prepareStatement(this.query);
            preparedStmt.setInt(1, index);
            rs = preparedStmt.executeQuery();
            while (rs.next()) {
                value = rs.getString("korpus");
            }
            rs.close();
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
        }
        return value;
    }

    public Double getKK(int index) {
        String value = null;

        try {
            this.query = "select kk from tb_korpustraining where id_korpus=?";
            preparedStmt = (PreparedStatement) Conn.getConn().prepareStatement(this.query);
            preparedStmt.setInt(1, index);
            rs = preparedStmt.executeQuery();
            while (rs.next()) {
                value = rs.getString("kk");
            }
            rs.close();
        } catch (Exception ex) {
            System.out.println("getKK Error: " + ex);
        }
        return Double.valueOf(value);
    }

    public Double getIdf(int index) {
        String value = null;

        try {
            this.query = "select idf from tb_korpustraining where id_korpus=?";
            preparedStmt = (PreparedStatement) Conn.getConn().prepareStatement(this.query);
            preparedStmt.setInt(1, index);
            rs = preparedStmt.executeQuery();
            while (rs.next()) {
                value = rs.getString("idf");
            }
            rs.close();
        } catch (Exception ex) {
            System.out.println("getIdf Error: " + ex);
        }
        return Double.valueOf(value);
    }
    
    public String getTF(int index) {
        String value = null;

        try {
            this.query = "select tf from tb_korpustraining where id_korpus=?";
            preparedStmt = (PreparedStatement) Conn.getConn().prepareStatement(this.query);
            preparedStmt.setInt(1, index);
            rs = preparedStmt.executeQuery();
            while (rs.next()) {
                value = rs.getString("tf");
            }
            rs.close();
        } catch (Exception ex) {
            System.out.println("getTF Error: " + ex);
        }
        return value;
    }

    public String getKorpusKK(int index) {
        String value = null;

        try {
            this.query = "select termKK from tb_korpusKK where id_korpusKK=?";
            preparedStmt = (PreparedStatement) Conn.getConn().prepareStatement(this.query);
            preparedStmt.setInt(1, index);
            rs = preparedStmt.executeQuery();
            while (rs.next()) {
                value = rs.getString("termKK");
            }
            rs.close();
        } catch (Exception ex) {
            System.out.println("getKorpusKK Error: " + ex);
        }
        return value;
    }

    public Integer gettfKK(int index) {
        int value = 0;

        try {
            this.query = "select tfKK from tb_korpusKK where id_korpusKK=?";
            preparedStmt = (PreparedStatement) Conn.getConn().prepareStatement(this.query);
            preparedStmt.setInt(1, index);
            rs = preparedStmt.executeQuery();
            while (rs.next()) {
                value = rs.getInt("tfKK");
            }
            rs.close();
        } catch (Exception ex) {
            System.out.println("gettfKK Error: " + ex);
        }
        return value;
    }

    public TableModel viewKorpus(int cbx) {
        model = null;
        model = new DefaultTableModel();
        String[] o = new String[7];
        model.addColumn("No");
        if (cbx == 1) {
            model.addColumn("Term");
        } else if (cbx == 2) {
            model.addColumn("Stem");
        }
        model.addColumn("Freq");
        model.addColumn("TF");
        model.addColumn("DF");
        model.addColumn("IDF");
        model.addColumn("TFIDF");

        try {
            this.query = "select * from tb_korpustraining";
            preparedStmt = (PreparedStatement) Conn.getConn().prepareStatement(this.query);
            rs = preparedStmt.executeQuery();
            while (rs.next()) {
                o[0] = rs.getString("id_korpus");
                o[1] = rs.getString("korpus");
                o[2] = String.valueOf(rs.getInt("freq"));
                o[3] = rs.getString("tf");
                o[4] = rs.getString("df");
                o[5] = rs.getString("idf");
                o[6] = rs.getString("tfidf");
                model.addRow(o);
            }
            rs.close();
        } catch (Exception ex) {
            System.out.println("getlistFile Error: " + ex);
        }
        return model;
    }
}
