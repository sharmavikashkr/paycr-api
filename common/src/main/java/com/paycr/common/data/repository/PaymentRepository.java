package com.paycr.common.data.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.Payment;
import com.paycr.common.type.PayMode;
import com.paycr.common.type.PayType;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

	public List<Payment> findByInvoiceCode(String invoiceCode);

	public List<Payment> findByInvoiceCodeAndPayType(String invoiceCode, PayType payType);

	@Query("SELECT p from Payment p WHERE p.status = ?1 AND p.payType = ?2 AND p.created BETWEEN ?3 AND ?4")
	public List<Payment> findPaysWithStatus(String status, PayType payType, Date startDate, Date endDate);

	@Query("SELECT p from Payment p WHERE p.status = ?1 AND p.payType = ?2 AND p.merchant = ?3 AND p.created BETWEEN ?4 AND ?5")
	public List<Payment> findPaysWithStatusForMerchant(String status, PayType payType, Merchant merchant,
			Date startDate, Date endDate);

	@Query("SELECT p from Payment p WHERE p.payMode = ?1 AND p.payType = ?2 AND p.created BETWEEN ?3 AND ?4")
	public List<Payment> findPaysWithMode(PayMode payMode, PayType payType, Date startDate, Date endDate);

	@Query("SELECT p from Payment p WHERE p.payMode = ?1 AND p.payType = ?2 AND p.merchant = ?3 AND p.created BETWEEN ?4 AND ?5")
	public List<Payment> findPaysWithModeForMerchant(PayMode payMode, PayType payType, Merchant merchant,
			Date startDate, Date endDate);

}
