# Guião de Simulação — GEstSaude

## Configuração inicial
O sistema arranca com o relógio às **08:00** e as seguintes consultas marcadas:

| SNS | Utente | Especialidade | Data | Hora |
|-----|--------|---------------|------|------|
| 122 | Daniel Mendes Rodrigues | Orto1 | Hoje | 08:10 |
| 120 | Dália Ribeiro Sanches   | Ped1  | Hoje | 08:10 |
| 121 | Raquel Marques Soares   | Ped2  | Hoje | 08:10 |
| 125 | Felizberto Desgraçado   | Derm1 | Hoje | 08:20 |
| 126 | Antonina Martins Pires  | Ped1  | Hoje | 08:30 |
| 127 | Camaleão das Neves Freitas | Ped1 | Hoje | 08:40 |
| 127 | Camaleão das Neves Freitas | Ped1 | Amanhã | 08:10 |
| 123 | Zeferino Dias Torres    | Ped1  | Amanhã | 08:20 |

---

## Teste 1 — Listagens na Secretaria
**Objetivo:** verificar que as listagens funcionam corretamente.

1. Abre a **Secretaria**
2. Clica **Todas** → devem aparecer as 8 consultas
3. Clica **Hoje** → devem aparecer as 6 consultas de hoje
4. Clica **Utente** → introduz SNS `127` → devem aparecer 2 consultas do Camaleão
5. Clica **Utente** → introduz SNS `124` → tabela vazia (Anabela não tem consultas)
6. Clica **Utente** → introduz SNS `999` → mensagem "Utente inválido"
7. Clica **Especialidade** → introduz `Ped1` → devem aparecer 5 consultas
8. Clica **Especialidade** → introduz `XXX` → mensagem "Especialidade inválida"

---

## Teste 2 — Criar e apagar consulta
**Objetivo:** verificar criação e remoção de consultas.

1. Na Secretaria clica **Nova Consulta**
2. Introduz SNS `124` (Anabela Dias Santos) → nome deve aparecer
3. Seleciona especialidade `Card1`
4. Escolhe data de amanhã e hora `09:00`
5. Mensagem deve ser "Está tudo OK!" → clica OK
6. Verifica que a consulta aparece na listagem **Todas**
7. Clica **x** na consulta da Anabela → confirma remoção
8. Verifica que desaparece da listagem

---

## Teste 3 — Editar consulta
**Objetivo:** verificar edição de consulta.

1. Na listagem **Todas** clica **e** na consulta do Daniel (SNS 122, Orto1, 08:10)
2. Tenta mudar a hora para `11:10` → "Está tudo OK!" → clica OK
3. Verifica que a consulta aparece com a nova hora

---

## Teste 4 — Validar consulta na Máquina de Entrada (utente a horas)
**Objetivo:** utente chega a horas e retira senha.

**Pré-condição:** relógio entre as 08:00 e as 08:10 (arranque do sistema)

1. Abre a **Máquina de Entrada**
2. Clica **Validar Consulta**
3. Introduz SNS `122` (Daniel, consulta às 08:10)
4. Deve aparecer mensagem com o número da senha (ex: "A1")
5. Na janela **Orto1** deve aparecer 1 utente em espera
6. Repete para SNS `120` (Dália, Ped1 08:10) → senha "A2"
7. Na janela **Ped1** deve aparecer 1 utente em espera

---

## Teste 5 — Utente sem consulta próxima
**Objetivo:** utente tenta retirar senha sem ter consulta nas próximas 3 horas.

**Pré-condição:** relógio às 08:00, consulta do Zeferino só amanhã

1. Na Máquina de Entrada introduz SNS `123` (Zeferino)
2. Deve aparecer "não tem consultas hoje!"

---

## Teste 6 — Especialidade chama utente
**Objetivo:** fluxo completo de atendimento.

**Pré-condição:** Daniel já retirou senha (Teste 4)

1. Na janela **Orto1** clica **Chamar Utente**
2. Deve aparecer o número da senha e o nome "Daniel Mendes Rodrigues"
3. Clica **Validar Consulta** → aparece menu com "Finalizar Consulta" e "Encaminhar"
4. Clica **Finalizar Consulta**
5. Consulta do Daniel deve desaparecer da listagem **Todas** na Secretaria
6. Janela Orto1 deve mostrar 0 utentes

---

## Teste 7 — Encaminhar utente para serviço
**Objetivo:** encaminhamento para radiologia.

**Pré-condição:** Dália retirou senha para Ped1 (Teste 4)

1. Na janela **Ped1** chama o utente → aparece Dália
2. Clica **Validar Consulta**
3. Clica **Encaminhar**
4. Introduz `Rad` → aparece na lista de serviços
5. Deixa em branco para terminar → OK
6. Na janela **Rad** deve aparecer 1 utente em espera
7. Na janela **Rad** chama o utente → aparece Dália
8. Clica terminar → consulta eliminada do sistema

---

## Teste 8 — Utente ausente na especialidade
**Objetivo:** utente é chamado mas não aparece.

**Pré-condição:** Felizberto retirou senha para Derm1 (retirar às 08:20 ou acelerar relógio)

1. Na janela **Derm1** chama o utente → aparece Felizberto
2. Clica **Utente ausente**
3. A hora de atendimento é adiada 15 minutos
4. Felizberto volta para o fim da fila com nova hora

---

## Teste 9 — Utente atrasado
**Objetivo:** utente chega atrasado mas dentro do limite de 2 horas.

1. No **Relógio Simulado** avança o relógio para as 09:30 (mais de 1h depois das 08:10)
2. Na Máquina de Entrada introduz SNS `122` (Daniel, consulta às 08:10 — se ainda existir)
3. Deve aparecer mensagem de atraso adiado (hora prevista = hora entrada + 45 min)

---

## Teste 10 — Utente demasiado atrasado
**Objetivo:** utente chega com mais de 2 horas de atraso — consulta é anulada.

1. No Relógio Simulado avança para as **10:15** (mais de 2h depois das 08:10)
2. Na Máquina de Entrada introduz o SNS de um utente com consulta às 08:10
3. Deve aparecer "atraso na consulta superior ao permitido, a consulta foi anulada"
4. A consulta deve desaparecer da listagem na Secretaria
