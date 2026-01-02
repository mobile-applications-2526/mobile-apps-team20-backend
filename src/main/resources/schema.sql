-- -- =================================================================================
-- -- SCHEMA: COMMENT CLEAN UP & TABLE CREATION AFTER FIRST RUN
-- -- =================================================================================
--
-- -- Cleanup (Optional)
-- DROP TABLE IF EXISTS event_participant CASCADE;
-- DROP TABLE IF EXISTS chat_message CASCADE;
-- DROP TABLE IF EXISTS event_chat CASCADE;
-- DROP TABLE IF EXISTS event CASCADE;
-- DROP TABLE IF EXISTS event_bio_tags CASCADE;
-- DROP TABLE IF EXISTS event_bio CASCADE;
-- DROP TABLE IF EXISTS event_organiser CASCADE;
-- DROP TABLE IF EXISTS user_languages CASCADE;
-- DROP TABLE IF EXISTS user_bio_tags CASCADE;
-- DROP TABLE IF EXISTS users CASCADE;
-- DROP TABLE IF EXISTS user_profile CASCADE;
--
-- -- -- 1. User Profile
-- CREATE TABLE user_profile (
--     id UUID PRIMARY KEY,
--     user_name VARCHAR(255) NOT NULL UNIQUE,
--     age INTEGER NOT NULL,
--     nationality VARCHAR(255),
--     profile_picture BYTEA,
--     city VARCHAR(255),
--     country VARCHAR(255)
-- );
--
-- -- Auxiliary tables
-- CREATE TABLE user_languages (
--     user_profile_id UUID NOT NULL,
--     language VARCHAR(255),
--     FOREIGN KEY (user_profile_id) REFERENCES user_profile(id)
-- );
--
-- CREATE TABLE user_bio_tags (
--     user_bio_id UUID NOT NULL,
--     tag VARCHAR(255),
--     FOREIGN KEY (user_bio_id) REFERENCES user_profile(id)
-- );
--
-- -- 2. User
-- CREATE TABLE users (
--     user_id UUID PRIMARY KEY,
--     email VARCHAR(255) NOT NULL UNIQUE,
--     is_active BOOLEAN NOT NULL,
--     user_profile_id UUID,
--     FOREIGN KEY (user_profile_id) REFERENCES user_profile(id) ON DELETE CASCADE
-- );
--
-- -- 3. Event Organiser
-- CREATE TABLE event_organiser (
--     id UUID PRIMARY KEY,
--     email VARCHAR(255) NOT NULL UNIQUE,
--     user_profile_id UUID,
--     FOREIGN KEY (user_profile_id) REFERENCES user_profile(id)
-- );
--
-- -- 4. Event Bio
-- CREATE TABLE event_bio (
--     id UUID PRIMARY KEY,
--     description VARCHAR(255),
--     image BYTEA
-- );
--
-- CREATE TABLE event_bio_tags (
--     event_bio_id UUID NOT NULL,
--     tag VARCHAR(255),
--     FOREIGN KEY (event_bio_id) REFERENCES event_bio(id)
-- );
--
-- -- 5. Event
-- CREATE TABLE event (
--     event_id UUID PRIMARY KEY,
--     name VARCHAR(255),
--     start_date TIMESTAMP,
--     end_date TIMESTAMP,
--     event_status VARCHAR(50),
--     city VARCHAR(255),
--     place_name VARCHAR(255),
--     event_bio_id UUID,
--     organiser_id UUID,
--     FOREIGN KEY (event_bio_id) REFERENCES event_bio(id) ON DELETE CASCADE,
--     FOREIGN KEY (organiser_id) REFERENCES event_organiser(id)
-- );
--
-- -- 6. Event Participant
-- CREATE TABLE event_participant (
--     id UUID PRIMARY KEY,
--     email VARCHAR(255) NOT NULL,
--     user_profile_id UUID,
--     event_id UUID,
--     FOREIGN KEY (user_profile_id) REFERENCES user_profile(id),
--     FOREIGN KEY (event_id) REFERENCES event(event_id)
-- );
--
-- -- 7. Event Chat
-- CREATE TABLE event_chat (
--     id UUID PRIMARY KEY,
--     event_id UUID NOT NULL UNIQUE,
--     FOREIGN KEY (event_id) REFERENCES event(event_id)
-- );
--
-- -- 8. Chat Message
-- CREATE TABLE chat_message (
--     id UUID PRIMARY KEY,
--     encrypted_text VARCHAR(255) NOT NULL,
--     sent_at TIMESTAMP,
--     chat_id UUID NOT NULL,
--     sender_id UUID NOT NULL,
--     FOREIGN KEY (chat_id) REFERENCES event_chat(id) ON DELETE CASCADE,
--     FOREIGN KEY (sender_id) REFERENCES user_profile(id)
-- );

-- =============================================
-- DATA SEEDING
-- =============================================

-- 1. INSERT PROFILES
INSERT INTO user_profile (id, user_name, age, nationality, profile_picture, city, country)
VALUES
    ('a0000000-0000-0000-0000-000000000001', 'alice_wonder', 28, 'USA', NULL, 'New York', 'USA'),
    ('b0000000-0000-0000-0000-000000000002', 'bob_builder', 32, 'UK', NULL, 'London', 'UK')
    ON CONFLICT (id) DO NOTHING;

-- 1.1 AUXILIARY TABLES (Limpiar e insertar de nuevo)
DELETE FROM user_languages
WHERE user_profile_id IN ('a0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000002');

INSERT INTO user_languages (user_profile_id, language) VALUES
                                                           ('a0000000-0000-0000-0000-000000000001', 'English'),
                                                           ('a0000000-0000-0000-0000-000000000001', 'Spanish'),
                                                           ('b0000000-0000-0000-0000-000000000002', 'English');

DELETE FROM user_bio_tags
WHERE user_bio_id IN ('a0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000002');

-- CORREGIDO: 'CODING' no existe -> Cambiado a 'TECHNOLOGY'
INSERT INTO user_bio_tags (user_bio_id, tag) VALUES
                                                 ('a0000000-0000-0000-0000-000000000001', 'TECHNOLOGY'),
                                                 ('b0000000-0000-0000-0000-000000000002', 'SPORTS');

-- 2. INSERT USERS
INSERT INTO users (user_id, email, is_active, user_profile_id)
VALUES
    ('10000000-0000-0000-0000-000000000001', 'alice@example.com', true, 'a0000000-0000-0000-0000-000000000001'),
    ('10000000-0000-0000-0000-000000000002', 'bob@example.com', true, 'b0000000-0000-0000-0000-000000000002')
    ON CONFLICT (user_id) DO NOTHING;

-- 3. INSERT ORGANISER
INSERT INTO event_organiser (id, email, user_profile_id)
VALUES
    ('90000000-0000-0000-0000-000000000001', 'alice@example.com', 'a0000000-0000-0000-0000-000000000001')
    ON CONFLICT (id) DO NOTHING;

-- 4. INSERT EVENT BIO & EVENT
INSERT INTO event_bio (id, description, image_url)
VALUES
    ('eb000000-0000-0000-0000-000000000001', 'A cool tech meetup for developers', NULL)
    ON CONFLICT (id) DO NOTHING;

DELETE FROM event_bio_tags WHERE event_bio_id = 'eb000000-0000-0000-0000-000000000001';

-- ESTE YA ERA CORRECTO ('TECHNOLOGY' existe en el Enum)
INSERT INTO event_bio_tags (event_bio_id, tag) VALUES
    ('eb000000-0000-0000-0000-000000000001', 'TECHNOLOGY');

INSERT INTO event (event_id, name, start_date, end_date, event_status, city, place_name, event_bio_id, organiser_id)
VALUES
    (
        'e0000000-0000-0000-0000-000000000001',
        'Campus Tech Talk',
        '2025-12-01 10:00:00',
        '2025-12-01 12:00:00',
        'ACTIVE',
        'Leuven',
        'Conference Hall A',
        'eb000000-0000-0000-0000-000000000001',
        '90000000-0000-0000-0000-000000000001'
    )
    ON CONFLICT (event_id) DO NOTHING;

-- 5. INSERT PARTICIPANT
INSERT INTO event_participant (id, email, user_profile_id, event_id)
VALUES
    (
        '70000000-0000-0000-0000-000000000001',
        'bob@example.com',
        'b0000000-0000-0000-0000-000000000002',
        'e0000000-0000-0000-0000-000000000001'
    )
    ON CONFLICT (id) DO NOTHING;

-- 6. INSERT CHAT & MESSAGES
INSERT INTO event_chat (id, event_id)
VALUES
    ('c0000000-0000-0000-0000-000000000001', 'e0000000-0000-0000-0000-000000000001')
    ON CONFLICT (id) DO NOTHING;
