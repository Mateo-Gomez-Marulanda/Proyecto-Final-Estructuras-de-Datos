package proyectofinal.controllers;

import java.io.IOException;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import proyectofinal.Inmueble.TypeProperty;
import proyectofinal.Personal.Client;

public class ClientesController {

    // ─── Table ───────────────────────────────────────────────
    @FXML private TableView<Client>             tablaClientes;
    @FXML private TableColumn<Client, String>   colIdentificacion;
    @FXML private TableColumn<Client, String>   colNombre;
    @FXML private TableColumn<Client, String>   colCorreo;
    @FXML private TableColumn<Client, String>   colTelefono;
    @FXML private TableColumn<Client, String>   colTipoCliente;
    @FXML private TableColumn<Client, String>   colPresupuesto;
    @FXML private TableColumn<Client, String>   colInmuebleDeseado;
    @FXML private TableColumn<Client, String>   colEstadoBusqueda;

    // ─── Filters ─────────────────────────────────────────────
    @FXML private TextField              campoBusqueda;
    @FXML private ComboBox<String>       filtroTipoCliente;
    @FXML private ComboBox<TypeProperty> filtroInmuebleDeseado;
    @FXML private ComboBox<String>       filtroEstadoBusqueda;

    // ─── Buttons ─────────────────────────────────────────────
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnVerHistorial;
    @FXML private Button btnVerFavoritos;

    private ObservableList<Client> masterList;
    private FilteredList<Client>   filteredList;

    @FXML
    public void initialize() {
        configurarColumnas();
        configurarFiltros();
        cargarDatos();
        configurarSeleccion();
    }

    // ─────────────────────────────────────────────────────────
    // Setup
    // ─────────────────────────────────────────────────────────

    private void configurarColumnas() {
        colIdentificacion.setCellValueFactory(d ->  new SimpleStringProperty(d.getValue().getId()));
        colNombre.setCellValueFactory(d ->           new SimpleStringProperty(d.getValue().getName()));
        colCorreo.setCellValueFactory(d ->           new SimpleStringProperty(d.getValue().getEmail()));
        colTelefono.setCellValueFactory(d ->         new SimpleStringProperty(d.getValue().getPhoneNumber()));
        colTipoCliente.setCellValueFactory(d ->      new SimpleStringProperty(d.getValue().getClientType()));
        colPresupuesto.setCellValueFactory(d ->      new SimpleStringProperty(
                String.format("$%,.0f", d.getValue().getBudget())));
        colInmuebleDeseado.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getDesiredPropertyType() != null
                        ? d.getValue().getDesiredPropertyType().toString() : "—"));
        colEstadoBusqueda.setCellValueFactory(d ->  new SimpleStringProperty(d.getValue().getSearchStatus()));
    }

    private void configurarFiltros() {
        filtroTipoCliente.getItems().setAll("Regular", "Premium", "Frecuente");
        filtroInmuebleDeseado.getItems().setAll(TypeProperty.values());
        filtroEstadoBusqueda.getItems().setAll("Buscando", "Interesado", "En negociación", "Inactivo");
    }

    private void cargarDatos() {
        masterList   = FXCollections.observableArrayList();
        filteredList = new FilteredList<>(masterList, c -> true);

        for (Client c : AppContext.getInstance().getClientManager().getAllClients()) {
            masterList.add(c);
        }
        tablaClientes.setItems(filteredList);
    }

    private void configurarSeleccion() {
        tablaClientes.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, selected) -> {
                    boolean haySeleccion = selected != null;
                    btnEditar.setDisable(!haySeleccion);
                    btnEliminar.setDisable(!haySeleccion);
                    btnVerHistorial.setDisable(!haySeleccion);
                    btnVerFavoritos.setDisable(!haySeleccion);
                });
    }

    // ─────────────────────────────────────────────────────────
    // Filters
    // ─────────────────────────────────────────────────────────

    @FXML
    public void filtrarTabla() {
        String texto      = campoBusqueda.getText().toLowerCase();
        String tipo       = filtroTipoCliente.getValue();
        TypeProperty tp   = filtroInmuebleDeseado.getValue();
        String estado     = filtroEstadoBusqueda.getValue();

        filteredList.setPredicate(c -> {
            boolean matchTexto = texto.isEmpty()
                    || c.getId().toLowerCase().contains(texto)
                    || c.getName().toLowerCase().contains(texto)
                    || c.getEmail().toLowerCase().contains(texto);
            boolean matchTipo  = tipo == null  || c.getClientType().equalsIgnoreCase(tipo);
            boolean matchTp    = tp == null    || c.getDesiredPropertyType() == tp;
            boolean matchEstado= estado == null || c.getSearchStatus().equalsIgnoreCase(estado);
            return matchTexto && matchTipo && matchTp && matchEstado;
        });
    }

    @FXML
    public void limpiarFiltros() {
        campoBusqueda.clear();
        filtroTipoCliente.setValue(null);
        filtroInmuebleDeseado.setValue(null);
        filtroEstadoBusqueda.setValue(null);
        filteredList.setPredicate(c -> true);
    }

    // ─────────────────────────────────────────────────────────
    // Actions
    // ─────────────────────────────────────────────────────────

    @FXML
    public void abrirFormularioRegistro() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/proyectofinal/views/registro-cliente.fxml"));
            Parent root = loader.load();

            RegistroClienteController ctrl = loader.getController();
            ctrl.setOnRegistroExitoso(nuevoCliente -> {
                masterList.add(nuevoCliente);
            });

            Stage dialog = new Stage();
            dialog.setTitle("Registrar cliente");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(tablaClientes.getScene().getWindow());
            dialog.setResizable(false);

            Scene scene = new Scene(root);
            if (!tablaClientes.getScene().getStylesheets().isEmpty()) {
                scene.getStylesheets().addAll(tablaClientes.getScene().getStylesheets());
            }
            dialog.setScene(scene);
            dialog.showAndWait();

        } catch (IOException e) {
            mostrarError("No se pudo abrir el formulario:\n" + e.getMessage());
        }
    }

    @FXML
    public void editarClienteSeleccionado() {
        Client selected = tablaClientes.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/proyectofinal/views/editar-cliente.fxml"));
            Parent root = loader.load();

            EdicionClienteController ctrl = loader.getController();
            ctrl.setClientToEdit(selected);
            ctrl.setOnEdicionExitosa(() -> tablaClientes.refresh());

            Stage dialog = new Stage();
            dialog.setTitle("Editar cliente — " + selected.getName());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(tablaClientes.getScene().getWindow());
            dialog.setResizable(false);

            Scene scene = new Scene(root);
            if (!tablaClientes.getScene().getStylesheets().isEmpty()) {
                scene.getStylesheets().addAll(tablaClientes.getScene().getStylesheets());
            }
            dialog.setScene(scene);
            dialog.showAndWait();

        } catch (IOException e) {
            mostrarError("No se pudo abrir el formulario de edición:\n" + e.getMessage());
        }
    }

    @FXML
    public void eliminarClienteSeleccionado() {
        Client selected = tablaClientes.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar al cliente " + selected.getName() + "?",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                AppContext.getInstance().getClientManager().getAllClients().remove(selected);
                masterList.remove(selected);
            }
        });
    }

    @FXML
    public void verHistorialCliente() {
        Client selected = tablaClientes.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        int visitadas  = selected.getVisitedPropertiesHistory().size();
        int favoritos  = selected.getFavoriteProperties().size();

        mostrarInfo("Cliente: " + selected.getName()
                + "\nPropiedades visitadas: " + visitadas
                + "\nFavoritos guardados: " + favoritos);
    }

    @FXML
    public void verFavoritosCliente() {
        Client selected = tablaClientes.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        StringBuilder sb = new StringBuilder("Favoritos de " + selected.getName() + ":\n");
        if (selected.getFavoriteProperties().size() == 0) {
            sb.append("Sin favoritos aún.");
        } else {
            for (var p : selected.getFavoriteProperties()) {
                sb.append("• ").append(p.getCode()).append(" — ").append(p.getAddress()).append("\n");
            }
        }
        mostrarInfo(sb.toString());
    }

    // ─────────────────────────────────────────────────────────
    private void mostrarInfo(String msg)  { new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait(); }
    private void mostrarError(String msg) { new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait(); }
}
