PRAGMA foreign_keys=OFF;
BEGIN TRANSACTION;
-- update integration table
INSERT INTO integration (id, uuid, created_date, last_modified_date, system, base_url, schema_url, authentication_type, user_name, password, access_token, service_type, read_timeout_ms, connection_timeout_ms, http_headers_content_type) VALUES (1, '', current_timestamp, current_timestamp, 'BREG', 'https://data.brreg.no/enhetsregisteret/api/enheter/', null, 'NONE', null, null, null, 'REST', 5000, 5000, 'application/vnd.brreg.enhetsregisteret.enhet.v2+json;charset=UTF-8');

-- update user account table data
INSERT INTO user_account (id, uuid, created_date, last_modified_date, user_name, password, account_type, organization_id, person_id, is_default_user) VALUES (1, '', current_timestamp, current_timestamp, 'guro', 'change-me', 'BUSINESS', 1001, null, 1 );

-- update address table data
INSERT INTO address (id, uuid, created_date, last_modified_date, street_address, postal_code, city, country, country_code) VALUES (1001, '', current_timestamp, current_timestamp, 'Stavangergata 35', '0467', 'Oslo', 'Norge', 'NO' );
INSERT INTO address (id, uuid, created_date, last_modified_date, street_address, postal_code, city, country, country_code) VALUES (1002, '', current_timestamp, current_timestamp, 'Lille Grensen 5', '0159', 'Oslo', 'Norge', 'NO' );

-- update contact info table data
INSERT INTO contact_info (id, uuid, created_date, last_modified_date, email_address, mobile_number_country_code, mobile_number) VALUES (1001, '', current_timestamp, current_timestamp, 'gunnar_ronneberg@yahoo.no', '+47', '45465500');
INSERT INTO contact_info (id, uuid, created_date, last_modified_date, email_address, mobile_number_country_code, mobile_number) VALUES (1002, '', current_timestamp, current_timestamp, 'jan.erik.karlsen@omegapoint.no', '+47', '981874714');

-- update person table data
INSERT INTO person (id, uuid, created_date, last_modified_date, full_name, address_id, contact_info_id) VALUES (1001, '', current_timestamp, current_timestamp, 'Gunnar Rønneberg', null, 1001);
INSERT INTO person (id, uuid, created_date, last_modified_date, full_name, address_id, contact_info_id) VALUES (1002, '', current_timestamp, current_timestamp, 'Jan Erik Karlsen', null, 1002);

-- update organization table data
INSERT INTO organization (id, uuid, created_date, last_modified_date, organization_name, organization_number, bank_account_number, business_address_id, postal_address_id, contact_info_id) VALUES (1001, '', current_timestamp, current_timestamp, 'GUNNARRO AS', '828707922', '92302698831', 1001, 1001, 1001);
INSERT INTO organization (id, uuid, created_date, last_modified_date, organization_name, organization_number, bank_account_number, business_address_id, postal_address_id, contact_info_id) VALUES (1002, '', current_timestamp, current_timestamp, 'OMEGAPOINT NORGE AS', '981874714', null, 1002, 1002, 1002);

-- update client data
INSERT INTO client (id, uuid, created_date, last_modified_date, organization_id, contact_person_id, name, status, invoice_type, invoice_email_address) VALUES(1001, '', current_timestamp, current_timestamp, 1002, 1002, 'OMEGAPOINT NORGE AS', 'ACTIVE', 'EMAIL', '');

-- update project data
INSERT INTO project (id, uuid, created_date, last_modified_date, client_id, name, description, status, hourly_rate) VALUES(1055, '', current_timestamp, current_timestamp, 1001, 'Apotek1', 'Utviklng og forvalting av lagerstyingsløsning', 'ACTIVE', 1060);

COMMIT;