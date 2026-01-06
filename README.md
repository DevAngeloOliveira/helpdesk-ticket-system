# 🎫 Sistema de Chamados (Ticketing System)

Sistema profissional de gerenciamento de chamados técnicos desenvolvido com Spring Boot 4.0.1 e Java 17, seguindo arquitetura em camadas, princípios SOLID, Clean Code e segurança com autenticação JWT.

## 📋 Índice

- [Visão Geral](#-visão-geral)
- [Tecnologias](#-tecnologias)
- [Funcionalidades](#-funcionalidades)
- [Arquitetura](#-arquitetura)
- [Modelo de Dados](#-modelo-de-dados)
- [Instalação](#-instalação)
- [Executando o Projeto](#️-executando-o-projeto)
- [Autenticação JWT](#-autenticação-jwt)
- [API Endpoints](#-api-endpoints)
- [Exemplos de Uso](#-exemplos-de-uso)
- [Regras de Negócio](#-regras-de-negócio)
- [Dados Iniciais](#-dados-iniciais)
- [Configuração](#️-configuração)
- [Segurança](#-segurança)
- [Documentação Técnica](#-documentação-técnica)
- [Melhorias Futuras](#-melhorias-futuras)

## 🎯 Visão Geral

Sistema completo de Service Desk/Help Desk que permite:

- ✅ Autenticação JWT stateless com Spring Security 7
- ✅ Cadastro e gerenciamento de usuários com diferentes perfis (Admin, Técnico, Usuário)
- ✅ Criptografia de senhas com BCrypt
- ✅ Abertura, categorização e priorização de chamados
- ✅ Atribuição de chamados a responsáveis
- ✅ Controle de status com histórico completo de mudanças (auditoria)
- ✅ Comunicação via comentários
- ✅ Gerenciamento de anexos
- ✅ Rastreabilidade completa de ações
- ✅ Validação robusta de dados com Bean Validation

## 🛠 Tecnologias

### Core
- **Java 17**
- **Spring Boot 4.0.1**
  - Spring Data JPA
  - Spring Web MVC
  - Spring Validation
  - Spring Security 7.0.2
  - Spring Boot DevTools

### Segurança
- **JSON Web Tokens (JWT)** - io.jsonwebtoken 0.12.6
- **BCrypt** - Criptografia de senhas

### Persistência
- **Hibernate 7.2.0** (ORM)
- **H2 Database** (Desenvolvimento em memória)
- **PostgreSQL** (Produção - Configurável)
- **HikariCP** (Connection Pool)

### Utilitários
- **Lombok** - Redução de boilerplate
- **Maven** - Gerenciador de dependências

## ⚡ Funcionalidades

### Autenticação e Autorização
- 🔐 Login com JWT (email + senha)
- 📝 Registro de novos usuários
- 🔑 Tokens JWT com expiração de 24 horas
- 🛡️ Proteção de endpoints por autenticação
- 👥 Controle de acesso baseado em roles (RBAC)

### Gestão de Chamados
- 📋 CRUD completo de tickets
- 🏷️ Categorização e priorização
- 👤 Atribuição a técnicos
- 📊 Controle de status workflow
- 📜 Histórico imutável de mudanças
- 💬 Sistema de comentários
- 📎 Gerenciamento de anexos

### Administração
- 👥 Gerenciamento de usuários
- 🔧 Configuração de categorias
- ⚡ Definição de prioridades
- 📊 Gestão de status personalizados

## 🏗 Arquitetura

O sistema segue arquitetura em camadas com separação clara de responsabilidades:

```
┌─────────────────────────────────────┐
│       Controllers (REST API)         │  ← Endpoints HTTP (JSON)
├─────────────────────────────────────┤
│    Security (JWT Filter + Config)    │  ← Autenticação/Autorização
├─────────────────────────────────────┤
│          Services (Lógica)           │  ← Regras de negócio
├─────────────────────────────────────┤
│      Repositories (Data Access)      │  ← Spring Data JPA
├─────────────────────────────────────┤
│         Entities (Modelos)           │  ← Mapeamento ORM
├─────────────────────────────────────┤
│         Database (H2/PostgreSQL)     │  ← Persistência
└─────────────────────────────────────┘
```

### Estrutura de Pacotes

```
io.github.angelo.TicketingSystem/
├── config/
│   ├── DataLoader.java          # Carga inicial de dados
│   └── SecurityConfig.java      # Configuração Spring Security
├── controller/
│   ├── HomeController.java      # Endpoint raiz
│   ├── AuthController.java      # Login/Register
│   ├── UserController.java      # CRUD Usuários
│   ├── TicketController.java    # CRUD Chamados
│   ├── CategoryController.java  # CRUD Categorias
│   ├── PriorityController.java  # CRUD Prioridades
│   ├── StatusController.java    # CRUD Status
│   ├── CommentController.java   # CRUD Comentários
│   └── AttachmentController.java# CRUD Anexos
├── dto/
│   ├── request/
│   │   ├── LoginRequest.java
│   │   ├── RegisterRequest.java
│   │   ├── UserRequest.java
│   │   ├── TicketRequest.java
│   │   ├── UpdateStatusRequest.java
│   │   └── ...
│   └── response/
│       ├── AuthResponse.java
│       ├── UserResponse.java
│       ├── TicketResponse.java
│       └── ...
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   ├── DuplicateResourceException.java
│   └── InvalidOperationException.java
├── model/
│   ├── User.java
│   ├── Ticket.java
│   ├── Status.java
│   ├── Priority.java
│   ├── Category.java
│   ├── StatusHistory.java
│   ├── Comment.java
│   ├── Attachment.java
│   └── enums/
│       └── UserRole.java
├── repository/
│   ├── UserRepository.java
│   ├── TicketRepository.java
│   ├── StatusRepository.java
│   ├── PriorityRepository.java
│   ├── CategoryRepository.java
│   ├── StatusHistoryRepository.java
│   ├── CommentRepository.java
│   └── AttachmentRepository.java
├── security/
│   ├── JwtService.java              # Geração/validação JWT
│   ├── JwtAuthenticationFilter.java # Filtro HTTP
│   ├── CustomUserDetailsService.java# UserDetailsService
│   └── AuthService.java             # Lógica de autenticação
└── service/
    ├── UserService.java
    ├── TicketService.java
    ├── CategoryService.java
    ├── PriorityService.java
    ├── StatusService.java
    ├── CommentService.java
    └── AttachmentService.java
```

## 📊 Modelo de Dados

### Diagrama Entidade-Relacionamento

```
┌─────────────┐       ┌──────────────┐       ┌─────────────┐
│    USER     │───┬───│   TICKET     │───┬───│  CATEGORY   │
└─────────────┘   │   └──────────────┘   │   └─────────────┘
                  │          │            │
                  │          │            │   ┌─────────────┐
                  │          │            └───│  PRIORITY   │
                  │          │                └─────────────┘
                  │          │
                  │          │                ┌─────────────┐
                  │          └────────────────│   STATUS    │
                  │                           └─────────────┘
                  │                                  │
┌─────────────┐   │          ┌──────────────┐       │
│   COMMENT   │───┘          │STATUS_HISTORY│───────┘
└─────────────┘              └──────────────┘

┌─────────────┐
│ ATTACHMENT  │──────────────► TICKET
└─────────────┘
```

### Entidades Principais

#### USER - Usuários do sistema
```sql
- id (PK, BIGINT, AUTO_INCREMENT)
- name (VARCHAR(100), NOT NULL)
- email (VARCHAR(150), UNIQUE, NOT NULL)
- password (VARCHAR(255), NOT NULL) -- BCrypt hash
- role (ENUM: ADMIN, TECHNICIAN, USER)
- created_at (TIMESTAMP, NOT NULL)
```

#### TICKET - Chamados
```sql
- id (PK, BIGINT, AUTO_INCREMENT)
- title (VARCHAR(200), NOT NULL)
- description (TEXT, NOT NULL)
- created_at (TIMESTAMP, NOT NULL)
- updated_at (TIMESTAMP)
- user_id (FK → USER, NOT NULL)       -- Criador
- assigned_to (FK → USER)             -- Responsável
- category_id (FK → CATEGORY, NOT NULL)
- priority_id (FK → PRIORITY, NOT NULL)
- status_id (FK → STATUS, NOT NULL)
```

#### STATUS - Estados do chamado
```sql
- id (PK, BIGINT, AUTO_INCREMENT)
- name (VARCHAR(50), UNIQUE, NOT NULL)
-- Exemplos: Open, In Progress, Resolved, Closed
```

#### PRIORITY - Níveis de prioridade
```sql
- id (PK, BIGINT, AUTO_INCREMENT)
- name (VARCHAR(50), UNIQUE, NOT NULL)
- level (INT, NOT NULL)
-- 0=Critical, 1=High, 2=Medium, 3=Low
```

#### CATEGORY - Categorias de chamados
```sql
- id (PK, BIGINT, AUTO_INCREMENT)
- name (VARCHAR(100), UNIQUE, NOT NULL)
- description (TEXT)
-- Exemplos: Hardware, Software, Network, Access
```

#### STATUS_HISTORY - Histórico de mudanças (Auditoria)
```sql
- id (PK, BIGINT, AUTO_INCREMENT)
- ticket_id (FK → TICKET, NOT NULL)
- old_status_id (FK → STATUS)         -- NULL na criação
- new_status_id (FK → STATUS, NOT NULL)
- changed_by (FK → USER, NOT NULL)    -- Quem mudou
- changed_at (TIMESTAMP, NOT NULL)
```

#### COMMENT - Comentários em chamados
```sql
- id (PK, BIGINT, AUTO_INCREMENT)
- message (TEXT, NOT NULL)
- ticket_id (FK → TICKET, NOT NULL)
- user_id (FK → USER, NOT NULL)
- created_at (TIMESTAMP, NOT NULL)
```

#### ATTACHMENT - Anexos
```sql
- id (PK, BIGINT, AUTO_INCREMENT)
- file_name (VARCHAR(255), NOT NULL)
- file_url (VARCHAR(500), NOT NULL)
- ticket_id (FK → TICKET, NOT NULL)
- uploaded_at (TIMESTAMP, NOT NULL)
```

## 🚀 Instalação

### Pré-requisitos

- ☕ **Java 17** ou superior ([Download](https://adoptium.net/))
- 📦 **Maven 3.6+** (ou use o wrapper incluído)
- 🔧 **Git** (opcional)

### Passos

1. **Clone o repositório**
```bash
git clone <repository-url>
cd TicketingSystem
```

2. **Compile o projeto**

**Windows:**
```cmd
.\mvnw.cmd clean install
```

**Linux/Mac:**
```bash
./mvnw clean install
```

## ▶️ Executando o Projeto

### Modo Desenvolvimento

**Windows:**
```cmd
.\mvnw.cmd spring-boot:run
```

**Linux/Mac:**
```bash
./mvnw spring-boot:run
```

### Executável JAR

```bash
# Gerar JAR
.\mvnw.cmd clean package

# Executar
java -jar target/TicketingSystem-0.0.1-SNAPSHOT.jar
```

### Acessos

- **🏠 Home**: `http://localhost:8080/`
- **🔐 API Auth**: `http://localhost:8080/api/auth`
- **📡 API Base**: `http://localhost:8080/api`
- **💾 H2 Console**: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:ticketdb`
  - Username: `sa`
  - Password: *(vazio)*

## 🔐 Autenticação JWT

O sistema utiliza autenticação stateless com JSON Web Tokens (JWT).

### Fluxo de Autenticação

```
┌─────────┐                          ┌─────────┐
│ Cliente │                          │ Servidor│
└────┬────┘                          └────┬────┘
     │                                    │
     │  POST /api/auth/login              │
     │  { email, password }               │
     │───────────────────────────────────>│
     │                                    │
     │         Valida credenciais         │
     │              BCrypt                │
     │                                    │
     │  200 OK                            │
     │  { token, type, user }             │
     │<───────────────────────────────────│
     │                                    │
     │  GET /api/tickets                  │
     │  Authorization: Bearer {token}     │
     │───────────────────────────────────>│
     │                                    │
     │      Valida JWT → Autentica        │
     │                                    │
     │  200 OK                            │
     │  [ tickets... ]                    │
     │<───────────────────────────────────│
```

### Registro de Usuário

**Endpoint:** `POST /api/auth/register`

**Request:**
```json
{
  "name": "João Silva",
  "email": "joao@example.com",
  "password": "senha123",
  "role": "USER"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "user": {
    "id": 5,
    "name": "João Silva",
    "email": "joao@example.com",
    "role": "USER",
    "createdAt": "2025-12-29T16:00:00"
  }
}
```

### Login

**Endpoint:** `POST /api/auth/login`

**Request:**
```json
{
  "email": "admin@ticketsystem.com",
  "password": "admin123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "user": {
    "id": 1,
    "name": "Admin User",
    "email": "admin@ticketsystem.com",
    "role": "ADMIN",
    "createdAt": "2025-12-29T15:00:00"
  }
}
```

### Usando o Token JWT

Inclua o token no header `Authorization` de todas as requisições protegidas:

```bash
curl -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
     http://localhost:8080/api/tickets
```

### Configuração JWT

As configurações podem ser alteradas em `application.properties`:

```properties
# Chave secreta (base64) - ALTERAR EM PRODUÇÃO!
jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970

# Expiração do token (24 horas em milissegundos)
jwt.expiration=86400000
```

### Endpoints Públicos (Sem Autenticação)

- `GET /` - Home
- `POST /api/auth/login` - Login
- `POST /api/auth/register` - Registro
- `GET /h2-console/**` - Console H2
- `GET /error` - Erros

### Endpoints Protegidos (Requerem JWT)

Todos os demais endpoints exigem autenticação via JWT:
- `/api/users/**`
- `/api/tickets/**`
- `/api/categories/**`
- `/api/priorities/**`
- `/api/statuses/**`
- `/api/comments/**`
- `/api/attachments/**`

## 📡 API Endpoints

### 🏠 Home

```http
GET /                           # Informações da API
```

### 🔐 Authentication

```http
POST /api/auth/register         # Registrar novo usuário
POST /api/auth/login            # Fazer login (obter JWT)
```

### 👤 Users

```http
POST   /api/users                    # Criar usuário
GET    /api/users                    # Listar todos
GET    /api/users/{id}               # Buscar por ID
GET    /api/users/email/{email}      # Buscar por email
PUT    /api/users/{id}               # Atualizar
DELETE /api/users/{id}               # Remover
```

### 🎫 Tickets

```http
POST   /api/tickets                    # Criar chamado
GET    /api/tickets                    # Listar todos
GET    /api/tickets/{id}               # Buscar por ID
GET    /api/tickets/user/{userId}      # Chamados do usuário
GET    /api/tickets/assigned/{userId}  # Chamados atribuídos
GET    /api/tickets/status/{statusId}  # Por status
PUT    /api/tickets/{id}               # Atualizar
PATCH  /api/tickets/status             # Atualizar status (cria histórico)
GET    /api/tickets/{id}/history       # Histórico de mudanças
DELETE /api/tickets/{id}               # Remover
```

### 🏷 Categories

```http
POST   /api/categories         # Criar categoria
GET    /api/categories         # Listar todas
GET    /api/categories/{id}    # Buscar por ID
PUT    /api/categories/{id}    # Atualizar
DELETE /api/categories/{id}    # Remover
```

### ⚡ Priorities

```http
POST   /api/priorities         # Criar prioridade
GET    /api/priorities         # Listar todas
GET    /api/priorities/{id}    # Buscar por ID
PUT    /api/priorities/{id}    # Atualizar
DELETE /api/priorities/{id}    # Remover
```

### 📊 Statuses

```http
POST   /api/statuses           # Criar status
GET    /api/statuses           # Listar todos
GET    /api/statuses/{id}      # Buscar por ID
PUT    /api/statuses/{id}      # Atualizar
DELETE /api/statuses/{id}      # Remover
```

### 💬 Comments

```http
POST   /api/comments                # Criar comentário
GET    /api/comments/{id}           # Buscar por ID
GET    /api/comments/ticket/{ticketId} # Comentários do chamado
DELETE /api/comments/{id}           # Remover
```

### 📎 Attachments

```http
POST   /api/attachments                # Adicionar anexo
GET    /api/attachments/{id}           # Buscar por ID
GET    /api/attachments/ticket/{ticketId} # Anexos do chamado
DELETE /api/attachments/{id}           # Remover
```

## 📝 Exemplos de Uso

### 1. Registrar e Fazer Login

```bash
# Registrar novo usuário
curl -X POST http://localhost:8080/api/auth/register \
-H "Content-Type: application/json" \
-d '{
  "name": "Maria Santos",
  "email": "maria@example.com",
  "password": "senha123",
  "role": "USER"
}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
-H "Content-Type: application/json" \
-d '{
  "email": "admin@ticketsystem.com",
  "password": "admin123"
}'

# Resposta contém o token JWT
# Copie o valor do campo "token" para usar nas próximas requisições
```

### 2. Criar Chamado (com JWT)

**⚠️ IMPORTANTE:** O usuário criador é obtido automaticamente do token JWT. Não é necessário enviar `userId`.

```bash
curl -X POST http://localhost:8080/api/tickets \
-H "Content-Type: application/json" \
-H "Authorization: Bearer SEU_TOKEN_AQUI" \
-d '{
  "title": "Computador não liga",
  "description": "Após atualização do Windows, o computador não inicia mais",
  "categoryId": 1,
  "priorityId": 2,
  "assignedToId": 2
}'
```

### 3. Listar Todos os Chamados

```bash
curl -X GET http://localhost:8080/api/tickets \
-H "Authorization: Bearer SEU_TOKEN_AQUI"
```

### 4. Atualizar Status do Chamado

```bash
curl -X PATCH http://localhost:8080/api/tickets/status \
-H "Content-Type: application/json" \
-H "Authorization: Bearer SEU_TOKEN_AQUI" \
-d '{
  "ticketId": 1,
  "newStatusId": 2,
  "changedByUserId": 2
}'
```

### 5. Adicionar Comentário

```bash
curl -X POST http://localhost:8080/api/comments \
-H "Content-Type: application/json" \
-H "Authorization: Bearer SEU_TOKEN_AQUI" \
-d '{
  "message": "Verificando o problema agora",
  "ticketId": 1,
  "userId": 2
}'
```

### 6. Ver Histórico de Mudanças

```bash
curl -X GET http://localhost:8080/api/tickets/1/history \
-H "Authorization: Bearer SEU_TOKEN_AQUI"
```

## 📐 Regras de Negócio

### RN-01: Integridade de Chamados
- ✅ Todo chamado deve ter: usuário criador, categoria, prioridade e status
- ✅ Não é possível criar chamado sem esses dados obrigatórios
- ✅ Validação realizada em tempo de criação

### RN-02: Histórico de Status (Auditoria)
```

### RN-02: Histórico de Status (Auditoria)
- ✅ **Toda** mudança de status gera registro histórico imutável
- ✅ Registra: status anterior, novo status, data e usuário responsável
- ✅ Permite auditoria completa das ações
- ✅ Histórico não pode ser editado ou deletado

### RN-03: Validações de Entrada
- ✅ Emails devem ser únicos no sistema
- ✅ Senhas: mínimo 6 caracteres
- ✅ Títulos de chamados: 5-200 caracteres
- ✅ Descrições: mínimo 10 caracteres
- ✅ Validação com Bean Validation (JSR 380)

### RN-04: Status Inicial
- ✅ Chamados criados automaticamente com status "Open"
- ✅ Se status "Open" não existir, usa o primeiro status cadastrado
- ✅ Mudança de status sempre cria entrada no histórico

### RN-05: Relacionamentos
- ✅ Comentários e anexos sempre vinculados a um chamado
- ✅ Ao deletar chamado, comentários e anexos são removidos (cascade)
- ✅ Histórico de status é preservado para auditoria

### RN-06: Segurança
- ✅ Senhas sempre armazenadas com BCrypt (custo 10)
- ✅ JWT com expiração de 24 horas
- ✅ Tokens stateless (sem sessão servidor)
- ✅ Proteção contra CSRF (desabilitado para API REST stateless)

### RN-07: Controle de Acesso
- ✅ Endpoints de autenticação são públicos
- ✅ Todos os demais endpoints requerem autenticação JWT
- ✅ Usuários só podem ver seus próprios dados (futura implementação de autorização)

## 🔐 Dados Iniciais

O sistema carrega automaticamente na primeira execução via `DataLoader`:

### 👥 Usuários Pré-cadastrados

| Nome          | Email                    | Senha    | Role       |
|---------------|--------------------------|----------|------------|
| Admin User    | admin@ticketsystem.com   | admin123 | ADMIN      |
| John Technician| tech@ticketsystem.com   | tech123  | TECHNICIAN |
| Alice Johnson | alice@example.com        | user123  | USER       |
| Bob Smith     | bob@example.com          | user123  | USER       |

> ⚠️ **Senhas armazenadas com BCrypt**: As senhas acima são criptografadas antes de serem salvas no banco.

### 📊 Status Padrões

1. **Open** - Chamado aberto
2. **In Progress** - Em andamento
3. **Resolved** - Resolvido
4. **Closed** - Fechado

### ⚡ Prioridades Padrões

| Nome     | Level | Descrição              |
|----------|-------|------------------------|
| Critical | 0     | Crítico - Urgente      |
| High     | 1     | Alta prioridade        |
| Medium   | 2     | Prioridade média       |
| Low      | 3     | Baixa prioridade       |

### 🏷 Categorias Padrões

1. **Hardware** - Problemas de equipamento
2. **Software** - Problemas de aplicativos
3. **Network** - Problemas de rede
4. **Access** - Problemas de acesso

### 🎫 Tickets de Demonstração

3 chamados de exemplo são criados automaticamente com:
- Diferentes combinações de usuários, categorias e prioridades
- Status inicial "Open"
- Histórico de mudanças de status

## ⚙️ Configuração

### application.properties

```properties
# ===== Configurações do Servidor =====
server.port=8080

# ===== Configurações do Banco H2 (Desenvolvimento) =====
spring.datasource.url=jdbc:h2:mem:ticketdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# ===== Console H2 =====
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# ===== JPA/Hibernate =====
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.open-in-view=true

# ===== JWT Configuration =====
jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
jwt.expiration=86400000

# ===== Logging =====
logging.level.io.github.angelo.TicketingSystem=INFO
logging.level.org.springframework.security=DEBUG
```

### Configuração PostgreSQL (Produção)

Para usar PostgreSQL em produção, altere em `application.properties`:

```properties
# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/ticketdb
spring.datasource.username=postgres
spring.datasource.password=sua_senha
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
```

### Variáveis de Ambiente (Produção)

```bash
# JWT Secret (MUDAR EM PRODUÇÃO!)
export JWT_SECRET=sua_chave_secreta_muito_forte_aqui

# Expiração JWT (24h em ms)
export JWT_EXPIRATION=86400000

# Database
export DB_URL=jdbc:postgresql://seu-servidor:5432/ticketdb
export DB_USERNAME=usuario
export DB_PASSWORD=senha
```

## 🛡 Segurança

### Boas Práticas Implementadas

✅ **Criptografia de Senhas**
- BCrypt com salt automático
- Custo de processamento: 10 rounds
- Senhas nunca armazenadas em texto plano

✅ **JWT Stateless**
- Tokens assinados com HMAC SHA-256
- Não armazena estado no servidor
- Expiração configurável (padrão 24h)

✅ **Spring Security**
- Proteção CSRF desabilitada (API REST stateless)
- Sessões stateless (STATELESS policy)
- Filtro de autenticação JWT
- Endpoints públicos e protegidos configuráveis

✅ **Validação de Dados**
- Bean Validation em todos os DTOs
- Validação de email, tamanhos, campos obrigatórios
- Mensagens de erro padronizadas

✅ **Tratamento de Erros**
- Global Exception Handler
- Respostas HTTP apropriadas
- Não expõe stack traces em produção

### Recomendações para Produção

⚠️ **IMPORTANTE - Antes de ir para produção:**

1. **Altere a chave JWT**
   ```properties
   # Gere uma chave forte de 256+ bits em Base64
   jwt.secret=SUA_CHAVE_SUPER_SEGURA_AQUI
   ```

2. **Use HTTPS**
   - Configure SSL/TLS
   - Redirecione HTTP para HTTPS

3. **Configure CORS**
   - Defina origins permitidas
   - Não use `*` em produção

4. **Banco de Dados**
   - Use PostgreSQL/MySQL
   - Configure backup automático
   - Use migrations (Flyway/Liquibase)

5. **Logging e Monitoramento**
   - Configure níveis de log apropriados
   - Use ferramentas de APM
   - Monitore tentativas de login falhas

6. **Rate Limiting**
   - Implemente limitação de taxa
   - Proteja contra brute force

7. **Auditoria**
   - Log de todas as ações sensíveis
   - Rastreamento de IP e User-Agent

## 📚 Documentação Técnica

Para documentação técnica detalhada, consulte:
- [DOCUMENTACAO_TECNICA.md](DOCUMENTACAO_TECNICA.md) - Arquitetura e design
- [API_EXAMPLES.http](API_EXAMPLES.http) - Exemplos de requisições HTTP

## 🧪 Testes

### Executar Testes

```bash
# Todos os testes
.\mvnw.cmd test

# Com cobertura
.\mvnw.cmd clean test jacoco:report
```

### Estrutura de Testes

```
src/test/java/
└── io.github.angelo.TicketingSystem/
    ├── controller/      # Testes de API (MockMvc)
    ├── service/         # Testes unitários
    └── integration/     # Testes de integração
```

## 🚀 Deploy

### Docker (Recomendado)

```dockerfile
# Dockerfile exemplo
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
```

Build e execução:
```bash
# Build
docker build -t ticketing-system .

# Run
docker run -p 8080:8080 \
  -e JWT_SECRET=sua_chave_aqui \
  -e DB_URL=jdbc:postgresql://db:5432/ticketdb \
  ticketing-system
```

### Docker Compose

```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - DB_URL=jdbc:postgresql://db:5432/ticketdb
      - DB_USERNAME=postgres
      - DB_PASSWORD=senha
      - JWT_SECRET=chave_secreta
    depends_on:
      - db
  
  db:
    image: postgres:15-alpine
    environment:
      - POSTGRES_DB=ticketdb
      - POSTGRES_PASSWORD=senha
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

---

## 📚 Documentação Técnica

Para informações técnicas detalhadas sobre arquitetura, implementação e especificações do sistema, consulte:

### [📖 Documentação Técnica Completa](DOCUMENTACAO_TECNICA.md)

A documentação técnica inclui:

- ✅ Requisitos funcionais e não-funcionais implementados (IEEE 830/29148)
- 🏗️ Arquitetura em camadas detalhada com diagramas
- 📦 Estrutura completa de pacotes e classes
- 🗄️ Modelo de dados com DDL e relacionamentos
- 🔄 Fluxos de dados e diagramas de sequência
- 🔐 Implementação de segurança (JWT, BCrypt, Spring Security 7)
- ⚡ Otimizações de performance
- 📊 Estatísticas do projeto (74 arquivos, 5.755 linhas)
- 🧪 Estratégias de testes
- 🚀 Configurações de produção
- 📈 Roadmap técnico

**Recomendado para:**
- Desenvolvedores que querem entender a implementação
- Arquitetos avaliando o design do sistema
- Tech leads planejando manutenção ou extensões
- Documentação de conformidade com requisitos

### [🔧 Guia de Integração Frontend](FRONTEND_INTEGRATION_GUIDE.md)

Guia completo para integração com aplicações frontend (Angular, React, Vue):

- ✅ **Correção aplicada:** "usuário não logado" ao criar tickets - Janeiro 2026
- 🔐 Como funciona a autenticação JWT passo a passo
- 🚀 Exemplos completos em TypeScript/Angular (prontos para usar)
- 📝 Services, Interceptors e Components completos
- 🧪 Testes de integração e troubleshooting
- ⚠️ Solução de problemas comuns
- 📋 Checklist de implementação

**Recomendado para:**
- Desenvolvedores frontend integrando com a API
- Resolução do erro "usuário não logado"
- Implementação correta de login e criação de tickets
- Entendimento do fluxo JWT completo

---

## 🎓 Melhorias Futuras

### Em Desenvolvimento
- [ ] Autorização granular por role (RBAC completo)
- [ ] Filtros avançados e paginação
- [ ] Ordenação customizável
- [ ] Busca full-text

### Planejadas
- [ ] Upload real de arquivos (S3/MinIO)
- [ ] Notificações por email (SMTP)
- [ ] Notificações em tempo real (WebSocket)
- [ ] SLA por prioridade
- [ ] Dashboard com métricas e KPIs
- [ ] Relatórios exportáveis (PDF/Excel)
- [ ] API de integração com sistemas externos
- [ ] Multitenancy (múltiplas organizações)
- [ ] Internacionalização (i18n)
- [ ] Tema claro/escuro
- [ ] Mobile app (React Native)

### Testes
- [ ] Cobertura de testes > 80%
- [ ] Testes de integração completos
- [ ] Testes end-to-end (E2E)
- [ ] Testes de carga/performance

### DevOps
- [ ] CI/CD com GitHub Actions
- [ ] Kubernetes deployment
- [ ] Helm charts
- [ ] Monitoring com Prometheus/Grafana
- [ ] Centralized logging (ELK Stack)
- [ ] Health checks e readiness probes

## 🤝 Contribuindo

Contribuições são bem-vindas! Por favor:

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/MinhaFeature`)
3. Commit suas mudanças (`git commit -m 'Adiciona MinhaFeature'`)
4. Push para a branch (`git push origin feature/MinhaFeature`)
5. Abra um Pull Request

### Padrões de Código

- Siga os princípios SOLID
- Use nomes descritivos
- Documente código complexo
- Escreva testes para novas features
- Mantenha cobertura de testes

## 📄 Licença

Este projeto está licenciado sob a **MIT License** - veja o arquivo [LICENSE](LICENSE) para mais detalhes.

**Resumo da Licença MIT:**
- ✅ Uso comercial permitido
- ✅ Modificação permitida
- ✅ Distribuição permitida
- ✅ Uso privado permitido
- ⚠️ Sem garantias
- ⚠️ Limitação de responsabilidade

**Projeto de Portfólio** - Desenvolvido para demonstração de habilidades técnicas em:
- Arquitetura de software enterprise
- Spring Boot e ecossistema Spring
- Segurança com JWT e Spring Security
- Design patterns e princípios SOLID
- APIs RESTful
- Persistência de dados com JPA/Hibernate

## 👨‍💻 Autor

**Angelo Oliveira**  
GitHub: [@DevAngeloOliveira](https://github.com/DevAngeloOliveira)

## 📞 Suporte

Para questões e suporte:
- 🐛 Issues: [GitHub Issues](https://github.com/DevAngeloOliveira/helpdesk-ticket-system/issues)
- 📖 Documentação: [README.md](README.md) | [DOCUMENTACAO_TECNICA.md](DOCUMENTACAO_TECNICA.md)

---

**Desenvolvido com ☕ Java e ❤️ Spring Boot**
