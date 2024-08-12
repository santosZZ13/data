package org.data.util;

import lombok.extern.log4j.Log4j2;
import org.bson.Document;
import org.data.conts.ActionStatus;
import org.data.dto.audit.AuditTrailDto;
import org.data.dto.audit.AuditTrailFieldDto;
import org.data.service.audit.MongoAuditTrailService;
import org.springframework.data.mongodb.core.mapping.event.AfterConvertEvent;
import org.springframework.data.mongodb.core.mapping.event.AfterDeleteEvent;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.stereotype.Component;
import org.apache.commons.lang3.ObjectUtils;

import java.util.List;
import java.util.Objects;

@Log4j2
@Component
/**
 * https://phsophea101.medium.com/change-data-capture-or-audit-data-change-with-spring-boot-and-mongodb-f5dfd34e68ef
 */
public class MongoAuditListener<T> {
	private static final String ERROR_MESSAGE = "error occurred in audit {}";

//	@EventListener
	public void onAfterConvert(AfterConvertEvent<T> event) {
		try {
			Document source = event.getDocument();
			if (Boolean.TRUE.equals(MongoAuditTrailService.isAudited(event.getSource().getClass())) && Objects.nonNull(source)) {
				String identityKey = MongoAuditTrailService.getIdentityKey(event.getSource());
				InheritableContextHolder.setObject(identityKey, source);
			}
		} catch (Exception e) {
			log.error(ERROR_MESSAGE, e.getMessage(), e);
		}
	}

//	@EventListener
	public void onAfterSave(AfterSaveEvent<T> event) {
		try {
			Document newSource = event.getDocument();
			if (Objects.nonNull(newSource)) {
				String identityKey = MongoAuditTrailService.getIdentityKey(event.getSource());
				Document oldSource = InheritableContextHolder.getObject(identityKey, Document.class);
				doAudit(oldSource, event);
				InheritableContextHolder.remove(identityKey);
			}
		} catch (Exception e) {
			log.error(ERROR_MESSAGE, e.getMessage(), e);
		}
	}

//	@EventListener
	public void onAfterDelete(AfterDeleteEvent<T> event) {
		try {
			if (Boolean.TRUE.equals(MongoAuditTrailService.isAudited(event.getSource().getClass()))) {
				AuditTrailDto auditTableDto = MongoAuditTrailService.getCreatedData(event.getSource());
				if ("NA".equalsIgnoreCase(auditTableDto.getTableName()))
					auditTableDto.setTableName(event.getCollectionName());
				auditTableDto.setAction(String.valueOf(ActionStatus.DELETED));
				MongoAuditTrailService.generateLog(auditTableDto);
			}
		} catch (Exception e) {
			log.error(ERROR_MESSAGE, e.getMessage(), e);
		}
	}

	private void doAudit(Document oldSource, AfterSaveEvent<T> event) {
		Document newSource = event.getDocument();
		T source = event.getSource();
		try {
			if (Boolean.TRUE.equals(MongoAuditTrailService.isAudited(source.getClass())) && Objects.nonNull(newSource)) {
				AuditTrailDto auditTableDto = MongoAuditTrailService.getCreatedData(source);
				if ("NA".equalsIgnoreCase(auditTableDto.getTableName()))
					auditTableDto.setTableName(event.getCollectionName());
				if (Objects.nonNull(oldSource)) {
					List<AuditTrailFieldDto> changedFields = MongoAuditTrailService.getChangedFields(source.getClass(), oldSource, newSource);
					if (ObjectUtils.isNotEmpty(changedFields)) {
						auditTableDto.setFields(changedFields);
						if (!String.valueOf(ActionStatus.DELETED).equalsIgnoreCase(auditTableDto.getAction()))
							auditTableDto.setAction(String.valueOf(ActionStatus.MODIFIED));
						MongoAuditTrailService.generateLog(auditTableDto);
					}
				} else {
					MongoAuditTrailService.generateLog(auditTableDto);
				}
			}
		} catch (Exception e) {
			log.error(ERROR_MESSAGE, e.getMessage(), e);
		}
	}


}
