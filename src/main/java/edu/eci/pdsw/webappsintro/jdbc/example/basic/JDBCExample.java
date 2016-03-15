/*
 * Copyright (C) 2015 hcadavid
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.eci.pdsw.webappsintro.jdbc.example.basic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class JDBCExample {
    
    public static void main(String args[]){
        try {
            String url="jdbc:mysql://desarrollo.is.escuelaing.edu.co:3306/bdprueba";
            String driver="com.mysql.jdbc.Driver";
            String user="bdprueba";
            String pwd="bdprueba";
                        
            Class.forName(driver);
            Connection con=DriverManager.getConnection(url,user,pwd);
            con.setAutoCommit(false);
                 
            
            System.out.println("Valor total pedido 102103021: "+valorTotalPedido(con, 102103021));
            
            List<String> prodsPedido=nombresProductosPedido(con, 102103021);
            
            
            System.out.println("Productos del pedido 1:");
            System.out.println("-----------------------");
            for (String nomprod:prodsPedido){
                System.out.println(nomprod);
            }
            System.out.println("-----------------------");
            
            
            int suCodigoECI=2103021;
            registrarNuevoProducto(con, suCodigoECI, "Lamborghini", 1000000000);            
            con.commit();
            
            cambiarNombreProducto(con, suCodigoECI, "jaguar");
            con.commit();
            
            
            con.close();
                                   
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(JDBCExample.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    /**
     * Agregar un nuevo producto con los parámetros dados
     * @param con la conexión JDBC
     * @param codigo
     * @param nombre
     * @param precio
     * @throws SQLException 
     */
    public static void registrarNuevoProducto(Connection con, int codigo, String nombre,int precio) throws SQLException{
        
        PreparedStatement registrarProducto= null;
        String nProducto = "INSERT INTO ORD_PRODUCTOS VALUES (?,?,?)";
        try{
            con.setAutoCommit(false);

            registrarProducto = con.prepareStatement(nProducto);
            registrarProducto.setInt(1, codigo);
            registrarProducto.setString(2, nombre);
            registrarProducto.setInt(3, precio);
            registrarProducto.executeUpdate();
            
            con.commit();
        }catch(SQLException e){
            e.printStackTrace();
            System.out.println("No se pudo hacer la insercion");
        }
        //usar 'execute'

        
        con.commit();
        
    }
    
    /**
     * Consultar los nombres de los productos asociados a un pedido
     * @param con la conexión JDBC
     * @param codigoPedido el código del pedido
     * @return 
     */
    public static List<String> nombresProductosPedido(Connection con, int codigoPedido){
        List<String> np=new LinkedList<>();
        
        PreparedStatement nProdutosPedidos = null;
        String codPedido = "SELECT prod.nombre FROM ORD_PEDIDOS ped INNER JOIN ORD_DETALLES_PEDIDO det ON ped.codigo = det.pedido_fk INNER JOIN ORD_PRODUCTOS prod ON prod.codigo = det.producto_fk WHERE ped.codigo = ?";
        try{
            con.setAutoCommit(false);
            nProdutosPedidos = con.prepareStatement(codPedido);
            nProdutosPedidos.setInt(1, codigoPedido);
            ResultSet executeQuery = nProdutosPedidos.executeQuery();
            con.commit();
            while (executeQuery.next()){
                np.add(executeQuery.getString("nombre"));
            }
        }catch (SQLException e){
            e.printStackTrace();
            System.out.println("No se pudo obtener datos");
            
        }
        
        return np;
    }

    
    /**
     * Calcular el costo total de un pedido
     * @param con
     * @param codigoPedido código del pedido cuyo total se calculará
     * @return el costo total del pedido (suma de: cantidades*precios)
     */
    public static int valorTotalPedido(Connection con, int codigoPedido){
        int suma = 0;
        PreparedStatement valorTotal = null;
        String consul = "SELECT SUM(prod.precio) suma FROM ORD_PEDIDOS ped INNER JOIN ORD_DETALLES_PEDIDO det ON ped.codigo = det.pedido_fk INNER JOIN ORD_PRODUCTOS prod ON prod.codigo = det.producto_fk WHERE ped.codigo = ?";
        try{
            con.setAutoCommit(false);
            valorTotal = con.prepareStatement(consul);
            valorTotal.setInt(1, codigoPedido);
            ResultSet executeQuery = valorTotal.executeQuery();
            con.commit();
            executeQuery.next();
            suma = executeQuery.getInt("suma");
            
        }catch(SQLException e){
            e.printStackTrace();
            System.out.println("No se pudo obtener los datos de la consulta");
        }
        
        return suma;
    }
    

    /**
     * Cambiar el nombre de un producto
     * @param con
     * @param codigoProducto codigo del producto cuyo nombre se cambiará
     * @param nuevoNombre el nuevo nombre a ser asignado
     */
    public static void cambiarNombreProducto(Connection con, int codigoProducto, 
            String nuevoNombre) throws SQLException{
        PreparedStatement nuevonombre = null;
        String update = "UPDATE ORD_PRODUCTOS SET nombre = ? WHERE codigo=?";
        try{
            con.setAutoCommit(false);

            nuevonombre = con.prepareStatement(update);
            nuevonombre.setString(1, nuevoNombre);
            nuevonombre.setInt(2, codigoProducto);            
            nuevonombre.executeUpdate();
            
            con.commit();
        }catch(SQLException e){
            e.printStackTrace();
            System.out.println("No se pudo modificar el nombre del producto");
        }
        con.commit();
        //Crear prepared statement
        //asignar parámetros
        //usar executeUpdate
        //verificar que se haya actualizado exactamente un registro
        
        
    }
    
    
    
}
