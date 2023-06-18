CREATE TABLE animal (
    id UUID PRIMARY KEY,
    species VARCHAR(50),
    food VARCHAR(20),
    amount INT,
    enclosure_id UUID
);