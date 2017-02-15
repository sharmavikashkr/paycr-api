package com.payme.common.data.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@Component
public class DuAmountDao {

	@Autowired
	private MongoTemplate mongoTemp;

	@Autowired
	private MongoOperations mongoOpt;

	@SuppressWarnings("unchecked")
	public List<String> findDistinctCourse(String college) {
		DBObject dbObject = new BasicDBObject("college", college);
		return mongoTemp.getCollection("du_amount").distinct("course", dbObject);
	}

	@SuppressWarnings("unchecked")
	public List<String> findDistinctCategory(String college) {
		DBObject dbObject = new BasicDBObject("college", college);
		return mongoTemp.getCollection("du_amount").distinct("category", dbObject);
	}

	public String findDistinctFee(String college, String course, String category) {
		DBObject dbObject = new BasicDBObject("college", college).append("course", course).append("category", category);
		return (String) mongoTemp.getCollection("du_amount").distinct("fee", dbObject).get(0);
	}

}
