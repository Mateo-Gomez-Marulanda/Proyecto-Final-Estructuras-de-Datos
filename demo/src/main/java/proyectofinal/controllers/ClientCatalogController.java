package proyectofinal.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import proyectofinal.Inmueble.Property;
import proyectofinal.Inmueble.TypeProperty;
import proyectofinal.SistemaGestion.GestionInmuebles.PropertySorter;
import proyectofinal.EstructurasDeDatos.Listas.SimpleLinkedList;

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
    @FXML private ComboBox<String>       filtroOrden;

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
        colZona.setCellValueFactory(d ->      new SimpleStringProperty(d.getValue().getZone().name()));
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
                
        // Opciones de ordenamiento
        if (filtroOrden != null) {
            filtroOrden.getItems().setAll(
                "Precio: Menor a Mayor", 
                "Precio: Mayor a Menor", 
                "Área: Mayor a Menor", 
                "Más Visitados (Demanda)"
            );
            filtroOrden.setOnAction(e -> aplicarOrden());
        }
    }

    @FXML
    public void aplicarOrden() {
        if (filtroOrden.getValue() == null) return;
        
        String seleccion = filtroOrden.getValue();
        SimpleLinkedList<Property> listaOrdenada = null;
        
        var pm = AppContext.getInstance().getPropertyManager();
        var vm = AppContext.getInstance().getVisitManager();

        switch (seleccion) {
            case "Precio: Menor a Mayor":
                listaOrdenada = pm.getPriceTree().getInOrderList(); 
                break;
            case "Precio: Mayor a Menor":
                listaOrdenada = pm.getPriceTree().getReverseInOrderList();
                break;
            case "Área: Mayor a Menor":
                listaOrdenada = PropertySorter.sortProperties(
                        pm.getProperties(), 
                        PropertySorter.SortCriterion.AREA_DESC, 
                        vm
                );
                break;
            case "Más Visitados (Demanda)":
                listaOrdenada = PropertySorter.sortProperties(
                        pm.getProperties(), 
                        PropertySorter.SortCriterion.DEMAND_DESC, 
                        vm
                );
                break;
        }

        if (listaOrdenada != null) {
            masterList.clear();
            for (Property p : listaOrdenada) {
                if (p.isAvailable()) {
                    masterList.add(p);
                }
            }
        }
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
                    || p.getZone().name().toLowerCase().contains(texto)
                    || p.getAddress().toLowerCase().contains(texto);
            
            boolean matchTipo      = tipo == null || p.getType() == tipo;
            boolean matchFinalidad = finalidad == null
                    || p.getPurpose().equalsIgnoreCase(finalidad);
            boolean matchPrecio    = rango == null || matchesPrecio(p.getPrice(), rango);

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
        if (filtroOrden != null) filtroOrden.setValue(null);
        
        cargarDatos(); 
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