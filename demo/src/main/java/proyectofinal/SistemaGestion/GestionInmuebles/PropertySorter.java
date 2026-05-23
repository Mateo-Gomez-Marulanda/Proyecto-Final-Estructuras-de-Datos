package proyectofinal.SistemaGestion.GestionInmuebles;

import proyectofinal.EstructurasDeDatos.Listas.SimpleLinkedList;
import proyectofinal.Inmueble.Property;
import proyectofinal.SistemaGestion.AgendamientoVisitas.VisitManager;
import proyectofinal.SistemaGestion.AgendamientoVisitas.VisitRequest;

public class PropertySorter {

    public enum SortCriterion {
        PRICE_DESC,     // Precio (Mayor a menor) - Solo si no usas el In-Orden inverso del Árbol
        AREA_DESC,      // Área (Mayor a menor)
        DEMAND_DESC     // Demanda de visitas (Mayor a menor)
    }

    /**
     * Ordena una lista de inmuebles utilizando Merge Sort (O(n log n)).
     */
    public static SimpleLinkedList<Property> sortProperties(
            SimpleLinkedList<Property> inventory, 
            SortCriterion criterion, 
            VisitManager vm) {

        int size = getListSize(inventory);
        if (size <= 1) return inventory;

        // 1. Extraer a un arreglo para facilitar el MergeSort sin romper el encapsulamiento de tu lista
        Property[] arr = new Property[size];
        int index = 0;
        for (Property p : inventory) {
            arr[index++] = p;
        }

        // 2. Ejecutar Merge Sort
        mergeSort(arr, 0, arr.length - 1, criterion, vm);

        // 3. Reconstruir en una nueva SimpleLinkedList para proteger los datos originales
        SimpleLinkedList<Property> sortedList = new SimpleLinkedList<>();
        for (Property p : arr) {
            sortedList.add(p);
        }
        
        return sortedList;
    }

    private static void mergeSort(Property[] arr, int left, int right, SortCriterion criterion, VisitManager vm) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSort(arr, left, mid, criterion, vm);
            mergeSort(arr, mid + 1, right, criterion, vm);
            merge(arr, left, mid, right, criterion, vm);
        }
    }

    private static void merge(Property[] arr, int left, int mid, int right, SortCriterion criterion, VisitManager vm) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        Property[] L = new Property[n1];
        Property[] R = new Property[n2];

        System.arraycopy(arr, left, L, 0, n1);
        System.arraycopy(arr, mid + 1, R, 0, n2);

        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            // Evaluamos si L[i] debe ir antes que R[j] según el criterio
            if (compare(L[i], R[j], criterion, vm)) {
                arr[k] = L[i];
                i++;
            } else {
                arr[k] = R[j];
                j++;
            }
            k++;
        }

        while (i < n1) {
            arr[k] = L[i];
            i++;
            k++;
        }
        while (j < n2) {
            arr[k] = R[j];
            j++;
            k++;
        }
    }

    /**
     * Retorna true si 'a' debe estar posicionado ANTES que 'b'
     */
    private static boolean compare(Property a, Property b, SortCriterion criterion, VisitManager vm) {
        switch (criterion) {
            case PRICE_DESC:
                return a.getPrice() >= b.getPrice();
            case AREA_DESC:
                return a.getArea() >= b.getArea();
            case DEMAND_DESC:
                return getDemandScore(a, vm) >= getDemandScore(b, vm);
            default:
                return false;
        }
    }

    private static int getDemandScore(Property p, VisitManager vm) {
        int count = 0;
        if (vm == null) return 0;
        for (VisitRequest v : vm.getVisitHistory()) {
            if (v.getProperty().getCode().equals(p.getCode())) {
                count++;
            }
        }
        return count;
    }

    private static int getListSize(SimpleLinkedList<Property> list) {
        int count = 0;
        for (Property ignored : list) count++;
        return count;
    }
}