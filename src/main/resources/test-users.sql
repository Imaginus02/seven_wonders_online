-- Test users script - executed on each application restart
-- Clear and recreate test users

DELETE FROM users WHERE username IN ('alice', 'bob', 'charlie', 'admin');

INSERT INTO users (username, password, role) VALUES
('alice', '{noop}password1', 'ROLE_USER'),
('bob', '{noop}password2', 'ROLE_USER'),
('charlie', '{noop}password3', 'ROLE_USER'),
('admin', '{noop}demo', 'ROLE_ADMIN');
