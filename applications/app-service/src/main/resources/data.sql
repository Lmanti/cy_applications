INSERT INTO loan_status (name, description) VALUES 
('PENDIENTE', 'Solicitud pendiente de revisión'),
('APROBADO', 'Solicitud ha sido aprobada'),
('RECHAZADO', 'Solicitud ha sido rechazada'),
('EN REVISIÓN', 'Solicitud está actualmente en revisión'),
('DESEMBOLSADO', 'Préstamo ha sido desembolsado al cliente');

INSERT INTO loan_type (name, min_amount, max_amount, interest_rate, auto_validation) VALUES 
('Préstamo Personal', 1000000.00, 10000000.00, 12.5, false),
('Efectivo Rápido', 500000.00, 3000000.00, 18.0, true),
('Mejoras del Hogar', 5000000.00, 50000000.00, 9.5, false),
('Consolidación de Deudas', 2000000.00, 20000000.00, 14.0, false),
('Préstamo Educativo', 1000000.00, 50000000.00, 8.5, false);