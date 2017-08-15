package com.paycr.common.data.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.Payment;
import com.paycr.common.type.PayType;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

	public List<Payment> findByInvoiceCode(String invoiceCode);

	public List<Payment> findByInvoiceCodeAndPayType(String invoiceCode, PayType payType);

	@Query("SELECT p from Payment p WHERE p.payType = ?3 AND p.created BETWEEN ?1 AND ?2")
	public List<Payment> findPays(Date startDate, Date endDate, PayType payType);

	@Query("SELECT p from Payment p WHERE p.merchant = ?4 AND p.payType = ?3 AND p.created BETWEEN ?1 AND ?2")
	public List<Payment> findPaysForMerchant(Date startDate, Date endDate, PayType payType, Merchant merchant);

}
