# PetCare API 🐾

API REST para gerenciamento de **Tutores**, **Pets**, **Adoções** e **Cuidados** (banho, tosa, vacina, etc.), com regras de negócio e testes automatizados.

---

## 🚀 Tecnologias

- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA
- Jakarta Validation
- PostgreSQL
- Swagger / OpenAPI (springdoc)
- JUnit 5 + Mockito + MockMvc (testes)

---

## ✅ Funcionalidades

### 👤 Tutores
- Criar, listar, buscar por id, atualizar e deletar tutor.

### 🐶 Pets
- CRUD de pets (com regras de negócio)
- Listar pets disponíveis/adotados
- Adoção e devolução
- Listar pets por tutor

### 🤝 Adoções
- Histórico de adoções por pet
- Histórico de adoções por tutor
- Controle de adoção **ATIVA** e encerramento na devolução

### 🧼 Cuidados
- Registrar cuidados do pet (vacina, banho, etc.)
- Listar cuidados por pet
- Listar cuidados por tipo
- Listar cuidados por pet e tipo

---

## 📌 Regras de Negócio (principais)

- Pet **não pode** ser criado como `ADOTADO`.
- Pet **não pode** ser criado já com `tutor`.
- Alterar **status** e **tutor** do pet só via:
    - `PUT /pets/{idPet}/adotar?tutorId=...`
    - `PUT /pets/{idPet}/devolver`
- Um pet não pode ter mais de uma adoção **ATIVA** ao mesmo tempo.
- Deleção:
    - **Tutor** não pode ser deletado se tiver pets vinculados.
    - **Pet** só deve ser deletado se respeitar as dependências (adoções/cuidados) conforme regra adotada no projeto.

---

## ▶️ Como rodar localmente

### 1) Pré-requisitos

- Java 17 instalado
- Maven instalado
- PostgreSQL rodando

---

### 2) Criar o banco de dados

No PostgreSQL, crie o banco:

```sql
CREATE DATABASE petcare;

```

### 3) Configurar o application.properties

Edite o arquivo em:

src/main/resources/application.properties

Exemplo:

spring.datasource.url=jdbc:postgresql://localhost:5432/petcare
spring.datasource.username=SEU_USUARIO
spring.datasource.password=SUA_SENHA

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

### Swagger (opcional, mas recomendado)
springdoc.swagger-ui.path=/swagger-ui.html


### 4) Rodar a aplicação 

Na raiz do projeto:

mvn clean package
mvn spring-boot:run


A aplicação normalmente sobe em:

http://localhost:8080

### 📚 Documentação Swagger (OpenAPI)

Com o projeto rodando, acesse:

Swagger UI:
http://localhost:8080/swagger-ui/index.html

(Dependendo da config, também pode funcionar:)

http://localhost:8080/swagger-ui.html

### 🧪 Testes Automatizados

Para rodar os testes:

mvn test

Para rodar testes e gerar o build:

mvn clean package


### O projeto utiliza testes de:

Service (unit) com Mockito

Controller (WebMvcTest) com MockMvc

### 📦 Exportar rotas do Postman (para versionar no repositório)

Abra o Postman

Vá em Collections

Selecione sua collection do projeto

Clique em … (três pontos) → Export

Selecione Collection v2.1 (recommended)

Salve o arquivo como, por exemplo:

postman/PetCare.postman_collection.json

Depois, você pode versionar isso junto do projeto (idealmente numa pasta postman/).

### 🤖 Evolução futura (IA: “match” Tutor x Pet)

Uma evolução planejada é criar um recurso de recomendação (“match”) entre perfil do tutor e perfil do pet, por exemplo:

Preferência do tutor (porte, espécie, rotina, tempo disponível, crianças em casa, etc.)

Necessidades do pet (energia, idade, cuidados, comportamento)

A ideia seria expor um endpoint como:

GET /recomendacoes?tutorId=...

E retornar uma lista ordenada de pets recomendados com “pontuação” e justificativa.

### 📌 Projeto PetCare

Projeto desenvolvido para demonstrar:

Modelagem simples (Tutor, Pet, Adoção, Cuidado)

Regras de negócio no Service

API REST com controllers claros

Tratamento padronizado de erros (exceptions)

Testes automatizados (services e controllers)

Documentação via Swagger/OpenAPI
