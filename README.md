<img width="488" height="157" alt="image" src="https://github.com/user-attachments/assets/e4b3565f-f210-4cee-ac43-b91791d97880" />

# 🚚 Actividad Sumativa III – Gestión de pedidos con operaciones CRUD y JDBC

---

## 👤 Datos del estudiante

**Nombre:** Camilo Pinto

**Carrera:** Analista Programador

**Asignatura:** Desarrollo Orientado a Objetos II

**Semana:** 8

**Caso:** SpeedFast


---

## 📌 Descripción

En esta actividad se completa el sistema de entregas **SpeedFast**, incorporando operaciones **CRUD** (crear, listar, editar y eliminar) sobre una base de datos **MySQL** mediante **JDBC**.

La aplicación permite gestionar repartidores, pedidos y entregas desde una interfaz gráfica en **Java Swing**.

Los datos se guardan de forma permanente en la base de datos, y antes de cada operación se validan las entradas del usuario.

---

## 🗃️ Base de datos

La base de datos `speedfast_db` contiene las siguientes tablas:

* `repartidores`: almacena los repartidores.
* `pedidos`: almacena la dirección, el tipo y el estado de cada pedido.
* `entregas`: relaciona un pedido con un repartidor, con fecha y hora.

La tabla `entregas` se relaciona con `pedidos` y `repartidores` mediante llaves foráneas.

---

## 🏗️ Estructura del proyecto

```text
src/main/java
│
├── cl.duoc
│   │
│   ├── conexion
│   │   └── ConexionDB.java
│   │
│   ├── dao
│   │   ├── EntregaDAO.java
│   │   ├── PedidoDAO.java
│   │   └── RepartidorDAO.java
│   │
│   ├── interfaz
│   │   ├── Cancelable.java
│   │   ├── Despachable.java
│   │   ├── Identificable.java
│   │   └── Rastreable.java
│   │
│   ├── modelo
│   │   ├── Entrega.java
│   │   ├── EstadoPedido.java
│   │   ├── Pedido.java
│   │   ├── PedidoComida.java
│   │   ├── PedidoEncomienda.java
│   │   ├── PedidoExpress.java
│   │   ├── Repartidor.java
│   │   └── TipoPedido.java
│   │
│   ├── tareas
│   │   └── TareaEntrega.java
│   │
│   ├── util
│   │   ├── Combos.java
│   │   ├── Mensajes.java
│   │   └── Validador.java
│   │
│   └── vistas
│       ├── PanelEntregas.java
│       ├── PanelPedidos.java
│       ├── PanelRepartidores.java
│       └── VentanaPrincipal.java
│
└── org.example
    └── Main.java
```

---

## 📦 Descripción de las clases

### `ConexionDB`

Gestiona la conexión con la base de datos MySQL mediante `DriverManager`.

### `Pedido`

Clase abstracta que representa los pedidos de SpeedFast. Contiene el ID, la dirección y el estado del pedido.

### `PedidoComida`

Representa los pedidos de comida realizados a través de SpeedFast.

### `PedidoEncomienda`

Representa los pedidos correspondientes a encomiendas.

### `PedidoExpress`

Representa los pedidos que utilizan el servicio de entrega express.

### `EstadoPedido`

Enum utilizado para controlar los diferentes estados de un pedido:

* `PENDIENTE`
* `EN_REPARTO`
* `ENTREGADO`

### `TipoPedido`

Enum utilizado para indicar el tipo de pedido:

* `COMIDA`
* `ENCOMIENDA`
* `EXPRESS`

### `Repartidor`

Representa a un repartidor registrado en el sistema.

### `Entrega`

Representa la entrega de un pedido realizada por un repartidor, con su fecha y hora.

### `RepartidorDAO`, `PedidoDAO` y `EntregaDAO`

Clases encargadas del acceso a la base de datos.

Cada una implementa los métodos `create()`, `readAll()`, `update()` y `delete()` usando `PreparedStatement` y `ResultSet`.

`PedidoDAO` y `EntregaDAO` también permiten filtrar los resultados.

### `TareaEntrega`

Implementa `Runnable` y simula el reparto de un pedido mediante un `Thread`.

Cambia el estado del pedido y lo guarda en la base de datos.

### `Validador`

Revisa que los datos ingresados sean correctos antes de guardarlos, por ejemplo: campos obligatorios, formato de fecha y formato de hora.

### `Mensajes`

Muestra mensajes de éxito, advertencia, confirmación y error mediante `JOptionPane`.

### `Combos`

Permite seleccionar los elementos de un `JComboBox` según su ID.

### `VentanaPrincipal`

Es la ventana principal de SpeedFast.

Contiene tres pestañas: **Repartidores**, **Pedidos** y **Entregas**.

### `PanelRepartidores`

Permite registrar, editar, eliminar y listar repartidores.

### `PanelPedidos`

Permite registrar, editar, eliminar y listar pedidos, con filtros por estado y tipo.

### `PanelEntregas`

Permite registrar, editar, eliminar y listar entregas, con filtros por pedido y repartidor.

También permite simular el reparto de un pedido.

### Interfaces

El proyecto mantiene las interfaces utilizadas anteriormente:

* `Cancelable`
* `Despachable`
* `Rastreable`

Además, se agregó la interfaz `Identificable`, que permite identificar las entidades por su ID.

---

## 🖥️ Interfaz gráfica

La aplicación utiliza componentes de **Java Swing**, entre ellos:

* `JFrame`
* `JPanel`
* `JTabbedPane`
* `JLabel`
* `JTextField`
* `JComboBox`
* `JButton`
* `JTable`
* `DefaultTableModel`
* `JOptionPane`

Al seleccionar una fila de una tabla, sus datos se cargan en el formulario para editarla o eliminarla.

---

## ▶️ Ejecución

La aplicación comienza desde la clase `Main`.

Al ejecutar el programa, se verifica la conexión con la base de datos y luego se abre la ventana principal de **SpeedFast**.

Desde esta ventana se pueden realizar las siguientes acciones:

### Gestionar repartidores

Permite ingresar el nombre del repartidor.

También se pueden editar y eliminar los repartidores registrados.

### Gestionar pedidos

Permite ingresar:

* Dirección de entrega.
* Tipo de pedido.
* Estado del pedido.

Los pedidos se muestran en una tabla y se pueden filtrar por estado o tipo.

### Gestionar entregas

Permite seleccionar un pedido y un repartidor desde listas desplegables, e ingresar la fecha y la hora.

Las entregas se muestran en una tabla y se pueden filtrar por pedido o repartidor.

### Simular reparto

Al seleccionar una entrega, se inicia un `Thread` que simula el reparto.

El estado del pedido cambia desde `PENDIENTE` a `EN_REPARTO` y finalmente a `ENTREGADO`.

---

## 🛡️ Validaciones y manejo de errores

* Se validan los campos obligatorios y los formatos antes de guardar.
* Se pide confirmación antes de eliminar un registro.
* Los errores de la base de datos se muestran con mensajes claros.
* No se puede eliminar un pedido o repartidor que tenga entregas asociadas.

---

📁 Repositorio


Proyecto: Sistema de gestión de entregas SpeedFast

https://github.com/cpintomartinezsoc-cmyk/Poo2Actividad8.git

Entrega: 05/10/2026

---
