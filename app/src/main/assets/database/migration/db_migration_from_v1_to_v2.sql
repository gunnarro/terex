PRAGMA foreign_keys=OFF;
BEGIN TRANSACTION;
    ALTER TABLE project ADD start_date INTEGER null;
    ALTER TABLE project ADD end_date INTEGER null;
COMMIT;
