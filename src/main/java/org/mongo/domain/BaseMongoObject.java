package org.mongo.domain;

import org.bson.types.ObjectId;
import org.mongo.utils.ObjectIdSerializer;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
public abstract class BaseMongoObject {
	public BaseMongoObject() {
		super();
		this.id = ObjectId.get();
	}

	@Id
	@JsonSerialize(using=ObjectIdSerializer.class)
	private ObjectId id;

	public ObjectId getId() {
		return id;
	}

	@Override
	public int hashCode() {
		return this.id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(this.id==null) return false;
		return this.id.equals(((BaseMongoObject)obj).getId());
	}

}
