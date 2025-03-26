
CREATE EXTENSION IF NOT EXISTS pgcrypto;
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



CREATE TABLE IF NOT EXISTS category (
    category_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    type VARCHAR(10) NOT NULL  -- 'expense' or 'income'
);

--INSERT INTO category (name, type) VALUES
--('Groceries', 'expense'),
--('Dining', 'expense'),
--('Transportation', 'expense'),
--('Entertainment', 'expense'),
--('Bills', 'expense'),
--('Healthcare', 'expense'),
--('Education', 'expense'),
--('Personal Care', 'expense'),
--('Shopping', 'expense'),
--('Others', 'expense');
--
--INSERT INTO category (name, type) VALUES
--('Salary', 'income'),
--('Investment', 'income'),
--('Gift', 'income'),
--('Other Income', 'income');

CREATE TABLE IF NOT EXISTS budget(
    budget_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    amount DOUBLE PRECISION NOT NULL,
    account_id UUID REFERENCES account(account_id),
    category_id UUID REFERENCES category(category_id),
    month INT NOT NULL CHECK (month BETWEEN 1 AND 12),
    year INT NOT NULL

);


CREATE TABLE IF NOT EXISTS expense (
    expense_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    title VARCHAR(50),
    description VARCHAR(200),
    category_id UUID REFERENCES category(category_id),
    amount DOUBLE PRECISION NOT NULL,
    interval VARCHAR(20),
    date TIMESTAMP DEFAULT NOW(),
    automated BOOLEAN DEFAULT FALSE,
    account_id UUID REFERENCES account(account_id)
);


CREATE TABLE IF NOT EXISTS income (
    income_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    title VARCHAR(50),
    description VARCHAR(200),
    category_id UUID REFERENCES category(category_id),
    amount DOUBLE PRECISION NOT NULL,
    interval VARCHAR(20),
    date TIMESTAMP DEFAULT NOW(),
    automated BOOLEAN DEFAULT FALSE,
    account_id UUID REFERENCES account(account_id)
);