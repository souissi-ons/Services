SELECT 'CREATE DATABASE reservation_db'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'reservation_db')\gexec

SELECT 'CREATE DATABASE auth_db'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'auth_db')\gexec

SELECT 'CREATE DATABASE archives_db' 
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'archives_db')\gexec

SELECT 'CREATE DATABASE notification_db' 
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'notification_db')\gexec

SELECT 'CREATE DATABASE cursus_db' 
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'cursus_db')\gexec