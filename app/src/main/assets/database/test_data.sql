-- update address table data
DELETE FROM address WHERE id > 0
INSERT INTO address (id, created_date, last_modified_date, street_name, street_number, street_number_prefix, post_code, city, country, country_code) VALUES (1, current_timestamp, current_timestamp, 'Stavangergata', '35', null, '0467', 'Oslo', 'Norge', 'NO' )
INSERT INTO address (id, created_date, last_modified_date, street_name, street_number, street_number_prefix, post_code, city, country, country_code) VALUES (2, current_timestamp, current_timestamp, 'Grensen', '16', null, '0159', 'Oslo', 'Norge', 'NO' )

-- update contact info table data
DELETE FROM contact_info WHERE id > 0
INSERT INTO contact_info (id, created_date, last_modified_date, email_address, mobile_number_country_code, mobile_number) VALUES (1, current_timestamp, current_timestamp, 'gunnar_ronneberg@yahoo.no', '+47', '45465500')
INSERT INTO contact_info (id, created_date, last_modified_date, email_address, mobile_number_country_code, mobile_number) VALUES (2, current_timestamp, current_timestamp, 'Anita@norway-consulting.no', '+47', '97301271')

-- update person table data
DELETE FROM person WHERE id > 0
INSERT INTO person (id, created_date, last_modified_date, first_name, middle_name, last_name, address_id, contact_info_id) VALUES (1, current_timestamp, current_timestamp, 'Gunnar', null, 'Rønneberg', null, 1)
INSERT INTO person (id, created_date, last_modified_date, first_name, middle_name, last_name, address_id, contact_info_id) VALUES (2, current_timestamp, current_timestamp, 'Anita', null, 'Lundtveit', null, 2)

-- update organization table data
-- DELETE FROM organization WHERE id > 0
-- INSERT INTO organization (id, created_date, last_modified_date, company_name, organization_number, bank_account_number, address_id, contact_info_id, contact_person_id) VALUES (1, current_timestamp, current_timestamp, 'gunnarro as', '828707922', '92302698831', 1, 1, 1)
-- INSERT INTO organization (id, created_date, last_modified_date, company_name, organization_number, bank_account_number, address_id, contact_info_id, contact_person_id) VALUES (2, current_timestamp, current_timestamp, 'Norway Consulting AS', '917616647', null, 2, 2, 2)
-- INSERT INTO organization (id, created_date, last_modified_date, company_name, organization_number, bank_account_number, address_id, contact_info_id, contact_person_id) VALUES (3, current_timestamp, current_timestamp, 'CatalystOne Solutions AS', ' 986811095', null, null, null, null)

-- update client data
DELETE FROM client WHERE id > 0
INSERT INTO client (id, created_date, last_modified_date, company_id) VALUES(1, current_timestamp, current_timestamp, 2)

-- update project data
DELETE FROM project WHERE id > 0
INSERT INTO project (id, created_date, last_modified_date, client_id, project_name, project_description, project_status) VALUES(1, current_timestamp, current_timestamp, 2, 'CatalystOne HRIS modernisering', 'Fra monlith til microservice arkitektur', 'ACTIVE')

DELETE FROM consultant_broker WHERE id > 0
INSERT INTO consultant_broker (id, created_date, last_modified_date, company_id, name, status) VALUES(1, current_timestamp, current_timestamp, 2, 'Norway Consulting AS', 'ACTIVE')

DELETE FROM consultant WHERE id > 0
INSERT INTO consultant (id, created_date, last_modified_date, company_id, name, status) VALUES(1, current_timestamp, current_timestamp, 1, 'gunnarro as', 'ACTIVE')