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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PedidosDao {
    
    private static final Logger logger = LoggerFactory.getLogger(PedidosDao.class);
    Connection con = null;
    PreparedStatement ps, ps2;
    ResultSet rs;
    int r;

    public int IdPedido() {
        int id = 0;
        String sql = "SELECT MAX(id) FROM pedidos";
        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                id = rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Op error "+ e.toString());
        }
        return id;
    }

    public int IdPedidoMesa(int mesa, int id_sala) {
        int id_pedido = 0;
        String sql = "SELECT id FROM pedidos WHERE num_mesa=? AND id_sala=?";
        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, mesa);
            ps.setInt(2, id_sala);
            rs = ps.executeQuery();
            if (rs.next()) {
                id_pedido = rs.getInt("id");
            }
        } catch (SQLException e) {
            logger.error("Op error "+ e.toString());
        }
        return id_pedido;
    }

    public int verificarStado(int mesa, int id_sala) {
        int id_pedido = 0;
        String sql = "SELECT id FROM pedidos WHERE num_mesa=? AND id_sala=? AND estado = ?";
        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, mesa);
            ps.setInt(2, id_sala);
            ps.setString(3, "PENDIENTE");
            rs = ps.executeQuery();
            if (rs.next()) {
                id_pedido = rs.getInt("id");
            }
        } catch (SQLException e) {
            logger.error("Op error "+ e.toString());
        }
        return id_pedido;
    }

    public int RegistrarPedido(Pedidos ped) {
        String sql2 = "ALTER TABLE pedidos AUTO_INCREMENT = 0";
        String sql = "INSERT INTO pedidos (id_sala, num_mesa, total, usuario) VALUES (?,?,?,?)";
        try {
            con = Conexion.getConnection();
            ps2 = con.prepareStatement(sql2);
            ps2.execute();
            ps = con.prepareStatement(sql);
            ps.setInt(1, ped.getId_sala());
            ps.setInt(2, ped.getNum_mesa());
            ps.setDouble(3, ped.getTotal());
            ps.setString(4, ped.getUsuario());
            ps.execute();
        } catch (SQLException e) {
            logger.error("Op error "+ e.toString());
        }
        return r;
    }

    public boolean CambiarMesa(int id_pedido, int mesa) {
        String sql = "UPDATE pedidos SET num_mesa = ? WHERE id = ?";
        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, Integer.toString(mesa));
            ps.setInt(2, id_pedido);
            ps.execute();
            return true;
        } catch (SQLException e) {
            logger.error("Op error "+ e.toString());
            return false;
        }
    }

    public boolean CambiarSala(int id_pedido, int sala) {
        String sql = "UPDATE pedidos SET id_sala = ? WHERE id = ?";
        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, Integer.toString(sala));
            ps.setInt(2, id_pedido);
            ps.execute();
            return true;
        } catch (SQLException e) {
            logger.error("Op error "+ e.toString());
            return false;
        }
    }

    public boolean Eliminar(int id) {
        String sql = "DELETE FROM pedidos WHERE id = ?";
        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
            return true;
        } catch (SQLException e) {
            logger.error("Op error "+ e.toString());
            return false;
        }
    }

    public boolean EliminarPlatoPed(int id) {
        String sql = "DELETE FROM detalle_pedidos WHERE id = ?";
        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
            return true;
        } catch (SQLException e) {
            logger.error("Op error "+ e.toString());
            return false;
        }
    }

    public int RegistrarDetalle(DetallePedido det) {
        String sql2 = "ALTER TABLE detalle_pedidos AUTO_INCREMENT = 0";
        String sql = "INSERT INTO detalle_pedidos (nombre, precio, cantidad, comentario, id_pedido) VALUES (?,?,?,?,?)";
        try {
            con = Conexion.getConnection();
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
            logger.error("Op error "+ e.toString());
        }
        return r;
    }

    public int EditarTotalPedido(double antprec, int mesa, int id_sala, int id_pedido) {
        String sql = "UPDATE  pedidos SET total = ? WHERE num_mesa=? AND id_sala=? AND id = ?";
        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            ps.setDouble(1, antprec);
            ps.setInt(2, mesa);
            ps.setInt(3, id_sala);
            ps.setInt(4, id_pedido);
            ps.execute();
        } catch (SQLException e) {
            logger.error("Op error "+ e.toString());
        }
        return r;
    }

    public int EditarCantidad(double cantidad, int id) {
        String sql = "UPDATE  detalle_pedidos SET cantidad = ? WHERE id=? ";
        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            ps.setDouble(1, cantidad);
            ps.setInt(2, id);
            ps.execute();
        } catch (SQLException e) {
            logger.error("Op error "+ e.toString());
        }
        return r;
    }

    public double VerTotalPedido(int mesa, int id_sala, int id_pedido) {
        double total = 0;
        String sql = "SELECT total FROM pedidos WHERE num_mesa=? AND id_sala=? AND id = ?";
        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, mesa);
            ps.setInt(2, id_sala);
            ps.setInt(3, id_pedido);
            rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getInt("total");
            }
        } catch (SQLException e) {
            logger.error("Op error "+ e.toString());
        }
        return total;

    }

    public List verPedidoDetalle(int id_pedido) {
        List<DetallePedido> Lista = new ArrayList();
        String sql = "SELECT d.* FROM pedidos p INNER JOIN detalle_pedidos d ON p.id = d.id_pedido WHERE p.id = ?";
        try {
            con = Conexion.getConnection();
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
            logger.error("Op error "+ e.toString());
        }
        return Lista;
    }

    public Pedidos verPedido(int id_pedido) {
        Pedidos ped = new Pedidos();
        String sql = "SELECT p.*, s.nombre FROM pedidos p INNER JOIN salas s ON p.id_sala = s.id WHERE p.id = ?";
        try {
            con = Conexion.getConnection();
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
            logger.error("Op error "+ e.toString());
        }
        return ped;
    }

    public List finalizarPedido(int id_pedido) {
        List<DetallePedido> Lista = new ArrayList();
        String sql = "SELECT d.* FROM pedidos p INNER JOIN detalle_pedidos d ON p.id = d.id_pedido WHERE p.id = ?";
        try {
            con = Conexion.getConnection();
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
            logger.error("Op error "+ e.toString());
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
            
            con = Conexion.getConnection();
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
            
            con = Conexion.getConnection();
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
            
            con = Conexion.getConnection();
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
            logger.error("PDF error "+ e.toString());
        } catch (SQLException e) {
                logger.error("Op error "+ e.toString());
        }
    }

    public void ticketPedido(int id_pedido, String lugarTicket, int x) {
        String informacion = "SELECT c.*, c.ImpresoraC, c.ImpresoraB FROM config c WHERE c.id = ?";
        String[] printerName = new String[2];
        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(informacion);
            ps.setInt(1, 1);
            rs = ps.executeQuery();

            if (rs.next()) {
                printerName[0] = rs.getString("ImpresoraC");
                System.out.println(printerName[0]);
                printerName[1] = rs.getString("ImpresoraB");
                System.out.println(printerName[1]);
            }
        } catch (SQLException e) {
            logger.error("Op error "+ e.toString());
        }
        //String[ ] printerName = {"XP-80C2", "XP-80C"}; 
        if ("BARR/COCI".equals(lugarTicket)) {
            ticketCocina(id_pedido, printerName[x + 1], false);
            ticketBar(id_pedido, printerName[x], false);
            actualizarImpresoPlat(id_pedido);

        }
        if ("BARRA".equals(lugarTicket)) {
            ticketBar(id_pedido, printerName[x], true);
            actualizarImpresoPlat(id_pedido);
        }
        if ("COCINA".equals(lugarTicket)) {
            ticketCocina(id_pedido, printerName[x + 1], true);
            actualizarImpresoPlat(id_pedido);

        }
        if ("CLIENTE".equals(lugarTicket)) {
            ticketusuario(id_pedido, printerName[x]);
        }
    }

    public void ticketBar(int id_pedido, String printerName, boolean impreso) {
        String cuantas = "SELECT d.nombre, d.comentario, pl.categoria, d.cantidad FROM pedidos p INNER JOIN detalle_pedidos d ON p.id = d.id_pedido INNER JOIN platos pl ON d.nombre = pl.nombre WHERE p.id = ? && d.impreso = ? ORDER BY (pl.categoria && d.nombre)";
        Ticket ticket = new Ticket();

        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(cuantas);
            ps.setInt(1, id_pedido);
            ps.setBoolean(2, impreso);
            rs = ps.executeQuery();

            while (rs.next()) {
                if ("Bebidas".equals(rs.getString("categoria"))) {
                    TicketEntry entry = new TicketEntry();
                    entry.Name = rs.getString("nombre");
                    entry.Number = (int) rs.getDouble("cantidad");
                    entry.Comments.add(rs.getString("comentario"));
                    System.out.println(entry.Name);
                    ticket.addEntry(entry);
                }
            }
        } catch (SQLException e) {
            logger.error("Op error "+ e.toString());
        }

        if (ticket.Entries.isEmpty()) {
            return;
        }

        String num_mesa = null, sala = null, Bebidas = null;
        PrintService printService = PrinterOutputStream.getPrintServiceByName(printerName);
        EscPos escpos;
        try {
            escpos = new EscPos(new PrinterOutputStream(printService));
            Style title = new Style()
                    .setFontSize(Style.FontSize._3, Style.FontSize._2)
                    .setJustification(EscPosConst.Justification.Center);
            String informacion = "SELECT p.*, s.nombre FROM pedidos p INNER JOIN salas s ON p.id_sala = s.id WHERE p.id = ?";
            
            ps = con.prepareStatement(informacion);
            ps.setInt(1, id_pedido);
            rs = ps.executeQuery();

            if (rs.next()) {
                num_mesa = rs.getString("num_mesa");
                sala = rs.getString("nombre");
            }
            
            Style subtitle = new Style(escpos.getStyle())
                    .setJustification(EscPosConst.Justification.Center);
            Style miestilo = new Style()
                    .setFontSize(Style.FontSize._2, Style.FontSize._1)
                    .setJustification(EscPosConst.Justification.Left_Default);
            escpos.writeLF(title, "Goa Restaurant");
            escpos.writeLF("N_Mesa: " + num_mesa);
            escpos.writeLF("N_Sala: " + sala);
            for (TicketEntry entry : ticket.Entries) {
                String row = entry.Number + " " + entry.Name;
                escpos.writeLF(miestilo, row);
                if (entry.Comments.isEmpty()) {
                    continue;
                }
                for (String comment : entry.Comments) {
                    if (comment.isEmpty()) {
                        continue;
                    }
                    String row2 = "  " + comment;
                    escpos.writeLF(row2);
                }
            }
            escpos.feed(1);
            escpos.writeLF(subtitle, "No. de Bebidas" + " " + ticket.elementCount());
            escpos.feed(5);
            escpos.cut(EscPos.CutMode.FULL);
            escpos.close();
        } catch (IOException | SQLException e) {
            logger.error("Op error "+ e.toString());
        }

    }

    public void ticketCocina(int id_pedido, String printerName, boolean impreso) {
        String cuantas = "SELECT d.nombre, d.comentario, pl.categoria, d.cantidad FROM pedidos p INNER JOIN detalle_pedidos d ON p.id = d.id_pedido INNER JOIN platos pl ON d.nombre = pl.nombre WHERE p.id = ? && d.impreso = ? ORDER BY (pl.categoria && d.nombre)";
        Ticket ticket = new Ticket();

        try {
            ps = con.prepareStatement(cuantas);
            ps.setInt(1, id_pedido);
            ps.setBoolean(2, impreso);
            rs = ps.executeQuery();
            while (rs.next()) {
                if (!"Bebidas".equals(rs.getString("categoria"))) {
                    TicketEntry entry = new TicketEntry();

                    entry.Name = rs.getString("nombre");
                    entry.Number = (int) rs.getDouble("cantidad");
                    entry.Comments.add(rs.getString("comentario"));

                    System.out.println(rs.getString("nombre"));
                    ticket.addEntry(entry);
                }
            }
        } catch (SQLException e) {
            logger.error("Op error "+ e.toString());
        }

        if (ticket.Entries.isEmpty()) {
            return;
        }

        String num_mesa = null, sala = null;
        PrintService printService = PrinterOutputStream.getPrintServiceByName(printerName);
        EscPos escpos;

        try {
            escpos = new EscPos(new PrinterOutputStream(printService));
            Style title = new Style()
                    .setFontSize(Style.FontSize._3, Style.FontSize._2)
                    .setJustification(EscPosConst.Justification.Center);
            String informacion = "SELECT p.*, s.nombre FROM pedidos p INNER JOIN salas s ON p.id_sala = s.id WHERE p.id = ?";
           
            ps = con.prepareStatement(informacion);
            ps.setInt(1, id_pedido);
            rs = ps.executeQuery();

            if (rs.next()) {
                num_mesa = rs.getString("num_mesa");
                sala = rs.getString("nombre");
            }  

            Style subtitle = new Style(escpos.getStyle())
                    .setJustification(EscPosConst.Justification.Center);
            Style miestilo = new Style()
                    .setFontSize(Style.FontSize._2, Style.FontSize._1)
                    .setJustification(EscPosConst.Justification.Left_Default);
            String categ = "SELECT d.nombre, d.comentario, pl.categoria FROM pedidos p INNER JOIN detalle_pedidos d ON p.id = d.id_pedido INNER JOIN platos pl ON d.nombre = pl.nombre WHERE p.id = ? && d.impreso = ? ORDER BY (pl.categoria && d.nombre)";
            escpos.writeLF(title, "Goa Restaurant");
            escpos.writeLF("N_Mesa: " + num_mesa);
            escpos.writeLF("N_Sala: " + sala);
            for (TicketEntry entry : ticket.Entries) {
                String row = entry.Number + " " + entry.Name;
                escpos.writeLF(miestilo, row);
                if (entry.Comments.isEmpty()) {
                    continue;
                }
                for (String comment : entry.Comments) {
                    if (comment.isEmpty()) {
                        continue;
                    }
                    String row2 = "  " + comment;
                    escpos.writeLF(row2);
                }
            }
            escpos.feed(1);

            escpos.writeLF(subtitle, "No. de Platillos" + " " + ticket.elementCount());
            escpos.feed(5);
            escpos.cut(EscPos.CutMode.FULL);
            escpos.close();
        } catch (IOException | SQLException e) {
            logger.error("Op error "+ e.toString());
        }
    }

    public void ticketusuario(int id_pedido, String printerName) {
        String fechaPedido = null, total = null, num_mesa = null, sala = null;
        BufferedImage imagen = null;
        double totalTicket = 0.0;
        PrintService printService = PrinterOutputStream.getPrintServiceByName(printerName);
        EscPos escpos;
        try {
            imagen = ImageIO.read(getClass().getResource("/Img/logo-dark-modo.png"));

            BufferedImage imageBufferedImage = imagen;
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
     

            ps = con.prepareStatement(informacion);
            ps.setInt(1, id_pedido);
            rs = ps.executeQuery();

            if (rs.next()) {
                num_mesa = rs.getString("num_mesa");
                sala = rs.getString("nombre");
                fechaPedido = rs.getString("fecha");
                total = rs.getString("total");

            }

            algorithm = new BitonalOrderedDither(3, 2, 150, 150);
            escposImage = new EscPosImage(new CoffeeImageImpl(imageBufferedImage), algorithm);
            escpos.write(imageWrapper, escposImage);

            escpos.writeLF(title, "Goa Restaurant");
            escpos.writeLF("Sucursal Lopez Cotilla 1520");
            escpos.writeLF("Col. Americana");
            escpos.writeLF("C.P. 44160. Gdl, Jal");
            escpos.writeLF("TEL (33) 3615 6173");
            escpos.writeLF("N_Mesa: " + num_mesa + "          " + "Fecha: " + fechaPedido);
            escpos.writeLF("N_Sala: " + sala);
            escpos.writeLF("N_ticket: " + id_pedido);
            escpos.feed(1);
            escpos.writeLF(bold,
                    "Plato                     P.SubTotal  P.Total")
                    .writeLF(bold,
                            "---------------------------------------------");
            String product = "SELECT d.* FROM pedidos p INNER JOIN detalle_pedidos d ON p.id = d.id_pedido WHERE p.id = ?";
            Ticket ticket = new Ticket();
            
            ps = con.prepareStatement(product);
            ps.setInt(1, id_pedido);
            rs = ps.executeQuery();
            while (rs.next()) {
                TicketEntry entry = new TicketEntry();
                entry.Name = rs.getString("nombre");
                entry.Number = (int) rs.getDouble("cantidad");
                entry.Cost = rs.getDouble("precio");
                ticket.addEntry(entry);
            }

            for (TicketEntry entry : ticket.Entries) {
                String text = entry.Number > 1 ? entry.Number + " " : "";
                text += entry.Name;
                escpos.writeLF(subtitle, calFilaTicket(text, entry.Cost, entry.Number * entry.Cost));
                escpos.feed(1);
            }

            escpos.writeLF(bold,
                    "---------------------------------------------");
            escpos.writeLF(bold,
                    "                              Total S/: " + total);
            String mensaje = "SELECT c.* FROM config c WHERE id = ?";
            
            ps = con.prepareStatement(mensaje);
            ps.setInt(1, 1);
            rs = ps.executeQuery();
            if (rs.next()) {
                escpos.feed(2);
                escpos.writeLF(subtitle, rs.getString("mensaje"));
                escpos.writeLF(subtitle, "Visitanos en goa.com.mx");
                escpos.writeLF(subtitle, "Gracias por su visita");
            }

            escpos.feed(5);
            escpos.cut(EscPos.CutMode.FULL);

            escpos.close();
        } catch (IOException | SQLException e) {
            logger.error("Op error "+ e.toString());
        }
    }

    public String calFilaTicket(String plato, double precio, double total) {

        String fila = null;
        int tamPlato = plato.length();
        int tamPrecio = Double.toString(precio).length();
        int tamTotal = Double.toString(total).length();
        int numSpaces = 45 - (tamTotal + tamPrecio + 6 + tamPlato);
        if (numSpaces < 0) {
            numSpaces = 0;
        }
        return fila = plato + " ".repeat(numSpaces) + "$" + precio + "    " + "$" + total;
    }

    public boolean actualizarEstado(int id_pedido) {
        String sql = "UPDATE pedidos SET estado = ? WHERE id = ?";
        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, "FINALIZADO");
            ps.setInt(2, id_pedido);
            ps.execute();
            return true;
        } catch (SQLException e) {
            logger.error("Op error "+ e.toString());
            return false;
        }
    }

    public boolean actualizarImpreso(int id_pedido, String impreso) {
        String sql = "UPDATE pedidos SET impreso = ? WHERE id = ?";
        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, impreso);
            ps.setInt(2, id_pedido);
            ps.execute();
            return true;
        } catch (SQLException e) {
            logger.error("Op error "+ e.toString());
            return false;
        }
    }

    public void actualizarImpresoPlat(int id_pedido) {
        String sql = "UPDATE detalle_pedidos SET impreso = ? WHERE id_pedido = ?";
        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            ps.setBoolean(1, true);
            ps.setInt(2, id_pedido);
            ps.execute();

        } catch (SQLException e) {
            logger.error("Op error "+ e.toString());

        }
    }

    public List listarTodosPedidos() {
        List<Pedidos> Lista = new ArrayList();
        String sql = "SELECT p.*, s.nombre FROM pedidos p INNER JOIN salas s ON p.id_sala = s.id ORDER BY p.fecha DESC;";
        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Pedidos ped = new Pedidos();
                ped.setId(rs.getInt("id"));
                ped.setId_sala(rs.getInt("id_sala"));
                ped.setSala(rs.getString("nombre"));
                ped.setNum_mesa(rs.getInt("num_mesa"));
                ped.setFecha(rs.getString("fecha"));
                ped.setTotal(rs.getDouble("total"));
                ped.setUsuario(rs.getString("usuario"));
                ped.setEstado(rs.getString("estado"));
                Lista.add(ped);
            }
        } catch (SQLException e) {
            logger.error("Op error "+ e.toString());
        }
        return Lista;
    }

    public List listarPedidos() {
        List<Pedidos> Lista = new ArrayList();
        String sql = "SELECT p.*, s.nombre FROM pedidos p INNER JOIN salas s ON p.id_sala = s.id WHERE p.fecha > CURRENT_DATE ORDER BY p.fecha DESC;";
        try {
            con = Conexion.getConnection();
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
            logger.error("Op error "+ e.toString());
        }
        return Lista;
    }

}
