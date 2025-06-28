# JaCaMo Smart Home Project (v1.2)

This is a JaCaMo project implementing a smart home system with multiple artifacts (Camera, Air Conditioner, Lamp, Door Lock, and Curtain) controlled by Jason agents. Each artifact has a GUI to display its state and provide user interaction, with the system enforcing realistic constraints and coordination for a cohesive smart home experience.

## Overview

The project simulates a smart home environment where:

- **Camera** detects people and their location (e.g., `frente`, `dentro`, `saindo`) and triggers actions for other devices.
- **Air Conditioner** displays and controls ambient and set temperatures.
- **Lamp** shows and toggles its on/off state.
- **Door Lock** manages open/closed and locked/unlocked states, with constraints preventing opening a locked door or locking an open door.
- **Curtain** displays and adjusts its opening level (0-100%) via a slider.

The system uses CArtAgO for artifacts, Jason for agent logic, and Swing for GUIs. The Camera GUI only shows its own data (`ligada`, `local`, `pessoa_presente`), while other artifacts display their specific states and controls.

## Prerequisites

- **Java**: OpenJDK 21
- **Gradle**: Included in the project (via `gradlew`)
- **JaCaMo**: Framework for multi-agent systems (included in dependencies)

### Setting Up Java

Ensure Java 21 is installed:

```bash
java -version
```

If not installed (Ubuntu)

```bash
sudo apt update
sudo apt install openjdk-21-jdk
```

Set `JAVA_HOME`:

```bash
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH
```

To make this change persistent, add it to your `~/.bashrc`:

```bash
echo 'export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64' >> ~/.bashrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bashrc
source ~/.bashrc
```

## Getting Started

1. **Clone the Repository**:

    ```bash
    git clone https://github.com/gabriel-feltes/template-mas.git
    cd template-mas
    ```

2. **Run the Application**:

    ```bash
    ./gradlew run
    ```

    This starts the JaCaMo application, launching GUIs for all artifacts.

## Project Structure

### Artifacts

Located in `src/artifacts/`:

- **Camera.java**: Displays camera state (`ligada`, `local`, `pessoa_presente`) with inputs for person and location. Validates unknown persons via a dialog.
- **ArCondicionado.java**: Shows ambient and set temperatures, with an input field to adjust the desired temperature.
- **Lampada.java**: Displays lamp state (`ligado`) with "Ligar" and "Desligar" buttons.
- **Fechadura.java**: Shows door state (`aberta`, `trancada`) with "Abrir", "Fechar", "Trancar", and "Destrancar" buttons. Enforces constraints: cannot open a locked door or lock an open door.
- **Cortina.java**: Displays curtain opening level (`nivel_abertura`) with a slider (0-100%).

### Agents

Located in `src/agt/`:

- **camera.asl**: Initializes the camera artifact, detects movement, and coordinates with other agents (e.g., unlocking the door for the homeowner).
- **ar_condicionado.asl**: Manages the air conditioner, adjusting temperature based on homeowner preferences or intruder alerts.
- **lampada.asl**: Controls the lamp, turning it on/off for homeowner presence or intruder scenarios, and handles manual button actions.
- **fechadura.asl**: Manages the door lock, enforcing constraints for manual actions and coordinating with camera events.
- **cortina.asl**: Adjusts the curtain’s opening level based on homeowner presence or manual slider input.

### Configuration

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

## Features

- **Camera**:
  - GUI displays: Camera status (Ligada/Desligada), Location (frente, saindo, dentro), Person (e.g., jonas, desconhecido).
    - Validates unknown persons with a confirmation dialog.
    - Triggers actions for other devices (e.g., unlock door, turn on lamp) based on person and location.
- **Air Conditioner**:
  - GUI displays: Ambient temperature, set temperature, input field for desired temperature.
    - Simulates temperature regulation (increases/decreases toward set temperature).
- **Lamp**:
  - GUI displays: Lamp status (Ligada/Desligada) with toggle buttons.
    - Responds to manual clicks and camera-driven events (e.g., turn off for intruders).
- **Door Lock**:
  - GUI displays: Door state (Aberta/Fechada), Lock state (Trancada/Não) with buttons.
    - Constraints: Cannot open a locked door or lock an open door; shows error dialogs for invalid actions.
- **Curtain**:
  - GUI displays: Opening level (0-100%) with a slider.
    - Updates level based on manual slider input or camera-driven events (e.g., close for intruders).

## Testing Scenarios

1. **Homeowner Arriving**:
    - Camera GUI: Set `pessoa = "jonas"`, `local = "frente"`, click "OK".
    - Expected:
        - Log: `[camera] Proprietário jonas chegando na frente da casa`, `[fechadura] Porta destrancada e aberta`, `[lampada] Lâmpada ligada`, `[cortina] Cortina aberta`, `[ar_condicionado] Definindo temperatura: 25`.
        - GUIs: Camera (Pessoa: jonas, Local: frente), Fechadura (Porta: Aberta, Trancada: Não), Lampada (Ligada), Cortina (100%), ArCondicionado (25°C).

2. **Homeowner Leaving**:
    - Camera GUI: Set `pessoa = "jonas"`, `local = "saindo"`, click "OK".
    - Expected:
        - Log: `[camera] Proprietário jonas saindo da casa`, `[fechadura] Porta fechada e trancada`, `[lampada] Lâmpada desligada`, `[cortina] Cortina fechada`, `[ar_condicionado] Ar-condicionado desligado`.
        - GUIs: Camera (Local: saindo), Fechadura (Porta: Fechada, Trancada: Sim), Lampada (Desligada), Cortina (0%), ArCondicionado (off).

3. **Intruder Detected**:
    - Camera GUI: Set `pessoa = "desconhecido"`, `local = "dentro"`, click "OK", confirm dialog.
    - Expected:
        - Log: `[camera] Intruso detectado`, `[fechadura] Porta fechada e trancada`, `[lampada] Lâmpada desligada`, `[cortina] Cortina fechada`, `[ar_condicionado] Definindo temperatura: 35`.
        - GUIs: Camera (Pessoa: desconhecido, Local: dentro), Fechadura (Porta: Fechada, Trancada: Sim), Lampada (Desligada), Cortina (0%), ArCondicionado (35°C).

4. **Manual Controls**:
    - **Lampada**: Click "Ligar" (GUI: Ligada, Log: `[lampada] Liguei a Lâmpada!`), click "Desligar" (GUI: Desligada).
    - **Fechadura**:
        - With door closed and locked: Click "Abrir" (Error: "Não é possível abrir a porta enquanto ela está trancada").
        - Click "Destrancar" then "Abrir" (GUI: Porta: Aberta, Trancada: Não).
        - With door open: Click "Trancar" (Error: "Não é possível trancar a porta enquanto ela está aberta").
        - Click "Fechar" then "Trancar" (GUI: Porta: Fechada, Trancada: Sim).
    - **Cortina**: Move slider to 50% (GUI: 50%, Log: `[cortina] Nível de abertura DEPOIS: 50`).
    - **ArCondicionado**: Set temperature to 22 (GUI: 22°C, Log: `[ar_condicionado] Nova temperatura desejada definida: 22`).

## Future Improvements

- Add more constraints (e.g., air conditioner only operates within a specific temperature range).
- Reduce console output for cleaner logs.
- Implement a centralized dashboard to monitor all devices.
- Implement persistence for device states across sessions.

## Contributing

Feel free to fork the repository, make changes, and submit pull requests.

---

This project is built using the JaCaMo framework. For more details, visit [JaCaMo](https://jacamo-lang.github.io/).
