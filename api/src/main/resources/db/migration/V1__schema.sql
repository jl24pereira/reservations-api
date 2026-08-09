create extension if not exists btree_gist;

create table usuario (
    id            uuid primary key default uuidv7(),
    email         varchar(120) not null unique,
    password_hash varchar(100) not null,
    nombre        varchar(120) not null,
    rol           varchar(20)  not null,
    activo        boolean      not null default true,
    creado_en     timestamptz  not null default now(),
    constraint ck_usuario_rol check (rol in ('ADMIN', 'USER'))
);

create table espacio (
    id           uuid primary key default uuidv7(),
    nombre       varchar(120)   not null,
    tipo         varchar(30)    not null,
    capacidad    integer        not null,
    ubicacion    varchar(160)   not null,
    tarifa_hora  numeric(10,2)  not null,
    activo       boolean        not null default true,
    creado_en    timestamptz    not null default now(),
    constraint ck_espacio_tipo      check (tipo in ('SALA_REUNIONES','PUESTO_TRABAJO','OFICINA_PRIVADA','AUDITORIO')),
    constraint ck_espacio_capacidad check (capacidad > 0),
    constraint ck_espacio_tarifa    check (tarifa_hora >= 0)
);

create table reserva (
    id          uuid        primary key default uuidv7(),
    espacio_id  uuid        not null references espacio(id) on delete restrict,
    usuario_id  uuid        not null references usuario(id) on delete restrict,
    inicio      timestamptz not null,
    fin         timestamptz not null,
    estado      varchar(20) not null,
    monto_total numeric(10,2),
    creado_en   timestamptz not null default now(),
    constraint ck_reserva_estado check (estado in ('PENDING','PENDING_PAYMENT','CONFIRMED','CANCELLED','COMPLETED')),
    constraint ck_reserva_rango  check (fin > inicio),
    constraint ex_reserva_solapada exclude using gist (
        espacio_id with =,
        tstzrange(inicio, fin) with &&
    ) where (estado <> 'CANCELLED')
);

create index ix_reserva_usuario on reserva (usuario_id);
create index ix_reserva_espacio_inicio on reserva (espacio_id, inicio);

create table pago (
    id               uuid        primary key default uuidv7(),
    reserva_id       uuid        not null references reserva(id) on delete cascade,
    estado           varchar(20) not null,
    autorizacion_id  varchar(80),
    metodo           varchar(40) not null,
    creado_en        timestamptz not null default now(),
    constraint ck_pago_estado check (estado in ('APPROVED','REJECTED','PENDING','FAILED'))
);

create index ix_pago_reserva on pago (reserva_id);
