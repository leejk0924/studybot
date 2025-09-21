-- Team
CREATE TABLE IF NOT EXISTS team (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    channel_name VARCHAR(255) NOT NULL,
    voice_channel_id BIGINT,
    chat_channel_id BIGINT
);

-- Channel
CREATE TABLE IF NOT EXISTS channel (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    discord_channel_id VARCHAR(255) NOT NULL,
    channel_type INTEGER NOT NULL
);

-- Member
CREATE TABLE IF NOT EXISTS member (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    discord_id BIGINT NOT NULL,
    nick_name VARCHAR(255),
    team_id BIGINT NOT NULL,
    FOREIGN KEY (team_id) REFERENCES team(id)
);

-- VoiceChannelLog
CREATE TABLE IF NOT EXISTS voice_channel_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    nick_name VARCHAR(255),
    channel_id BIGINT,
    channel_name VARCHAR(255),
    duration BIGINT DEFAULT 0,
    recorded_at TIMESTAMP,
    user_name VARCHAR(255),
    current_session_start TIMESTAMP,
    is_currently_studying BOOLEAN DEFAULT FALSE
);

-- FK
ALTER TABLE team
ADD FOREIGN KEY (voice_channel_id) REFERENCES channel(id),
ADD FOREIGN KEY (chat_channel_id) REFERENCES channel(id);

-- index
CREATE INDEX idx_voice_channel_logs_user_name ON voice_channel_logs(user_name);
CREATE INDEX idx_voice_channel_logs_recorded_at ON voice_channel_logs(recorded_at);
CREATE INDEX idx_voice_channel_logs_user_active ON voice_channel_logs(user_name, is_currently_studying);
CREATE INDEX idx_member_discord_id ON member(discord_id);
CREATE INDEX idx_member_team_id ON member(team_id);