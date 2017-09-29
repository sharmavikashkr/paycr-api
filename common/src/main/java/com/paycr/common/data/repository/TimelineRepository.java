package com.paycr.common.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.Timeline;
import com.paycr.common.type.ObjectType;

@Repository
public interface TimelineRepository extends JpaRepository<Timeline, Integer> {

	public List<Timeline> findByObjectTypeAndObjectId(ObjectType objectType, Integer objectId);

}
