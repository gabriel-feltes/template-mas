/* Initial goals */
!inicializar_fechadura.

/* Plans */
+!inicializar_fechadura
  <- makeArtifact("fechadura","artifacts.Fechadura",[],F);
     focus(F);
     close;
     lock;
     updateGUI.

+!unlock_and_open
  <- unlock;
     open;
     .print("Porta destrancada e aberta para o proprietário");
     updateGUI.

+!close_and_lock
  <- close;
     lock;
     .print("Porta fechada e trancada após saída do proprietário");
     updateGUI.

+!intruder_alert
  <- close;
     lock;
     .print("Porta fechada e trancada para impedir intruso");
     updateGUI.

+!open : trancada(true)
  <- .print("Não é possível abrir a porta: está trancada");
     updateGUI.

+!open : trancada(false)
  <- open;
     .print("Porta aberta manualmente");
     updateGUI.

+!close
  <- close;
     .print("Porta fechada manualmente");
     updateGUI.

+!lock : aberta(true)
  <- .print("Não é possível trancar a porta: está aberta");
     updateGUI.

+!lock : aberta(false)
  <- lock;
     .print("Porta trancada manualmente");
     updateGUI.

+!unlock
  <- unlock;
     .print("Porta destrancada manualmente");
     updateGUI.

+closed <- .print("Close event from GUIInterface").

+!updateGUI
  <- .send(fechadura, achieve, updateGUI).