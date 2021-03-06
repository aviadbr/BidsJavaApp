



import java.sql.Connection;
import java.sql.Date;
import java.lang.Integer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import static javax.swing.UIManager.get;
import javax.swing.table.DefaultTableModel;

public class client {
    static int NUMBER_OF_COLUMNS_CLIENT = 8;
    public void insertUpdateDeleteClient(char operation, Integer id, String fname, String lname,
            String address, String mphone, String hphone, String comments, String dateAdded)
    {
        Connection con = MyConnection.getConnection();
        PreparedStatement ps1, ps2;
        
        // i for insert
        if (operation == 'i')
        {
            try {
                ps1 = con.prepareStatement("INSERT INTO person(first_name,"
                        + " last_name, m_phone, h_phone, date_added, address, comments) VALUES (?,?,?,?,?,?,?)");
                ps1.setString(1, fname);
                ps1.setString(2, lname);
                ps1.setString(3, mphone);
                ps1.setString(4, hphone);
                ps1.setString(5, dateAdded);
                ps1.setString(6, address);
                ps1.setString(7, comments);     
            
                int valid1,valid2;
                valid1 = ps1.executeUpdate();
                if(valid1>0)
                { 
                    ps2 = con.prepareStatement("INSERT INTO client(p_id) VALUES (LAST_INSERT_ID())");
                    valid2 = ps2.executeUpdate();
                    if(valid2>0)
                    {
                        JOptionPane.showMessageDialog(null, "New Client Was Added");
                        return;
                    }
                }          
                
            } catch (SQLException ex) {
                handleError.showErrorMessage(true, "Adding was unsuccessful." + ex.getMessage(), null);
                Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
            }    
            
        }
        
        // u for update
        else if (operation == 'u')
        {
            try {
                ps1 = con.prepareStatement("UPDATE `person` SET `first_name`=?,`last_name`=?,"
                        + "`m_phone`=?,`h_phone`=?,`address`=?,`comments`=? WHERE `p_id` = ?");
                ps1.setString(1, fname);
                ps1.setString(2, lname);
                ps1.setString(3, mphone);
                ps1.setString(4, hphone);
                ps1.setString(5, address);
                ps1.setString(6, comments);     
                ps1.setInt(7, id);
                
                if(ps1.executeUpdate()>0)
                { 
                    JOptionPane.showMessageDialog(null, "Client Data Updated");
                    return;
                }                                      
            } catch (SQLException ex) {
                handleError.showErrorMessage(true, "Updating was unsuccessful." + ex.getMessage(), null);
                Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
            }               
        }
        
         // d for delete
        else if (operation == 'd')
        {
            try {
                ps1 = con.prepareStatement("DELETE FROM `client` WHERE `c_id` = ?"); 
                ps1.setInt(1, id);
                
                if(ps1.executeUpdate()>0)
                { 
                    JOptionPane.showMessageDialog(null, "Client Deleted");
                    return;
                }                                      
            } catch (SQLException ex) {
                handleError.showErrorMessage(true, "Deleting was unsuccessful. " + ex.getMessage(), null);
                Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
            }               
        }
    }
    
    public void fillClientJTable(JTable table, String valueToSearch)
    {
        Connection con = MyConnection.getConnection();
        PreparedStatement ps;
        try {
            ps=con.prepareStatement("SELECT * FROM `person` NATURAL JOIN client WHERE CONCAT('c_id',`first_name`,`last_name`,`m_phone`,`h_phone`,`date_added`,`address`,`comments`) LIKE ?");
            ps.setString(1,'%' +valueToSearch+ '%');
            ResultSet rs = ps.executeQuery();
            DefaultTableModel model = (DefaultTableModel)table.getModel();
            Object[] row;
            while(rs.next())
            {
                row=new Object[NUMBER_OF_COLUMNS_CLIENT];
                row[0] = rs.getInt(9);  //c_id pos is 9
                row[1] = rs.getString(2);
                row[2] = rs.getString(3);
                row[3] = rs.getString(4);
                row[4] = rs.getString(5);
                row[5] = rs.getString(6);
                row[6] = rs.getString(7);
                row[7] = rs.getString(8);
                model.addRow(row);
            }
        } catch (SQLException ex) {
            Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void fillTableAgain(String searchValue)
    {
        DefaultTableModel tableModel = new DefaultTableModel(null, new Object[]{"ID","First Name","First Name",
                "Mobile Phone","Home Phone","Date Added","Address","Comments"}) {

        @Override
        public boolean isCellEditable(int row, int column) {
           //all cells false
           return false;
        }
    };
        manageClientsForm.tbl_clients.setModel(tableModel);
        fillClientJTable(manageClientsForm.tbl_clients, searchValue);
    }
}
