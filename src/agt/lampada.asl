/* Initial goals */
!inicializar_lampada.

/* Plans */
+!inicializar_lampada
  <- makeArtifact("lampada","artifacts.Lampada",[],L);
     focus(L);
     ligar;
     updateGUI.

+!set_preferred_lighting
  <- ligar;
     .print("Lâmpada ligada para o proprietário");
     updateGUI.

+!turn_off
  <- desligar;
     .print("Lâmpada desligada após saída do proprietário");
     updateGUI.

+!intruder_alert
  <- desligar;
     .print("Lâmpada desligada para dificultar intruso");
     updateGUI.

+!ligar
  <- ligar;
     .print("Lâmpada ligada manualmente");
     updateGUI.

+!desligar
  <- desligar;
     .print("Lâmpada desligada manualmente");
     updateGUI.

+closed <- .print("Close event from GUIInterface").

+!updateGUI
  <- .send(lampada, achieve, updateGUI).