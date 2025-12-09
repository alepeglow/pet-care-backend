-- ============================================
-- CRIAÇÃO DAS TABELAS - SISTEMA ADOÇÃO E CUIDADOS PET
-- Banco: PostgreSQL
-- ============================================

-- 1) TABELA TUTOR
CREATE TABLE tutor (
    id           BIGSERIAL       PRIMARY KEY,
    nome         VARCHAR(100)    NOT NULL,
    telefone     VARCHAR(20),
    email        VARCHAR(120)    NOT NULL UNIQUE,
    endereco     VARCHAR(255)
);

-- 2) TABELA PET
CREATE TABLE pet (
    id            BIGSERIAL      PRIMARY KEY,
    nome          VARCHAR(80)    NOT NULL,
    especie       VARCHAR(30)    NOT NULL,        -- cachorro, gato, etc.
    raca          VARCHAR(80),
    idade         INTEGER,
    status        VARCHAR(20)    NOT NULL,        -- DISPONIVEL, ADOTADO
    data_entrada  DATE           NOT NULL,
    id_tutor      BIGINT,                         -- null se ainda não adotado

    CONSTRAINT fk_pet_tutor
        FOREIGN KEY (id_tutor) REFERENCES tutor(id)
);

-- 3) TABELA ADOCAO
CREATE TABLE adocao (
    id          BIGSERIAL   PRIMARY KEY,
    id_pet      BIGINT      NOT NULL,
    id_tutor    BIGINT      NOT NULL,
    data_adocao DATE        NOT NULL,
    observacoes TEXT,

    CONSTRAINT fk_adocao_pet
        FOREIGN KEY (id_pet) REFERENCES pet(id),

    CONSTRAINT fk_adocao_tutor
        FOREIGN KEY (id_tutor) REFERENCES tutor(id),

    -- um pet só pode ter uma adoção
    CONSTRAINT uq_adocao_pet UNIQUE (id_pet)
);

-- 4) TABELA EVENTO_CUIDADO
CREATE TABLE evento_cuidado (
    id         BIGSERIAL    PRIMARY KEY,
    id_pet     BIGINT       NOT NULL,
    tipo       VARCHAR(20)  NOT NULL,   -- VACINA, CONSULTA, BANHO, REMEDIO, OUTRO
    data_hora  TIMESTAMP    NOT NULL,
    descricao  VARCHAR(255),
    status     VARCHAR(20)  NOT NULL,   -- AGENDADO, REALIZADO, CANCELADO

    CONSTRAINT fk_evento_pet
        FOREIGN KEY (id_pet) REFERENCES pet(id)
);

INSERT INTO tutor (nome, telefone, email, endereco)
VALUES
('Maria Silva', '51999990000', 'maria.silva@example.com', 'Rua das Flores, 123'),
('João Pereira', '51988887777', 'joao.pereira@example.com', 'Av. Central, 450'),
('Ana Costa', '51991234567', 'ana.costa@example.com', 'Rua Verde, 89');

INSERT INTO pet (nome, especie, raca, idade, status, data_entrada, id_tutor)
VALUES
('Bidu', 'Cachorro', 'Vira-lata', 3, 'DISPONIVEL', '2024-03-01', NULL),
('Mimi', 'Gato', 'Siamês', 2, 'DISPONIVEL', '2024-03-05', NULL),
('Thor', 'Cachorro', 'Golden Retriever', 1, 'ADOTADO', '2024-02-20', 1),
('Luna', 'Gato', 'Persa', 4, 'ADOTADO', '2024-01-10', 2);

INSERT INTO adocao (id_pet, id_tutor, data_adocao, observacoes)
VALUES
(3, 1, '2024-02-21', 'Pet muito dócil, adaptação rápida.'),
(4, 2, '2024-01-12', 'Precisa de cuidados com pelos.');

INSERT INTO evento_cuidado (id_pet, tipo, data_hora, descricao, status)
VALUES
(3, 'VACINA', '2024-03-15 10:00:00', 'Vacina antirrábica', 'REALIZADO'),
(3, 'CONSULTA', '2024-04-10 14:00:00', 'Retorno veterinário', 'AGENDADO');

INSERT INTO evento_cuidado (id_pet, tipo, data_hora, descricao, status)
VALUES
(4, 'BANHO', '2024-03-20 15:30:00', 'Banho e tosa completa', 'REALIZADO'),
(4, 'VACINA', '2024-04-05 09:30:00', 'Vacina tríplice felina', 'AGENDADO');

--Tutores Cadastrados
SELECT * FROM tutor;

--Pets Cadastrados
SELECT * FROM pet;

--Adoções registradas
SELECT * FROM adocao;

--Eventos de cuidado
SELECT * FROM evento_cuidado;

--Listar pets com o nome do tutor
SELECT
    p.id AS pet_id,
    p.nome AS pet,
    p.status,
    t.nome AS tutor
FROM pet p
LEFT JOIN tutor t ON t.id = p.id_tutor;

--Listar adoções com informações do pet e do tutor
SELECT
    a.id AS adocao_id,
    p.nome AS pet,
    t.nome AS tutor,
    a.data_adocao
FROM adocao a
JOIN pet p ON p.id = a.id_pet
JOIN tutor t ON t.id = a.id_tutor;

--Ver todos os eventos de um pet específico
SELECT
    e.id AS evento_id,
    e.tipo,
    e.data_hora,
    e.status,
    p.nome AS pet
FROM evento_cuidado e
JOIN pet p ON p.id = e.id_pet
WHERE p.id = 3;

;


