# 🔧 Solução: Token JWT Expirado

## ❌ Problema Identificado

Você está recebendo erro **500 Internal Server Error** porque o token JWT armazenado no frontend **expirou**.

### Detalhes do Erro:
```
io.jsonwebtoken.ExpiredJwtException: JWT expired 9473840 milliseconds ago
```

**Causa:** O token JWT tem validade de **24 horas**. Após esse período, ele expira e não pode mais ser usado.

---

## ✅ Solução Imediata

### 1. Faça Logout no Frontend

No console do navegador (F12), execute:

```javascript
// Remover token expirado
localStorage.removeItem('jwt_token');
localStorage.removeItem('user');

// Recarregar página
window.location.reload();
```

### 2. Faça Login Novamente

Acesse a tela de login e entre novamente com suas credenciais:

- **Email:** `admin@ticketsystem.com`
- **Senha:** `admin123`

Isso gerará um **novo token válido** por mais 24 horas.

---

## 🔧 Correção Aplicada no Backend

Modifiquei o `JwtAuthenticationFilter` para tratar corretamente tokens expirados:

**Antes (❌):**
- Token expirado causava erro 500
- Frontend não sabia que o problema era token expirado

**Agora (✅):**
- Token expirado retorna erro **401 Unauthorized**
- Resposta JSON clara: `{"error":"Token expirado","message":"Seu token JWT expirou. Por favor, faça login novamente."}`
- Frontend pode detectar e redirecionar para login automaticamente

---

## 🚀 Implementação no Frontend (Angular)

### Opção 1: Tratar Erro 401 no Interceptor

Modifique o `AuthInterceptor` para fazer logout automático quando o token expira:

```typescript
// auth.interceptor.ts
import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.authService.getToken();
    
    if (token) {
      req = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }
    
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        // Se token expirado ou inválido, fazer logout automático
        if (error.status === 401) {
          console.error('Token expirado ou inválido. Redirecionando para login...');
          this.authService.logout();
          this.router.navigate(['/login'], {
            queryParams: { expired: 'true' }
          });
        }
        return throwError(() => error);
      })
    );
  }
}
```

### Opção 2: Verificar Expiração Antes de Usar Token

Adicione método no `AuthService` para verificar se o token está expirado:

```typescript
// auth.service.ts
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly TOKEN_KEY = 'jwt_token';

  constructor(private router: Router) {}

  getToken(): string | null {
    const token = localStorage.getItem(this.TOKEN_KEY);
    
    if (token && this.isTokenExpired(token)) {
      console.warn('Token expirado. Fazendo logout...');
      this.logout();
      return null;
    }
    
    return token;
  }

  isTokenExpired(token: string): boolean {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const exp = payload.exp * 1000; // Converter para millisegundos
      const now = Date.now();
      
      return now >= exp;
    } catch (e) {
      console.error('Erro ao verificar expiração do token:', e);
      return true; // Se não conseguir verificar, considerar expirado
    }
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem('user');
    this.router.navigate(['/login']);
  }

  // ... outros métodos
}
```

### Opção 3: Exibir Mensagem de Sessão Expirada

No componente de login:

```typescript
// login.component.ts
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html'
})
export class LoginComponent implements OnInit {
  sessionExpired = false;

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    // Verificar se foi redirecionado por token expirado
    this.route.queryParams.subscribe(params => {
      this.sessionExpired = params['expired'] === 'true';
    });
  }
}
```

```html
<!-- login.component.html -->
<div class="alert alert-warning" *ngIf="sessionExpired">
  <strong>Sessão Expirada!</strong> Por favor, faça login novamente.
</div>
```

---

## 🕒 Informações sobre Validade do Token

### Configuração Atual

- **Validade:** 24 horas (86.400.000 millisegundos)
- **Configurável em:** `application.properties`

```properties
# Expiração do token (24 horas em milissegundos)
jwt.expiration=86400000
```

### Alterar Validade (Opcional)

Para alterar a validade do token, edite `application.properties`:

```properties
# 1 hora (3.600.000 ms)
jwt.expiration=3600000

# 7 dias (604.800.000 ms)
jwt.expiration=604800000

# 30 dias (2.592.000.000 ms)
jwt.expiration=2592000000
```

**⚠️ Atenção:** Tokens com validade muito longa são menos seguros!

---

## 📋 Checklist de Verificação

Quando encontrar erro de token expirado:

- [ ] Limpar localStorage do navegador
- [ ] Fazer login novamente
- [ ] Verificar se o novo token funciona
- [ ] Implementar tratamento de erro 401 no frontend
- [ ] Considerar implementar refresh token (melhoria futura)

---

## 🔄 Melhorias Futuras

### Refresh Token (Recomendado para Produção)

Implementar sistema de refresh token para renovar automaticamente tokens expirados:

1. **Access Token:** Curta duração (15 min - 1h)
2. **Refresh Token:** Longa duração (7 dias - 30 dias)
3. **Endpoint de Refresh:** `POST /api/auth/refresh`

Quando access token expira:
- Frontend usa refresh token para obter novo access token
- Usuário não precisa fazer login novamente
- Mais segurança + melhor UX

### Validação Prévia no Frontend

Verificar expiração do token antes de fazer requisições:

```typescript
// Antes de cada requisição
if (this.authService.isTokenExpired()) {
  this.authService.logout();
  return;
}
```

---

## 🧪 Testando a Correção

### 1. Simular Token Expirado

No navegador (Console F12):

```javascript
// Salvar um token JWT expirado
const expiredToken = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...'; // Token antigo
localStorage.setItem('jwt_token', expiredToken);
```

### 2. Fazer Requisição

Tente criar um ticket ou acessar qualquer endpoint protegido.

### 3. Verificar Resposta

**Antes da correção:**
```json
HTTP 500 Internal Server Error
{
  "error": "Internal Server Error",
  "message": "..."
}
```

**Após a correção:**
```json
HTTP 401 Unauthorized
{
  "error": "Token expirado",
  "message": "Seu token JWT expirou. Por favor, faça login novamente."
}
```

---

## 📞 Próximos Passos

1. **Imediato:** Faça login novamente no frontend
2. **Curto prazo:** Implemente tratamento de erro 401 no interceptor
3. **Médio prazo:** Considere implementar refresh token
4. **Longo prazo:** Adicione monitoramento de sessão

---

**Data da correção:** 6 de Janeiro de 2026  
**Versão da API:** 1.1.1
