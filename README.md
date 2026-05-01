# Sistema de Gestión Inmobiliaria PropTech - Proyecto Final Estructuras de Datos

## 📋 Descripción General

Plataforma digital integral (PropTech) para la gestión completa de operaciones inmobiliarias. Sistema diseñado con énfasis en **estructuras de datos avanzadas** para optimizar búsquedas, almacenamiento jerárquico y análisis de información inmobiliaria.

**Propósito:** Demostrar aplicación práctica de estructuras de datos en un caso de negocio real, implementando Listas Enlazadas, Pilas, Colas, Colas de Prioridad, Tablas Hash, Árboles y Grafos.

**Estado:** 🔄 En desarrollo - Fase 1 completada (70%), Fases 2-5 en progreso

---

## ⚡ Resumen Ejecutivo

| Aspecto | Estado | % |
|--------|--------|---|
| **Estructuras de Datos** | 85% Implementadas | ✅ Listas, Pilas, Colas, Árboles, Hash, PriorityQueue |
| **Modelos de Datos** | Completados | ✅ Todas las entidades |
| **Sistema de Gestión** | Parcial | ⚠️ Historial implementado |
| **Servicios/Managers** | No iniciado | ❌ |
| **Interfaz de Usuario** | No iniciado | ❌ |
| **Pruebas Unitarias** | No iniciado | ❌ |

---

## 📂 Estructura del Proyecto

### Organización de Carpetas

```
demo/
├── pom.xml                                      # Configuración Maven (Java 21)
├── src/
│   ├── main/java/proyectofinal/
│   │   ├── Main.java                           # Punto de entrada principal
│   │   │
│   │   ├── EstructurasDeDatos/                 # ⭐ NÚCLEO del proyecto
│   │   │   ├── Listas/
│   │   │   │   ├── Node.java                   # Nodo genérico <T>
│   │   │   │   ├── SimpleLinkedList.java       # ✅ Implementada
│   │   │   │   └── SimpleLinkedListIterator.java
│   │   │   ├── Pilas/
│   │   │   │   ├── Node.java
│   │   │   │   ├── Stack.java                  # ✅ Implementada (LIFO)
│   │   │   │   └── StackIterator.java
│   │   │   ├── Colas/
│   │   │   │   ├── Node.java
│   │   │   │   ├── Queue.java                  # ✅ Implementada (FIFO)
│   │   │   │   └── QueueIterator.java
│   │   │   ├── ColasDePrioridad/
│   │   │   │   ├── PriorityNode.java           # ⏳ Pendiente
│   │   │   │   └── PriorityQueue.java
│   │   │   ├── TablasHash/
│   │   │   │   ├── Node.java                   # ⏳ En desarrollo
│   │   │   │   └── HashTable.java              # Parcialmente completada
│   │   │   ├── Arboles/
│   │   │   │   └── Node.java                   # ⏳ Pendiente
│   │   │   └── Grafo/                          # ⏳ Vacía
│   │   │
│   │   ├── Inmueble/                           # 🏢 Gestión de propiedades
│   │   │   ├── Inmueble.java                   # ✅ Modelo completo
│   │   │   └── TipoInmueble.java               # Enum: APARTAMENTO, CASA, etc.
│   │   │
│   │   ├── Personal/                           # 👥 Gestión de personas
│   │   │   ├── Cliente.java                    # ✅ Modelo con favoritos
│   │   │   └── Asesor.java                     # ✅ Modelo con cartera
│   │   │
│   │   └── SistemaGestion/                     # 🎯 Lógica del negocio
│   │       ├── AgendamientoVisitas/
│   │       │   ├── Visita.java                 # ✅ Con 5 estados
│   │       │   └── EstadoVisita.java           # Enum
│   │       ├── OperacionDeNegocio/
│   │       │   ├── OperacionNegocio.java       # ✅ Transacciones
│   │       │   └── TipoOperacion.java          # Enum
│   │       ├── HistorialInteres/
│   │       │   ├── Historial.java              # ✅ Clase abstracta
│   │       │   ├── HistorialInmueblesConsultados.java
│   │       │   ├── HistorialOperacionesRealizadas.java
│   │       │   ├── HistorialPropiedadesVisitadas.java
│   │       │   └── info.txt
│   │       ├── Alertas/                        # ⏳ Pendiente
│   │       │   └── nota.txt
│   │       └── Contratos/                      # ⏳ Pendiente
│   │           └── nota.txt
│   │
│   └── test/java/                              # 🧪 Tests (vacío)
│
└── target/                                      # 📦 Compilados Maven
    ├── classes/
    └── generated-sources/
```

---

## ✅ Estado de Implementación por Componente

### Estructuras de Datos

| Estructura | Implementación | Métodos Clave | Estado |
|-----------|-----------------|---------------|--------|
| **Lista Enlazada** | SimpleLinkedList<T> | addFirst/Last, remove, get, indexOf, iterator | ✅ Completa |
| **Pila (Stack)** | Stack<T> | push, pop, peek, size, clear, iterator | ✅ Completa |
| **Cola (Queue)** | Queue<T> | enqueue, dequeue, peek, size, clear, iterator | ✅ Completa |
| **Tabla Hash** | HashTable<K,V> | put, get, remove, resize, calculateIndex, containsKey | ✅ Completa |
| **Árbol Binario de Búsqueda** | Tree<T> | put, remove, binarySearch, height, weight, levels, findMin | ✅ Completa |
| **Cola de Prioridad** | PriorityQueue<T> | enqueue, dequeue, peek, siftUp, siftDown, resize | ✅ Completa |
| **Grafo** | Graph | - | ❌ No iniciado |

### Modelos de Datos

| Entidad | Atributos | Métodos | Estado |
|---------|-----------|---------|--------|
| **Inmueble** | código, dirección, precio, tipo, etc. (14 atributos) | getters/setters, toString | ✅ Completa |
| **Cliente** | identificación, nombre, presupuesto, favoritos, etc. | getters/setters, consultarInmuebles (stub) | ✅ Modelo |
| **Asesor** | identificación, nombre, especialidad, cartera | getters/setters, toString | ✅ Modelo |
| **Visita** | cliente, inmueble, fecha, estado, observaciones | getters/setters, 5 estados posibles | ✅ Modelo |
| **OperacionNegocio** | inmueble, cliente, asesor, valor, comisión | getters/setters, tipos de operación | ✅ Modelo |
| **Historial** | Lista<T> genérica | agregarHistorial, consultarElemento (abstract) | ✅ Clase |

### Sistema de Gestión

| Módulo | Componentes | Estado |
|--------|------------|--------|
| **Gestión de Inmuebles** | Inmueble, TipoInmueble | ✅ Modelos |
| **Gestión de Clientes** | Cliente, inmueblesFavoritos | ✅ Modelos |
| **Gestión de Asesores** | Asesor, inmuebleAsignados | ✅ Modelos |
| **Agendamiento de Visitas** | Visita, EstadoVisita (5 estados) | ✅ Modelos |
| **Historial de Interés** | Historial (abstracta), 4 subclases | ✅ Implementado |
| **Operaciones Comerciales** | OperacionNegocio, TipoOperacion | ✅ Modelos |
| **Alertas Automáticas** | AlertasManager (pendiente) | ❌ Por hacer |
| **Contratos** | ContratoManager (pendiente) | ❌ Por hacer |

---

## 🔧 Requisitos del Proyecto Especificación

### Requisitos Funcionales - Estado

✅ **COMPLETADOS:**
- Registrar inmuebles con 14 atributos mínimos
- Registrar clientes con preferencias
- Registrar asesores con especialidades
- Programar visitas (5 estados: PENDIENTE, CONFIRMADA, REALIZADA, CANCELADA, REPROGRAMADA)
- Historial de interés y favoritos
- Operaciones de negocio (ARRIENDO, VENTA, RENOVACIÓN, CANCELACIÓN)

⏳ **EN PROGRESO:**
- Grafos para modelar relaciones

❌ **PENDIENTES:**
- Alertas automáticas (6 tipos previstos)
- Recomendación de inmuebles
- Detección de comportamientos inusuales
- Reportes y análisis
- Interfaz de usuario
- Pruebas unitarias
- Capa de servicios/Managers

---

## 🚀 Guía de Uso

### Configuración del Entorno

**Requisitos:**
- Java 21+ (especificado en pom.xml)
- Maven 3.9+
- IDE: IntelliJ, Eclipse o VS Code

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

1. **Genéricos <T>** - SimpleLinkedList, Stack, Queue, HashTable
2. **Iteradores Personalizados** - SimpleLinkedListIterator, StackIterator, QueueIterator
3. **Herencia Abstracta** - Historial<T> con subclases especializadas
4. **Enumeraciones** - TipoInmueble, EstadoVisita, TipoOperacion
5. **Composición** - Cliente contiene lista de Inmuebles favoritos

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

## 🎯 Roadmap de Desarrollo

### Fase 1: ✅ COMPLETADA (85%)
- [x] Estructuras básicas (Listas, Pilas, Colas)
- [x] Tablas Hash con encadenamiento
- [x] Colas de Prioridad con Heap
- [x] Árboles Binarios de Búsqueda
- [x] Modelos de datos (Entidades principales)
- [x] Sistema de Historial

### Fase 2: ⏳ EN PROGRESO
- [ ] Grafos dirigidos/no dirigidos

### Fase 3: ⏳ SIGUIENTE
- [ ] Capa de Servicios (Managers)
- [ ] InmuebleManager
- [ ] ClienteManager
- [ ] AsesorManager
- [ ] VisitaManager

### Fase 4: ❌ FUTURO
- [ ] Lógica de Negocio avanzada
- [ ] Alertas automáticas
- [ ] Motor de recomendaciones
- [ ] Detección de anomalías

### Fase 5: ❌ FUTURO
- [ ] Interfaz de Usuario (CLI/GUI)
- [ ] Persistencia de datos
- [ ] Pruebas unitarias
- [ ] Documentación Javadoc

---

## 📝 Notas de Desarrollo

### Decisiones de Diseño

1. **Genéricos:** Máxima reutilización de estructuras
2. **Iteradores personalizados:** Control total sobre la iteración
3. **Enums para tipos cerrados:** Mayor type-safety
4. **java.time para fechas:** Manejo moderno de temporalidad
5. **SimpleLinkedList como base:** Otras estructuras la usan internamente

### Problemas Conocidos

- `Main.java` solo imprime "Hello world!" - Requiere menú interactivo
- Falta implementación de métodos stub en Cliente/Asesor
- No hay persistencia de datos (archivo/BD)
- Validaciones limitadas en modelos

### Mejoras Futuras Recomendadas

- [ ] Implementar persistencia JSON/XML
- [ ] Agregar validaciones (@NotNull, @Min, etc.)
- [ ] Logging con SLF4J
- [ ] Pruebas unitarias con JUnit 5
- [ ] Documentación Javadoc
- [ ] Docker para deployment
- [ ] Base de datos SQL

---

## 👥 Autoría y Referencias

**Autores:** Mateo Gómez Marulanda  
**Proyecto:** Proyecto Final - Estructuras de Datos (Curso 2026-1)  
**Institución:** Universidad [A completar]  
**Fecha de creación:** 2026  

### Especificación Original

Documento: "Proyecto Final estructuras de datos dia 2026-1.pdf"  
Requisitos: 9 funcionalidades principales  
Estructuras: 7 tipos diferentes  
Modelos: 8+ entidades  

---

## 📞 Contacto y Soporte

Para dudas sobre el proyecto:
- Revisar la documentación en cada clase (Javadoc)
- Consultar el README.md para guía general
- Revisar los unit tests (cuando estén disponibles)

**Estado de la documentación:** Actualizada al 30 de abril de 2026 ✅

#### Inmueble.java ✅
Representa una propiedad con los siguientes atributos:
```
- código
- dirección
- ciudad
- barrio/zona
- tipoInmueble (enum)
- finalidad (venta/arriendo)
- precio (double)
- área (double)
- numeroHabitaciones (int)
- numeroBanios (int)
- estadoInmueble (String)
- disponibilidad (boolean)
- asesorResponsable (Asesor)
```
**Métodos:** Getters, setters y toString()

#### TipoInmueble.java ✅
Enumeración con tipos válidos:
```
APARTAMENTO, CASA, LOCAL_COMERCIAL, 
OFICINA, LOTE, BODEGA
```

#### Cliente.java ✅
Representa un cliente interesado en comprar/arrendar:
```
- identificacion, nombre, correo, teléfono
- tipoCliente, presupuesto
- zonasInteres (Object - estructura por definir)
- tipoInmuebleDeseado (TipoInmueble)
- cantidadMinimaHabitaciones
- estadoBusqueda
```
**Métodos stub:** `consultarInmuebles()`, `agendarVisita()`, `marcarFavorito()`, `registrarIntencionCompraArriendo()`, `consultarHistorialInteracciones()`

#### Asesor.java ✅
Representa un asesor inmobiliario:
```
- identificacion, nombre, contacto
- especialidadZona
- inmuebleAsignados (Object - estructura por definir)
- visitasAgendadas (Object - estructura por definir)
- cierresRealizados (int)
```

#### Visita.java ✅
Registra una visita a una propiedad:
```
- cliente (Cliente)
- inmueble (Inmueble)
- fecha (LocalDate)
- hora (LocalTime)
- asesorAsignado (Asesor)
- estadoVisita (EstadoVisita)
- observacionesPosteriores (String)
```

#### EstadoVisita.java ✅
Enumeración de estados:
```
PENDIENTE, CONFIRMADA, REALIZADA, CANCELADA, REPROGRAMADA
```

#### OperacionNegocio.java ✅
Registra operaciones de compra/arriendo:
```
- identificador, inmuebleRelacionado, cliente, asesor
- fecha (LocalDate)
- tipoOperacion (TipoOperacion)
- valorAcordado (double)
- comision (double)
- estadoProceso (String)
```

#### TipoOperacion.java ✅
Enumeración de operaciones:
```
ARRIENDO, VENTA, RENOVACION_CONTRATO, CANCELACION_NEGOCIO
```

---

## 🚀 Características Previstas (Por Completar)

### Estructuras de Datos Faltantes

| Estructura | Uso Previsto | Estado |
|-----------|--------------|--------|
| **Pilas** | Deshacer cambios, historial de acciones | ✅ Implementada |
| **Colas** | Solicitudes de clientes, visitas pendientes | ✅ Implementada |
| **Colas de Prioridad** | Visitas VIP, alertas urgentes | ✅ Implementada |
| **Tablas Hash** | Búsqueda rápida de clientes, inmuebles, asesores | ✅ Implementada |
| **Árboles Binarios de Búsqueda** | Ordenar inmuebles por precio, clientes por presupuesto | ✅ Implementada |
| **Grafos** | Relaciones cliente-inmueble, análisis de movilidad | ⏳ Pendiente |

### Funcionalidades del Sistema

#### Gestión de Recursos
- [ ] Registrar, modificar, eliminar y consultar inmuebles
- [ ] Registrar, modificar, eliminar y consultar clientes
- [ ] Registrar, modificar y consultar asesores
- [ ] Programar, reprogramar y cancelar visitas

#### Análisis e Inteligencia
- [ ] Historial de interés y favoritos
- [ ] Alertas automáticas (contratos vencidos, propiedades sin visitas, etc.)
- [ ] Recomendación de inmuebles según preferencias
- [ ] Detección de comportamientos comerciales inusuales
- [ ] Ranking de zonas con mayor actividad
- [ ] Ranking de asesores por efectividad

#### Reportes y Consultas
- [ ] Consultar reportes por zona, precio, visitas y cierres
- [ ] Ordenar inmuebles por precio, área o demanda
- [ ] Análisis de relaciones estructurales entre clientes e inmuebles
- [ ] Filtros combinados para búsquedas avanzadas

---

## 🔧 Configuración del Proyecto

### Requisitos
- **Java 17** (especificado en pom.xml)
- **Maven** para compilación y gestión de dependencias

### Compilación
```bash
cd demo
mvn clean compile
```

### Ejecución
```bash
mvn exec:java -Dexec.mainClass="proyectofinal.Main"
```

### Compilación de Clases Específicas
```bash
javac -d target/classes src/main/java/proyectofinal/**/*.java
```

---

## 📊 Estado de Implementación

### Resumen de Progreso

| Componente | Completitud | Notas |
|-----------|------------|-------|
| **Estructuras de Datos** | 85% | Listas, Pilas, Colas, HashTable, Árboles BST, ColasPrioridad implementadas. Falta: Grafos |
| **Modelos de Datos** | 80% | Todas las entidades principales creadas |
| **Sistema de Gestión** | 20% | Historial de intereses implementado, falta Alertas y Contratos |
| **Interfaz de Usuario** | 0% | Main.java solo imprime "Hello world!" |
| **Pruebas Unitarias** | 0% | Carpeta test vacía |

### Próximos Pasos Recomendados

**Fase 1: Estructuras de Datos - ✅ COMPLETADA**
1. ✅ Implementar `Stack` - COMPLETADO
2. ✅ Implementar `Queue` - COMPLETADO
3. ✅ Implementar `PriorityQueue` - COMPLETADO
4. ✅ Implementar `HashTable` - COMPLETADO
5. ✅ Implementar `BinarySearchTree` - COMPLETADO
6. ⏳ Implementar `Graph` - EN PROGRESO

**Fase 2: Servicios/Managers**
1. `InmuebleManager` - Gestión de propiedades
2. `ClienteManager` - Gestión de clientes
3. `AsesorManager` - Gestión de asesores
4. `VisitaManager` - Programación de visitas
5. `OperacionesManager` - Registro de operaciones
6. `HistorialManager` - Historial de interacciones
7. `AlertasManager` - Sistema de alertas
8. `RecomendacionesManager` - Motor de recomendaciones

**Fase 3: Lógica de Negocio**
1. Implementar validaciones (presupuesto, disponibilidad, consistencia)
2. Detección de comportamientos inusuales
3. Generación de reportes

**Fase 4: Interfaz de Usuario**
1. Menú interactivo en consola
2. Opciones de CRUD para cada entidad
3. Consultas y reportes

**Fase 5: Testing**
1. Pruebas unitarias de estructuras de datos
2. Pruebas de integración
3. Casos de prueba de ejemplo

---

## 📝 Notas de Desarrollo

### Decisiones de Diseño Actuales

1. **Genéricos:** Las estructuras de datos (Node, SimpleLinkedList) usan genéricos `<T>` para máxima reutilización
2. **Modelos Ricos:** Las entidades incluyen métodos de lógica básica (stubs por ahora)
3. **Enumeraciones:** Se usan enums para tipos cerrados de valores
4. **Fechas/Horas:** Se usa `java.time` para manejo moderno de fechas

### Problemas Conocidos

- `Main.java` actualmente solo imprime "Hello world!" - requiere implementación de menú interactivo
- Estructuras de Tablas Hash, Árboles y Grafos no están implementadas
- ColasDePrioridad solo tiene clase Node sin la implementación completa
- Los campos `Object` en Cliente y Asesor (zonasInteres, inmuebleAsignados, visitasAgendadas) deben especificarse con estructuras reales
- Falta la capa de servicios (Managers) para aplicar la lógica de negocio
- Las carpetas de Alertas y Contratos existen pero sin implementación

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
- Preparación para análisis de datos e inteligencia de negocio