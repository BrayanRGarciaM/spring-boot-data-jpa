package com.bolsadeideas.springboot.app.util.paginator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;

public class PageRender<T> {

	private String url;
	private Page<T> page;
	private int totalPaginas;
	private int numElementosXPagina;
	private int paginaActual;
	private List<PageItem> paginas;
	
	public PageRender(String url, Page<T> page) {
		
		int desde, hasta;
		
		hasta = 0;
		this.url = url;
		this.page = page;
		this.paginas = new ArrayList();
		
		numElementosXPagina = page.getSize();
		totalPaginas = page.getTotalPages();
		paginaActual = page.getNumber() + 1;
		
		
		if(totalPaginas <= numElementosXPagina) {
			desde = 1;
			hasta = totalPaginas;
		} else {
			if(paginaActual <= numElementosXPagina/2) {
				desde = 1;
				hasta = numElementosXPagina;
			} else {
				if (paginaActual >= totalPaginas - numElementosXPagina/2) {
					desde = totalPaginas - numElementosXPagina + 1;
					hasta = numElementosXPagina;
				} else {
					desde = paginaActual - numElementosXPagina/2;
					hasta = numElementosXPagina;
				}
			}
		}
		
		for (int i = 0; i < hasta; i++) {
			paginas.add(new PageItem(desde + i, paginaActual == desde + i));
		}
		
		
	}

	public String getUrl() {
		return url;
	}

	public int getTotalPaginas() {
		return totalPaginas;
	}

	public int getPaginaActual() {
		return paginaActual;
	}

	public List<PageItem> getPaginas() {
		return paginas;
	}
	
	public boolean isFirst() {
		return page.isFirst();
	}
	
	public boolean isLast() {
		return page.isLast();
	}
	
	public boolean isHasNext() {
		return page.hasNext();
	}
	
	public boolean isHasPrevious() {
		return page.hasPrevious();
	}
}
