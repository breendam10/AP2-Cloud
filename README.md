# AP2 Projeto Cloud

### 🌐 API de Trading Bot (Spring Boot + Swagger)

Este projeto expõe uma API RESTful para gerenciamento de usuários, configurações, moedas ativas e ordens de trading integradas à Binance.

---

## 🔗 Painel Swagger

- **Swagger UI:**
  Acesse a documentação interativa e teste os endpoints em:
  ```
  http://localhost/swagger-ui.html
  ```
  ou
  ```
  http://localhost/swagger-ui/index.html
  ```

---

## 🧑‍💼 Usuários

### Criar Usuário

- ✅ **POST /usuario**
  ```json
  {
    "usuario_login": "usuario1",
    "usuario_senha": "senha123",
    "usuario_binanceApiKey": "API_KEY",
    "usuario_binanceSecretKey": "SECRET_KEY",
    "valor_compra": 100.0,
    "pct_ganho": 5.0,
    "pct_perda": 2.0
  }
  ```
  **Descrição:** Cria um novo usuário e configurações iniciais.

  **Resposta (201):**
  ```json
  {
    "message": "Usuário criado com sucesso",
    "usuario_id": 1
  }
  ```
  **Erros:**
  - 400: Usuário já existe

---

### Login

- ✅ **POST /usuario/login**
  ```json
  {
    "usuario_login": "usuario1",
    "usuario_senha": "senha123"
  }
  ```
  **Descrição:** Realiza login do usuário.

  **Resposta (200):**
  ```json
  {
    "success": true,
    "usuario_id": 1,
    "message": "Login efetuado com sucesso"
  }
  ```
  **Erros:**
  - 401: Credenciais inválidas

---

### Obter Detalhes do Usuário

- 🔍 **GET /usuario/{usuario_id}**
  **Descrição:** Retorna detalhes do usuário.
  **Resposta (200):**
  ```json
  {
    "usuario_id": 1,
    "usuario_login": "usuario1",
    "usuario_saldo": 0.0,
    "has_binance_keys": true
  }
  ```
  **Erros:**
  - 404: Usuário não encontrado

---

### Excluir Usuário

- 🗑 **DELETE /usuario/{usuario_id}**
  **Descrição:** Remove o usuário e todos os dados associados.
  **Resposta (200):**
  ```json
  {
    "message": "Usuário excluído com sucesso"
  }
  ```
  **Erros:**
  - 404: Usuário não encontrado

---

### Configurações do Usuário

- 🔍 **GET /usuario/{usuario_id}/config**
- 🔄 **PUT /usuario/{usuario_id}/config**
  ```json
  {
    "valor_compra": 100.0,
    "pct_ganho": 5.0,
    "pct_perda": 2.0
  }
  ```
  **Descrição:** Obtém ou atualiza as configurações de trading do usuário.
  **Resposta (200):**
  ```json
  {
    "valor_compra": 100.0,
    "pct_ganho": 5.0,
    "pct_perda": 2.0
  }
  ```
  **Erros:**
  - 404: Usuário ou configuração não encontrada

---

## 💱 Moedas Ativas

### Listar Pares de Trading Disponíveis

- 🔍 **GET /moedas/pares**
  **Descrição:** Lista todos os pares de trading disponíveis na Binance.
  **Resposta (200):**
  ```json
  {
    "success": true,
    "trading_pairs": ["BTCUSDT", "ETHUSDT", "..."],
    "count": 2
  }
  ```

---

### Listar Moedas Ativas do Usuário

- 🔍 **GET /moedas/{usuario_id}**
  **Descrição:** Lista as moedas monitoradas pelo usuário.
  **Resposta (200):**
  ```json
  {
    "total": 1,
    "moedas": [
      {
        "id": 1,
        "simbolo": "BTCUSDT",
        "ultimo_preco": 68000.0
      }
    ]
  }
  ```
  **Erros:**
  - 404: Usuário não encontrado
  - 400: Chaves da Binance não configuradas

---

### Adicionar Moeda Ativa

- ✅ **POST /moedas/{usuario_id}**
  ```json
  {
    "simbolo": "BTCUSDT"
  }
  ```
  ou
  ```json
  {
    "simbolos": ["BTCUSDT", "ETHUSDT"]
  }
  ```
  **Descrição:** Adiciona uma ou mais moedas à lista de monitoramento do usuário.
  **Resposta (201):**
  ```json
  {
    "message": "2 moeda(s) adicionada(s) com sucesso",
    "adicionadas": ["BTCUSDT", "ETHUSDT"],
    "ignoradas": []
  }
  ```
  **Erros:**
  - 404: Usuário não encontrado
  - 400: Nenhum símbolo fornecido

---

### Remover Moeda Ativa

- 🗑 **DELETE /moedas/{usuario_id}/{moeda_id}**
- 🗑 **DELETE /moedas/{usuario_id}/simbolo/{simbolo}**
  **Descrição:** Remove moeda ativa por ID ou símbolo.
  **Resposta (200):**
  ```json
  {
    "message": "Moeda ativa removida com sucesso"
  }
  ```
  **Erros:**
  - 404: Usuário ou moeda não encontrada

---

### Quantidade Mínima para Trading

- 🔍 **GET /moedas/binance/min_quantity/{simbolo}**
  **Descrição:** Retorna a quantidade mínima permitida para trading do par informado.
  **Resposta (200):**
  ```json
  {
    "success": true,
    "min_quantity": 0.0001
  }
  ```

---

## 📈 Ordens

### Criar Ordem

- ✅ **POST /ordem/{usuario_id}**
  ```json
  {
    "simbolo": "BTCUSDT",
    "tp_operacao": "COMPRA",
    "quantidade": 0.001,
    "tipo": "MERCADO"
  }
  ```
  **Descrição:** Cria uma ordem de compra ou venda na Binance e registra no sistema.
  **Resposta (201):**
  ```json
  {
    "message": "Ordem criada com sucesso",
    "ordem_id": "123456",
    "simbolo": "BTCUSDT",
    "tipo": "MERCADO",
    "tp_operacao": "COMPRA",
    "quantidade": 0.001,
    "preco": 68000.0,
    "status": "EM_CARTEIRA",
    "fills": [
      {
        "preco": 68000.0,
        "quantidade": 0.001
      }
    ]
  }
  ```
  **Erros:**
  - 404: Usuário não encontrado
  - 400: Chaves da Binance não configuradas ou tipo de operação inválido

---

### Listar Ordens

- 🔍 **GET /ordem/{usuario_id}**
  **Descrição:** Lista todas as ordens do usuário.
  **Resposta (200):**
  ```json
  {
    "total": 2,
    "ordens": [
      {
        "simbolo": "BTCUSDT",
        "ordem_id": "123456",
        "qtd_executada": 0.001,
        "tipo": "MERCADO",
        "tp_operacao": "COMPRA",
        "preco": 68000.0,
        "status": "EM_CARTEIRA",
        "fills": null
      }
    ]
  }
  ```

---

### Obter Ordem Específica

- 🔍 **GET /ordem/{usuario_id}/{ordem_id}**
  **Descrição:** Detalha uma ordem específica do usuário.
  **Resposta (200):**
  ```json
  {
    "simbolo": "BTCUSDT",
    "ordem_id": "123456",
    "qtd_executada": 0.001,
    "tipo": "MERCADO",
    "tp_operacao": "COMPRA",
    "preco": 68000.0,
    "status": "EM_CARTEIRA",
    "fills": null
  }
  ```
  **Erros:**
  - 404: Usuário ou ordem não encontrada

---

### Listar Ordens Abertas

- 🔍 **GET /ordem/{usuario_id}/abertos**
  **Descrição:** Lista ordens em aberto (em carteira) do usuário.
  **Resposta (200):**
  ```json
  {
    "total": 1,
    "ordens_abertas": [
      {
        "id": 1,
        "moeda": "BTCUSDT",
        "quantidade": 0.001,
        "preco_compra": 68000.0,
        "data_operacao": "2025-06-01T12:00:00",
        "status": "EM_CARTEIRA"
      }
    ]
  }
  ```

---

## 🛠️ Observações

- Todos os endpoints retornam mensagens de erro detalhadas em caso de falha.
- Para testar a API, utilize o Swagger UI ou ferramentas como Postman.
- As operações de trading exigem que o usuário tenha configurado as chaves da Binance.

---
