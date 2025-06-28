package artifacts;

import java.awt.event.WindowEvent;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;

import cartago.*;
import cartago.tools.GUIArtifact;

public class Cortina extends GUIArtifact {
    
    private InterfaceCortina frame;
    private int nivel_abertura = 0;
    
    public void setup() {
        defineObsProperty("nivel_abertura", nivel_abertura);
        create_frame();
        System.out.println("[cortina] Nível de abertura ANTES: " + nivel_abertura);
        definir_nivel_abertura(100);
    }
    
    void create_frame() {
        frame = new InterfaceCortina();
        linkChangeEventToOp(frame.slider, "setNivelAbertura");
        linkWindowClosingEventToOp(frame, "closed");
        frame.setVisible(true);
    }

    @OPERATION
    void definir_nivel_abertura(int nivel) {
        if (nivel >= 0 && nivel <= 100) {
            nivel_abertura = nivel;
            getObsProperty("nivel_abertura").updateValue(nivel_abertura);
            System.out.println("[cortina] Nível de abertura DEPOIS: " + nivel_abertura);
            execInternalOp("updateGUI");
        }
    }
    
    @INTERNAL_OPERATION
    void setNivelAbertura(ChangeEvent ev) {
        JSlider source = (JSlider) ev.getSource();
        definir_nivel_abertura(source.getValue());
    }
    
    @INTERNAL_OPERATION
    void closed(WindowEvent ev) {
        signal("closed");
    }
    
    @OPERATION
    void updateGUI() {
        frame.updateStatus();
    }
    
    class InterfaceCortina extends JFrame {
        private JSlider slider;
        private JLabel nivelLabel;
        
        public InterfaceCortina() {
            setTitle("Cortina");
            setSize(200, 150);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            
            nivelLabel = new JLabel("Nível de Abertura: " + nivel_abertura + "%");
            slider = new JSlider(JSlider.HORIZONTAL, 0, 100, nivel_abertura);
            slider.setMajorTickSpacing(20);
            slider.setPaintTicks(true);
            slider.setPaintLabels(true);
            
            panel.add(nivelLabel);
            panel.add(slider);
            
            setContentPane(panel);
            updateStatus();
        }
        
        public void updateStatus() {
            nivelLabel.setText("Nível de Abertura: " + nivel_abertura + "%");
            slider.setValue(nivel_abertura);
        }
    }
}