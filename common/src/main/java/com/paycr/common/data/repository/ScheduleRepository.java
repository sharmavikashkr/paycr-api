package com.paycr.common.data.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.Report;
import com.paycr.common.data.domain.Schedule;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

	@Query("SELECT s FROM Schedule s WHERE active = true AND nextDate BETWEEN ?1 and ?2")
	public List<Schedule> findTodaysSchedules(Date start, Date end);

	@Query("SELECT s FROM Schedule s WHERE active = true AND nextDate < ?1")
	public List<Schedule> findOldSchedules(Date today);

	public Schedule findByReportAndMerchant(Report report, Merchant merchant);

	public List<Schedule> findByReport(Report report);

}
