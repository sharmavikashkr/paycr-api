package com.paycr.common.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.domain.Schedule;
import com.paycr.common.data.domain.ScheduleUser;
import com.paycr.common.data.domain.Report;

@Repository
public interface ScheduleUserRepository extends JpaRepository<ScheduleUser, Integer> {

	public List<ScheduleUser> findByPcUser(PcUser user);

	@Query("SELECT su FROM ScheduleUser su WHERE su.pcUser = ?1 AND su.schedule.report = ?2")
	public ScheduleUser findByUserAndReport(PcUser user, Report report);

	public List<ScheduleUser> findBySchedule(Schedule schedule);

}
