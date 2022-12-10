
package Modelo;

import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.EscPosConst;
import com.github.anastaciocintra.escpos.Style;
import com.github.anastaciocintra.escpos.image.Bitonal;
import com.github.anastaciocintra.escpos.image.BitonalOrderedDither;
import com.github.anastaciocintra.escpos.image.BitonalThreshold;
import com.github.anastaciocintra.escpos.image.CoffeeImageImpl;
import com.github.anastaciocintra.escpos.image.EscPosImage;
import com.github.anastaciocintra.escpos.image.RasterBitImageWrapper;
import com.github.anastaciocintra.output.PrinterOutputStream;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.print.PrintService;
import javax.swing.filechooser.FileSystemView;

public class PedidosDao {
    Connection con;
    Conexion cn = new Conexion();
    PreparedStatement ps, ps2;
    ResultSet rs;
    int r;
    
    public int IdPedido(){
        int id = 0;
        String sql = "SELECT MAX(id) FROM pedidos";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                id = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return id;
    }
    
    public int IdPedidoMesa(int mesa, int id_sala){
        int id_pedido = 0;
        String sql = "SELECT id FROM pedidos WHERE num_mesa=? AND id_sala=?";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, mesa);
            ps.setInt(2, id_sala);
            rs = ps.executeQuery();
            if(rs.next()){
                id_pedido = rs.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return id_pedido;
    }
    
    
    public int verificarStado(int mesa, int id_sala){
        int id_pedido = 0;
        String sql = "SELECT id FROM pedidos WHERE num_mesa=? AND id_sala=? AND estado = ?";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, mesa);
            ps.setInt(2, id_sala);
            ps.setString(3, "PENDIENTE");
            rs = ps.executeQuery();
            if(rs.next()){
                id_pedido = rs.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return id_pedido;
    }
    public int RegistrarPedido(Pedidos ped){
        String sql2 = "ALTER TABLE pedidos AUTO_INCREMENT = 0";
        String sql = "INSERT INTO pedidos (id_sala, num_mesa, total, usuario) VALUES (?,?,?,?)";
        try {
            con = cn.getConnection();
            ps2 = con.prepareStatement(sql2);
            ps2.execute();
            ps = con.prepareStatement(sql);
            ps.setInt(1, ped.getId_sala());
            ps.setInt(2, ped.getNum_mesa());
            ps.setDouble(3, ped.getTotal());
            ps.setString(4, ped.getUsuario());
            ps.execute();
        } catch (SQLException e) {
            System.out.println(e.toString());
        }finally{
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
        return r;
    }
    public boolean Eliminar(int id) {
        String sql = "DELETE FROM pedidos WHERE id = ?";
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false;
        } finally {
            try {
                con.close();
            } catch (SQLException ex) {
                System.out.println(ex.toString());
            }
        }
    }
    
    public boolean EliminarPlatoPed(int id){
        String sql = "DELETE FROM detalle_pedidos WHERE id = ?";
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false;
        } finally {
            try {
                con.close();
            } catch (SQLException ex) {
                System.out.println(ex.toString());
            }
        }
    }
    
    public int RegistrarDetalle(DetallePedido det){
        String sql2 = "ALTER TABLE detalle_pedidos AUTO_INCREMENT = 0";
       String sql = "INSERT INTO detalle_pedidos (nombre, precio, cantidad, comentario, id_pedido) VALUES (?,?,?,?,?)";
        try {
            con = cn.getConnection();
            ps2 = con.prepareStatement(sql2);
            ps2.execute();
            ps = con.prepareStatement(sql);
            ps.setString(1, det.getNombre());
            ps.setDouble(2, det.getPrecio());
            ps.setDouble(3, det.getCantidad());
            ps.setString(4, det.getComentario());
            ps.setInt(5, det.getId_pedido());
            ps.execute();
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return r;
    }
    
    public int EditarTotalPedido(double antprec, int mesa, int id_sala){
        String sql = "UPDATE  pedidos SET total = ? WHERE num_mesa=? AND id_sala=?";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setDouble(1, antprec);
            ps.setInt(2, mesa);
            ps.setInt(3, id_sala);
            ps.execute();
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return r;
    }
    
    public int EditarCantidad(double cantidad, int id){
        String sql = "UPDATE  detalle_pedidos SET cantidad = ? WHERE id=? ";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setDouble(1, cantidad);
            ps.setInt(2, id);
            ps.execute();
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return r;
    }
    
    public double VerTotalPedido(int mesa, int id_sala){
        double total = 0;
        String sql = "SELECT total FROM pedidos WHERE num_mesa=? AND id_sala=?";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, mesa);
            ps.setInt(2, id_sala);
            rs = ps.executeQuery();
            if(rs.next()){
                total = rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return total;
        
    }
    
    public List verPedidoDetalle(int id_pedido){
       List<DetallePedido> Lista = new ArrayList();
       String sql = "SELECT d.* FROM pedidos p INNER JOIN detalle_pedidos d ON p.id = d.id_pedido WHERE p.id = ?";
       try {
           con = cn.getConnection();
           ps = con.prepareStatement(sql);
           ps.setInt(1, id_pedido);
           rs = ps.executeQuery();
           while (rs.next()) {               
               DetallePedido det = new DetallePedido();
               det.setId(rs.getInt("id"));
               det.setNombre(rs.getString("nombre"));
               det.setPrecio(rs.getDouble("precio"));
               det.setCantidad(rs.getDouble("cantidad"));
               det.setComentario(rs.getString("comentario"));
               Lista.add(det);
           }
       } catch (SQLException e) {
           System.out.println(e.toString());
       }
       return Lista;
   }
    
    public Pedidos verPedido(int id_pedido){
        Pedidos ped = new Pedidos();
       String sql = "SELECT p.*, s.nombre FROM pedidos p INNER JOIN salas s ON p.id_sala = s.id WHERE p.id = ?";
       try {
           con = cn.getConnection();
           ps = con.prepareStatement(sql);
           ps.setInt(1, id_pedido);
           rs = ps.executeQuery();
            if (rs.next()) {               
               
               ped.setId(rs.getInt("id"));
               ped.setFecha(rs.getString("fecha"));
               ped.setSala(rs.getString("nombre"));
               ped.setNum_mesa(rs.getInt("num_mesa"));
               ped.setTotal(rs.getDouble("total"));
           }
       } catch (SQLException e) {
           System.out.println(e.toString());
       }
       return ped;
   }
    
    public List finalizarPedido(int id_pedido){
       List<DetallePedido> Lista = new ArrayList();
       String sql = "SELECT d.* FROM pedidos p INNER JOIN detalle_pedidos d ON p.id = d.id_pedido WHERE p.id = ?";
       try {
           con = cn.getConnection();
           ps = con.prepareStatement(sql);
           ps.setInt(1, id_pedido);
           rs = ps.executeQuery();
           while (rs.next()) {               
               DetallePedido det = new DetallePedido();
               det.setId(rs.getInt("id"));
               det.setNombre(rs.getString("nombre"));
               det.setPrecio(rs.getDouble("precio"));
               det.setCantidad(rs.getInt("cantidad"));
               det.setComentario(rs.getString("comentario"));
               Lista.add(det);
           }
       } catch (SQLException e) {
           System.out.println(e.toString());
       }
       return Lista;
   }
    
    public void pdfPedido(int id_pedido) {
        String fechaPedido = null, usuario = null, total = null,
                sala = null, num_mesa = null;
        try {
            FileOutputStream archivo;
            String url = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
            File salida = new File(url + File.separator + "pedido.pdf");
            archivo = new FileOutputStream(salida);
            Document doc = new Document();
            PdfWriter.getInstance(doc, archivo);
            doc.open();
            Image img = Image.getInstance(getClass().getResource("/Img/logo.png"));
            //Fecha
            String informacion = "SELECT p.*, s.nombre FROM pedidos p INNER JOIN salas s ON p.id_sala = s.id WHERE p.id = ?";
            try {
                ps = con.prepareStatement(informacion);
                ps.setInt(1, id_pedido);
                rs = ps.executeQuery();
                if (rs.next()) {
                    num_mesa = rs.getString("num_mesa");
                    sala = rs.getString("nombre");
                    fechaPedido = rs.getString("fecha");
                    usuario = rs.getString("usuario");
                    total = rs.getString("total");
                }

            } catch (SQLException e) {
                System.out.println(e.toString());
            }
            
            PdfPTable Encabezado = new PdfPTable(4);
            Encabezado.setWidthPercentage(100);
            Encabezado.getDefaultCell().setBorder(0);
            float[] columnWidthsEncabezado = new float[]{20f, 20f, 60f, 60f};
            Encabezado.setWidths(columnWidthsEncabezado);
            Encabezado.setHorizontalAlignment(Element.ALIGN_LEFT);
            Encabezado.addCell(img);
            Encabezado.addCell("");
            //info empresa
            String config = "SELECT * FROM config";
            String mensaje = "";
            try {
                con = cn.getConnection();
                ps = con.prepareStatement(config);
                rs = ps.executeQuery();
                if (rs.next()) {
                    mensaje = rs.getString("mensaje");
                    Encabezado.addCell("RFC:    " + rs.getString("ruc") 
                            + "\nNombre: " + rs.getString("nombre") 
                            + "\nTeléfono: " + rs.getString("telefono") 
                            + "\nDirección: " + rs.getString("direccion")
                    );
                }
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
            //
            Paragraph info = new Paragraph();
            Font negrita = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLUE);
            info.add("Atendido: " + usuario 
                    + "\nN° Pedido: " + id_pedido 
                    + "\nFecha: " + fechaPedido
                    + "\nSala: " + sala
                    + "\nN° Mesa: " + num_mesa
            );
            Encabezado.addCell(info);
            
            doc.add(Encabezado);
            doc.add(Chunk.NEWLINE);
            PdfPTable tabla = new PdfPTable(4);
            tabla.setWidthPercentage(100);
            tabla.getDefaultCell().setBorder(0);
            float[] columnWidths = new float[]{10f, 50f, 15f, 15f};
            tabla.setWidths(columnWidths);
            tabla.setHorizontalAlignment(Element.ALIGN_LEFT);
            PdfPCell c1 = new PdfPCell(new Phrase("Cant.", negrita));
            PdfPCell c2 = new PdfPCell(new Phrase("Plato.", negrita));
            PdfPCell c3 = new PdfPCell(new Phrase("P. unt.", negrita));
            PdfPCell c4 = new PdfPCell(new Phrase("P. Total", negrita));
            c1.setBorder(Rectangle.NO_BORDER);
            c2.setBorder(Rectangle.NO_BORDER);
            c3.setBorder(Rectangle.NO_BORDER);
            c4.setBorder(Rectangle.NO_BORDER);
            c1.setBackgroundColor(BaseColor.LIGHT_GRAY);
            c2.setBackgroundColor(BaseColor.LIGHT_GRAY);
            c3.setBackgroundColor(BaseColor.LIGHT_GRAY);
            c4.setBackgroundColor(BaseColor.LIGHT_GRAY);
            tabla.addCell(c1);
            tabla.addCell(c2);
            tabla.addCell(c3);
            tabla.addCell(c4);
            String product = "SELECT d.* FROM pedidos p INNER JOIN detalle_pedidos d ON p.id = d.id_pedido WHERE p.id = ?";
            try {
                ps = con.prepareStatement(product);
                ps.setInt(1, id_pedido);
                rs = ps.executeQuery();
                while (rs.next()) {
                    double subTotal = rs.getInt("cantidad") * rs.getDouble("precio");
                    tabla.addCell(rs.getString("cantidad"));
                    tabla.addCell(rs.getString("nombre"));
                    tabla.addCell(rs.getString("precio"));
                    tabla.addCell(String.valueOf(subTotal));
                }
                
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
            doc.add(tabla);
            Paragraph agra = new Paragraph();
            agra.add(Chunk.NEWLINE);
            agra.add("Total S/: " + total);
            agra.setAlignment(Element.ALIGN_RIGHT);
            doc.add(agra);
            Paragraph firma = new Paragraph();
            firma.add(Chunk.NEWLINE);
            firma.add("Cancelacion \n\n");
            firma.add("------------------------------------\n");
            firma.add("Firma \n");
            firma.setAlignment(Element.ALIGN_CENTER);
            doc.add(firma);
            Paragraph gr = new Paragraph();
            gr.add(Chunk.NEWLINE);
            gr.add(mensaje);
            gr.setAlignment(Element.ALIGN_CENTER);
            doc.add(gr);
            doc.close();
            archivo.close();
            Desktop.getDesktop().open(salida);
        } catch (DocumentException | IOException e) {
            System.out.println(e.toString());
        }finally{
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
    }
    
    public void ticketPedido (int id_pedido, String lugarTicket){
        int x = 0;
        String[ ] printerName = {"XP-80C1", "XP-80C"}; 
        ticketusuario(id_pedido, printerName[x]);
        ticketBar(id_pedido, printerName[x]);
        
    }
    
    public void ticketBar(int id_pedido, String printerName){
        
    }
    
    public void ticketCocina(int id_pedido, String printerName){
        String num_mesa = null, sala = null;
        PrintService printService = PrinterOutputStream.getPrintServiceByName(printerName);
        EscPos escpos;
        try{
            escpos = new EscPos(new PrinterOutputStream(printService));
            Style title = new Style()
                    .setFontSize(Style.FontSize._3, Style.FontSize._2)
                    .setJustification(EscPosConst.Justification.Center);

            Style subtitle = new Style(escpos.getStyle())
                    .setJustification(EscPosConst.Justification.Center);
            
        }catch(IOException e){
            System.out.println(e.toString());
        }
    }
    
    public void ticketusuario(int id_pedido, String printerName){
        String fechaPedido = null, total = null, num_mesa = null, sala = null;
        BufferedImage imagen = null;
        double totalTicket = 0.0;
        PrintService printService = PrinterOutputStream.getPrintServiceByName(printerName);
        EscPos escpos;
        try {
            imagen = ImageIO.read(getClass().getResource("/Img/logo-dark-modo.png"));
            
            BufferedImage  imageBufferedImage = imagen;
            RasterBitImageWrapper imageWrapper = new RasterBitImageWrapper();
            
            escpos = new EscPos(new PrinterOutputStream(printService));
            Bitonal algorithm = new BitonalThreshold();
            EscPosImage escposImage = new EscPosImage(new CoffeeImageImpl(imageBufferedImage), algorithm);
            
            Style title = new Style()
                    .setFontSize(Style.FontSize._3, Style.FontSize._2)
                    .setJustification(EscPosConst.Justification.Center);

            Style subtitle = new Style(escpos.getStyle())
                    .setJustification(EscPosConst.Justification.Center);
            Style bold = new Style(escpos.getStyle())
                    .setBold(true);
            String informacion = "SELECT p.*, s.nombre FROM pedidos p INNER JOIN salas s ON p.id_sala = s.id WHERE p.id = ?";
            try {
                
                ps = con.prepareStatement(informacion);
                ps.setInt(1, id_pedido);
                rs = ps.executeQuery();
                
                if (rs.next()) {
                    num_mesa = rs.getString("num_mesa");
                    sala = rs.getString("nombre");
                    fechaPedido = rs.getString("fecha");
                    total = rs.getString("total");
                    
                }
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
                       
            algorithm = new BitonalOrderedDither(3,2,150,150);
            escposImage = new EscPosImage(new CoffeeImageImpl(imageBufferedImage), algorithm);
            escpos.write(imageWrapper, escposImage);
 
            escpos.writeLF(title,"Goa Restaurant");
            escpos.writeLF("N_Mesa: " + num_mesa + "          " + "Fecha: " + fechaPedido );
            escpos.writeLF("N_Sala: " + sala);
            escpos.writeLF("N_ticket: " + id_pedido);
            escpos.feed(1);
            escpos.writeLF(bold, 
                             "Plato                     P.SubTotal  P.Total")
                    .writeLF(bold,
                            "---------------------------------------------");
                    String product = "SELECT d.* FROM pedidos p INNER JOIN detalle_pedidos d ON p.id = d.id_pedido WHERE p.id = ?";
                    try{
                    ps = con.prepareStatement(product);
                    ps.setInt(1, id_pedido);
                    rs = ps.executeQuery();
                    while (rs.next()){
                        
                        if(rs.getDouble("cantidad") <= 1){
                            totalTicket =rs.getDouble("precio");
                        }else{
                            totalTicket = (rs.getDouble("precio") - ((rs.getDouble("cantidad") * rs.getDouble("precio"))));
                        }
                        
                        escpos.writeLF(subtitle,calFilaTicket(rs.getString("nombre") , rs.getDouble("precio"), totalTicket));
                        escpos.feed(1);
                    }
                    }catch (SQLException e)  {
                        System.out.println(e.toString());
                    }
            escpos.writeLF(bold,
                            "---------------------------------------------");
            escpos.writeLF(bold,
                                "                              Total S/: " + total);
            String mensaje = "SELECT c.* FROM config c WHERE id = ?";
            try{
                ps = con.prepareStatement(mensaje);
                ps.setInt(1, 1);
                rs = ps.executeQuery();
                if(rs.next()){
                    escpos.feed(2);
                    escpos.writeLF(subtitle,rs.getString("mensaje"));
                    escpos.writeLF(subtitle,"Visitanos en goa.com.mx");
                    escpos.writeLF(subtitle,"Gracias por su visita");
                }
            }catch(SQLException e){
               System.out.println(e.toString()); 
            }
            
            escpos.feed(5);
            escpos.cut(EscPos.CutMode.FULL);
            
            
            escpos.close(); 
        } catch (IOException e) {
            System.out.println(e.toString());
        }finally{
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }  
    }
    public String calFilaTicket(String plato, double precio, double total){
        
       String fila = null; 
       int tamPlato = plato.length();
       int tamPrecio = Double.toString(precio).length();
       int tamTotal = Double.toString(total).length();
       int numSpaces = 45 - (tamTotal + tamPrecio + 6 + tamPlato);
       if(numSpaces < 0){
           numSpaces = 0;
       }
       return fila = plato + " ".repeat(numSpaces) + "$" + precio + "    " + "$" + total;
    }
    
    public boolean actualizarEstado (int id_pedido){
        String sql = "UPDATE pedidos SET estado = ? WHERE id = ?";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, "FINALIZADO");
            ps.setInt(2, id_pedido);
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false;
        }
    }
    
    public boolean actualizarImpreso (int id_pedido, String impreso){
        String sql = "UPDATE pedidos SET impreso = ? WHERE id = ?";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, impreso);
            ps.setInt(2, id_pedido);
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false;
        }
    }
    
    public List listarPedidos(){
       List<Pedidos> Lista = new ArrayList();
       String sql = "SELECT p.*, s.nombre FROM pedidos p INNER JOIN salas s ON p.id_sala = s.id ORDER BY p.fecha DESC";
       try {
           con = cn.getConnection();
           ps = con.prepareStatement(sql);
           rs = ps.executeQuery();
           while (rs.next()) {               
               Pedidos ped = new Pedidos();
               ped.setId(rs.getInt("id"));
               ped.setSala(rs.getString("nombre"));
               ped.setNum_mesa(rs.getInt("num_mesa"));
               ped.setFecha(rs.getString("fecha"));
               ped.setTotal(rs.getDouble("total"));
               ped.setUsuario(rs.getString("usuario"));
               ped.setEstado(rs.getString("estado"));
               Lista.add(ped);
           }
       } catch (SQLException e) {
           System.out.println(e.toString());
       }
       return Lista;
   }
    
}