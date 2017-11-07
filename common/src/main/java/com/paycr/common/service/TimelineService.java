package com.paycr.common.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paycr.common.data.domain.Timeline;
import com.paycr.common.data.repository.TimelineRepository;
import com.paycr.common.type.ObjectType;

@Service
public class TimelineService {

	@Autowired
	private TimelineRepository tlRepo;

	public void saveToTimeline(Integer objectId, ObjectType objectType, String message, boolean internal,
			String createdBy) {
		Timeline tlParent = new Timeline();
		tlParent.setCreatedBy(createdBy);
		tlParent.setCreated(new Date());
		tlParent.setInternal(internal);
		tlParent.setMessage(message);
		tlParent.setObjectId(objectId);
		tlParent.setObjectType(objectType);
		tlRepo.save(tlParent);
	}

}
