package com.paycr.common.data.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.ResetPassword;

@Repository
public interface ResetPasswordRepository extends JpaRepository<ResetPassword, Integer> {

	public ResetPassword findByResetCode(String resetCode);

	@Query("SELECT COUNT(rp) FROM ResetPassword rp WHERE (rp.created BETWEEN :start AND :end)")
	public int findResetCount(@Param("start") Date start, @Param("end") Date end);

}
