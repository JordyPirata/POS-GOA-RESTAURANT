package Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlatosDao {
    
    private static final Logger logger = LoggerFactory.getLogger(PlatosDao.class);
    Connection con;
    PreparedStatement ps,ps2;
    ResultSet rs;

    public boolean Registrar(Platos pla) {
        String sql = "INSERT INTO platos (nombre, precio, fecha, categoria, sub_categoria) VALUES (?,?,?,?,?)";
        String sql2 = "ALTER TABLE platos AUTO_INCREMENT = 0";
        try {
            con = Conexion.getConnection();
            ps2 = con.prepareStatement(sql2);
            ps2.execute();
            ps = con.prepareStatement(sql);
            ps.setString(1, pla.getNombre());
            ps.setDouble(2, pla.getPrecio());
            ps.setString(3, pla.getFecha());
            ps.setString(4, pla.getCategoria());
            ps.setString(5, pla.getSubCategoria());
            ps.execute();
            return true;
        } catch (SQLException e) {
            logger.error("Op error "+ e.toString());
            return false;
        }
    }
    public List ListarCateg(String valor, String fecha) {
        List<Platos> Lista = new ArrayList();
        String sql = "SELECT * FROM platos";
        String consulta = "SELECT * FROM platos WHERE categoria LIKE '%"+valor+"%'";
        try {
            con = Conexion.getConnection();
            if(valor.equalsIgnoreCase("")){
                ps = con.prepareStatement(sql);
            }else{
                ps = con.prepareStatement(consulta);
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                Platos pl = new Platos();
                pl.setId(rs.getInt("id"));
                pl.setNombre(rs.getString("nombre"));
                pl.setPrecio(rs.getDouble("precio"));
                pl.setCategoria(rs.getString("categoria"));
                pl.setSubCategoria(rs.getString("sub_categoria"));
                
                Lista.add(pl);
            }
        } catch (SQLException e) {
            logger.error("Op error "+ e.toString());
        }
        
        return Lista;
    }

    public List Listar(String valor, String fecha) {
        List<Platos> Lista = new ArrayList();
        String sql = "SELECT * FROM platos";
        String consulta = "SELECT * FROM platos WHERE nombre LIKE '%"+valor+"%'";
        try {
            con = Conexion.getConnection();
            if(valor.equalsIgnoreCase("")){
                ps = con.prepareStatement(sql);
            }else{
                ps = con.prepareStatement(consulta);
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                Platos pl = new Platos();
                pl.setId(rs.getInt("id"));
                pl.setNombre(rs.getString("nombre"));
                pl.setPrecio(rs.getDouble("precio"));
                pl.setCategoria(rs.getString("categoria"));
                pl.setSubCategoria(rs.getString("sub_categoria"));
                
                Lista.add(pl);
            }
        } catch (SQLException e) {
            logger.error("Op error "+ e.toString());
        }
        return Lista;
    }

    public boolean Eliminar(int id) {
        String sql = "DELETE FROM platos WHERE id = ?";
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
            return true;
        } catch (SQLException e) {
            logger.error("Op error "+ e.toString());
            return false;
        }
    }

    public boolean Modificar(Platos pla) {
        String sql = "UPDATE platos SET nombre=?, precio=?, categoria=?, sub_categoria=? WHERE id=?";
        String sql2 = "ALTER TABLE platos AUTO_INCREMENT = 0";
        try {
            con = Conexion.getConnection();
            ps2 = con.prepareStatement(sql2);
            ps2.execute();
            ps = con.prepareStatement(sql);
            ps.setString(1, pla.getNombre());
            ps.setDouble(2, pla.getPrecio());
            ps.setString(3, pla.getCategoria());
            ps.setString(4, pla.getSubCategoria());
            ps.setInt(5, pla.getId());
            ps.execute();
            return true;
        } catch (SQLException e) {
            logger.error("Op error "+ e.toString());
            return false;
        }
    }

}
