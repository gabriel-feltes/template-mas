/* Initial goals */
!inicializar_cortina.

/* Plans */
+!inicializar_cortina
  <- makeArtifact("cortina","artifacts.Cortina",[],C);
     focus(C);
     updateGUI.

+!set_preferred_lighting
  <- definir_nivel_abertura(100);
     .print("Cortina aberta para o proprietário");
     updateGUI.

+!turn_off
  <- definir_nivel_abertura(0);
     .print("Cortina fechada após saída do proprietário");
     updateGUI.

+!intruder_alert
  <- definir_nivel_abertura(0);
     .print("Cortina fechada para dificultar intruso");
     updateGUI.

+closed <- .print("Close event from GUIInterface").

+!updateGUI
  <- .send(cortina, achieve, updateGUI).