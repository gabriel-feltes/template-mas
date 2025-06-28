# Projeto JaCaMo Smart Home (v1.2)

Este é um projeto JaCaMo que implementa um sistema de casa inteligente com múltiplos artefatos (Câmera, Ar-Condicionado, Lâmpada, Fechadura e Cortina) controlados por agentes Jason. Cada artefato possui uma interface gráfica (GUI) para exibir seu estado e permitir interação do usuário, com o sistema aplicando restrições realistas e coordenação para uma experiência coesa de casa inteligente.

## Visão Geral

O projeto simula um ambiente de casa inteligente onde:

- **Câmera** detecta pessoas e sua localização (ex: `frente`, `dentro`, `saindo`) e aciona ações nos demais dispositivos.
- **Ar-Condicionado** exibe e controla as temperaturas ambiente e desejada.
- **Lâmpada** mostra e alterna seu estado (ligada/desligada).
- **Fechadura** gerencia os estados aberta/fechada e trancada/destrancada, com restrições que impedem abrir uma porta trancada ou trancar uma porta aberta.
- **Cortina** exibe e ajusta o nível de abertura (0-100%) via um slider.

O sistema utiliza CArtAgO para artefatos, Jason para lógica dos agentes e Swing para GUIs. A GUI da Câmera mostra apenas seus próprios dados (`ligada`, `local`, `pessoa_presente`), enquanto os outros artefatos exibem seus estados e controles específicos.

## Pré-requisitos

- **Java**: OpenJDK 21
- **Gradle**: Incluído no projeto (via `gradlew`)
- **JaCaMo**: Framework para sistemas multiagentes (incluído nas dependências)

### Configurando o Java

Garanta que o Java 21 está instalado:

```bash
java -version
```

Se não estiver instalado (Ubuntu):

```bash
sudo apt update
sudo apt install openjdk-21-jdk
```

Defina o `JAVA_HOME`:

```bash
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH
```

Para tornar essa alteração persistente, adicione ao seu `~/.bashrc`:

```bash
echo 'export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64' >> ~/.bashrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bashrc
source ~/.bashrc
```

## Primeiros Passos

1. **Clone o Repositório**:

    ```bash
    git clone https://github.com/gabriel-feltes/template-mas.git
    cd template-mas
    ```

2. **Execute a Aplicação**:

    ```bash
    ./gradlew run
    ```

    Isso inicia a aplicação JaCaMo, abrindo as GUIs de todos os artefatos.

## Estrutura do Projeto

### Artefatos

Localizados em `src/artifacts/`:

- **Camera.java**: Exibe o estado da câmera (`ligada`, `local`, `pessoa_presente`) com entradas para pessoa e local. Valida pessoas desconhecidas via diálogo.
- **ArCondicionado.java**: Mostra temperaturas ambiente e desejada, com campo para ajustar a temperatura desejada.
- **Lampada.java**: Exibe o estado da lâmpada (`ligado`) com botões "Ligar" e "Desligar".
- **Fechadura.java**: Mostra o estado da porta (`aberta`, `trancada`) com botões "Abrir", "Fechar", "Trancar" e "Destrancar". Aplica restrições: não é possível abrir uma porta trancada ou trancar uma porta aberta.
- **Cortina.java**: Exibe o nível de abertura da cortina (`nivel_abertura`) com um slider (0-100%).

### Agentes

Localizados em `src/agt/`:

- **camera.asl**: Inicializa o artefato câmera, detecta movimento e coordena com outros agentes (ex: destrancar a porta para o proprietário).
- **ar_condicionado.asl**: Gerencia o ar-condicionado, ajustando a temperatura conforme preferências do proprietário ou alertas de intruso.
- **lampada.asl**: Controla a lâmpada, ligando/desligando para presença do proprietário ou cenários de intruso, e lida com ações manuais.
- **fechadura.asl**: Gerencia a fechadura, aplicando restrições para ações manuais e coordenando com eventos da câmera.
- **cortina.asl**: Ajusta o nível de abertura da cortina conforme presença do proprietário ou entrada manual pelo slider.

### Configuração

- **main.jcm**:

```jcm
mas main {
    agent ar_condicionado: ar_condicionado.asl
    agent camera: camera.asl
    agent cortina: cortina.asl
    agent lampada: lampada.asl
    agent fechadura: fechadura.asl
}
```

## Funcionalidades

- **Câmera**:
  - GUI exibe: Status da câmera (Ligada/Desligada), Local (frente, saindo, dentro), Pessoa (ex: jonas, desconhecido).
    - Valida pessoas desconhecidas com diálogo de confirmação.
    - Aciona ações nos outros dispositivos (ex: destrancar porta, ligar lâmpada) conforme pessoa e local.
- **Ar-Condicionado**:
  - GUI exibe: Temperatura ambiente, temperatura desejada, campo para ajuste.
    - Simula regulação de temperatura (aumenta/diminui até atingir a desejada).
- **Lâmpada**:
  - GUI exibe: Status da lâmpada (Ligada/Desligada) com botões de alternância.
    - Responde a cliques manuais e eventos da câmera (ex: desligar para intrusos).
- **Fechadura**:
  - GUI exibe: Estado da porta (Aberta/Fechada), Estado da tranca (Trancada/Não) com botões.
    - Restrições: Não é possível abrir porta trancada ou trancar porta aberta; mostra diálogos de erro para ações inválidas.
- **Cortina**:
  - GUI exibe: Nível de abertura (0-100%) com slider.
    - Atualiza nível conforme slider ou eventos da câmera (ex: fechar para intrusos).

## Cenários de Teste

1. **Proprietário Chegando**:
    - GUI da Câmera: Defina `pessoa = "jonas"`, `local = "frente"`, clique "OK".
    - Esperado:
        - Log: `[camera] Proprietário jonas chegando na frente da casa`, `[fechadura] Porta destrancada e aberta`, `[lampada] Lâmpada ligada`, `[cortina] Cortina aberta`, `[ar_condicionado] Definindo temperatura: 25`.
        - GUIs: Câmera (Pessoa: jonas, Local: frente), Fechadura (Porta: Aberta, Trancada: Não), Lâmpada (Ligada), Cortina (100%), ArCondicionado (25°C).

2. **Proprietário Saindo**:
    - GUI da Câmera: Defina `pessoa = "jonas"`, `local = "saindo"`, clique "OK".
    - Esperado:
        - Log: `[camera] Proprietário jonas saindo da casa`, `[fechadura] Porta fechada e trancada`, `[lampada] Lâmpada desligada`, `[cortina] Cortina fechada`, `[ar_condicionado] Ar-condicionado desligado`.
        - GUIs: Câmera (Local: saindo), Fechadura (Porta: Fechada, Trancada: Sim), Lâmpada (Desligada), Cortina (0%), ArCondicionado (off).

3. **Intruso Detectado**:
    - GUI da Câmera: Defina `pessoa = "desconhecido"`, `local = "dentro"`, clique "OK", confirme no diálogo.
    - Esperado:
        - Log: `[camera] Intruso detectado`, `[fechadura] Porta fechada e trancada`, `[lampada] Lâmpada desligada`, `[cortina] Cortina fechada`, `[ar_condicionado] Definindo temperatura: 35`.
        - GUIs: Câmera (Pessoa: desconhecido, Local: dentro), Fechadura (Porta: Fechada, Trancada: Sim), Lâmpada (Desligada), Cortina (0%), ArCondicionado (35°C).

4. **Controles Manuais**:
    - **Lâmpada**: Clique "Ligar" (GUI: Ligada, Log: `[lampada] Liguei a Lâmpada!`), clique "Desligar" (GUI: Desligada).
    - **Fechadura**:
        - Com porta fechada e trancada: Clique "Abrir" (Erro: "Não é possível abrir a porta enquanto ela está trancada").
        - Clique "Destrancar" e depois "Abrir" (GUI: Porta: Aberta, Trancada: Não).
        - Com porta aberta: Clique "Trancar" (Erro: "Não é possível trancar a porta enquanto ela está aberta").
        - Clique "Fechar" e depois "Trancar" (GUI: Porta: Fechada, Trancada: Sim).
    - **Cortina**: Mova o slider para 50% (GUI: 50%, Log: `[cortina] Nível de abertura DEPOIS: 50`).
    - **ArCondicionado**: Defina temperatura para 22 (GUI: 22°C, Log: `[ar_condicionado] Nova temperatura desejada definida: 22`).

## Melhorias Futuras

- Adicionar mais restrições (ex: ar-condicionado só opera dentro de uma faixa de temperatura).
- Reduzir a saída no console para logs mais limpos.
- Implementar um painel centralizado para monitorar todos os dispositivos.
- Implementar persistência dos estados dos dispositivos entre sessões.

## Contribuindo

Sinta-se à vontade para fazer fork do repositório, propor mudanças e enviar pull requests.

---

Este projeto é construído usando o framework JaCaMo. Para mais detalhes, visite [JaCaMo](https://jacamo-lang.github.io/).
