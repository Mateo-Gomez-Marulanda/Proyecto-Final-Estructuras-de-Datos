# Sistema de Gestión Inmobiliaria PropTech - Proyecto Final Estructuras de Datos

## 📋 Descripción General

Plataforma digital integral (PropTech) para la gestión completa de operaciones inmobiliarias. Sistema diseñado con énfasis en **estructuras de datos avanzadas** para optimizar búsquedas, almacenamiento jerárquico y análisis de información inmobiliaria.

**Propósito:** Demostrar aplicación práctica de estructuras de datos en un caso de negocio real, implementando Listas Enlazadas, Pilas, Colas, Colas de Prioridad, Tablas Hash, Árboles y Grafos.

**Estado:** ✅ Completo - Fase 1-4 completadas (100%)

---

## ⚡ Resumen Ejecutivo

| Aspecto | Estado | % |
|--------|--------|---|
| **Estructuras de Datos** | 100% Implementadas | ✅ Listas, Pilas, Colas, Árboles, Hash, PriorityQueue, Grafos |
| **Modelos de Datos** | 100% Completados | ✅ Todas las entidades con atributos completos |
| **Servicios/Managers** | 100% Implementados | ✅ PropertyManager, ClientManager, VisitManager, AlertEngine, etc. |
| **Sistema de Gestión** | 100% Implementado | ✅ Todos los módulos funcionales |
| **Patrones de Diseño** | 100% Implementados | ✅ Observer, Stack-based History, Priority Queues |
| **Interfaz de Usuario** | 100% Implementada | ✅ Controladores GUI y vistas FXML completamente funcionales |

---

## ✅ Estado de Implementación por Componente

### Estructuras de Datos

| Estructura | Implementación | Métodos Clave | Estado |
|-----------|-----------------|---------------|--------|
| **Lista Enlazada** | SimpleLinkedList<T> | addFirst, add (con índice), remove (por índice/dato), get, indexOf, isEmpty, size, clearList, iterator | ✅ Completa |
| **Pila (Stack)** | Stack<T> | push, pop, peek, size, clear, isEmpty, contains, iterator | ✅ Completa |
| **Cola (Queue)** | Queue<T> | enqueue, dequeue, peek, size, clear, isEmpty, contains, iterator | ✅ Completa |
| **Tabla Hash** | HashTable<K,V> | put, get, remove, clear, resize, calculateIndex, containsKey, isEmpty, size, capacity | ✅ Completa |
| **Árbol Binario de Búsqueda** | Tree<T> | put, remove, binarySearch, findMin, weight, isEmpty, size, root | ✅ Completa |
| **Cola de Prioridad** | PriorityQueue<T> | enqueue, dequeue, peek, peekPriority, size, isEmpty, siftUp, siftDown, resize | ✅ Completa |
| **Grafo Dirigido Ponderado** | Graph<T> con Vertex<T> y Edge<T> | addVertex, removeVertex, addEdge, removeEdge, getVertex, containsVertex, isEmpty, size, edgeCount, getVertex, getEdges | ✅ Completa |

### Modelos de Datos

| Entidad | Implementación | Atributos Clave | Estado |
|---------|-----------------|-----------------|--------|
| **Property** | Property.java | code, address, city, zone, propertyType, purpose, price, area, rooms, bathrooms, propertyStatus, isAvailable, responsibleAdvisor, priceChangeCount | ✅ Completa |
| **Client** | Client.java | id, name, email, phoneNumber, clientType, budget, favoriteProperties, visitedPropertyHistory, visitasProgramadas, searchStatus | ✅ Completa |
| **Advisor** | Advisor.java | id, name, email, phoneNumber, specialty, assignedProperties, completedClosings | ✅ Completa |
| **Visit** | Visit.java | client, property, date, time, assignedAdvisor, visitStatus, postVisitObservations | ✅ Completa |
| **BusinessOperation** | BusinessOperation.java | identifier, relatedProperty, client, advisor, date, operationType, agreedValue, commission, processStatus | ✅ Completa |
| **Contract** | Contract.java | id, name, address, owner, relatedProperty, creationDate, expirationDate, approvingAdvisor, status | ✅ Completa |
| **History<T>** | History.java (abstracta) | historyList (SimpleLinkedList<T>) | addToHistory, removeFromHistory, displayHistory, findElementById | ✅ Completa |

### Sistema de Gestión

| Módulo | Componentes | Estado |
|--------|------------|--------|
| **Gestión de Inmuebles** | PropertyManager, PropertySorter, PropertyChange, TypeProperty, ZoneProperty | ✅ Implementado |
| **Gestión de Clientes** | ClientManager, Client (con SimpleLinkedList para favoritos) | ✅ Implementado |
| **Gestión de Asesores** | Advisor (integrado en sistema) | ✅ Implementado |
| **Agendamiento de Visitas** | VisitManager, Visit, VisitStatus (Queue y PriorityQueue integradas) | ✅ Implementado |
| **Historial de Interés** | History<T> (abstracta), PropertyConsultationHistory, VisitedPropertiesHistory, BusinessOperationHistory | ✅ Implementado |
| **Operaciones Comerciales** | BusinessOperation, OperationType, ProcessStatus | ✅ Implementado |
| **Alertas Automáticas** | AlertEngine, Alert, AlertType, AlertStatus, AnomalyDetector | ✅ Implementado |
| **Contratos** | Contract, ContractStatus (registro estático de contratos) | ✅ Implementado |
| **Reportes** | ReportEngine (análisis por zonas, asesores, propiedades) | ✅ Implementado |
| **Patrones** | Observer (ContractGeneratorObserver, OperationPublisher, OperationEvent) | ✅ Implementado |
| **Persistencia** | PersistenceManager | ✅ Implementado |
| **Grafos** | GraphService | ✅ Implementado |

---

## 🔧 Requisitos del Proyecto Especificación

### Requisitos Funcionales - Estado

✅ **COMPLETADOS:**
- Registrar inmuebles con 14+ atributos (Property con tracking de cambios de precio)
- Registrar clientes con preferencias y gestión de favoritos con SimpleLinkedList
- Registrar asesores con especialidades y cartera de propiedades
- Programar visitas (5 estados: PENDING, CONFIRM, REALIZED, CANCELED, RESCHEDULED)
- Historial de interés y favoritos implementado con History genérica
- Operaciones de negocio (SALE, RENT, RENEWAL, CANCELLATION)
- Alertas automáticas con detección de anomalías (AlertEngine, AnomalyDetector)
- Sistema de contratos con validación de vencimiento
- Reportes y análisis por zonas, asesores y propiedades (ReportEngine)
- Todas las estructuras de datos (Listas, Pilas, Colas, Hash, Árboles, Colas de Prioridad, Grafos)
- Capa completa de servicios/Managers (PropertyManager, VisitManager, ClientManager, etc.)
- Patrones de diseño avanzados (Observer, Stack-based History)
- Persistencia de datos (PersistenceManager)
- Servicios de Grafos para análisis de relaciones entre entidades

⏳ **EN PROGRESO (Mejoras Futuras):**
- Motor de recomendación inteligente de inmuebles (versión mejorada)
- Optimización avanzada de detección de comportamientos inusuales

---

## 🚀 Guía de Uso

### Configuración del Entorno

**Requisitos:**
- Java 21+ (especificado en pom.xml)
- Maven 3.9+
- IDE: IntelliJ IDEA, Eclipse o VS Code

### Compilación

```bash
cd demo
mvn clean compile
```

### Ejecución

```bash
# Ejecutar Main.java
mvn exec:java -Dexec.mainClass="proyectofinal.Main"

# O compilar manualmente
javac -d target/classes src/main/java/proyectofinal/**/*.java
java -cp target/classes proyectofinal.Main
```

### Compilación de módulos específicos

```bash
# Solo estructuras de datos
javac -d target/classes src/main/java/proyectofinal/EstructurasDeDatos/**/*.java

# Solo modelos
javac -d target/classes src/main/java/proyectofinal/Inmueble/*.java
javac -d target/classes src/main/java/proyectofinal/Personal/*.java
```

---

## 📊 Análisis Técnico

### Patrones de Diseño Implementados

1. **Genéricos <T>** - SimpleLinkedList, Stack, Queue, HashTable, Tree, PriorityQueue, Graph, History
2. **Iteradores Personalizados** - SimpleLinkedListIterator, StackIterator, QueueIterator
3. **Herencia Abstracta** - History<T> con subclases especializadas (PropertyConsultationHistory, VisitedPropertiesHistory, BusinessOperationHistory)
4. **Enumeraciones** - TypeProperty, ZoneProperty, VisitStatus, OperationType, ProcessStatus, AlertType, AlertStatus, ContractStatus
5. **Composición** - Client contiene SimpleLinkedList de Properties favoritas, Visit, etc.
6. **Observer Pattern** - ContractGeneratorObserver, OperationObserver, OperationPublisher para notificaciones de eventos
7. **Stack-based History** - PropertyChange, statusHistory, modificationHistory en PropertyManager
8. **Factory Pattern** - AlertEngine genera diferentes tipos de alertas automáticamente
9. **Strategy Pattern** - PropertySorter ordena por diferentes criterios (precio, área, demanda)
10. **Singleton Pattern** - AppContext para contexto global de la aplicación

---

## 🎨 Interfaz Gráfica de Usuario (GUI)

### Rutas de Implementación

| Componente | Ruta |
|-----------|------|
| **Controladores** | `demo/src/main/java/proyectofinal/controllers/` |
| **Vistas FXML** | `demo/src/main/resources/proyectofinal/views/` |
| **Estilos CSS** | `demo/src/main/resources/proyectofinal/css/` |

### Controladores por Módulo (30 controladores)

#### 🏠 Gestión de Inmuebles
- InmueblesController.java
- RegistroInmuebleController.java
- EdicionInmuebleController.java
- GestionInmueblesAsesorController.java
- HistorialInmueblesController.java

#### 👥 Gestión de Clientes
- ClientesController.java
- RegistroClienteController.java
- EdicionClienteController.java

#### 🧑‍💼 Gestión de Asesores
- AsesoresController.java
- RegistroAsesorController.java
- EdicionAsesorController.java

#### 📅 Gestión de Visitas
- VisitasController.java
- AgendarVisitaController.java
- AgendarVisitaCLIController.java
- ClientVisitsController.java

#### 💼 Operaciones y Contratos
- OperacionesController.java
- CrearContratoController.java

#### 📊 Sistema Central
- ShellController.java
- DashboardController.java
- AlertasController.java
- ReportesController.java
- AppContext.java

#### 🔐 Portal de Cliente
- ClientShellController.java
- ClientDashboardController.java
- ClientCatalogController.java
- ClientFavoritesController.java
- ClientProfileController.java

#### 🔑 Autenticación
- LoginController.java
- RegisterController.java

### Vistas FXML por Módulo (25 interfaces)

#### 🏠 Gestión de Inmuebles
- inmuebles-content.fxml
- registro-inmueble.fxml
- editar-inmueble.fxml
- gestionar-inmuebles-asesor.fxml
- historial-inmuebles.fxml

#### 👥 Gestión de Clientes
- clientes-content.fxml
- registro-cliente.fxml
- editar-cliente.fxml

#### 🧑‍💼 Gestión de Asesores
- asesores-content.fxml
- registro-asesor.fxml
- editar-asesor.fxml

#### 📅 Gestión de Visitas
- visitas-content.fxml
- agendar-visita.fxml
- agendar-visita-cliente.fxml
- client-visits.fxml

#### 💼 Operaciones y Contratos
- operaciones-content.fxml
- CrearContratoModal.fxml

#### 📊 Dashboard y Reportes
- main-shell.fxml
- dashboard-content.fxml
- Reportes.fxml

#### 🔐 Portal de Cliente
- client-shell.fxml
- client-dashboard.fxml
- client-catalog.fxml
- client-favorites.fxml
- client-profile.fxml

#### 🔑 Autenticación
- login.fxml
- register-content.fxml

### Estilos CSS
- **estilos.css** - Estilos generales y temas de la aplicación

### Complejidades Algorítmicas (Teóricas)

| Operación | Listas Enlazadas | Pilas | Colas | Hash Tables |
|-----------|------------------|-------|-------|-------------|
| **Inserción** | O(n) | O(1) | O(1) | O(1) promedio |
| **Búsqueda** | O(n) | O(n) | O(n) | O(1) promedio |
| **Eliminación** | O(n) | O(1) | O(1) | O(1) promedio |
| **Acceso** | O(n) | O(1) tope | O(1) frente | O(1) promedio |

### Consideraciones de Memoria

- **Listas enlazadas**: Overhead por nodos (nextNode + dato)
- **Hash Tables**: Factor de carga 0.75, redimensiona al 2x capacity
- **Pilas/Colas**: O(n) memoria para n elementos
- **Iteradores**: O(1) memoria adicional

---

## 👥 Autoría y Referencias

**Autores:**
- Tomas Castaño Ortiz
- Andrés Felipe Valencia Arias
- Mateo Gómez Marulanda

**Proyecto:** Proyecto Final - Estructuras de Datos (Curso 2026-1)  
**Institución:** Universidad del Quindio
**Fecha de creación:** 2026  

---

**Estado de la documentación:** Actualizada al 26 de mayo de 2026 ✅

---

## 📊 Estado de Implementación

### Resumen de Progreso

| Componente | Completitud | Notas |
|-----------|------------|-------|
| **Estructuras de Datos** | 100% | Listas, Pilas, Colas, HashTable, Árboles BST, ColasPrioridad, Grafos - TODAS COMPLETAS |
| **Modelos de Datos** | 100% | Property, Client, Advisor, Visit, BusinessOperation, Contract, History - TODAS COMPLETAS |
| **Sistema de Gestión** | 100% | PropertyManager, ClientManager, VisitManager, AlertEngine, ContractManager, ReportEngine - TODOS IMPLEMENTADOS |
| **Servicios/Managers** | 100% | Capa de servicios completamente funcional con todas las operaciones CRUD |
| **Patrones de Diseño** | 100% | Observer, Stack-based History, Priority Queues implementados |
| **Interfaz Gráfica** | 100% | 30 controladores, 25 vistas FXML, estilos CSS - COMPLETAMENTE FUNCIONAL |

### Próximos Pasos Recomendados

**Fase 1: Estructuras de Datos - ✅ COMPLETADA (100%)**
1. ✅ Implementar `Stack` - COMPLETADO
2. ✅ Implementar `Queue` - COMPLETADO
3. ✅ Implementar `PriorityQueue` - COMPLETADO
4. ✅ Implementar `HashTable` - COMPLETADO
5. ✅ Implementar `BinarySearchTree` - COMPLETADO
6. ✅ Implementar `Graph` - COMPLETADO

**Fase 2: Servicios/Managers - ✅ COMPLETADA (100%)**
1. ✅ `PropertyManager` - Gestión de propiedades
2. ✅ `ClientManager` - Gestión de clientes
3. ✅ `Advisor` Manager - Gestión de asesores
4. ✅ `VisitManager` - Programación de visitas
5. ✅ `BusinessOperationManager` - Registro de operaciones
6. ✅ `History<T>` Manager - Historial de interacciones
7. ✅ `AlertEngine` - Sistema de alertas inteligente
8. ✅ `ReportEngine` - Motor de reportes

**Fase 3: Lógica de Negocio - ✅ COMPLETADA (100%)**
1. ✅ Validaciones de presupuesto, disponibilidad y consistencia
2. ✅ Detección de comportamientos inusuales (AnomalyDetector)
3. ✅ Generación de reportes por zonas, asesores, propiedades
4. ✅ Sistema de contratos con vencimiento automático
5. ✅ Persistencia de datos (PersistenceManager)
6. ✅ Patrones Observer para notificaciones

**Fase 4: Interfaz de Usuario - ✅ COMPLETADA (100%)**
1. ✅ Controladores GUI funcionales (30 controladores)
2. ✅ Vistas FXML completas (25 interfaces)
3. ✅ Estilos CSS implementados
4. ✅ Dashboard de administrador funcional
5. ✅ Portal de cliente funcional
6. ✅ Portal de asesor funcional
7. ✅ Módulos de gestión completamente funcionales

---

## 📚 Referencia de Requisitos Documentados

El proyecto fue especificado en el documento adjunto: *"Proyecto Final estructuras de datos dia 2026-1.pdf"*

### Funcionalidades Clave Del SPEC

**4.1 Gestión de Inmuebles** - Registrar propiedades con 14 atributos
**4.2 Gestión de Clientes** - Registrar clientes con preferencias y historial
**4.3 Gestión de Asesores** - Administrar asesores con cargas de trabajo
**4.4 Programación de Visitas** - Agendar visitas con 5 estados posibles
**4.5 Historial de Interés** - Guardar favoritos e interacciones
**4.6 Operaciones de Negocio** - Registrar transacciones (arriendo/venta)
**4.7 Alertas Automáticas** - 6 tipos de alertas previstas
**4.8 Recomendaciones** - Sugerir inmuebles según criterios
**4.9 Detección de Comportamientos** - Detectar patrones inusuales

---

## ✨ Notas Finales

Este proyecto es una implementación completa de un sistema PropTech que demuestra:
- Uso correcto de estructuras de datos para cada caso de uso
- Diseño orientado a objetos con entidades bien definidas
- Patrones de cascada para gestión de datos relacionales
- Integración de estructuras de datos avanzadas en un caso de negocio real