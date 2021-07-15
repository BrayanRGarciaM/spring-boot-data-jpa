package com.bolsadeideas.springboot.app.util.paginator;


public class PageItem {
	
	private int numeroPagina;
	private boolean paginaActual;
	
	
	public PageItem(int numeroPagina, boolean paginaActual) {
		this.numeroPagina = numeroPagina;
		this.paginaActual = paginaActual;
	}
	public int getNumeroPagina() {
		return numeroPagina;
	}
	public boolean isPaginaActual() {
		return paginaActual;
	}
	
	
}
