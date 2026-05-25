package proyectofinal.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
    
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Ficha Técnica — " + p.getCode());
        dialog.initOwner(tablaInmuebles.getScene().getWindow());
    
        ButtonType btnCerrar = new ButtonType("Entendido", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(btnCerrar);
    
        VBox root = new VBox(16);
        root.setPadding(new javafx.geometry.Insets(24));
        root.setPrefWidth(450);
        root.setStyle("-fx-background-color: #f8fafc;"); // Fondo claro moderno
    
        VBox header = new VBox(4);
        Label lblTitulo = new Label(p.getType().toString() + " en " + p.getPurpose());
        lblTitulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        Label lblCodigo = new Label("Código del inmueble: " + p.getCode());
        lblCodigo.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");
        header.getChildren().addAll(lblTitulo, lblCodigo);
    
        HBox precioBox = new HBox();
        precioBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        precioBox.setStyle("-fx-background-color: #f1f5f9; -fx-padding: 12 16 12 16; -fx-background-radius: 8;");
        Label lblPrecioVal = new Label(String.format("$%,.0f COP", p.getPrice()));
        lblPrecioVal.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        precioBox.getChildren().add(lblPrecioVal);
    
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(12);
        grid.setStyle("-fx-padding: 8 0 8 0;");
    
        String[][] datos = {
            {"📍 Ubicación:", p.getAddress()},
            {"🏙️ Ciudad:", p.getCity() + " (" + p.getZone().name() + ")"},
            {"📐 Área Privada:", p.getArea() + " m²"},
            {"🛏️ Habitaciones:", String.valueOf(p.getRooms())},
            {"🚽 Baños:", String.valueOf(p.getBathrooms())},
            {"💼 Asesor a cargo:", p.getResponsibleAdvisor() != null ? p.getResponsibleAdvisor().getName() : "Por asignar"}
        };
    
        for (int i = 0; i < datos.length; i++) {
            Label lblIcono = new Label(datos[i][0]);
            lblIcono.setStyle("-fx-font-weight: bold; -fx-text-fill: #475569; -fx-font-size: 13px;");
            
            Label lblValor = new Label(datos[i][1]);
            lblValor.setStyle("-fx-text-fill: #334155; -fx-font-size: 13px;");
            lblValor.setWrapText(true);
    
            grid.add(lblIcono, 0, i);
            grid.add(lblValor, 1, i);
        }
    
        HBox estadoBox = new HBox(8);
        estadoBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label lblEstadoTit = new Label("Estado actual:");
        lblEstadoTit.setStyle("-fx-font-weight: bold; -fx-text-fill: #475569; -fx-font-size: 13px;");
        
        Label lblEstadoBadge = new Label(" " + p.getPropertyStatus().toUpperCase() + " ");
        if (p.isAvailable()) {
            lblEstadoBadge.setStyle("-fx-background-color: #dcfce7; -fx-text-fill: #15803d; -fx-font-weight: bold; -fx-background-radius: 4; -fx-font-size: 11px;");
        } else {
            lblEstadoBadge.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #b91c1c; -fx-font-weight: bold; -fx-background-radius: 4; -fx-font-size: 11px;");
        }
        estadoBox.getChildren().addAll(lblEstadoTit, lblEstadoBadge);
    
        Separator sep1 = new Separator();
        Separator sep2 = new Separator();
    
        root.getChildren().addAll(header, sep1, precioBox, grid, sep2, estadoBox);
        dialog.getDialogPane().setContent(root);
    
        if (!tablaInmuebles.getScene().getStylesheets().isEmpty()) {
            dialog.getDialogPane().getStylesheets().addAll(tablaInmuebles.getScene().getStylesheets());
        }
    
        dialog.showAndWait();
    }

    private void mostrarInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}