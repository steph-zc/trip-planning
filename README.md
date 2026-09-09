# TripPlanning

Assistente de planejamento de viagens para Android.

Autor: Stefan Zanini

---

## O que o aplicativo faz

O usuário informa para onde vai, quando vai e o que gosta de fazer. A partir dessas
preferências o aplicativo sugere atividades, deixa ajustar os detalhes de cada uma
(duração, dificuldade, horário) e monta um roteiro com tudo o que foi escolhido.

São quatro telas encadeadas, e a viagem é montada aos poucos, passando de uma para a outra.

---

## Fluxo do aplicativo

```
 ┌───────────────────────────┐
 │ 1. Configuração da viagem │
 └─────────────┬─────────────┘
               │  Continuar
               ▼
 ┌───────────────────────────┐    Ver resumo     ┌──────────────────────┐
 │  2. Atividades sugeridas  │ ────────────────▶ │ 4. Resumo da viagem  │
 └───┬───────────────────▲───┘                   └──────────────────────┘
     │                   │
     │  toca em uma      │  volta com a
     │  atividade        │  atividade ajustada
     ▼                   │
 ┌───┴───────────────────┴───┐
 │ 3. Detalhes da atividade  │
 └───────────────────────────┘
```

O caminho entre a tela 2 e a tela 3 é um ciclo: o usuário abre uma atividade, ajusta os
detalhes, confirma e volta para a lista, podendo repetir isso quantas vezes quiser. O
resumo só é aberto quando houver pelo menos uma atividade escolhida.

---

## As telas

### 1. Configuração da viagem: `TripSetupActivity`

Formulário inicial: destino, data de partida, data de retorno e as preferências
(Aventura, Cultura, Praia, Gastronomia, Vida noturna).

As datas são escolhidas no calendário do Android. O campo de retorno só abre depois da
partida ser definida, e o calendário desabilita os dias anteriores a ela, tornando
impossível montar uma viagem que volta antes de sair. Se o usuário mudar a partida para
depois de um retorno já escolhido, o retorno é apagado com um aviso explicando o motivo.

O botão Continuar valida tudo antes de seguir e mostra um alerta com o primeiro problema
encontrado.

### 2. Atividades sugeridas: `AttractionListActivity`

Lista personalizada com as atividades que combinam com as preferências marcadas, cada uma
em um cartão com imagem, descrição e a linha de informações (categoria, duração e
dificuldade sugeridas).

O topo repete o resumo da viagem para o usuário não perder o contexto, e um contador mostra
quantas atividades já foram escolhidas.

### 3. Detalhes da atividade: `AttractionDetailsActivity`

A atividade escolhida aparece com os valores sugeridos pelo catálogo, e o usuário ajusta o
que quiser: duração, nível de dificuldade, horário de início e observações. Há também a
página da atividade, aberta dentro do próprio aplicativo.

Ao confirmar, a atividade entra no roteiro e a tela devolve a viagem atualizada para a
lista. Reabrir a mesma atividade e confirmar de novo substitui a versão anterior, em vez
de duplicar o item.

### 4. Resumo da viagem: `TripSummaryActivity`

O roteiro final: todas as atividades escolhidas com seus detalhes, o total de horas
planejadas comparado com o tempo disponível da viagem, e controles para ordenar a lista
(por horário, duração ou nome) e alternar entre visão detalhada e compacta.

---

## Registro no console

A cada transição, a tela que envia e a tela que recebe imprimem o estado da viagem no
Logcat, sob a tag `TripPlanning`:

```
===== AttractionListActivity -> TripSummaryActivity =====
Destino: Florianópolis
Partida: 09/09/2026 | Retorno: 20/09/2026
Preferências: Aventura, Praia
Atividades escolhidas (2):
  - Trilha na montanha | 4h | Difícil | início 09:00
  - Rafting no rio | 3h | Média | início 09:00
Total de horas: 7
===== TripSummaryActivity recebeu os dados =====
...
```

Toda a impressão está concentrada em `util/TripLogger.kt`, em vez de espalhada em chamadas
de `Log.d` pelas telas.

---

## Arquitetura

```
app/src/main/java/br/com/example/tripplanning/
├── model/      classes de dados: o que a viagem é
│   ├── Trip.kt                 destino, datas, preferências e atividades escolhidas
│   ├── Attraction.kt           uma atividade do catálogo
│   ├── PlannedAttraction.kt    uma atividade depois dos ajustes do usuário
│   ├── Preference.kt           enum das preferências
│   └── Difficulty.kt           enum dos níveis de dificuldade
│
├── data/       de onde vêm as atividades
│   └── AttractionRepository.kt  catálogo com 15 atividades e a busca por preferência
│
├── ui/         uma pasta por tela
│   ├── setup/       TripSetupActivity
│   ├── attractions/ AttractionListActivity
│   ├── details/     AttractionDetailsActivity
│   ├── summary/     TripSummaryActivity
│   └── adapter/     AttractionAdapter e PlannedAttractionAdapter
│
└── util/       o encanamento entre as telas
    ├── Extras.kt      chaves dos extras do Intent
    └── TripLogger.kt  impressão no console a cada transição
```

---

## Componentes utilizados

| Componente | Onde está | Função |
|---|---|---|
| `EditText` | Telas 1 e 3 | Digitar o destino da viagem e as observações da atividade |
| `Button` | Todas as telas | Confirmar etapas, abrir os seletores de data e hora |
| `TextView` | Todas as telas | Rótulos, títulos e informações das listas |
| `ImageView` | Telas 1, 2, 3 e 4 | Ilustração do topo e as imagens das atividades |
| `CheckBox` | Tela 1 | Marcar preferências, podendo escolher várias ao mesmo tempo |
| `DatePicker` | Tela 1 | Escolher partida e retorno, com o calendário travado na ordem correta |
| `AlertDialog` | Telas 1 e 2 | Avisar o que falta preencher antes de seguir |
| `RecyclerView` | Tela 2 | Lista das atividades sugeridas |
| `CardView` | Telas 2, 3 e 4 | Cartões com cantos arredondados e sombra |
| `SeekBar` | Tela 3 | Ajustar a duração da atividade, de 1 a 12 horas |
| `RadioButton` | Tela 3 | Escolher a dificuldade entre opções que se excluem |
| `TimePicker` | Tela 3 | Definir o horário de início, em formato de 24 horas |
| `WebView` | Tela 3 | Abrir a página da atividade sem sair do aplicativo |
| `ProgressBar` | Telas 3 e 4 | Carregamento da página e proporção de horas já planejadas |
| `ListView` | Tela 4 | Lista das atividades escolhidas |
| `Spinner` | Tela 4 | Ordenar o roteiro por horário, duração ou nome |
| `ToggleButton` | Tela 4 | Alternar entre visão detalhada e compacta |

---

## Ambiente

| Item | Versão |
|---|---|
| Linguagem | Kotlin, com layouts em XML |
| compileSdk | 36.1 |
| minSdk / targetSdk | 36 / 36 |
| Android Gradle Plugin | 9.3.2 |
| Gradle | 9.5.0 |
| JDK | 21 |

## Como executar

1. Abrir o projeto no Android Studio e aguardar o Gradle sincronizar.
2. Escolher um emulador ou aparelho com **Android 16 (API 36)** ou superior.
3. Executar o módulo `app`.

A tela 3 carrega uma página da web, então o aparelho precisa de conexão com a internet para
essa parte funcionar. O restante do aplicativo roda offline.

Para acompanhar o registro das transições, abrir o **Logcat** e filtrar pela tag
`TripPlanning`.
