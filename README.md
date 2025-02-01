# Aplicativo de Música

O M_App é um aplicativo Android que permite aos usuários gerenciar e reproduzir suas músicas favoritas. O aplicativo possui uma interface amigável e recursos como detalhes do álbum, controle de reprodução e notificações.

## Funcionalidades

- Reproduzir músicas
- Exibir detalhes do álbum
- Controle de reprodução com notificações
- Integração com o Google AdMob para monetização

## Tecnologias Utilizadas
- Android SDK
- Java
- Google Mobile Ads SDK(OBS:Implementacao não concluida)

## Instalação

1. Clone o repositório:
   
   ```bash
   git clone [https://github.com/seu_usuario/m_app.git](https://github.com/SilverPro17/AppMusic_tf)
   ```
   
2. Abra o projeto no Android Studio.
3. Sincronize o projeto com os arquivos Gradle.
4. Execute o aplicativo em um dispositivo ou emulador Android.

## Permissões

O aplicativo requer as seguintes permissões:

- WRITE_EXTERNAL_STORAGE: Permite que o aplicativo grave dados no armazenamento externo.
- FOREGROUND_SERVICE: Permite que o aplicativo execute serviços em primeiro plano.
- READ_PHONE_STATE: Permite que o aplicativo acesse o estado do telefone.

## Estrutura do Código
O código do aplicativo é organizado da seguinte forma:

- ApplicationClass: Classe base da aplicação que pode ser usada para inicializar bibliotecas e gerenciar o estado global do aplicativo.

- MainActivity: A atividade principal que serve como ponto de entrada do aplicativo e gerencia a navegação entre os fragmentos.

- AlbumFragment: Fragmento que exibe uma lista de álbuns disponíveis.

- SongsFragment: Fragmento que exibe uma lista de músicas disponíveis.

- NowPlayingFragmentBottom: Fragmento que exibe informações sobre a música atualmente em reprodução na parte inferior da tela.

- PlayerActivity: Atividade que controla a reprodução de música, permitindo ao usuário interagir com os controles de reprodução.

- MusicService: Um serviço em primeiro plano que gerencia a reprodução de música, permitindo que a música continue tocando mesmo quando o aplicativo não está em primeiro plano.

- NotificationReceiver: Recebe e processa ações de notificação, como reproduzir, pausar ou mudar a música.

- AlbumAdapter: Adaptador que liga os dados dos álbuns à interface do usuário, permitindo que os álbuns sejam exibidos em uma lista.

- AlbumDetailsAdapter: Adaptador que exibe detalhes de um álbum específico, como faixas e informações adicionais.

- MusicAdapter: Adaptador que liga os dados das músicas à interface do usuário, permitindo que as músicas sejam exibidas em uma lista.

- MusicFiles: Classe que gerencia a coleta e manipulação de arquivos de música no dispositivo.

- ActionPlaying: Classe que gerencia as ações de reprodução, como play, pause e skip.

## Observações
- O app nao esta totalmente terminado pois, há alguns reajustes a fazer.
- Mudei completamente o layout por motivos de comflito, eliminei a parte de customviewholder pois causou complito com o layout em relação ao dispositivo utilizado para testes.
   
## Contribuição

Sinta-se à vontade para contribuir com melhorias, correções de bugs ou novas funcionalidades. Abra uma issue ou envie um pull request com suas sugestões.



Este `README.md` fornece uma visão geral completa do projeto, explicando como configurar o ambiente de desenvolvimento, executar o aplicativo e fornecendo uma visão geral de cada classe e sua funcionalidade.
