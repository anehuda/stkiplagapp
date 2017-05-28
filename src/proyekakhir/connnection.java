/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyekakhir;

import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author exphuda
 */
public class connnection {

    private Connection conn = null;

    private String driver = "com.mysql.jdbc.Driver";
    private String url = "jdbc:mysql://localhost:3306/plagapp";
    private String user = "root";
    private String pass = "";
    private boolean status = false;

    public Connection getConn() {
        return conn;
    }
    
    public boolean Status(){
        return status;
    }

    public connnection() throws ClassNotFoundException {
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, pass);
            status = true;
        } catch (SQLException e) {
            status = false;
        }
    }

}
