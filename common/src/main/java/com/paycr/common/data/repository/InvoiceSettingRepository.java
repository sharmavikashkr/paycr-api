package com.paycr.common.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.InvoiceSetting;

@Repository
public interface InvoiceSettingRepository extends JpaRepository<InvoiceSetting, Integer> {

}
