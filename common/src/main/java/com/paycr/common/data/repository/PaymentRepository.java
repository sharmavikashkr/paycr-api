package com.paycr.common.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.Payment;
import com.paycr.common.type.PayType;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

	public List<Payment> findByInvoiceCode(String invoiceCode);

	public List<Payment> findByInvoiceCodeAndPayType(String invoiceCode, PayType payType);

}
