package cl.duoc.dao;

import cl.duoc.conexion.ConexionDB;
import cl.duoc.modelo.Entrega;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


public class EntregaDAO {

    private static final String SELECT_BASE = """
            SELECT e.id, e.id_pedido, e.id_repartidor, e.fecha, e.hora,
                   p.direccion, r.nombre
              FROM entregas e
              LEFT JOIN pedidos p ON p.id = e.id_pedido
              LEFT JOIN repartidores r ON r.id = e.id_repartidor
             WHERE 1 = 1
            """;

    public boolean create(Entrega entrega) throws SQLException {
        String sql = "INSERT INTO entregas (id_pedido, id_repartidor, fecha, hora) VALUES (?, ?, ?, ?)";

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            cargarParametros(ps, entrega);
            int filas = ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entrega.setId(rs.getInt(1));
                }
            }
            return filas > 0;
        }
    }

    public List<Entrega> readAll() throws SQLException {
        return readByFiltro(null, null);
    }


    public List<Entrega> readByFiltro(Integer idPedido, Integer idRepartidor) throws SQLException {
        StringBuilder sql = new StringBuilder(SELECT_BASE);
        List<Integer> parametros = new ArrayList<>();

        if (idPedido != null) {
            sql.append(" AND e.id_pedido = ?");
            parametros.add(idPedido);
        }
        if (idRepartidor != null) {
            sql.append(" AND e.id_repartidor = ?");
            parametros.add(idRepartidor);
        }
        sql.append(" ORDER BY e.id");

        List<Entrega> entregas = new ArrayList<>();

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql.toString())) {

            for (int i = 0; i < parametros.size(); i++) {
                ps.setInt(i + 1, parametros.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    entregas.add(mapear(rs));
                }
            }
        }
        return entregas;
    }

    public boolean update(Entrega entrega) throws SQLException {
        String sql = "UPDATE entregas SET id_pedido = ?, id_repartidor = ?, fecha = ?, hora = ? WHERE id = ?";

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            cargarParametros(ps, entrega);
            ps.setInt(5, entrega.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM entregas WHERE id = ?";

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private void cargarParametros(PreparedStatement ps, Entrega entrega) throws SQLException {
        ps.setInt(1, entrega.getIdPedido());
        ps.setInt(2, entrega.getIdRepartidor());
        ps.setDate(3, Date.valueOf(entrega.getFecha()));   // LocalDate -> DATE
        ps.setTime(4, Time.valueOf(entrega.getHora()));    // LocalTime -> TIME
    }

    private Entrega mapear(ResultSet rs) throws SQLException {
        Date fecha = rs.getDate("fecha");
        Time hora = rs.getTime("hora");

        LocalDate fechaLocal = fecha != null ? fecha.toLocalDate() : null;
        LocalTime horaLocal = hora != null ? hora.toLocalTime() : null;

        return new Entrega(
                rs.getInt("id"),
                rs.getInt("id_pedido"),
                rs.getInt("id_repartidor"),
                fechaLocal,
                horaLocal,
                rs.getString("direccion"),
                rs.getString("nombre")
        );
    }
}