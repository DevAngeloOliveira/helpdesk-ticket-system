# 🔧 Guia de Integração Frontend - Sistema de Chamados

**Versão:** 2.0.0 | **Data:** 6 de Janeiro de 2026

## 📋 Índice

1. [Informações Gerais](#-informações-gerais)
2. [Autenticação](#-autenticação)
3. [Endpoints Completos](#-endpoints-completos)
4. [Modelos TypeScript](#-modelos-typescript)
5. [Integração Angular](#-integração-angular)
6. [Tratamento de Erros](#-tratamento-de-erros)
7. [Credenciais de Teste](#-credenciais-de-teste)

---

## 🌐 Informações Gerais

### Configuração
- **URL Base:** `http://localhost:8080`
- **Framework:** Spring Boot 4.0.1
- **Java:** 17
- **Database:** H2 in-memory
- **Autenticação:** JWT (24h expiração)
- **CORS:** Habilitado para `http://localhost:4200`

### ⚠️ Mudanças Recentes (Janeiro 2026)
1. ✅ **userId** não é mais obrigatório - extraído automaticamente do JWT
2. ✅ **description** não tem tamanho mínimo (antes: 10 caracteres)
3. ✅ Token expirado retorna 401 (antes: 500)

---

## 🔐 Autenticação

### Fluxo
1. Login → Obter token JWT
2. Incluir token em todas as requisições: `Authorization: Bearer {token}`
3. Backend extrai usuário do token automaticamente

### Endpoints

#### Registrar
```http
POST /api/auth/register
Content-Type: application/json

{
  "name": "João Silva",
  "email": "joao@example.com",
  "password": "senha123",
  "role": "USER"
}
```

**Response 201:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "type": "Bearer",
  "userId": 5,
  "name": "João Silva",
  "email": "joao@example.com",
  "role": "USER"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "admin@ticketsystem.com",
  "password": "admin123"
}
```

**Response 200:** Mesma estrutura do registro

---

## 📡 Endpoints Completos

### 🏠 Home
```http
GET /    # Informações da API (público)
```

### 👤 Usuários
```http
POST   /api/users                # Criar
GET    /api/users                # Listar todos
GET    /api/users/{id}           # Buscar por ID
GET    /api/users/email/{email}  # Buscar por email
PUT    /api/users/{id}           # Atualizar
DELETE /api/users/{id}           # Deletar
```

### 🎫 Tickets
```http
POST   /api/tickets                       # Criar
GET    /api/tickets                       # Listar todos
GET    /api/tickets/{id}                  # Buscar por ID
GET    /api/tickets/user/{userId}         # Por usuário criador
GET    /api/tickets/assigned/{userId}     # Por usuário atribuído
GET    /api/tickets/status/{statusId}     # Por status
PUT    /api/tickets/{id}                  # Atualizar
PATCH  /api/tickets/status                # Atualizar apenas status
GET    /api/tickets/{id}/history          # Histórico de mudanças
DELETE /api/tickets/{id}                  # Deletar
```

### 📁 Categorias
```http
POST   /api/categories       # Criar
GET    /api/categories       # Listar
GET    /api/categories/{id}  # Buscar
PUT    /api/categories/{id}  # Atualizar
DELETE /api/categories/{id}  # Deletar
```

### ⭐ Prioridades
```http
POST   /api/priorities       # Criar
GET    /api/priorities       # Listar
GET    /api/priorities/{id}  # Buscar
PUT    /api/priorities/{id}  # Atualizar
DELETE /api/priorities/{id}  # Deletar
```

### 📊 Status
```http
POST   /api/statuses       # Criar
GET    /api/statuses       # Listar
GET    /api/statuses/{id}  # Buscar
PUT    /api/statuses/{id}  # Atualizar
DELETE /api/statuses/{id}  # Deletar
```

### 💬 Comentários
```http
POST   /api/comments                   # Criar
GET    /api/comments/{id}              # Buscar
GET    /api/comments/ticket/{ticketId} # Listar por ticket
DELETE /api/comments/{id}              # Deletar
```

### 📎 Anexos
```http
POST   /api/attachments                   # Criar
GET    /api/attachments/{id}              # Buscar
GET    /api/attachments/ticket/{ticketId} # Listar por ticket
DELETE /api/attachments/{id}              # Deletar
```

---

## 📦 Modelos TypeScript

### Enums
```typescript
enum UserRole {
  ADMIN = 'ADMIN',
  TECHNICIAN = 'TECHNICIAN',
  USER = 'USER'
}
```

### Request Models

```typescript
interface TicketRequest {
  title: string;           // 5-200 caracteres
  description: string;     // Obrigatório (SEM mínimo)
  categoryId: number;
  priorityId: number;
  userId?: number;         // Opcional - obtido do JWT
  assignedToId?: number;
}

interface UserRequest {
  name: string;        // 3-100 caracteres
  email: string;       // Válido, máx 150
  password: string;    // Mín 6 caracteres
  role: UserRole;
}

interface CategoryRequest {
  name: string;            // 2-100 caracteres
  description?: string;    // Máx 500
}

interface PriorityRequest {
  name: string;      // 2-50 caracteres
  level: number;     // Positivo
}

interface StatusRequest {
  name: string;      // 2-50 caracteres
}

interface CommentRequest {
  message: string;
  ticketId: number;
  userId: number;
}

interface StatusUpdateRequest {
  ticketId: number;
  newStatusId: number;
  changedByUserId: number;
}

interface AttachmentRequest {
  fileName: string;     // Máx 255
  fileUrl: string;      // Máx 500
  ticketId: number;
}
```

### Response Models

```typescript
interface AuthResponse {
  token: string;
  type: string;        // "Bearer"
  userId: number;
  name: string;
  email: string;
  role: UserRole;
}

interface UserResponse {
  id: number;
  name: string;
  email: string;
  role: UserRole;
  createdAt: string;   // ISO 8601
}

interface TicketResponse {
  id: number;
  title: string;
  description: string;
  createdAt: string;
  updatedAt: string | null;
  user: UserResponse;
  assignedTo: UserResponse | null;
  category: CategoryResponse;
  priority: PriorityResponse;
  status: StatusResponse;
}

interface CategoryResponse {
  id: number;
  name: string;
  description: string | null;
}

interface PriorityResponse {
  id: number;
  name: string;
  level: number;
}

interface StatusResponse {
  id: number;
  name: string;
}

interface CommentResponse {
  id: number;
  message: string;
  createdAt: string;
  ticketId: number;
  user: UserResponse;
}

interface StatusHistoryResponse {
  id: number;
  changedAt: string;
  ticketId: number;
  oldStatus: StatusResponse | null;
  newStatus: StatusResponse;
  changedBy: UserResponse;
}

interface AttachmentResponse {
  id: number;
  fileName: string;
  fileUrl: string;
  uploadedAt: string;
  ticketId: number;
}

interface ErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  details?: string[];
}
```

---

## 🚀 Integração Angular

### 1. Auth Service

```typescript
// auth.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly API_URL = 'http://localhost:8080/api/auth';
  private readonly TOKEN_KEY = 'jwt_token';

  constructor(private http: HttpClient) {}

  login(email: string, password: string): Observable<any> {
    return this.http.post(`${this.API_URL}/login`, { email, password })
      .pipe(tap(res => {
        localStorage.setItem(this.TOKEN_KEY, res.token);
        localStorage.setItem('user', JSON.stringify(res));
      }));
  }

  logout(): void {
    localStorage.clear();
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  getCurrentUser(): any {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }
}
```

### 2. HTTP Interceptor

```typescript
// auth.interceptor.ts
import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const token = authService.getToken();
  
  const authReq = token 
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;
  
  return next(authReq).pipe(
    catchError(error => {
      if (error.status === 401) {
        authService.logout();
        router.navigate(['/login']);
      }
      return throwError(() => error);
    })
  );
};
```

**app.config.ts:**
```typescript
import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { authInterceptor } from './interceptors/auth.interceptor';
import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor]))
  ]
};
```

### 3. Ticket Service

```typescript
// ticket.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class TicketService {
  private readonly API = 'http://localhost:8080/api/tickets';

  constructor(private http: HttpClient) {}

  create(ticket: any): Observable<any> {
    // userId NÃO é enviado - obtido do JWT
    return this.http.post(this.API, ticket);
  }

  getAll(): Observable<any[]> {
    return this.http.get<any[]>(this.API);
  }

  getById(id: number): Observable<any> {
    return this.http.get(`${this.API}/${id}`);
  }

  update(id: number, ticket: any): Observable<any> {
    return this.http.put(`${this.API}/${id}`, ticket);
  }

  updateStatus(request: any): Observable<any> {
    return this.http.patch(`${this.API}/status`, request);
  }

  getHistory(id: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.API}/${id}/history`);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API}/${id}`);
  }
}
```

### 4. Component Exemplo

```typescript
// create-ticket.component.ts
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { TicketService } from '../services/ticket.service';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-create-ticket',
  templateUrl: './create-ticket.component.html'
})
export class CreateTicketComponent implements OnInit {
  form!: FormGroup;
  user: any;

  constructor(
    private fb: FormBuilder,
    private ticketService: TicketService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.user = this.authService.getCurrentUser();
    
    this.form = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(5)]],
      description: ['', Validators.required], // SEM mínimo
      categoryId: ['', Validators.required],
      priorityId: ['', Validators.required],
      assignedToId: ['']
    });
  }

  submit(): void {
    if (this.form.valid) {
      this.ticketService.create(this.form.value).subscribe({
        next: () => this.router.navigate(['/tickets']),
        error: (err) => {
          if (err.status === 401) {
            this.authService.logout();
            this.router.navigate(['/login']);
          }
        }
      });
    }
  }
}
```

---

## ⚠️ Tratamento de Erros

### Códigos HTTP

| Código | Descrição | Ação |
|--------|-----------|------|
| 200 | OK | Sucesso (GET, PUT, PATCH) |
| 201 | Created | Sucesso (POST) |
| 204 | No Content | Sucesso (DELETE) |
| 400 | Bad Request | Validação falhou |
| 401 | Unauthorized | Token inválido/expirado → Logout |
| 404 | Not Found | Recurso não existe |
| 409 | Conflict | Duplicado (ex: email) |
| 500 | Server Error | Erro interno |

### Exemplo 400 - Validação
```json
{
  "timestamp": "2026-01-06T16:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/tickets",
  "details": [
    "Title is required",
    "Category ID is required"
  ]
}
```

### Exemplo 401 - Token Expirado
```json
{
  "error": "Token expirado",
  "message": "Seu token JWT expirou. Por favor, faça login novamente."
}
```

---

## 🔑 Credenciais de Teste

### Usuários

| Nome | Email | Senha | Role | ID |
|------|-------|-------|------|---|
| Admin User | admin@ticketsystem.com | admin123 | ADMIN | 1 |
| John Technician | tech@ticketsystem.com | tech123 | TECHNICIAN | 2 |
| Alice User | alice@example.com | user123 | USER | 3 |
| Bob User | bob@example.com | user123 | USER | 4 |

### Dados Pré-cadastrados

**Categorias:**
- Hardware (1), Software (2), Network (3), Access (4)

**Prioridades:**
- Critical/1 (1), High/2 (2), Medium/3 (3), Low/4 (4)

**Status:**
- Open (1), In Progress (2), Resolved (3), Closed (4)

---

## 🧪 Testes com cURL

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@ticketsystem.com","password":"admin123"}'
```

### Criar Ticket
```bash
TOKEN="eyJhbGciOiJIUzI1NiIs..."

curl -X POST http://localhost:8080/api/tickets \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title": "Sistema lento",
    "description": "teste",
    "categoryId": 2,
    "priorityId": 2
  }'
```

### Listar Tickets
```bash
curl -X GET http://localhost:8080/api/tickets \
  -H "Authorization: Bearer $TOKEN"
```

---

## ✅ Checklist

- [ ] AuthService implementado (login, logout, getToken)
- [ ] HTTP Interceptor configurado
- [ ] Services criados (Ticket, Category, Priority, etc.)
- [ ] **NÃO** enviar userId ao criar tickets
- [ ] Tratar erro 401 com redirect para login
- [ ] Testar com descrição curta (sem mínimo)
- [ ] Validar CORS para localhost:4200

---

## 📚 Recursos

- **H2 Console:** http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:ticketdb`
  - Username: `sa`
  - Password: (vazio)

- **Documentos:**
  - README.md - Documentação completa
  - API_EXAMPLES.http - Exemplos de requisições

---

**Principais Mudanças:**
- ✅ userId opcional (JWT automático)
- ✅ Descrição sem mínimo
- ✅ Token expirado retorna 401
- ✅ Documentação completa
- ✅ Exemplos Angular 17+
