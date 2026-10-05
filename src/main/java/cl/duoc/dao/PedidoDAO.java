package cl.duoc.dao;

import cl.duoc.conexion.ConexionDB;
import cl.duoc.modelo.EstadoPedido;
import cl.duoc.modelo.Pedido;
import cl.duoc.modelo.TipoPedido;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class PedidoDAO {

    public boolean create(Pedido pedido) throws SQLException {
        String sql = "INSERT INTO pedidos (direccion, tipo, estado) VALUES (?, ?, ?)";

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, pedido.getDireccion());
            ps.setString(2, pedido.getTipo().name());
            ps.setString(3, pedido.getEstado().name());
            int filas = ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    pedido.setId(rs.getInt(1));
                }
            }
            return filas > 0;
        }
    }

    public List<Pedido> readAll() throws SQLException {
        return readByFiltro(null, null);
    }


    public List<Pedido> readByFiltro(EstadoPedido estado, TipoPedido tipo) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT id, direccion, tipo, estado FROM pedidos WHERE 1 = 1");
        List<String> parametros = new ArrayList<>();

        if (estado != null) {
            sql.append(" AND estado = ?");
            parametros.add(estado.name());
        }
        if (tipo != null) {
            sql.append(" AND tipo = ?");
            parametros.add(tipo.name());
        }
        sql.append(" ORDER BY id");

        List<Pedido> pedidos = new ArrayList<>();

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql.toString())) {

            for (int i = 0; i < parametros.size(); i++) {
                ps.setString(i + 1, parametros.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    pedidos.add(mapear(rs));
                }
            }
        }
        return pedidos;
    }

    public boolean update(Pedido pedido) throws SQLException {
        String sql = "UPDATE pedidos SET direccion = ?, tipo = ?, estado = ? WHERE id = ?";

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, pedido.getDireccion());
            ps.setString(2, pedido.getTipo().name());
            ps.setString(3, pedido.getEstado().name());
            ps.setInt(4, pedido.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateEstado(int id, EstadoPedido estado) throws SQLException {
        String sql = "UPDATE pedidos SET estado = ? WHERE id = ?";

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, estado.name());
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }


    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM pedidos WHERE id = ?";

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Pedido mapear(ResultSet rs) throws SQLException {
        String tipo = rs.getString("tipo");
        String estado = rs.getString("estado");

        return Pedido.crear(
                rs.getInt("id"),
                rs.getString("direccion"),
                tipo != null ? TipoPedido.valueOf(tipo) : TipoPedido.EXPRESS,
                estado != null ? EstadoPedido.valueOf(estado) : EstadoPedido.PENDIENTE
        );
    }
}