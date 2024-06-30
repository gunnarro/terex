PRAGMA foreign_keys=OFF;
BEGIN TRANSACTION;

-- no db changes
ALTER TABLE client ADD COLUMN invoice_email_address VARCHAR NULL;

COMMIT;
