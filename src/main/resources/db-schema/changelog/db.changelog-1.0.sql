-- liquibase formatted sql

-- changeset amipatil:14052019073000

CREATE	TABLE	ENCOUNTER(
	ENCOUNTER_ID	VARCHAR(36)	PRIMARY	KEY,
	PATIENT_ID	int NOT NULL,
	created_timestamp timestamp default now(), 
	updated_timestamp timestamp default now()
);
-- rollback DROP TABLE ENCOUNTER;
