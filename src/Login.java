import Ejercicio.AgregarProductos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Login {
    private JTextField userName;
    private JPasswordField userPass;
    private JButton ingresarButton;
    public JPanel loginPanel;
    private Connection conn = null;

    public Login() {
        ingresarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Datos de conexión
                String url = "jdbc:mysql://bzlerfvwxgajjydlak0p-mysql.services.clever-cloud.com:3306/bzlerfvwxgajjydlak0p";
                String username = "untmxbiu7cvvwswm";
                String password = "HacLKXupYhnrDTvGAn6c";

                try {
                    // Cargar el driver y establecer conexión
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    conn = DriverManager.getConnection(url, username, password);

                    // Obtener valores ingresados por el usuario
                    String parametroName = userName.getText();
                    String parametroPass = new String(userPass.getPassword());

                    // Consulta SQL usando los campos correctos de la tabla usuarios
                    String query = "SELECT * FROM usuarios WHERE nombre_usuario = ? AND password = ?";

                    try (PreparedStatement stmt = conn.prepareStatement(query)) {
                        stmt.setString(1, parametroName);
                        stmt.setString(2, parametroPass);

                        try (ResultSet rs = stmt.executeQuery()) {
                            if (rs.next()) {
                                String rol = rs.getString("rol");
                                JOptionPane.showMessageDialog(null, "Inicio de sesión exitoso\nRol: " + rol);

                                // Verificar el rol del usuario
                                if (rol.equals("administrador")) {
                                    // Abrir ventana para administrador
                                    JFrame frame = new JFrame("Panel de Administración");
                                    AgregarProductos.init(frame);
                                } else {
                                    // Abrir ventana para usuario normal
                                    JFrame frame = new JFrame("Panel de Usuario");
                                    AgregarProductos.init(frame);
                                }
                            } else {
                                JOptionPane.showMessageDialog(null,
                                        "Usuario o contraseña incorrectos",
                                        "Error de autenticación",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                } catch (ClassNotFoundException ex) {
                    JOptionPane.showMessageDialog(null,
                            "Error: Driver MySQL no encontrado\n" + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null,
                            "Error de conexión a la base de datos\n" + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                } finally {
                    if (conn != null) {
                        try {
                            conn.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });
    }
}