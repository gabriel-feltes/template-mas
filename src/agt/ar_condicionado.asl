/* Initial beliefs and rules */
temperatura_de_preferencia(jonas, 25).

/* Initial goals */
!inicializar_AC.

/* Plans */
+!inicializar_AC
  <- makeArtifact("ac_quarto","artifacts.ArCondicionado",[],D);
     focus(D);
     !definir_temperatura;
     !!climatizar.

+!set_preferred_temperature : temperatura_de_preferencia(jonas, TP)
  <- definir_temperatura(TP);
     .print("Definindo temperatura para o proprietário: ", TP);
     updateGUI;
     !!climatizar.

+!turn_off : ligado(true)
  <- desligar;
     .print("Ar-condicionado desligado após saída do proprietário");
     updateGUI.

+!turn_off : ligado(false)
  <- .print("Ar-condicionado já está desligado").

+!intruder_alert : true
  <- definir_temperatura(35);
     ligar;
     .print("Ar-condicionado configurado para 35°C para incomodar intruso");
     updateGUI;
     !!climatizar.

+alterado : temperatura_ambiente(TA) & temperatura_ac(TAC)
  <- .drop_intention(climatizar);
     .print("Nova temperatura desejada definida: ", TAC);
     updateGUI;
     !!climatizar.

+closed <- .print("Close event from GUIInterface").

+!definir_temperatura : temperatura_ambiente(TA) & temperatura_ac(TAC) & temperatura_de_preferencia(jonas, TP) & TP \== TAC & ligado(false)
  <- definir_temperatura(TP);
     .print("Definindo temperatura baseado na preferência do usuário jonas: ", TP);
     updateGUI.

+!definir_temperatura : temperatura_ambiente(TA) & temperatura_ac(TAC) & ligado(false)
  <- .print("Usando última temperatura: ", TAC).

+!climatizar : temperatura_ambiente(TA) & temperatura_ac(TAC) & TA \== TAC & ligado(false)
  <- ligar;
     .print("Ligando ar condicionado para atingir ", TAC);
     updateGUI;
     .wait(1000);
     !!climatizar.

+!climatizar : temperatura_ambiente(TA) & temperatura_ac(TAC) & TA \== TAC & ligado(true)
  <- .wait(4000);
     !!climatizar.

+!climatizar : temperatura_ambiente(TA) & temperatura_ac(TAC) & TA == TAC & ligado(true)
  <- desligar;
     .print("Temperatura regulada para ", TAC, ", desligando ar condicionado");
     updateGUI.

+!climatizar : temperatura_ambiente(TA) & temperatura_ac(TAC) & TA == TAC & ligado(false)
  <- .print("Temperatura já regulada para ", TAC, ", ar-condicionado desligado");
     updateGUI.