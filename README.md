# Juntos ♥

App Android nativo (Kotlin) que conta há quanto tempo vocês estão juntos — dias, horas, minutos e segundos desde uma data e hora que você escolhe — com um widget compacto para a tela inicial.

## Funcionalidades

- **Tela principal**: escolha a data e hora especial (date/time picker) com um preview ao vivo do contador.
- **Widget de uma fileira**: pílula compacta (♥ dias · HH:MM:SS) do tamanho de uma linha de ícones, com fundo em degradê translúcido (60% de opacidade) para combinar com o papel de parede.
- **Relógio ao vivo sem gastar bateria**: o `HH:MM:SS` usa `Chronometer`, que é ticado pelo próprio launcher (host do widget), não pelo processo do app.
- **Botão de alternância**: toque no ícone de sync no widget para trocar entre `812 dias` e o detalhamento `2 anos, 3 meses, 12 dias`.
- **Texto que se ajusta**: o rótulo dos dias usa `autoSizeTextType` para encolher a fonte em vez de cortar quando o texto é longo.
- **Atualização precisa**: um alarme leve recalcula a contagem exatamente no horário do aniversário (não à meia-noite) e sobrevive a reinicializações do aparelho.
- **Toque no widget** abre o app para editar a data.

### Sobre a tela de bloqueio

O widget declara `android:widgetCategory="home_screen|keyguard"`, mas o Android de fábrica (desde a versão 5) não permite mais widgets de terceiros na tela de bloqueio — isso só existe em customizações específicas de fabricante. O widget funciona normalmente na tela inicial em qualquer aparelho.

## Estrutura do projeto

```
app/src/main/
├── java/com/victorfaria/juntos/
│   ├── MainActivity.kt              # tela para escolher a data + preview ao vivo
│   ├── DateStore.kt                 # SharedPreferences: data alvo e modo de exibição
│   ├── TimeUtils.kt                 # cálculo de dias / anos-meses-dias / formatação
│   └── widget/
│       ├── RomanticWidgetProvider.kt  # lógica do widget (RemoteViews, cliques, modos)
│       └── AlarmScheduler.kt          # agenda a virada do contador no horário exato
└── res/
    ├── layout/
    │   ├── activity_main.xml
    │   └── widget_romantic.xml
    ├── xml/romantic_widget_info.xml   # metadata do widget (tamanho, categoria)
    └── values/, drawable/             # cores, strings, tema, ícones vetoriais
```

## Como rodar

Pré-requisitos: [Android Studio](https://developer.android.com/studio) recente (com JDK 17 embutido) e um celular com depuração USB ativada, ou um emulador.

1. Abra a pasta do projeto no Android Studio.
2. Deixe o Gradle sincronizar (se pedir para criar o wrapper, aceite).
3. Rode (▶) no dispositivo/emulador.
4. Na tela principal, escolha a data e toque em **Salvar**.
5. Toque em **Adicionar widget à tela inicial**, ou adicione manualmente segurando a tela inicial → Widgets → Juntos.

## Stack

Kotlin · Views + `RemoteViews` (sem Compose) · `AppWidgetProvider` · `AlarmManager` · `SharedPreferences` · ViewBinding.
