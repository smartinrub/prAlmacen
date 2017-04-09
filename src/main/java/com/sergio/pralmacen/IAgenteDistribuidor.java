package com.sergio.pralmacen;

public interface IAgenteDistribuidor {

    boolean hayAgenteDisponible();

    void solicitarAgente();

    void liberarAgente();
}
