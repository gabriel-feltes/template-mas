package artifacts;

import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cartago.*;
import cartago.tools.GUIArtifact;

public class ArCondicionado extends GUIArtifact {
    
    private InterfaceAC frame;
    private ArCondicionadoModel ac_model = new ArCondicionadoModel(true, 25, 25);
    
    public void setup() {
        ac_model.setOn(false);
        ac_model.setTemperatura(25);
        defineObsProperty("ligado", ac_model.isOn());
        defineObsProperty("temperatura_ac", ac_model.getTemperatura());
        defineObsProperty("temperatura_ambiente", ac_model.getTemperaturaAmbiente());
        System.out.println("Inicializado com " + ac_model.getTemperatura());
        create_frame();
        new TemperaturaAmbienteSimulator().start();
    }
    
    void create_frame() {
        frame = new InterfaceAC();
        linkActionEventToOp(frame.okButton, "ok");
        linkWindowClosingEventToOp(frame, "closed");
        frame.setVisible(true);
    }

    @OPERATION
    void ligar() {
        ac_model.setOn(true);
        getObsProperty("ligado").updateValue(ac_model.isOn());
        signal("alterado");
    }

    @OPERATION
    void desligar() {
        ac_model.setOn(false);
        getObsProperty("ligado").updateValue(ac_model.isOn());
        signal("alterado");
    }
    
    @OPERATION
    void definir_temperatura(int temp) {
        ac_model.setTemperatura(temp);
        getObsProperty("temperatura_ac").updateValue(ac_model.getTemperatura());
        signal("alterado");
    }
    
    @INTERNAL_OPERATION 
    void ok(ActionEvent ev) {
        try {
            ac_model.setTemperatura(Integer.parseInt(frame.getTemperatura()));
            getObsProperty("temperatura_ac").updateValue(ac_model.getTemperatura());
            signal("alterado");
        } catch (NumberFormatException e) {
            // Ignore invalid input
        }
    }
    
    @INTERNAL_OPERATION 
    void closed(WindowEvent ev) {
        signal("closed");
    }
    
    @OPERATION
    void updateGUI() {
        frame.updateStatus();
    }
    
    class ArCondicionadoModel {
        private boolean isOn = false;
        private int temperatura = 25;
        private int temperatura_ambiente = 25;
        
        public ArCondicionadoModel(boolean isOn, int temp, int temp_amb) {
            this.isOn = isOn;
            this.temperatura = temp;
            this.temperatura_ambiente = temp_amb;
        }

        public boolean isOn() {
            return isOn;
        }

        public void setOn(boolean isOn) {
            this.isOn = isOn;
        }

        public int getTemperatura() {
            return temperatura;
        }

        public void setTemperatura(int temperatura) {
            this.temperatura = temperatura;
        }
        
        public int getTemperaturaAmbiente() {
            return temperatura_ambiente;
        }

        public void setTemperaturaAmbiente(int temperatura_ambiente) {
            this.temperatura_ambiente = temperatura_ambiente;
        }
    }
    
    class TemperaturaAmbienteSimulator extends Thread {
        private boolean running = true;

        public void stopThread() {
            this.running = false;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    int temp_atual = ac_model.getTemperaturaAmbiente();
                    int temp_desejada = ac_model.getTemperatura();
                    if (temp_atual < temp_desejada) {
                        ac_model.setTemperaturaAmbiente(temp_atual + 1);
                    } else if (temp_atual > temp_desejada && ac_model.isOn()) {
                        ac_model.setTemperaturaAmbiente(temp_atual - 1);
                    }
                    getObsProperty("temperatura_ambiente").updateValue(ac_model.getTemperaturaAmbiente());
                    execInternalOp("updateGUI");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    class InterfaceAC extends JFrame {    
        private JButton okButton;
        private JTextField temperatura;
        private JLabel ambienteLabel, desejadaLabel;
        
        public InterfaceAC() {
            setTitle("Ar Condicionado");
            setSize(200, 250);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            
            JLabel tempL = new JLabel("Temperatura desejada:");
            temperatura = new JTextField(10);
            temperatura.setText("25");
            
            okButton = new JButton("OK");
            okButton.setSize(80, 50);
            
            ambienteLabel = new JLabel("Temperatura Ambiente: Desconhecido");
            desejadaLabel = new JLabel("Temperatura AC: Desconhecido");
            
            panel.add(tempL);
            panel.add(temperatura);
            panel.add(okButton);
            panel.add(new JLabel(" "));
            panel.add(ambienteLabel);
            panel.add(desejadaLabel);
            
            setContentPane(panel);
            updateStatus();
        }
        
        public String getTemperatura() {
            return temperatura.getText();
        }
        
        public void updateStatus() {
            ambienteLabel.setText("Temperatura Ambiente: " + ac_model.getTemperaturaAmbiente() + "°C");
            desejadaLabel.setText("Temperatura AC: " + ac_model.getTemperatura() + "°C");
        }
    }
}