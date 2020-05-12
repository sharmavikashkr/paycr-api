package com.paycr.common.data.repository;

import java.util.List;

import com.paycr.common.data.domain.Schedule;
import com.paycr.common.data.domain.ScheduleHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleHistoryRepository extends JpaRepository<ScheduleHistory, Integer> {

	public List<ScheduleHistory> findBySchedule(Schedule schedule);

}
