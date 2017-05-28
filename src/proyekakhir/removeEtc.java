/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyekakhir;

/**
 *
 * @author exphuda
 */
public class removeEtc {

    public removeEtc() {
    }

    public String replace(String line) {
        line = line.replace(".", " ");
        line = line.replace("/", " ");
        line = line.replace("(", " ");
        line = line.replace(")", " ");
        line = line.replace(",", " ");
        line = line.replace("<", " ");
        line = line.replace(">", " ");
        line = line.replace("?", " ");
        line = line.replace("\'", " ");
        line = line.replace("\"", " ");
        line = line.replace("!", " ");
        line = line.replace("@", " ");
        line = line.replace("#", " ");
        line = line.replace("$", " ");
        line = line.replace("&", " ");
        line = line.replace(":", " ");
        line = line.replace(";", " ");
        line = line.replace("[", " ");
        line = line.replace("]", " ");
        line = line.replace("{", " ");
        line = line.replace("}", " ");
        line = line.replace("\\", " ");
        line = line.replace("|", " ");
        line = line.replace("+", " ");
        line = line.replace("=", " ");
        line = line.replace("-", " ");
        line = line.replace("*", " ");
        line = line.replace("^", " ");
        line = line.replace("%", " ");
        line = line.replace("`", " ");
        line = line.replace("~", " ");
        line = line.replace("𝑘", " ");
        line = line.replace("–", " ");
        line = line.replace("≥", " ");
        line = line.replace("”", " ");
        line = line.replace("·", " ");
        line = line.replace("�", " ");
        line = line.replace("’", " ");
        line = line.replace("—", " ");
        line = line.replace("“", " ");
        line = line.replace("_", " ");
        line = line.replace("‘", " ");
        
        
        return line;
    }

}
