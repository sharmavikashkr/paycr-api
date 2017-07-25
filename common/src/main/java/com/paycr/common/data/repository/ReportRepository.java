package com.paycr.common.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {

	public List<Report> findByMerchant(Merchant merchant);

	public Report findByIdAndMerchant(Integer id, Merchant merchant);

}
