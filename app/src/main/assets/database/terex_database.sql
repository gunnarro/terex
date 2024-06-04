PRAGMA foreign_keys=OFF;
BEGIN TRANSACTION;
CREATE TABLE android_metadata (locale TEXT);
INSERT INTO android_metadata VALUES('en_US');
CREATE TABLE `user_account` (`user_name` TEXT, `password` TEXT, `account_type` TEXT, `organization_id` INTEGER, `person_id` INTEGER, `id` INTEGER PRIMARY KEY AUTOINCREMENT, `uuid` TEXT, `created_date` INTEGER NOT NULL, `last_modified_date` INTEGER NOT NULL);
INSERT INTO user_account VALUES('guro','change-me','BUSINESS',1,NULL,1,NULL,1717393874903,1717393874903);
CREATE TABLE `timesheet` (`user_account_id` INTEGER NOT NULL, `project_id` INTEGER NOT NULL, `year` INTEGER NOT NULL, `month` INTEGER NOT NULL, `from_date` INTEGER NOT NULL, `to_date` INTEGER NOT NULL, `status` TEXT NOT NULL DEFAULT 'OPEN', `description` TEXT, `working_days_in_month` INTEGER DEFAULT 0, `working_hours_in_month` INTEGER DEFAULT 0, `id` INTEGER PRIMARY KEY AUTOINCREMENT, `uuid` TEXT, `created_date` INTEGER NOT NULL, `last_modified_date` INTEGER NOT NULL);
CREATE TABLE `timesheet_entry` (`timesheet_id` INTEGER NOT NULL, `workday_week` INTEGER NOT NULL, `workday_date` INTEGER NOT NULL, `type` TEXT NOT NULL DEFAULT 'REGULAR', `start_time` INTEGER NOT NULL, `end_time` INTEGER NOT NULL, `worked_in_min` INTEGER NOT NULL, `break_in_min` INTEGER, `status` TEXT NOT NULL DEFAULT 'OPEN', `comment` TEXT, `use_as_default` INTEGER DEFAULT 0, `id` INTEGER PRIMARY KEY AUTOINCREMENT, `uuid` TEXT, `created_date` INTEGER NOT NULL, `last_modified_date` INTEGER NOT NULL, FOREIGN KEY(`timesheet_id`) REFERENCES `timesheet`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE );
CREATE TABLE `invoice` (`timesheet_id` INTEGER NOT NULL, `invoice_number` INTEGER NOT NULL, `client_id` INTEGER NOT NULL, `invoice_recipient_id` INTEGER NOT NULL, `invoice_issuer_id` INTEGER NOT NULL, `reference` TEXT, `invoice_status` TEXT NOT NULL DEFAULT 'OPEN', `billing_date` INTEGER NOT NULL, `billing_period_start_date` INTEGER, `billing_period_end_date` INTEGER, `due_date` INTEGER NOT NULL, `vat` REAL NOT NULL, `amount` REAL NOT NULL DEFAULT 0, `currency` TEXT NOT NULL DEFAULT 'NOK', `invoice_period` TEXT NOT NULL DEFAULT 'monthly', `id` INTEGER PRIMARY KEY AUTOINCREMENT, `uuid` TEXT, `created_date` INTEGER NOT NULL, `last_modified_date` INTEGER NOT NULL);
CREATE TABLE `timesheet_summary` (`timesheet_id` INTEGER, `year` INTEGER, `week_in_year` INTEGER, `from_date` INTEGER, `to_date` INTEGER, `total_worked_days` INTEGER DEFAULT 0, `total_days_off` INTEGER DEFAULT 0, `total_sick_leave_days` INTEGER DEFAULT 0, `total_worked_hours` REAL DEFAULT 0, `total_billed_amount` REAL DEFAULT 0, `currency` TEXT DEFAULT 'NOK', `id` INTEGER PRIMARY KEY AUTOINCREMENT, `uuid` TEXT, `created_date` INTEGER NOT NULL, `last_modified_date` INTEGER NOT NULL);
CREATE TABLE `project` (`client_id` INTEGER NOT NULL, `project_name` TEXT NOT NULL, `project_description` TEXT, `project_status` TEXT, `hourly_rate` INTEGER, `id` INTEGER PRIMARY KEY AUTOINCREMENT, `uuid` TEXT, `created_date` INTEGER NOT NULL, `last_modified_date` INTEGER NOT NULL);
INSERT INTO project VALUES(1,'apotek1','fullstack utvikler java','ACTIVE',1060,1,NULL,1717394341958,1717394341958);
CREATE TABLE `contact_info` (`mobile_number` TEXT, `mobile_number_country_code` TEXT, `email_address` TEXT, `id` INTEGER PRIMARY KEY AUTOINCREMENT, `uuid` TEXT, `created_date` INTEGER NOT NULL, `last_modified_date` INTEGER NOT NULL);
INSERT INTO contact_info VALUES('95818671','+47','jan.erik.karlsen@omegapoint.no',1,NULL,1717394234893,1717394234893);
CREATE TABLE `address` (`street_address` TEXT, `postal_code` TEXT, `city` TEXT, `country` TEXT, `country_code` TEXT, `id` INTEGER PRIMARY KEY AUTOINCREMENT, `uuid` TEXT, `created_date` INTEGER NOT NULL, `last_modified_date` INTEGER NOT NULL);
INSERT INTO address VALUES('Stavangergata 35','0467','OSLO','Norge',NULL,1,NULL,1717393874873,1717393874873);
INSERT INTO address VALUES('Lille Grensen 5','0159','OSLO','Norge',NULL,2,NULL,1717394234792,1717394234792);
CREATE TABLE `person` (`address_id` INTEGER, `contact_info_id` INTEGER, `full_name` TEXT NOT NULL, `date_of_birth` INTEGER, `social_security_number` TEXT, `gender` TEXT, `marital_status` TEXT, `id` INTEGER PRIMARY KEY AUTOINCREMENT, `uuid` TEXT, `created_date` INTEGER NOT NULL, `last_modified_date` INTEGER NOT NULL);
INSERT INTO person VALUES(NULL,1,'Jan Erik Karlsen',NULL,NULL,NULL,NULL,1,NULL,1717394234897,1717394234898);
CREATE TABLE `organization` (`business_address_id` INTEGER, `postal_address_id` INTEGER, `invoice_address_id` INTEGER, `contact_info_id` INTEGER, `organization_name` TEXT, `organization_number` TEXT, `organization_industry_type` TEXT, `bank_account_number` TEXT, `id` INTEGER PRIMARY KEY AUTOINCREMENT, `uuid` TEXT, `created_date` INTEGER NOT NULL, `last_modified_date` INTEGER NOT NULL);
INSERT INTO organization VALUES(1,NULL,NULL,NULL,'GUNNARRO AS','828707922',NULL,NULL,1,NULL,1717393874895,1717393874895);
INSERT INTO organization VALUES(2,NULL,NULL,NULL,'OMEGAPOINT NORGE AS','981874714',NULL,NULL,2,NULL,1717394234859,1717394234859);
CREATE TABLE `invoice_attachment` (`invoice_id` INTEGER NOT NULL, `attachment_file_name` TEXT NOT NULL, `attachment_type` TEXT, `attachment_file_type` TEXT NOT NULL, `attachment_file_content` BLOB NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT, `uuid` TEXT, `created_date` INTEGER NOT NULL, `last_modified_date` INTEGER NOT NULL);
CREATE TABLE `client` (`organization_id` INTEGER, `name` TEXT, `contact_person_id` INTEGER, `status` TEXT, `id` INTEGER PRIMARY KEY AUTOINCREMENT, `uuid` TEXT, `created_date` INTEGER NOT NULL, `last_modified_date` INTEGER NOT NULL);
INSERT INTO client VALUES(2,'OMEGAPOINT NORGE AS',1,'ACTIVE',1,NULL,1717394234909,1717394234909);
CREATE TABLE room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT);
INSERT INTO room_master_table VALUES(42,'06714b1d44f12eccc1fdef0365cc59ba');
DELETE FROM sqlite_sequence;
INSERT INTO sqlite_sequence VALUES('address',2);
INSERT INTO sqlite_sequence VALUES('organization',2);
INSERT INTO sqlite_sequence VALUES('user_account',1);
INSERT INTO sqlite_sequence VALUES('contact_info',1);
INSERT INTO sqlite_sequence VALUES('person',1);
INSERT INTO sqlite_sequence VALUES('client',1);
INSERT INTO sqlite_sequence VALUES('project',1);
CREATE UNIQUE INDEX `index_user_account_user_name` ON `user_account` (`user_name`);
CREATE UNIQUE INDEX `index_timesheet_user_account_id_project_id_year_month` ON `timesheet` (`user_account_id`, `project_id`, `year`, `month`);
CREATE UNIQUE INDEX `index_timesheet_entry_timesheet_id_workday_date` ON `timesheet_entry` (`timesheet_id`, `workday_date`);
CREATE UNIQUE INDEX `index_invoice_client_id_timesheet_id` ON `invoice` (`client_id`, `timesheet_id`);
CREATE UNIQUE INDEX `index_timesheet_summary_timesheet_id_year_week_in_year` ON `timesheet_summary` (`timesheet_id`, `year`, `week_in_year`);
CREATE UNIQUE INDEX `index_project_client_id_project_name` ON `project` (`client_id`, `project_name`);
CREATE UNIQUE INDEX `index_address_street_address` ON `address` (`street_address`);
CREATE UNIQUE INDEX `index_person_full_name` ON `person` (`full_name`);
CREATE UNIQUE INDEX `index_organization_organization_number` ON `organization` (`organization_number`);
CREATE UNIQUE INDEX `index_invoice_attachment_invoice_id_attachment_type_attachment_file_name_attachment_file_type` ON `invoice_attachment` (`invoice_id`, `attachment_type`, `attachment_file_name`, `attachment_file_type`);
CREATE UNIQUE INDEX `index_client_organization_id` ON `client` (`organization_id`);
CREATE VIEW `TimesheetView` AS SELECT * FROM timesheet_entry;
COMMIT;