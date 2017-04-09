package com.sergio.pralmacen;

@SuppressWarnings("serial")
public class ListaPedidosExcepcion extends RuntimeException {

    public ListaPedidosExcepcion(String mensaje) {
        super(mensaje);
    }
}
