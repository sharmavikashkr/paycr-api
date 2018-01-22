package com.paycr.common.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.TaxMaster;

@Repository
public interface TaxMasterRepository extends JpaRepository<TaxMaster, Integer> {

	public List<TaxMaster> findByActive(boolean active);

	public TaxMaster findByName(String name);
	
	public TaxMaster findByNameAndValue(String name, Float value);

	public List<TaxMaster> findByParent(TaxMaster parent);

}
