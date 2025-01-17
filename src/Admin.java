
import Ejercicio.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Admin {
    private JTabbedPane tabbedPane1;
    public JPanel usuario;
    private JTextField textNombre;
    private JTextField textPrecio;
    private JTextField textDescripcion;
    private JTextField textStock;
    private JButton guardarProducto;
    private JTextField textUsuario;
    private JCheckBox usuarioCheckBox;
    private JCheckBox administradorCheckBox;
    private JButton guardarUsuario;
    private JTable tableProductos;
    private JButton buscarProducto;
    private JTable tableUsuarios;
    private JPanel panelMostrar;
    private JButton buscarUsuario;
    private JButton mostarUsuarios;
    private JPasswordField passwordField1;

    public Admin() {

        this.inicializarTablas();
        this.cargarDatosUsuarios();
        this.CargarDatosProductos();
        this.guardarProducto.addActionListener((e) -> {
            this.productoGuardar();
        });
        this.guardarUsuario.addActionListener((e) -> {
            this.ingresoUser();
        });
    }

    public static void init(JFrame frame) {
        frame.setContentPane(new Admin().usuario);
        frame.setDefaultCloseOperation(2);
        frame.setSize(600, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void inicializarTablas() {
        DefaultTableModel modelProductos = new DefaultTableModel(new String[]{"ID", "Nombre", "Descripción", "Precio", "Stock"}, 0);
        this.tableProductos.setModel(modelProductos);
        DefaultTableModel modelUsuarios = new DefaultTableModel(new String[]{"ID", "Usuario", "Rol"}, 0);
        this.tableUsuarios.setModel(modelUsuarios);
    }

    private void cargarDatosUsuarios() {
        String query = "SELECT id_usuario, nombre_usuario, rol FROM usuarios";

        try (Connection connection = ConexionDB.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            DefaultTableModel model = (DefaultTableModel)this.tableUsuarios.getModel();
            model.setRowCount(0);

            while(resultSet.next()) {
                model.addRow(new Object[]{
                        resultSet.getInt("id_usuario"),
                        resultSet.getString("nombre_usuario"),
                        resultSet.getString("rol")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al cargar usuarios: " + e.getMessage(), "Error", 0);
        }
    }

    private void CargarDatosProductos() {
        String query = "SELECT * FROM productos";

        try (Connection connection = ConexionDB.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            DefaultTableModel model = (DefaultTableModel)this.tableProductos.getModel();
            model.setRowCount(0);

            while(resultSet.next()) {
                model.addRow(new Object[]{
                        resultSet.getInt("id_producto"),
                        resultSet.getString("nombre"),
                        resultSet.getString("descripcion"),
                        resultSet.getFloat("precio"),
                        resultSet.getInt("stock")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al cargar productos: " + e.getMessage(), "Error", 0);
        }
    }

    private void ingresoUser() {
        String userIngresado = this.textUsuario.getText().trim();
        String passIngresado = new String(this.passwordField1.getPassword());
        String rol = this.usuarioCheckBox.isSelected() ? "usuario" : "administrador";

        if (!userIngresado.isEmpty() && !passIngresado.isEmpty()) {
            String query = "INSERT INTO usuarios (nombre_usuario, password, rol) VALUES (?, ?, ?)";

            try (Connection connection = ConexionDB.getInstance().getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setString(1, userIngresado);
                statement.setString(2, passIngresado);
                statement.setString(3, rol);

                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(null, "Usuario registrado exitosamente", "Éxito", 1);
                    this.limpiarCampos();
                    this.cargarDatosUsuarios();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al registrar usuario: " + ex.getMessage(), "Error", 0);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Por favor, complete todos los campos", "Campos Vacíos", 2);
        }
    }

    private void productoGuardar() {
        String nombre = this.textNombre.getText().trim();
        String descripcion = this.textDescripcion.getText().trim();
        String precioStr = this.textPrecio.getText().trim();
        String stockStr = this.textStock.getText().trim();

        if (!nombre.isEmpty() && !descripcion.isEmpty() && !precioStr.isEmpty() && !stockStr.isEmpty()) {
            try {
                float precio = Float.parseFloat(precioStr);
                int stock = Integer.parseInt(stockStr);

                if (stock < 10 || stock > 350) {
                    JOptionPane.showMessageDialog(null, "El stock debe estar entre 10 y 350 unidades", "Stock Inválido", 0);
                    return;
                }

                String query = "INSERT INTO productos (nombre, descripcion, precio, stock) VALUES (?, ?, ?, ?)";

                try (Connection connection = ConexionDB.getInstance().getConnection();
                     PreparedStatement statement = connection.prepareStatement(query)) {

                    statement.setString(1, nombre);
                    statement.setString(2, descripcion);
                    statement.setFloat(3, precio);
                    statement.setInt(4, stock);

                    int rowsInserted = statement.executeUpdate();
                    if (rowsInserted > 0) {
                        JOptionPane.showMessageDialog(null, "Producto registrado exitosamente", "Éxito", 1);
                        this.limpiarCampos();
                        this.CargarDatosProductos();
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Por favor, ingrese valores numéricos válidos", "Error de Formato", 0);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al registrar producto: " + ex.getMessage(), "Error", 0);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Por favor, complete todos los campos", "Campos Vacíos", 2);
        }
    }

    private void limpiarCampos() {
        this.textNombre.setText("");
        this.textDescripcion.setText("");
        this.textStock.setText("");
        this.textPrecio.setText("");
        this.textUsuario.setText("");
        this.passwordField1.setText("");
        this.usuarioCheckBox.setSelected(false);
        this.administradorCheckBox.setSelected(false);
    }
}