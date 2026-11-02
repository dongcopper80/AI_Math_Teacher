CREATE TABLE IF NOT EXISTS education_levels (
    code         varchar(16) PRIMARY KEY,
    title        varchar(80) NOT NULL,
    grade_min    smallint NOT NULL,
    grade_max    smallint NOT NULL,
    sort_order   smallint NOT NULL UNIQUE,
    CONSTRAINT ck_education_levels_grade_range
        CHECK (grade_min >= 1 AND grade_max <= 12 AND grade_min <= grade_max)
);

CREATE TABLE IF NOT EXISTS lessons (
    id                uuid PRIMARY KEY,
    education_level   varchar(255),
    grade             integer NOT NULL,
    problem_text      text,
    image_object_key  varchar(255),
    lesson_json       text,
    created_at        timestamptz
);

CREATE INDEX IF NOT EXISTS idx_lessons_created_at
    ON lessons (created_at DESC);
