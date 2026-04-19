package proyectofinal;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    //Dimensiones de la ventana principal
    private static final double ANCHO_LOGIN = 500;
    private static final double ALTO_LOGIN  = 420;
    private static final double ANCHO_APP   = 1100;
    private static final double ALTO_APP    = 700;

    //Stage global accesible desde los controllers
    private static Stage stagePrincipal;

    @Override
    public void start(Stage stage) throws Exception {
        stagePrincipal = stage;

        // Cargar la pantalla de login al iniciar
        cargarLogin();

        stage.setTitle("PropTech — Sistema de Gestión Inmobiliaria");
        stage.setResizable(true);
        stage.show();
    }

    // Métodos de navegación entre pantallas
    // (los controllers los llaman via Main.cargarLogin() etc.)
    public static void cargarLogin() throws Exception {
        Parent vista = FXMLLoader.load(
            Main.class.getResource("/proyectofinal/views/login.fxml")
        );
        Scene escena = new Scene(vista, ANCHO_LOGIN, ALTO_LOGIN);
        stagePrincipal.setScene(escena);
        stagePrincipal.setMinWidth(ANCHO_LOGIN);
        stagePrincipal.setMinHeight(ALTO_LOGIN);
        stagePrincipal.centerOnScreen();
    }

    public static void cargarShellPrincipal() throws Exception {
        Parent vista = FXMLLoader.load(
            Main.class.getResource("/proyectofinal/views/main-shell.fxml")
        );
        Scene escena = new Scene(vista, ANCHO_APP, ALTO_APP);
        stagePrincipal.setScene(escena);
        stagePrincipal.setMinWidth(800);
        stagePrincipal.setMinHeight(550);
        stagePrincipal.centerOnScreen();
    }

    // Getter del stage para controllers que lo necesiten
    public static Stage getStagePrincipal() {
        return stagePrincipal;
    }

    public static void main(String[] args) {
        launch(args);
    }
}