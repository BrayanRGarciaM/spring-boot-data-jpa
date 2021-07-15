package com.bolsadeideas.springboot.app.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.bolsadeideas.springboot.app.models.entity.Cliente;


//PagingAndSortingRepository extends CrudRepository
public interface IClienteDaoCrudRepository extends PagingAndSortingRepository<Cliente, Long>{
	
}
