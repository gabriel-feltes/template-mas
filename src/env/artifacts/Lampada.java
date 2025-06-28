package artifacts;

import java.awt.event.WindowEvent;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cartago.*;
import cartago.tools.GUIArtifact;

public class Lampada extends GUIArtifact {
    
    private InterfaceLampada frame;
    private boolean ligado = false;
    
    public void setup() {
        defineObsProperty("ligado", ligado);
        create_frame();
    }
    
    void create_frame() {
        frame = new InterfaceLampada();
        linkActionEventToOp(frame.onButton, "ligar");
        linkActionEventToOp(frame.offButton, "desligar");
        linkWindowClosingEventToOp(frame, "closed");
        frame.setVisible(true);
    }

    @OPERATION
    void ligar() {
        System.out.println("[lampada] Operação ligar chamada");
        ligado = true;
        getObsProperty("ligado").updateValue(ligado);
        System.out.println("[lampada] Liguei a Lâmpada!");
        execInternalOp("updateGUI");
    }

    @OPERATION
    void desligar() {
        System.out.println("[lampada] Operação desligar chamada");
        ligado = false;
        getObsProperty("ligado").updateValue(ligado);
        System.out.println("[lampada] Lâmpada desligada!");
        execInternalOp("updateGUI");
    }
    
    @INTERNAL_OPERATION
    void closed(WindowEvent ev) {
        System.out.println("[lampada] Janela fechada");
        signal("closed");
    }
    
    @OPERATION
    void updateGUI() {
        System.out.println("[lampada] Atualizando GUI");
        frame.updateStatus();
    }
    
    class InterfaceLampada extends JFrame {
        private JButton onButton, offButton;
        private JLabel statusLabel;
        
        public InterfaceLampada() {
            setTitle("Lâmpada");
            setSize(200, 150);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            
            statusLabel = new JLabel("Lâmpada: Desligada");
            onButton = new JButton("Ligar");
            offButton = new JButton("Desligar");
            
            // Adicionar listeners manuais para depuração
            onButton.addActionListener(e -> {
                System.out.println("[lampada] Botão Ligar clicado");
                execInternalOp("ligar");
            });
            offButton.addActionListener(e -> {
                System.out.println("[lampada] Botão Desligar clicado");
                execInternalOp("desligar");
            });
            
            panel.add(statusLabel);
            panel.add(onButton);
            panel.add(offButton);
            
            setContentPane(panel);
            updateStatus();
        }
        
        public void updateStatus() {
            statusLabel.setText("Lâmpada: " + (ligado ? "Ligada" : "Desligada"));
        }
    }
}