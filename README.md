# Aplicativo de Música

Este é um aplicativo de música desenvolvido para Android que permite aos usuários reproduzir músicas, visualizar a arte do álbum, e controlar a reprodução através de notificações.

## Estrutura do Projeto

O projeto é organizado em vários pacotes e classes, cada uma com uma responsabilidade específica:

- **Model**: Contém as classes de modelo que representam os dados do aplicativo.
  - `Song`: Representa uma música.
  - `SongManager`: Gerencia a lista de músicas.

- **View**: Contém as classes de visualização que gerenciam a interface do usuário.
  - `MainActivity`: A atividade principal do aplicativo.
  - `CustomVisualizerView`: Uma visualização personalizada para exibir o visualizador de áudio.
  - `SongsAdapter`: Adapter para exibir a lista de músicas no RecyclerView.

- **ViewModel**: Contém as classes que gerenciam a lógica de negócios e a interação com o MediaPlayer.
  - `MediaPlayerManager`: Gerencia a reprodução de músicas.
  - `MusicService`: Serviço que gerencia a reprodução de música em primeiro plano.
  - `VisualizerManager`: Gerencia o visualizador de áudio.

- **Notifications**: Contém as classes que gerenciam as notificações.
  - `MusicNotificationManager`: Gerencia as notificações de música.
  - `NotificationActionReceiver`: Recebe ações de notificação.

## Configuração do Ambiente de Desenvolvimento

1. **Instalar o Android Studio**:
   - Baixe e instale o [Android Studio](https://developer.android.com/studio).

2. **Clonar o Repositório**:
   - Clone este repositório para o seu ambiente de desenvolvimento local.

3. **Abrir o Projeto**:
   - Abra o Android Studio e selecione "Open an existing Android Studio project".
   - Navegue até o diretório onde você clonou o repositório e selecione o projeto.

## Permissões

O aplicativo requer as seguintes permissões:

- `POST_NOTIFICATIONS`: Permite que o aplicativo envie notificações.
- `READ_EXTERNAL_STORAGE`: Permite que o aplicativo leia arquivos de armazenamento externo.
- `FOREGROUND_SERVICE`: Permite que o aplicativo execute serviços em primeiro plano.
- `FOREGROUND_SERVICE_MEDIA_PLAYBACK`: Permite que o aplicativo execute serviços em primeiro plano para reprodução de mídia.
- `RECORD_AUDIO`: Permite que o aplicativo grave áudio.
- `WAKE_LOCK`: Permite que o aplicativo mantenha o dispositivo acordado.

## Executando o Aplicativo

1. **Conectar um Dispositivo ou Iniciar um Emulador**:
   - Conecte um dispositivo Android ao seu computador ou inicie um emulador no Android Studio.

2. **Executar o Aplicativo**:
   - Clique no botão "Run" (ícone de play verde) no Android Studio para compilar e executar o aplicativo no dispositivo conectado ou no emulador.

## Visão Geral das Classes

### Model

- **Song**: Representa uma música com atributos como título, artista, caminho do arquivo, URI da arte do álbum e um indicador de reprodução.
- **SongManager**: Gerencia a lista de músicas, carregando-as do dispositivo.

### View

- **MainActivity**: A atividade principal do aplicativo que inicializa os componentes, configura a interface do usuário e gerencia a reprodução de músicas.
- **CustomVisualizerView**: Uma visualização personalizada para exibir o visualizador de áudio.
- **SongsAdapter**: Adapter para exibir a lista de músicas no RecyclerView.

### ViewModel

- **MediaPlayerManager**: Gerencia a reprodução de músicas, incluindo shuffle e repetição.
- **MusicService**: Serviço que gerencia a reprodução de música em primeiro plano e atualiza as notificações.
- **VisualizerManager**: Gerencia o visualizador de áudio.

### Notifications

- **MusicNotificationManager**: Gerencia as notificações de música.
- **NotificationActionReceiver**: Recebe ações de notificação e inicia o `MusicService` com a ação apropriada.

## Imagens da App

...

## Contribuição

Sinta-se à vontade para contribuir com melhorias, correções de bugs ou novas funcionalidades. Abra uma issue ou envie um pull request com suas sugestões.

## Licença

Este projeto está licenciado sob a Licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

## Contato

Se você tiver alguma dúvida ou precisar de ajuda, sinta-se à vontade para entrar em contato.

---

Este `README.md` fornece uma visão geral completa do projeto, explicando como configurar o ambiente de desenvolvimento, executar o aplicativo e fornecendo uma visão geral de cada classe e sua funcionalidade.
