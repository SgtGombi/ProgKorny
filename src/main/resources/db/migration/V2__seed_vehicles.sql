INSERT INTO vehicles (name, brand, plate_number, type, img_url, km, year_of_manufacture, color, price, fuel, condition_status, status, description)
VALUES
    ('Audi A4 2.0 TDI', 'Audi', 'ASD-123', 'GK', '/images/audi-a4.jpg', 182000, 2014, 'fekete', 4290000, 'dízel', 'használt', 1, 'Megkímélt, frissen szervizelt, jó fogyasztás.'),
    ('Ford Transit', 'Ford', 'TRN-450', 'TGK', '/images/transit.jpg', 240000, 2016, 'fehér', 3890000, 'dízel', 'használt', 1, 'Rendezett papírok, nagy raktér, ideális munkához.'),
    ('Yamaha MT-07', 'Yamaha', 'MOT-777', 'MOTOR', '/images/mt07.jpg', 32000, 2019, 'kék', 2590000, 'benzin', 'újszerű', 1, 'Megbízható motor, ABS, kevés futás.');

INSERT INTO features (name)
VALUES
    ('Klíma'),
    ('ABS'),
    ('ESP'),
    ('Tempomat'),
    ('Vonóhorog'),
    ('Összkerék');

INSERT INTO vehicle_features (vehicle_id, feature_id)
SELECT v.id, f.id
FROM vehicles v
JOIN features f ON f.name IN ('Klíma', 'ABS', 'ESP')
WHERE v.plate_number = 'ASD-123';

INSERT INTO vehicle_features (vehicle_id, feature_id)
SELECT v.id, f.id
FROM vehicles v
JOIN features f ON f.name IN ('ABS', 'Vonóhorog')
WHERE v.plate_number = 'TRN-450';

INSERT INTO vehicle_features (vehicle_id, feature_id)
SELECT v.id, f.id
FROM vehicles v
JOIN features f ON f.name IN ('ABS', 'Tempomat')
WHERE v.plate_number = 'MOT-777';
