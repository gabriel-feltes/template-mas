/* Initial beliefs and rules */
homeowner("jonas").
known_persons(["jonas", "alice", "bob"]).

/* Initial goals */
!inicializar_camera.

/* Plans */
+!inicializar_camera
  <- makeArtifact("camera_quarto","artifacts.Camera",[],D);
     focus(D);
     updateGUI.

+movimento : pessoa_presente(P) & local(L) & homeowner(H) & P == H & L == "frente"
  <- .print("Proprietário ", P, " chegando na frente da casa");
     .send(fechadura, achieve, unlock_and_open);
     .send(ar_condicionado, achieve, set_preferred_temperature);
     .send(lampada, achieve, set_preferred_lighting);
     .send(cortina, achieve, set_preferred_lighting);
     updateGUI.

+movimento : pessoa_presente(P) & local(L) & homeowner(H) & P == H & L == "saindo"
  <- .print("Proprietário ", P, " saindo da casa");
     .send(ar_condicionado, achieve, turn_off);
     .send(lampada, achieve, turn_off);
     .send(cortina, achieve, turn_off);
     .send(fechadura, achieve, close_and_lock);
     updateGUI.

+movimento : pessoa_presente(P) & local(L) & homeowner(H) & P == H & L == "dentro"
  <- .print("Proprietário ", P, " dentro da casa");
     .send(fechadura, achieve, unlock_and_open);
     .send(ar_condicionado, achieve, set_preferred_temperature);
     .send(lampada, achieve, set_preferred_lighting);
     .send(cortina, achieve, set_preferred_lighting);
     updateGUI.

+movimento : pessoa_presente(P) & local(L) & known_persons(KP) & not .member(P, KP)
  <- .print("Intruso detectado: ", P, " no local ", L);
     .send(ar_condicionado, achieve, intruder_alert);
     .send(lampada, achieve, intruder_alert);
     .send(cortina, achieve, intruder_alert);
     .send(fechadura, achieve, intruder_alert);
     updateGUI.

+!verificar_pessoa: pessoa_presente(P) & local(L)
  <- .print("Pessoa: ", P, " reconhecida no local ", L, " da casa.").

+closed <- .print("Close event from GUIInterface").

+!updateGUI
  <- .send(camera_quarto, achieve, updateGUI).