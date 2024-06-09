PRAGMA foreign_keys=OFF;
BEGIN TRANSACTION;
-- update user account table data
DELETE FROM user_account WHERE id > 1000;
INSERT INTO user_account (id, uuid, created_date, last_modified_date, user_name, password, account_type, organization_id, person_id, is_default_user) VALUES (2001, '', current_timestamp, current_timestamp, 'guro', 'change-me', 'BUSINESS', 1100, null, 1 );

-- update address table data
DELETE FROM address WHERE id > 1000;
INSERT INTO address (id, uuid, created_date, last_modified_date, street_address, postal_code, city, country, country_code) VALUES (1001, '', current_timestamp, current_timestamp, 'Stavangergata 35', '0467', 'Oslo', 'Norge', 'NO' );
INSERT INTO address (id, uuid, created_date, last_modified_date, street_address, postal_code, city, country, country_code) VALUES (1002, '', current_timestamp, current_timestamp, 'Grensen 16', '0159', 'Oslo', 'Norge', 'NO' );

-- update contact info table data
DELETE FROM contact_info WHERE id > 1000;
INSERT INTO contact_info (id, uuid, created_date, last_modified_date, email_address, mobile_number_country_code, mobile_number) VALUES (1001, '', current_timestamp, current_timestamp, 'gunnar_ronneberg@yahoo.no', '+47', '45465500');
INSERT INTO contact_info (id, uuid, created_date, last_modified_date, email_address, mobile_number_country_code, mobile_number) VALUES (1002, '', current_timestamp, current_timestamp, 'Anita@norway-consulting.no', '+47', '97301271');

-- update person table data
DELETE FROM person WHERE id > 1000;
INSERT INTO person (id, uuid, created_date, last_modified_date, full_name, address_id, contact_info_id) VALUES (1002, '', current_timestamp, current_timestamp, 'Gunnar Rønneberg', null, 1001);
INSERT INTO person (id, uuid, created_date, last_modified_date, full_name, address_id, contact_info_id) VALUES (1003, '', current_timestamp, current_timestamp, 'Anita Lundtveit', null, 1002);

-- update organization table data
DELETE FROM organization WHERE id > 1000;
INSERT INTO organization (id, uuid, created_date, last_modified_date, organization_name, organization_number, bank_account_number, business_address_id, postal_address_id, contact_info_id) VALUES (1001, '', current_timestamp, current_timestamp, 'gunnarro as', '828707922', '92302698831', 1001, 1001, 1001);
-- INSERT INTO organization (id, created_date, last_modified_date, organization_name, organization_number, bank_account_number, address_id, contact_info_id) VALUES (2, current_timestamp, current_timestamp, 'Norway Consulting AS', '917616647', null, 2, 2)
-- INSERT INTO organization (id, created_date, last_modified_date, organization_name, organization_number, bank_account_number, address_id, contact_info_id) VALUES (3, current_timestamp, current_timestamp, 'CatalystOne Solutions AS', ' 986811095', null, null, null)

-- update client data
DELETE FROM client WHERE id > 1000;
INSERT INTO client (id, uuid, created_date, last_modified_date, organization_id, name, status) VALUES(1001, '', current_timestamp, current_timestamp, 1001, 'gunnarro as', 'ACTIVE')

-- update project data
DELETE FROM project WHERE id > 1000;
INSERT INTO project (id, uuid, created_date, last_modified_date, client_id, name, description, status, hourly_rate) VALUES(1055, '', current_timestamp, current_timestamp, 1001, 'CatalystOne HRIS modernisering', 'Fra monlith til microservice arkitektur', 'ACTIVE', 1250);

-- update timesheet data
DELETE FROM timesheet WHERE id > 1000;
INSERT INTO timesheet (id, uuid, created_date, last_modified_date, user_account_id, project_id, year, month, from_date, working_days_in_month, working_hours_in_month, to_date, status, description) VALUES(1099, '', current_timestamp, current_timestamp, 1, 1055, 2024, 7, current_timestamp, 150, 20, current_timestamp, 'ACTIVE', 'test timesheet');
COMMIT;