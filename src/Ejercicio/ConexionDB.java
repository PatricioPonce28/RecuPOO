package Ejercicio;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
        private static final String URL = "jdbc:mysql://bzlerfvwxgajjydlak0p-mysql.services.clever-cloud.com:3306/bzlerfvwxgajjydlak0p";
        private static final String USERNAME = "untmxbiu7cvvwswm";
        private static final String PASSWORD = "HacLKXupYhnrDTvGAn6c";
        private static ConexionDB instance;

        private ConexionDB() {}

        public static ConexionDB getInstance() {
            if (instance == null) {
                instance = new ConexionDB();
            }
            return instance;
        }

        public Connection getConnection() throws SQLException, ClassNotFoundException {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        }
    }

