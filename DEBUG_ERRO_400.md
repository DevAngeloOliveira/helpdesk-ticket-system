# 🔍 Debug: Erro 400 ao Criar Ticket

## ❌ Problema Atual

**Erro:** `POST http://localhost:8080/api/tickets 400 (Bad Request)`

Isso significa que a requisição tem **dados inválidos** segundo as validações do backend.

---

## 🔧 Diagnóstico Rápido

### 1. Ver Detalhes do Erro no Console (Angular)

Abra o console do navegador (F12) e adicione este código temporário no seu component:

```typescript
// ticket-form.component.ts - onSubmit()
onSubmit(): void {
  if (this.ticketForm.invalid) {
    console.error('Formulário inválido:', this.ticketForm.errors);
    console.error('Valores:', this.ticketForm.value);
    return;
  }

  const ticketData = this.ticketForm.value;
  
  // 🔍 DEBUG: Ver exatamente o que está sendo enviado
  console.log('📤 Dados sendo enviados:', JSON.stringify(ticketData, null, 2));

  this.ticketService.createTicket(ticketData).subscribe({
    next: (response) => {
      console.log('✅ Ticket criado:', response);
      this.router.navigate(['/tickets']);
    },
    error: (error) => {
      console.error('❌ Erro completo:', error);
      console.error('📋 Resposta do servidor:', error.error);
      console.error('📊 Status:', error.status);
      console.error('📝 Mensagem:', error.error?.message);
      console.error('🔍 Detalhes validação:', error.error?.details);
      
      if (error.status === 400) {
        this.errorMessage = 'Dados inválidos: ' + JSON.stringify(error.error?.details || error.error?.message);
      }
      // ... resto do tratamento
    }
  });
}
```

### 2. Verificar Valores Sendo Enviados

Execute e verifique o console. Você deve ver algo como:

```json
📤 Dados sendo enviados: {
  "title": "Teste",
  "description": "Descrição do problema",
  "categoryId": "1",     // ⚠️ String ao invés de número!
  "priorityId": "2",     // ⚠️ String ao invés de número!
  "assignedToId": ""     // ⚠️ String vazia ao invés de null!
}
```

---

## ✅ Soluções Possíveis

### Problema 1: Valores String ao invés de Number

**Causa:** O formulário HTML retorna strings dos `<select>`, mas o backend espera números.

**Solução:** Converter para números antes de enviar:

```typescript
// ticket.service.ts ou component
onSubmit(): void {
  if (this.ticketForm.invalid) {
    return;
  }

  const formValue = this.ticketForm.value;
  
  // ✅ Converter strings para números
  const ticketData = {
    title: formValue.title,
    description: formValue.description,
    categoryId: Number(formValue.categoryId),
    priorityId: Number(formValue.priorityId),
    assignedToId: formValue.assignedToId ? Number(formValue.assignedToId) : null
  };

  this.ticketService.createTicket(ticketData).subscribe({
    // ... handlers
  });
}
```

### Problema 2: String Vazia ao invés de Null

**Causa:** Campo opcional `assignedToId` está sendo enviado como `""` (string vazia).

**Solução:** Converter string vazia para `null` ou `undefined`:

```typescript
const ticketData = {
  title: formValue.title,
  description: formValue.description,
  categoryId: Number(formValue.categoryId),
  priorityId: Number(formValue.priorityId),
  assignedToId: formValue.assignedToId && formValue.assignedToId !== '' 
    ? Number(formValue.assignedToId) 
    : undefined  // ou null
};
```

### Problema 3: Campos Vazios

**Causa:** Título ou descrição vazios.

**Solução:** Garantir validação no formulário:

```typescript
this.ticketForm = this.fb.group({
  title: ['', [
    Validators.required, 
    Validators.minLength(5), 
    Validators.maxLength(200)
  ]],
  description: ['', [
    Validators.required, 
    Validators.minLength(10)
  ]],
  categoryId: ['', Validators.required],  // ⚠️ Valor inicial vazio
  priorityId: ['', Validators.required],  // ⚠️ Valor inicial vazio
  assignedToId: ['']  // Opcional
});
```

---

## 🔧 Solução Completa Recomendada

### Component Corrigido

```typescript
// create-ticket.component.ts
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { TicketService } from './ticket.service';
import { AuthService } from './auth.service';

@Component({
  selector: 'app-create-ticket',
  templateUrl: './create-ticket.component.html'
})
export class CreateTicketComponent implements OnInit {
  ticketForm!: FormGroup;
  currentUser: any;
  errorMessage: string = '';
  isSubmitting = false;

  constructor(
    private fb: FormBuilder,
    private ticketService: TicketService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    
    if (!this.currentUser) {
      this.router.navigate(['/login']);
      return;
    }

    this.ticketForm = this.fb.group({
      title: ['', [
        Validators.required, 
        Validators.minLength(5), 
        Validators.maxLength(200)
      ]],
      description: ['', [
        Validators.required, 
        Validators.minLength(10)
      ]],
      categoryId: [null, Validators.required],  // ✅ null ao invés de ''
      priorityId: [null, Validators.required],  // ✅ null ao invés de ''
      assignedToId: [null]  // ✅ null ao invés de ''
    });
  }

  onSubmit(): void {
    // Marcar todos os campos como touched para mostrar erros
    if (this.ticketForm.invalid) {
      Object.keys(this.ticketForm.controls).forEach(key => {
        this.ticketForm.get(key)?.markAsTouched();
      });
      this.errorMessage = 'Por favor, preencha todos os campos obrigatórios corretamente.';
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';

    const formValue = this.ticketForm.value;
    
    // ✅ Construir objeto com conversão de tipos
    const ticketData = {
      title: formValue.title?.trim(),
      description: formValue.description?.trim(),
      categoryId: formValue.categoryId ? Number(formValue.categoryId) : null,
      priorityId: formValue.priorityId ? Number(formValue.priorityId) : null,
      assignedToId: formValue.assignedToId ? Number(formValue.assignedToId) : undefined
    };

    // Remover campos undefined
    Object.keys(ticketData).forEach(key => 
      ticketData[key] === undefined && delete ticketData[key]
    );

    console.log('📤 Enviando ticket:', ticketData);

    this.ticketService.createTicket(ticketData).subscribe({
      next: (response) => {
        console.log('✅ Ticket criado com sucesso:', response);
        this.isSubmitting = false;
        this.router.navigate(['/tickets']);
      },
      error: (error) => {
        console.error('❌ Erro ao criar ticket:', error);
        this.isSubmitting = false;
        
        if (error.status === 400) {
          // Erro de validação
          const details = error.error?.details;
          if (details) {
            const errorMessages = Object.values(details).join(', ');
            this.errorMessage = `Dados inválidos: ${errorMessages}`;
          } else {
            this.errorMessage = error.error?.message || 'Dados inválidos. Verifique os campos.';
          }
        } else if (error.status === 401) {
          this.errorMessage = 'Sessão expirada. Faça login novamente.';
          this.authService.logout();
          this.router.navigate(['/login']);
        } else {
          this.errorMessage = error.error?.message || 'Erro ao criar ticket. Tente novamente.';
        }
      }
    });
  }

  // Helper para exibir erros de validação
  getErrorMessage(fieldName: string): string {
    const control = this.ticketForm.get(fieldName);
    if (control?.hasError('required')) {
      return 'Este campo é obrigatório';
    }
    if (control?.hasError('minlength')) {
      const minLength = control.errors?.['minlength'].requiredLength;
      return `Mínimo de ${minLength} caracteres`;
    }
    if (control?.hasError('maxlength')) {
      const maxLength = control.errors?.['maxlength'].requiredLength;
      return `Máximo de ${maxLength} caracteres`;
    }
    return '';
  }
}
```

### HTML Melhorado

```html
<!-- create-ticket.component.html -->
<div class="container">
  <h2>Criar Novo Chamado</h2>
  
  <div class="user-info" *ngIf="currentUser">
    <p><strong>Criado por:</strong> {{ currentUser.name }} ({{ currentUser.email }})</p>
  </div>

  <div class="alert alert-danger" *ngIf="errorMessage">
    {{ errorMessage }}
  </div>

  <form [formGroup]="ticketForm" (ngSubmit)="onSubmit()">
    <!-- Título -->
    <div class="form-group">
      <label for="title">Título *</label>
      <input 
        type="text" 
        id="title" 
        class="form-control" 
        [class.is-invalid]="ticketForm.get('title')?.invalid && ticketForm.get('title')?.touched"
        formControlName="title"
        placeholder="Descreva o problema brevemente"
      />
      <div class="invalid-feedback" *ngIf="ticketForm.get('title')?.invalid && ticketForm.get('title')?.touched">
        {{ getErrorMessage('title') }}
      </div>
    </div>

    <!-- Descrição -->
    <div class="form-group">
      <label for="description">Descrição *</label>
      <textarea 
        id="description" 
        class="form-control"
        [class.is-invalid]="ticketForm.get('description')?.invalid && ticketForm.get('description')?.touched"
        formControlName="description"
        rows="5"
        placeholder="Descreva o problema em detalhes"
      ></textarea>
      <div class="invalid-feedback" *ngIf="ticketForm.get('description')?.invalid && ticketForm.get('description')?.touched">
        {{ getErrorMessage('description') }}
      </div>
    </div>

    <!-- Categoria -->
    <div class="form-group">
      <label for="categoryId">Categoria *</label>
      <select 
        id="categoryId" 
        class="form-control"
        [class.is-invalid]="ticketForm.get('categoryId')?.invalid && ticketForm.get('categoryId')?.touched"
        formControlName="categoryId"
      >
        <option [ngValue]="null">Selecione...</option>
        <option [ngValue]="1">Hardware</option>
        <option [ngValue]="2">Software</option>
        <option [ngValue]="3">Network</option>
        <option [ngValue]="4">Access</option>
      </select>
      <div class="invalid-feedback" *ngIf="ticketForm.get('categoryId')?.invalid && ticketForm.get('categoryId')?.touched">
        Categoria é obrigatória
      </div>
    </div>

    <!-- Prioridade -->
    <div class="form-group">
      <label for="priorityId">Prioridade *</label>
      <select 
        id="priorityId" 
        class="form-control"
        [class.is-invalid]="ticketForm.get('priorityId')?.invalid && ticketForm.get('priorityId')?.touched"
        formControlName="priorityId"
      >
        <option [ngValue]="null">Selecione...</option>
        <option [ngValue]="1">Crítica</option>
        <option [ngValue]="2">Alta</option>
        <option [ngValue]="3">Média</option>
        <option [ngValue]="4">Baixa</option>
      </select>
      <div class="invalid-feedback" *ngIf="ticketForm.get('priorityId')?.invalid && ticketForm.get('priorityId')?.touched">
        Prioridade é obrigatória
      </div>
    </div>

    <!-- Atribuir a (opcional) -->
    <div class="form-group">
      <label for="assignedToId">Atribuir a</label>
      <select id="assignedToId" class="form-control" formControlName="assignedToId">
        <option [ngValue]="null">Não atribuído</option>
        <option [ngValue]="2">John Technician</option>
      </select>
    </div>

    <!-- Botões -->
    <div class="form-actions">
      <button 
        type="submit" 
        class="btn btn-primary" 
        [disabled]="isSubmitting"
      >
        <span *ngIf="!isSubmitting">Criar Chamado</span>
        <span *ngIf="isSubmitting">Criando...</span>
      </button>
      <button 
        type="button" 
        class="btn btn-secondary" 
        (click)="router.navigate(['/tickets'])"
        [disabled]="isSubmitting"
      >
        Cancelar
      </button>
    </div>
  </form>
</div>
```

---

## 🔑 Pontos-Chave da Correção

### 1. `[ngValue]` ao invés de `value`

```html
<!-- ❌ Errado - retorna string -->
<option value="1">Hardware</option>

<!-- ✅ Correto - retorna número -->
<option [ngValue]="1">Hardware</option>
```

### 2. Valor Inicial `null` ao invés de `''`

```typescript
// ❌ Errado
categoryId: ['', Validators.required]

// ✅ Correto
categoryId: [null, Validators.required]
```

### 3. Conversão Explícita de Tipos

```typescript
// ✅ Garantir que são números
categoryId: Number(formValue.categoryId)
priorityId: Number(formValue.priorityId)
```

---

## 🧪 Teste Após Correção

1. Faça login novamente (se necessário)
2. Abra o formulário de criar ticket
3. Preencha todos os campos
4. Clique em "Criar Chamado"
5. Verifique no console:
   - `📤 Enviando ticket:` deve mostrar números, não strings
   - `✅ Ticket criado com sucesso:` deve aparecer

---

## 📊 Formato Esperado pelo Backend

```json
{
  "title": "Meu problema",
  "description": "Descrição detalhada do problema",
  "categoryId": 1,          // ✅ Número
  "priorityId": 2,          // ✅ Número
  "assignedToId": 2         // ✅ Número ou ausente (não string vazia!)
}
```

---

**Se o problema persistir, copie e cole aqui:**
1. O que aparece em `📤 Enviando ticket:`
2. O que aparece em `📋 Resposta do servidor:`
