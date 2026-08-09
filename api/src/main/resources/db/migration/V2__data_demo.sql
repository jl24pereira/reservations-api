-- Usuario ADMIN. Password en claro: Admin123!
insert into usuario (email, password_hash, nombre, rol) values
  ('admin@coworking.com',
   '$2a$10$XiKW9bVHorACO0im6gn6O.HM/C.TvLUiRJc0pcqeZ.Tfp7reVlDAO',
   'Administrador', 'ADMIN');

insert into espacio (nombre, tipo, capacidad, ubicacion, tarifa_hora) values
  ('Sala Ceiba',      'SALA_REUNIONES',  8,  'Piso 1 - Ala Norte', 15.00),
  ('Sala Maquilishuat','SALA_REUNIONES', 14, 'Piso 2 - Ala Sur',   25.00),
  ('Puesto Flex A1',  'PUESTO_TRABAJO',  1,  'Piso 1 - Open Space', 4.50),
  ('Puesto Flex A2',  'PUESTO_TRABAJO',  1,  'Piso 1 - Open Space', 4.50),
  ('Oficina Ejecutiva','OFICINA_PRIVADA', 4,  'Piso 3',            40.00),
  ('Auditorio Central','AUDITORIO',      80, 'Piso 1 - Entrada',  120.00);
