--DROP TABLE account CASCADE;


CREATE TABLE IF NOT EXISTS account(
    account_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    email VARCHAR(50),
    password CHAR(60),
    enabled BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS profile(
    profile_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    firstname VARCHAR(50),
    lastname VARCHAR(50),
    account_id UUID REFERENCES account(account_id)
);
--DROP TABLE authority CASCADE;
CREATE TABLE IF NOT EXISTS authority(
    account_id UUID REFERENCES account(account_id),
    role VARCHAR(20),
    PRIMARY KEY (account_id,role)
);

--DROP TABLE expense CASCADE;
--ALTER TABLE expense
--ADD COLUMN interval VARCHAR(20);
--ADD COLUMN date TIMESTAMP DEFAULT NOW(),
--ADD COLUMN automated BOOLEAN DEFAULT FALSE;
--ALTER TABLE expense ALTER COLUMN amount TYPE BIGINT;
CREATE TABLE IF NOT EXISTS expense(
    expense_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    title VARCHAR(50),
    description VARCHAR(200),
    amount INT NOT NULL,
    account_id UUID REFERENCES account(account_id)
);

--DROP TABLE income CASCADE;
--ALTER TABLE income
--ADD COLUMN interval VARCHAR(20);
--ADD COLUMN date TIMESTAMP DEFAULT NOW(),
--ADD COLUMN automated BOOLEAN DEFAULT FALSE;
--ALTER TABLE income ALTER COLUMN amount TYPE BIGINT;
CREATE TABLE IF NOT EXISTS income(
    income_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    title VARCHAR(50),
    description VARCHAR(200),
    amount DOUBLE PRECISION NOT NULL,
    account_id UUID REFERENCES account(account_id)
);

CREATE TABLE IF NOT EXISTS budget(
    budget_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    amount DOUBLE PRECISION NOT NULL,
    account_id UUID REFERENCES account(account_id)
);

