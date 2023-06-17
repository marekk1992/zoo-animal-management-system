CREATE TABLE enclosure (
    id uuid PRIMARY KEY,
    name VARCHAR(20),
    size VARCHAR(20),
    location VARCHAR(20),
    objects VARCHAR[],
    free_space INT,
    animals VARCHAR[]
);
