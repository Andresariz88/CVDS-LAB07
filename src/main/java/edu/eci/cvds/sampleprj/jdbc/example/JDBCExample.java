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
package edu.eci.cvds.sampleprj.jdbc.example;

import java.sql.*;
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
            String pwd="prueba2019";

            Class.forName(driver);
            Connection con=DriverManager.getConnection(url,user,pwd);
            con.setAutoCommit(false);


            System.out.println("Valor total pedido 1: "+valorTotalPedido(con, 1));

            List<String> prodsPedido=nombresProductosPedido(con, 1);


            System.out.println("Productos del pedido 1:");
            System.out.println("-----------------------");
            for (String nomprod:prodsPedido){
                System.out.println(nomprod);
            }
            System.out.println("-----------------------");


            int suCodigoECI=2165314;
            registrarNuevoProducto(con, suCodigoECI, "Andrés", 999999969);
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
        String insertString = "INSERT INTO ORD_PRODUCTOS (codigo, nombre, precio) VALUES (?, ?, ?)";
        //Crear preparedStatement
        PreparedStatement insertProduct = con.prepareStatement(insertString);
        //Asignar parámetros
        insertProduct.setInt(1, codigo);
        insertProduct.setString(2, nombre);
        insertProduct.setInt(3, precio);
        //usar 'execute'
        insertProduct.execute();
        con.commit();
    }

    /**
     * Consultar los nombres de los productos asociados a un pedido
     * @param con la conexión JDBC
     * @param codigoPedido el código del pedido
     * @return
     */
    public static List<String> nombresProductosPedido(Connection con, int codigoPedido) throws SQLException {
        List<String> np=new LinkedList<>();
        String queryString = "SELECT pr.nombre FROM ORD_PEDIDOS p JOIN ORD_DETALLE_PEDIDO dp ON (p.codigo = dp.pedido_fk) JOIN ORD_PRODUCTOS pr ON (dp.producto_fk = pr.codigo) WHERE p.codigo = ?";
        //Crear prepared statement
        PreparedStatement queryRentedProduct = con.prepareStatement(queryString);
        //asignar parámetros
        queryRentedProduct.setInt(1, codigoPedido);
        //usar executeQuery
        //Sacar resultados del ResultSet
        ResultSet rs = queryRentedProduct.executeQuery();
        //Llenar la lista y retornarla
        while (rs.next()) {
            np.add(rs.getString(1));
        }
        return np;
    }


    /**
     * Calcular el costo total de un pedido
     * @param con
     * @param codigoPedido código del pedido cuyo total se calculará
     * @return el costo total del pedido (suma de: cantidades*precios)
     */
    public static int valorTotalPedido(Connection con, int codigoPedido) throws SQLException{
        String queryString = "SELECT SUM(pr.precio) FROM ORD_DETALLE_PEDIDO dp JOIN ORD_PRODUCTOS pr ON (dp.producto_fk = pr.codigo) WHERE dp.pedido_fk = ?";
        //Crear prepared statement
        PreparedStatement queryTotalOrder = con.prepareStatement(queryString);
        //asignar parámetros
        queryTotalOrder.setInt(1, codigoPedido);
        //usar executeQuery
        //Sacar resultados del ResultSet
        ResultSet rs = queryTotalOrder.executeQuery();
        //Sacar resultado del ResultSet
        rs.next();
        return rs.getInt(1);
    }





}