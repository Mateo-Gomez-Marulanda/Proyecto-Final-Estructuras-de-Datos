package proyectofinal;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static final double ANCHO_LOGIN  = 500;
    private static final double ALTO_LOGIN   = 420;
    private static final double ALTO_REGISTRO = 620;
    private static final double ANCHO_APP    = 1100;
    private static final double ALTO_APP     = 700;

    private static Stage stagePrincipal;

    @Override
    public void start(Stage stage) throws Exception {
        stagePrincipal = stage;
        cargarLogin();
        stage.setTitle("PropTech — Sistema de Gestión Inmobiliaria");
        stage.setResizable(true);
        stage.show();
    }

    // ─── Navegación entre pantallas ──────────────────────────

    public static void cargarLogin() throws Exception {
        Parent vista = FXMLLoader.load(
                Main.class.getResource("/proyectofinal/views/login.fxml"));
        stagePrincipal.setScene(new Scene(vista, ANCHO_LOGIN, ALTO_LOGIN));
        stagePrincipal.setMinWidth(ANCHO_LOGIN);
        stagePrincipal.setMinHeight(ALTO_LOGIN);
        stagePrincipal.centerOnScreen();
    }

    public static void cargarRegistro() throws Exception {
        Parent vista = FXMLLoader.load(
                Main.class.getResource("/proyectofinal/views/register.fxml"));
        stagePrincipal.setScene(new Scene(vista, ANCHO_LOGIN, ALTO_REGISTRO));
        stagePrincipal.setMinWidth(ANCHO_LOGIN);
        stagePrincipal.setMinHeight(ALTO_REGISTRO);
        stagePrincipal.centerOnScreen();
    }

    /** Admin view — full management dashboard */
    public static void cargarShellPrincipal() throws Exception {
        Parent vista = FXMLLoader.load(
                Main.class.getResource("/proyectofinal/views/main-shell.fxml"));
        stagePrincipal.setScene(new Scene(vista, ANCHO_APP, ALTO_APP));
        stagePrincipal.setMinWidth(800);
        stagePrincipal.setMinHeight(550);
        stagePrincipal.centerOnScreen();
    }

    /** Client view — browse, favorites, visits, profile */
    public static void cargarShellCliente() throws Exception {
        Parent vista = FXMLLoader.load(
                Main.class.getResource("/proyectofinal/views/client-shell.fxml"));
        stagePrincipal.setScene(new Scene(vista, ANCHO_APP, ALTO_APP));
        stagePrincipal.setMinWidth(800);
        stagePrincipal.setMinHeight(550);
        stagePrincipal.centerOnScreen();
    }

    public static Stage getStagePrincipal() { return stagePrincipal; }

    public static void main(String[] args) {
        launch(args);
    }
}
