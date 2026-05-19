package proyectofinal.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import proyectofinal.Inmueble.Property;
import proyectofinal.Inmueble.TypeProperty;

public class ClientCatalogController {

    @FXML private TableView<Property>             tablaInmuebles;
    @FXML private TableColumn<Property, String>   colCodigo;
    @FXML private TableColumn<Property, String>   colDireccion;
    @FXML private TableColumn<Property, String>   colCiudad;
    @FXML private TableColumn<Property, String>   colZona;
    @FXML private TableColumn<Property, String>   colTipo;
    @FXML private TableColumn<Property, String>   colFinalidad;
    @FXML private TableColumn<Property, String>   colPrecio;
    @FXML private TableColumn<Property, String>   colHab;
    @FXML private TableColumn<Property, String>   colArea;
    @FXML private TableColumn<Property, String>   colAsesor;

    @FXML private TextField              campoBusqueda;
    @FXML private ComboBox<TypeProperty> filtroTipo;
    @FXML private ComboBox<String>       filtroFinalidad;
    @FXML private ComboBox<String>       filtroPrecio;

    @FXML private Button btnFavorito;
    @FXML private Button btnAgendar;
    @FXML private Button btnDetalles;

    private ObservableList<Property> masterList;
    private FilteredList<Property>   filteredList;

    @FXML
    public void initialize() {
        configurarColumnas();
        configurarFiltros();
        cargarDatos();
        configurarSeleccion();
    }

    private void configurarColumnas() {
        colCodigo.setCellValueFactory(d ->    new SimpleStringProperty(d.getValue().getCode()));
        colDireccion.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getAddress()));
        colCiudad.setCellValueFactory(d ->    new SimpleStringProperty(d.getValue().getCity()));
        colZona.setCellValueFactory(d ->      new SimpleStringProperty(d.getValue().getZone()));
        colTipo.setCellValueFactory(d ->      new SimpleStringProperty(d.getValue().getType().toString()));
        colFinalidad.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPurpose()));
        colPrecio.setCellValueFactory(d ->    new SimpleStringProperty(
                String.format("$%,.0f", d.getValue().getPrice())));
        colHab.setCellValueFactory(d ->       new SimpleStringProperty(
                String.valueOf(d.getValue().getRooms())));
        colArea.setCellValueFactory(d ->      new SimpleStringProperty(d.getValue().getArea() + " m²"));
        colAsesor.setCellValueFactory(d ->    new SimpleStringProperty(
                d.getValue().getResponsibleAdvisor() != null
                        ? d.getValue().getResponsibleAdvisor().getName() : "—"));
    }

    private void configurarFiltros() {
        filtroTipo.getItems().setAll(TypeProperty.values());
        filtroFinalidad.getItems().setAll("Venta", "Arriendo");
        filtroPrecio.getItems().setAll(
                "Hasta $500.000", "Hasta $1.000.000",
                "Hasta $5.000.000", "Más de $5.000.000");
    }

    private void cargarDatos() {
        masterList   = FXCollections.observableArrayList();
        filteredList = new FilteredList<>(masterList, p -> p.isAvailable());

        for (Property p : AppContext.getInstance().getPropertyManager().getProperties()) {
            if (p.isAvailable()) masterList.add(p);
        }
        tablaInmuebles.setItems(filteredList);
    }

    private void configurarSeleccion() {
        tablaInmuebles.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, sel) -> {
                    boolean hay = sel != null;
                    btnFavorito.setDisable(!hay);
                    btnAgendar.setDisable(!hay);
                    btnDetalles.setDisable(!hay);
                });
    }

    @FXML
    public void filtrar() {
        String texto     = campoBusqueda.getText().toLowerCase();
        TypeProperty tipo = filtroTipo.getValue();
        String finalidad = filtroFinalidad.getValue();
        String rango     = filtroPrecio.getValue();

        filteredList.setPredicate(p -> {
            if (!p.isAvailable()) return false;

            boolean matchTexto = texto.isEmpty()
                    || p.getCity().toLowerCase().contains(texto)
                    || p.getZone().toLowerCase().contains(texto)
                    || p.getAddress().toLowerCase().contains(texto);
            boolean matchTipo     = tipo == null || p.getType() == tipo;
            boolean matchFinalidad = finalidad == null
                    || p.getPurpose().equalsIgnoreCase(finalidad);
            boolean matchPrecio   = rango == null || matchesPrecio(p.getPrice(), rango);

            return matchTexto && matchTipo && matchFinalidad && matchPrecio;
        });
    }

    private boolean matchesPrecio(double price, String rango) {
        return switch (rango) {
            case "Hasta $500.000"    -> price <= 500_000;
            case "Hasta $1.000.000"  -> price <= 1_000_000;
            case "Hasta $5.000.000"  -> price <= 5_000_000;
            case "Más de $5.000.000" -> price > 5_000_000;
            default -> true;
        };
    }

    @FXML
    public void limpiarFiltros() {
        campoBusqueda.clear();
        filtroTipo.setValue(null);
        filtroFinalidad.setValue(null);
        filtroPrecio.setValue(null);
        filteredList.setPredicate(p -> p.isAvailable());
    }

    @FXML
    public void guardarFavorito() {
        Property selected = tablaInmuebles.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        AppContext.getInstance().getClientManager().markAsFavorite(selected);
        mostrarInfo("⭐ Guardado en favoritos: " + selected.getCode());
    }

    @FXML
    public void agendarVisita() {
        mostrarInfo("Para agendar una visita ve al módulo 'Mis visitas'.");
    }

    @FXML
    public void verDetalles() {
        Property p = tablaInmuebles.getSelectionModel().getSelectedItem();
        if (p == null) return;
        mostrarInfo(p.toString());
    }

    private void mostrarInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}
