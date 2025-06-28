package artifacts;

import java.awt.event.WindowEvent;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import cartago.*;
import cartago.tools.GUIArtifact;

public class Fechadura extends GUIArtifact {
    
    private InterfaceFechadura frame;
    private boolean aberta = false;
    private boolean trancada = true;
    
    public void setup() {
        defineObsProperty("aberta", aberta);
        defineObsProperty("trancada", trancada);
        create_frame();
        System.out.println("[fechadura] Porta Fechada!");
        System.out.println("[fechadura] TRANQUEI a porta!");
        System.out.println("[fechadura] Porta Trancada!");
    }
    
    void create_frame() {
        frame = new InterfaceFechadura();
        linkActionEventToOp(frame.openButton, "open");
        linkActionEventToOp(frame.closeButton, "close");
        linkActionEventToOp(frame.lockButton, "lock");
        linkActionEventToOp(frame.unlockButton, "unlock");
        frame.setVisible(true);
    }

    @OPERATION
    void open() {
        if (trancada) {
            System.out.println("[fechadura] Não é possível abrir: porta está trancada");
            JOptionPane.showMessageDialog(frame, "Não é possível abrir a porta enquanto ela está trancada.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        System.out.println("[fechadura] Operação open chamada");
        aberta = true;
        getObsProperty("aberta").updateValue(aberta);
        System.out.println("[fechadura] Porta aberta!");
        execInternalOp("updateGUI");
    }

    @OPERATION
    void close() {
        System.out.println("[fechadura] Operação close chamada");
        aberta = false;
        getObsProperty("aberta").updateValue(aberta);
        System.out.println("[fechadura] Porta fechada!");
        execInternalOp("updateGUI");
    }

    @OPERATION
    void lock() {
        if (aberta) {
            System.out.println("[fechadura] Não é possível trancar: porta está aberta");
            JOptionPane.showMessageDialog(frame, "Não é possível trancar a porta enquanto ela está aberta.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        System.out.println("[fechadura] Operação lock chamada");
        trancada = true;
        getObsProperty("trancada").updateValue(trancada);
        System.out.println("[fechadura] Porta trancada!");
        execInternalOp("updateGUI");
    }

    @OPERATION
    void unlock() {
        System.out.println("[fechadura] Operação unlock chamada");
        trancada = false;
        getObsProperty("trancada").updateValue(trancada);
        System.out.println("[fechadura] Porta destrancada!");
        execInternalOp("updateGUI");
    }
    
    @INTERNAL_OPERATION
    void closed(WindowEvent ev) {
        System.out.println("[fechadura] Janela fechada");
        signal("closed");
    }
    
    @OPERATION
    void updateGUI() {
        System.out.println("[fechadura] Atualizando GUI");
        frame.updateStatus();
    }
    
    class InterfaceFechadura extends JFrame {
        private JButton openButton, closeButton, lockButton, unlockButton;
        private JLabel statusLabel, trancadaLabel;
        
        public InterfaceFechadura() {
            setTitle("Fechadura");
            setSize(200, 200);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            
            statusLabel = new JLabel("Porta: Fechada");
            trancadaLabel = new JLabel("Porta Trancada: Sim");

            openButton = new JButton("Abrir");
            closeButton = new JButton("Fechar");
            lockButton = new JButton("Trancar");
            unlockButton = new JButton("Destrancar");
            
            openButton.addActionListener(e -> {
                System.out.println("[fechadura] Botão Abrir clicado");
                execInternalOp("open");
            });
            closeButton.addActionListener(e -> {
                System.out.println("[fechadura] Botão Fechar clicado");
                execInternalOp("close");
            });
            lockButton.addActionListener(e -> {
                System.out.println("[fechadura] Botão Trancar clicado");
                execInternalOp("lock");
            });
            unlockButton.addActionListener(e -> {
                System.out.println("[fechadura] Botão Destrancar clicado");
                execInternalOp("unlock");
            });
            
            panel.add(statusLabel);
            panel.add(trancadaLabel);
            panel.add(openButton);
            panel.add(closeButton);
            panel.add(lockButton);
            panel.add(unlockButton);
            
            setContentPane(panel);
            updateStatus();
        }
        
        public void updateStatus() {
            statusLabel.setText("Porta: " + (aberta ? "Aberta" : "Fechada"));
            trancadaLabel.setText("Porta Trancada: " + (trancada ? "Sim" : "Não"));
        }
    }
}