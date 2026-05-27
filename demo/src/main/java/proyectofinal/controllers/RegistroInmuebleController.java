package proyectofinal.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import proyectofinal.Inmueble.Property;
import proyectofinal.Inmueble.TypeProperty;
import proyectofinal.Inmueble.ZoneProperty;
import proyectofinal.Personal.Advisor;

import java.util.function.Consumer;

public class RegistroInmuebleController {

    // ── Identificación ────────────────────────────────────────
    @FXML
    private TextField campoCodigo;
    @FXML
    private ComboBox<TypeProperty> campoTipo;

    // ── Ubicación ─────────────────────────────────────────────
    @FXML
    private TextField campoDireccion;
    @FXML
    private TextField campoCiudad;
    @FXML
    private ComboBox<ZoneProperty> campoZona;

    // ── Características ───────────────────────────────────────
    @FXML
    private TextField campoArea;
    @FXML
    private TextField campoPrecio;
    @FXML
    private Spinner<Integer> campoHabitaciones;
    @FXML
    private Spinner<Integer> campoBanos;

    // ── Negocio ───────────────────────────────────────────────
    @FXML
    private ComboBox<String> campoFinalidad;
    @FXML
    private ComboBox<String> campoEstado;
    @FXML
    private CheckBox campoDisponible;
    @FXML
    private ComboBox<Advisor> campoAsesor;

    // ── Feedback ──────────────────────────────────────────────
    @FXML
    private Label labelError;

    // ── Callback al padre ─────────────────────────────────────
    private Consumer<Property> onRegistroExitoso;

    // ─────────────────────────────────────────────────────────
    // Init
    // ─────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        configurarComboZona();
        configurarComboTipo();
        configurarComboFinalidad();
        configurarComboEstado();
        configurarComboAsesor();
        configurarSpinners();
    }

    private void configurarComboTipo() {
        campoTipo.getItems().setAll(TypeProperty.values());
    }

    private void configurarComboZona() {
        campoZona.getItems().setAll(ZoneProperty.values());
    }

    private void configurarComboFinalidad() {
        campoFinalidad.getItems().setAll("Venta", "Arriendo");
    }

    private void configurarComboEstado() {
        campoEstado.getItems().setAll(
                "Disponible",
                "En negociación",
                "Vendido",
                "Arrendado",
                "En mantenimiento",
                "Reservado");
    }

    private void configurarComboAsesor() {
        // Converter: muestra el nombre del asesor en el ComboBox
        campoAsesor.setConverter(new StringConverter<>() {
            @Override
            public String toString(Advisor a) {
                return a == null ? "Sin asignar" : a.getName();
            }

            @Override
            public Advisor fromString(String s) {
                return null;
            }
        });

        // Opción vacía para "sin asignar"
        campoAsesor.getItems().add(null);

        // Cargar asesores desde AppContext (si el manager ya existe)
        // getAdvisors() devuelve SimpleLinkedList<Advisor> directamente desde
        // AppContext
        for (Advisor a : AppContext.getInstance().getAdvisors()) {
            campoAsesor.getItems().add(a);
        }

        campoAsesor.setValue(null); // sin asignar por defecto
    }

    private void configurarSpinners() {
        campoHabitaciones.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20, 0));
        campoBanos.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 0));
    }

    // ─────────────────────────────────────────────────────────
    // API pública: callback que InmueblesController inyecta
    // ─────────────────────────────────────────────────────────

    public void setOnRegistroExitoso(Consumer<Property> callback) {
        this.onRegistroExitoso = callback;
    }

    // ─────────────────────────────────────────────────────────
    // Actions
    // ─────────────────────────────────────────────────────────

    @FXML
    public void registrarInmueble() {
        ocultarError();

        // ── Validación ──────────────────────────────────────
        String codigo = campoCodigo.getText().trim();
        String direccion = campoDireccion.getText().trim();
        String ciudad = campoCiudad.getText().trim();
        ZoneProperty zona = campoZona.getValue();
        TypeProperty tipo = campoTipo.getValue();
        String finalidad = campoFinalidad.getValue();
        String estado = campoEstado.getValue();

        if (codigo.isEmpty()) {
            mostrarError("El código es obligatorio.");
            return;
        }
        if (direccion.isEmpty()) {
            mostrarError("La dirección es obligatoria.");
            return;
        }
        if (ciudad.isEmpty()) {
            mostrarError("La ciudad es obligatoria.");
            return;
        }
        if (zona == null) {
            mostrarError("La zona es obligatoria.");
            return;
        }
        if (tipo == null) {
            mostrarError("Seleccione el tipo de inmueble.");
            return;
        }
        if (finalidad == null) {
            mostrarError("Seleccione la finalidad.");
            return;
        }
        if (estado == null) {
            mostrarError("Seleccione el estado del inmueble.");
            return;
        }

        double precio, area;
        try {
            precio = Double.parseDouble(campoPrecio.getText().trim());
            if (precio <= 0)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            mostrarError("El precio debe ser un número positivo.");
            return;
        }

        try {
            area = Double.parseDouble(campoArea.getText().trim());
            if (area <= 0)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            mostrarError("El área debe ser un número positivo.");
            return;
        }

        // Verificar código duplicado mediante iteracion
        boolean codigoDuplicado = false;
        for (Property p : AppContext.getInstance().getPropertyManager().getProperties()) {
            if (p.getCode().equalsIgnoreCase(codigo)) {
                codigoDuplicado = true;
                break;
            }
        }
        if (codigoDuplicado) {
            mostrarError("Ya existe un inmueble con el código \"" + codigo + "\".");
            return;
        }

        // ── Construcción ────────────────────────────────────
        Property nuevo = new Property(
                codigo,
                direccion,
                ciudad,
                zona,
                tipo,
                finalidad,
                precio,
                area,
                campoHabitaciones.getValue(),
                campoBanos.getValue(),
                estado,
                campoDisponible.isSelected(),
                campoAsesor.getValue() // null si "sin asignar"
        );

        // ── Notificar al padre y cerrar ──────────────────────
        if (onRegistroExitoso != null) {
            onRegistroExitoso.accept(nuevo);
        }
        cerrarVentana();
    }

    @FXML
    public void cancelar() {
        cerrarVentana();
    }

    // ─────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────

    private void cerrarVentana() {
        ((Stage) campoCodigo.getScene().getWindow()).close();
    }

    private void mostrarError(String msg) {
        labelError.setText("⚠  " + msg);
        labelError.setVisible(true);
        labelError.setManaged(true);
    }

    private void ocultarError() {
        labelError.setVisible(false);
        labelError.setManaged(false);
    }
}