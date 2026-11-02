INSERT INTO education_levels (code, title, grade_min, grade_max, sort_order)
VALUES
    ('primary', 'Tiểu học', 1, 5, 1),
    ('middle',  'THCS',     6, 9, 2),
    ('high',    'THPT',    10, 12, 3)
ON CONFLICT (code) DO NOTHING;

-- Repair legacy rows created before education-level validation was added.
UPDATE lessons
SET education_level = CASE
    WHEN grade BETWEEN 1 AND 5 THEN 'primary'
    WHEN grade BETWEEN 6 AND 9 THEN 'middle'
    WHEN grade BETWEEN 10 AND 12 THEN 'high'
    ELSE NULL
END
WHERE education_level IS NULL
   OR education_level NOT IN ('primary', 'middle', 'high')
   OR (education_level = 'primary' AND grade NOT BETWEEN 1 AND 5)
   OR (education_level = 'middle' AND grade NOT BETWEEN 6 AND 9)
   OR (education_level = 'high' AND grade NOT BETWEEN 10 AND 12);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_lessons_education_level'
    ) THEN
        ALTER TABLE lessons
            ADD CONSTRAINT fk_lessons_education_level
            FOREIGN KEY (education_level) REFERENCES education_levels(code)
            ON UPDATE RESTRICT ON DELETE RESTRICT;
    END IF;
END $$;
