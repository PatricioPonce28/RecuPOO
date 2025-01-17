import Ejercicio.ConexionDB;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Login {
    private JTextField userName;
    private JPasswordField userPass;
    private JButton ingresarButton;
    public JPanel loginPanel;

    public Login() {
        this.$$$setupUI$$$();
        this.ingresarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try (Connection conn = ConexionDB.getInstance().getConnection()) {
                    String parametroName = Login.this.userName.getText();
                    String parametroPass = new String(Login.this.userPass.getPassword());
                    String query = "SELECT * FROM usuarios WHERE nombre_usuario = ? AND password = ?";

                    try (PreparedStatement stmt = conn.prepareStatement(query)) {
                        stmt.setString(1, parametroName);
                        stmt.setString(2, parametroPass);

                        try (ResultSet rs = stmt.executeQuery()) {
                            if (rs.next()) {
                                String rol = rs.getString("rol");
                                JOptionPane.showMessageDialog(null, "Inicio de sesión exitoso\nRol: " + rol);
                                JFrame frame;
                                if (rol.equals("administrador")) {
                                    frame = new JFrame("Panel de Administración");
                                } else {
                                    frame = new JFrame("Panel de Usuario");
                                }
                                AgregarProductos.init(frame);
                            } else {
                                JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrectos", "Error de autenticación", 0);
                            }
                        }
                    }
                } catch (ClassNotFoundException ex) {
                    JOptionPane.showMessageDialog(null, "Error: Driver MySQL no encontrado\n" + ex.getMessage(), "Error", 0);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error de conexión a la base de datos\n" + ex.getMessage(), "Error", 0);
                }
            }
        });
    }
}