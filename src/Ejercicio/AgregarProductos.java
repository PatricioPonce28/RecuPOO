package Ejercicio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AgregarProductos {
    private JTabbedPane tabbedPane1;
    private JPanel panel1;
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
    private JButton buscarUsuario;
    private JButton mostarUsuarios;
    private JPasswordField passwordField1;
    private JPanel panelMostrar;

    // Constantes para la conexión a la base de datos
    private static final String URL = "jdbc:mysql://bzlerfvwxgajjydlak0p-mysql.services.clever-cloud.com:3306/bzlerfvwxgajjydlak0p";
    private static final String USERNAME = "untmxbiu7cvvwswm";
    private static final String PASSWORD = "HacLKXupYhnrDTvGAn6c";

    public AgregarProductos() {
        inicializarTablas();
        cargarDatosUsuarios();
        CargarDatosProductos();

        guardarProducto.addActionListener(e -> productoGuardar());
        guardarUsuario.addActionListener(e -> ingresoUser());
    }


    public static void init(JFrame frame) {
        frame.setContentPane(new AgregarProductos().panel1);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void inicializarTablas() {

        // Inicializar modelo de tabla para productos
        DefaultTableModel modelProductos = new DefaultTableModel(
                new String[]{"ID", "Nombre", "Descripción", "Precio", "Stock"}, 0
        );
        tableProductos.setModel(modelProductos);

        // Inicializar modelo de tabla para usuarios
        DefaultTableModel modelUsuarios = new DefaultTableModel(
                new String[]{"ID", "Usuario", "Rol"}, 0
        );
        tableUsuarios.setModel(modelUsuarios);
    }

    private void cargarDatosUsuarios() {
        String query = "SELECT id_usuario, nombre_usuario, rol FROM usuarios";

        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            DefaultTableModel model = (DefaultTableModel) tableUsuarios.getModel();
            model.setRowCount(0);

            while (resultSet.next()) {
                model.addRow(new Object[]{
                        resultSet.getInt("id_usuario"),
                        resultSet.getString("nombre_usuario"),
                        resultSet.getString("rol")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al cargar usuarios: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void CargarDatosProductos() {
        String query = "SELECT * FROM productos";

        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            DefaultTableModel model = (DefaultTableModel) tableProductos.getModel();
            model.setRowCount(0);

            while (resultSet.next()) {
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
            JOptionPane.showMessageDialog(null, "Error al cargar productos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ingresoUser() {
        String userIngresado = textUsuario.getText().trim();
        String passIngresado = new String(passwordField1.getPassword());
        String rol = usuarioCheckBox.isSelected() ? "usuario" : "administrador";

        if (userIngresado.isEmpty() || passIngresado.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor, complete todos los campos",
                    "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String query = "INSERT INTO usuarios (nombre_usuario, password, rol) VALUES (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, userIngresado);
            statement.setString(2, passIngresado);
            statement.setString(3, rol);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(null, "Usuario registrado exitosamente",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
                cargarDatosUsuarios();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al registrar usuario: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void productoGuardar() {
        String nombre = textNombre.getText().trim();
        String descripcion = textDescripcion.getText().trim();
        String precioStr = textPrecio.getText().trim();
        String stockStr = textStock.getText().trim();

        if (nombre.isEmpty() || descripcion.isEmpty() || precioStr.isEmpty() || stockStr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor, complete todos los campos",
                    "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            float precio = Float.parseFloat(precioStr);
            int stock = Integer.parseInt(stockStr);

            if (stock < 10 || stock > 350) {
                JOptionPane.showMessageDialog(null,
                        "El stock debe estar entre 10 y 350 unidades",
                        "Stock Inválido", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String query = "INSERT INTO productos (nombre, descripcion, precio, stock) VALUES (?, ?, ?, ?)";

            try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                 PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setString(1, nombre);
                statement.setString(2, descripcion);
                statement.setFloat(3, precio);
                statement.setInt(4, stock);

                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(null, "Producto registrado exitosamente",
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    limpiarCampos();
                    CargarDatosProductos();
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Por favor, ingrese valores numéricos válidos",
                    "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al registrar producto: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        textNombre.setText("");
        textDescripcion.setText("");
        textStock.setText("");
        textPrecio.setText("");
        textUsuario.setText("");
        passwordField1.setText("");
        usuarioCheckBox.setSelected(false);
        administradorCheckBox.setSelected(false);
    }
}