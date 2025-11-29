-- Cleanup (Optional)
DROP TABLE IF EXISTS event_participant CASCADE;
DROP TABLE IF EXISTS chat_message CASCADE;
DROP TABLE IF EXISTS event_chat CASCADE;
DROP TABLE IF EXISTS event CASCADE;
DROP TABLE IF EXISTS event_bio_tags CASCADE;
DROP TABLE IF EXISTS event_bio CASCADE;
DROP TABLE IF EXISTS event_organiser CASCADE;
DROP TABLE IF EXISTS user_languages CASCADE;
DROP TABLE IF EXISTS user_bio_tags CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS user_profile CASCADE;

-- 1. User Profile (Includes @Embedded UserLocation)
CREATE TABLE user_profile (
                              id UUID PRIMARY KEY,
                              user_name VARCHAR(255) NOT NULL UNIQUE,
                              age INTEGER NOT NULL,
                              nationality VARCHAR(255),
                              profile_picture BYTEA, -- @Lob
    -- @Embedded UserLocation fields
                              city VARCHAR(255),
                              country VARCHAR(255)
);

-- Auxiliary tables for UserProfile (@ElementCollection)
CREATE TABLE user_languages (
                                user_profile_id UUID NOT NULL,
                                language VARCHAR(255),
                                FOREIGN KEY (user_profile_id) REFERENCES user_profile(id)
);

CREATE TABLE user_bio_tags (
                               user_bio_id UUID NOT NULL, -- Defined in @CollectionTable
                               tag VARCHAR(255),          -- Enum as String
                               FOREIGN KEY (user_bio_id) REFERENCES user_profile(id)
);

-- 2. User (OneToOne with UserProfile)
CREATE TABLE users (
                       user_id UUID PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       is_active BOOLEAN NOT NULL,
                       user_profile_id UUID,
                       FOREIGN KEY (user_profile_id) REFERENCES user_profile(id) ON DELETE CASCADE
);

-- 3. Event Organiser
CREATE TABLE event_organiser (
                                 id UUID PRIMARY KEY,
                                 email VARCHAR(255) NOT NULL UNIQUE,
                                 user_profile_id UUID,
                                 FOREIGN KEY (user_profile_id) REFERENCES user_profile(id)
);

-- 4. Event Bio
CREATE TABLE event_bio (
                           id UUID PRIMARY KEY,
                           description VARCHAR(255),
                           image BYTEA
);

-- Auxiliary table for EventBio (@ElementCollection)
CREATE TABLE event_bio_tags (
                                event_bio_id UUID NOT NULL,
                                tag VARCHAR(255), -- Enum InterestTag as String
                                FOREIGN KEY (event_bio_id) REFERENCES event_bio(id)
);

-- 5. Event (Includes @Embedded EventLocation)
CREATE TABLE event (
                       event_id UUID PRIMARY KEY,
                       name VARCHAR(255),
                       start_date TIMESTAMP,
                       end_date TIMESTAMP,
                       event_status VARCHAR(50), -- Enum EventStatus
    -- @Embedded EventLocation fields
                       city VARCHAR(255),
                       place_name VARCHAR(255),
    -- Relationships
                       event_bio_id UUID,
                       organiser_id UUID,
                       FOREIGN KEY (event_bio_id) REFERENCES event_bio(id) ON DELETE CASCADE,
                       FOREIGN KEY (organiser_id) REFERENCES event_organiser(id)
);

-- 6. Event Participant
CREATE TABLE event_participant (
                                   id UUID PRIMARY KEY,
                                   email VARCHAR(255) NOT NULL UNIQUE, -- Note: unique=true in entity restricts email globally
                                   user_profile_id UUID,
                                   event_id UUID,
                                   FOREIGN KEY (user_profile_id) REFERENCES user_profile(id),
                                   FOREIGN KEY (event_id) REFERENCES event(event_id)
);

-- 7. Event Chat (OneToOne with Event)
CREATE TABLE event_chat (
                            id UUID PRIMARY KEY,
                            event_id UUID NOT NULL UNIQUE, -- Unique constraint for OneToOne
                            FOREIGN KEY (event_id) REFERENCES event(event_id)
);

-- 8. Chat Message
CREATE TABLE chat_message (
                              id UUID PRIMARY KEY,
                              encrypted_text VARCHAR(255) NOT NULL,
                              sent_at TIMESTAMP,
                              chat_id UUID NOT NULL,
                              sender_id UUID NOT NULL,
                              FOREIGN KEY (chat_id) REFERENCES event_chat(id) ON DELETE CASCADE,
                              FOREIGN KEY (sender_id) REFERENCES user_profile(id)
);