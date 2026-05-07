package com.backendtoolkit.common.mongo.util;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

import com.backendtoolkit.common.mongo.annotation.idgenerator.TimeOrderedUuidDocument;
import com.github.f4b6a3.uuid.UuidCreator;

@Component
public class MongoTimeOrderedUuidUtil extends AbstractMongoEventListener<Object> {

	private final MongoMappingContext mappingContext;

	public MongoTimeOrderedUuidUtil(@Lazy MongoMappingContext mappingContext) {
		this.mappingContext = mappingContext;
	}

	@Override
	public void onBeforeConvert(BeforeConvertEvent<Object> event) {
		Object entity = event.getSource();

		MongoPersistentEntity<?> persistentEntity = mappingContext.getPersistentEntity(entity.getClass());
		if (persistentEntity == null) {
			return;
		}

		PersistentPropertyAccessor<Object> accessor = persistentEntity.getPropertyAccessor(entity);

		persistentEntity.doWithProperties((MongoPersistentProperty property) -> {
			if (!property.isAnnotationPresent(TimeOrderedUuidDocument.class)) {
				return;
			}

			Object value = accessor.getProperty(property);
			if (value == null) {
				accessor.setProperty(property, UuidCreator.getTimeOrdered().toString());
			}
		});
	}
}
