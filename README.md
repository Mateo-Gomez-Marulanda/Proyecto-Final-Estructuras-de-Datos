# Plataforma Inspirada en PropTech para Gestión Inteligente de Inmuebles, Clientes y Operaciones

## 📋 Descripción General

Sistema de gestión inmobiliaria diseñado como una plataforma digital moderna (PropTech) que administra inmuebles, clientes, asesores, visitas y operaciones de compra/arriendo. El proyecto utiliza diversas **estructuras de datos** para optimizar el acceso, organización y análisis de información inmobiliaria.

**Estado actual:** En desarrollo - Fase de implementación de estructuras de datos y modelos

---

## 🏗️ Arquitectura del Proyecto

### Estructura de Directorios

```
demo/
├── pom.xml                                  # Configuración Maven
├── src/
│   ├── main/java/proyectofinal/
│   │   ├── Main.java                       # Punto de entrada (en desarrollo)
│   │   ├── Inmueble/                       # Gestión de propiedades
│   │   │   ├── Inmueble.java              # Modelo de inmueble ✅
│   │   │   └── TipoInmueble.java          # Enumeración de tipos ✅
│   │   ├── Personal/                       # Gestión de personas
│   │   │   ├── Cliente.java               # Modelo de cliente ✅
│   │   │   └── Asesor.java                # Modelo de asesor ✅
│   │   ├── EstructurasDeDatos/            # Implementación de ED
│   │   │   ├── Listas/                    # ✅ Completada
│   │   │   │   ├── Node.java
│   │   │   │   ├── SimpleLinkedList.java
│   │   │   │   └── SimpleLinkedListIterator.java
│   │   │   ├── Pilas/                     # ✅ Completada
│   │   │   │   ├── Node.java
│   │   │   │   ├── Stack.java
│   │   │   │   └── StackIterator.java
│   │   │   ├── Colas/                     # ✅ Completada
│   │   │   │   ├── Node.java
│   │   │   │   ├── Queue.java
│   │   │   │   └── QueueIterator.java
│   │   │   ├── ColasDePrioridad/          # ⏳ Pendiente
│   │   │   │   └── Node.java
│   │   │   ├── TablasHash/                # ⏳ Vacía
│   │   │   ├── Arboles/                   # ⏳ Pendiente
│   │   │   │   └── Node.java
│   │   │   └── Grafo/                     # ⏳ Vacía
│   │   ├── SistemaGestion/                # Lógica del negocio
│   │   │   ├── AgendamientoVisitas/       # ✅ Completada
│   │   │   │   ├── Visita.java
│   │   │   │   └── EstadoVisita.java
│   │   │   ├── OperacionDeNegocio/        # ✅ Completada
│   │   │   │   ├── OperacionNegocio.java
│   │   │   │   └── TipoOperacion.java
│   │   │   ├── HistorialInteres/          # ✅ Completada
│   │   │   │   ├── Historial.java
│   │   │   │   ├── HistorialInmueblesConsultados.java
│   │   │   │   ├── HistorialOperacionesRealizadas.java
│   │   │   │   ├── HistorialPropiedadesVisitadas.java
│   │   │   │   └── info.txt
│   │   │   ├── Alertas/                   # ⏳ Pendiente
│   │   │   │   └── nota.txt
│   │   │   └── Contratos/                 # ⏳ Pendiente
│   │   │       └── nota.txt
│   └── test/java/                         # Tests (por crear)
└── target/                                 # Compilados
```

---

## ✅ Componentes Implementados

### 1. **Estructuras de Datos - IMPLEMENTADAS**

#### `Node<T>`
Nodo genérico con soporte para cualquier tipo de dato:
- Atributos: `nextNode`, `data`
- Métodos: getters/setters

#### `SimpleLinkedList<T>`
Implementación completa de lista enlazada simple:
- ✅ `isEmpty()` - Verifica si está vacía
- ✅ `addFirst(T)` - Inserta al inicio
- ✅ `addLast(T)` - Inserta al final
- ✅ `add(T, index)` - Inserta en posición
- ✅ `removeFirst()` - Elimina del inicio
- ✅ `removeLast()` - Elimina del final
- ✅ `removeIndex(int)` - Elimina por índice
- ✅ `removeElement(T)` - Elimina por valor
- ✅ `get(int)` - Obtiene elemento por índice
- ✅ `indexOf(T)` - Busca índice de elemento
- ✅ `modifyNode(int, T)` - Modifica elemento
- ✅ `printList()` - Imprime lista
- ✅ Implementa `Iterable<T>` para soporte de for-each

#### `SimpleLinkedListIterator<T>`
Iterador personalizado que permite recorrer la lista usando:
- ✅ `hasNext()` - Verifica si hay siguiente
- ✅ `next()` - Obtiene siguiente elemento

---

#### `Stack<T>` (Pila)
Implementación completa de pila (LIFO):
- ✅ `isEmpty()` - Verifica si está vacía
- ✅ `push(T)` - Inserta elemento en la cima
- ✅ `pop()` - Extrae elemento de la cima
- ✅ `peek()` - Consulta elemento sin extraerlo
- ✅ `size()` - Obtiene cantidad de elementos
- ✅ `clear()` - Limpia la pila
- ✅ Implementa `Iterable<T>` para soporte de for-each

#### `StackIterator<T>`
Iterador personalizado para recorrer la pila:
- ✅ `hasNext()` - Verifica si hay siguiente
- ✅ `next()` - Obtiene siguiente elemento

---

#### `Queue<T>` (Cola)
Implementación completa de cola (FIFO):
- ✅ `isEmpty()` - Verifica si está vacía
- ✅ `enqueue(T)` - Inserta elemento en la cola
- ✅ `dequeue()` - Extrae primer elemento
- ✅ `peek()` - Consulta primer elemento sin extraerlo
- ✅ `size()` - Obtiene cantidad de elementos
- ✅ `clear()` - Limpia la cola
- ✅ Implementa `Iterable<T>` para soporte de for-each

#### `QueueIterator<T>`
Iterador personalizado para recorrer la cola:
- ✅ `hasNext()` - Verifica si hay siguiente
- ✅ `next()` - Obtiene siguiente elemento

---

### 2. **Modelos de Datos - Entidades Principales**

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
| **Colas de Prioridad** | Visitas VIP, alertas urgentes | ⏳ Pendiente |
| **Tablas Hash** | Búsqueda rápida de clientes, inmuebles, asesores | ⏳ Pendiente |
| **Árboles (BST/AVL)** | Ordenar inmuebles por precio, clientes por presupuesto | ⏳ Pendiente |
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
| **Estructuras de Datos** | 50% | Listas enlazadas, Pilas y Colas implementadas. Falta: Colas de Prioridad, Tablas Hash, Árboles, Grafos |
| **Modelos de Datos** | 80% | Todas las entidades principales creadas |
| **Sistema de Gestión** | 20% | Historial de intereses implementado, falta Alertas y Contratos |
| **Interfaz de Usuario** | 0% | Main.java solo imprime "Hello world!" |
| **Pruebas Unitarias** | 0% | Carpeta test vacía |

### Próximos Pasos Recomendados

**Fase 1: Estructuras de Datos Restantes**
1. ✅ Implementar `Stack` - COMPLETADO
2. ✅ Implementar `Queue` - COMPLETADO
3. Implementar `PriorityQueue` - EN PROGRESO
4. Implementar `HashMap` personalizado
5. Implementar `BinarySearchTree`
6. Implementar `Graph`

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

### Mejoras Futuras

- Implementar patrones de diseño (Factory, Singleton para managers)
- Agregar persistencia de datos (archivo, base de datos)
- Crear DTOs para transferencia de datos
- Implementar logging con SLF4J
- Agregar validaciones con anotaciones (@NotNull, etc.)

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

**Estado:** Fase inicial completada (70%) ✅ → Implementación de estructuras avanzadas en progreso ⏳
