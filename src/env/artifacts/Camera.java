package artifacts;

import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cartago.*;
import cartago.tools.GUIArtifact;

public class Camera extends GUIArtifact {
    
    private InterfaceAC frame;
    private CameraLocal camera_model = new CameraLocal(true, "frente", "ninguem");
    private static final String[] KNOWN_PERSONS = {"jonas", "alice", "bob"};
    
    public void setup(String local, String pessoa) {
        camera_model.setLocal(local);
        camera_model.setPessoa(pessoa.toLowerCase());
        defineObsProperty("ligada", camera_model.isOn());
        defineObsProperty("local", camera_model.getLocal());    
        defineObsProperty("pessoa_presente", camera_model.getPessoa());    
        create_frame();
    }
    
    public void setup() {
        defineObsProperty("ligada", camera_model.isOn());
        defineObsProperty("local", camera_model.getLocal());    
        defineObsProperty("pessoa_presente", camera_model.getPessoa());    
        create_frame();
    }
    
    void create_frame() {
        frame = new InterfaceAC();
        linkActionEventToOp(frame.okButton, "ok");
        linkWindowClosingEventToOp(frame, "closed");
        frame.setVisible(true);
    }

    @OPERATION
    void ligar() {
        camera_model.setOn(true);
        getObsProperty("ligada").updateValue(camera_model.isOn());
        execInternalOp("updateGUI");
    }

    @OPERATION
    void desligar() {
        camera_model.setOn(false);
        getObsProperty("ligada").updateValue(camera_model.isOn());
        execInternalOp("updateGUI");
    }
    
    @INTERNAL_OPERATION 
    void ok(ActionEvent ev) {
        String pessoa = frame.getPessoa().toLowerCase();
        String local = (String) frame.getLocal();
        boolean isKnown = false;
        for (String known : KNOWN_PERSONS) {
            if (known.equals(pessoa)) {
                isKnown = true;
                break;
            }
        }
        if (!isKnown) {
            int choice = JOptionPane.showConfirmDialog(
                frame,
                "Pessoa '" + pessoa + "' não reconhecida. Deseja continuar como intruso?",
                "Aviso",
                JOptionPane.YES_NO_OPTION
            );
            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
        }
        camera_model.setLocal(local);
        camera_model.setPessoa(pessoa);
        getObsProperty("local").updateValue(camera_model.getLocal());
        getObsProperty("pessoa_presente").updateValue(camera_model.getPessoa());
        signal("movimento");
        execInternalOp("updateGUI");
    }
    
    @INTERNAL_OPERATION 
    void closed(WindowEvent ev) {
        signal("closed");
    }
    
    @OPERATION
    void updateGUI() {
        frame.updateStatus(camera_model.isOn(), camera_model.getLocal(), camera_model.getPessoa());
    }
    
    class CameraLocal {
        private boolean isOn = false;
        private String local = "unknown";
        private String pessoa = "noone";
        
        public CameraLocal(boolean isOn, String local, String p) {
            this.isOn = isOn;
            this.local = local;
            this.pessoa = p;
        }

        public boolean isOn() {
            return isOn;
        }

        public void setOn(boolean isOn) {
            this.isOn = isOn;
        }

        public String getLocal() {
            return local;
        }

        public void setLocal(String local) {
            this.local = local;
        }

        public String getPessoa() {
            return pessoa;
        }

        public void setPessoa(String pessoa) {
            this.pessoa = pessoa;
        }
    }
    
    class InterfaceAC extends JFrame {    
        private JButton okButton;
        private JTextField pessoa;
        private JComboBox<String> local;
        private JLabel ligadaStatus, localStatus, pessoaStatus;
        
        public InterfaceAC() {
            setTitle("Camera");
            setSize(300, 250);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            
            JLabel pessoaL = new JLabel("Nome da pessoa:");
            pessoa = new JTextField(10);
            pessoa.setText("jonas");
            
            JLabel localL = new JLabel("Local atual:");
            String[] locations = {"frente", "saindo", "dentro"};
            local = new JComboBox<>(locations);
            local.setSelectedItem("frente");
            local.setEditable(false);
            
            okButton = new JButton("OK");
            okButton.setSize(80, 50);
            
            ligadaStatus = new JLabel("Câmera: Desligada");
            localStatus = new JLabel("Local: desconhecido");
            pessoaStatus = new JLabel("Pessoa: nenhuma");
            
            panel.add(pessoaL);
            panel.add(pessoa);
            panel.add(localL);
            panel.add(local);
            panel.add(okButton);
            panel.add(new JLabel(" "));
            panel.add(ligadaStatus);
            panel.add(localStatus);
            panel.add(pessoaStatus);
            
            setContentPane(panel);
            updateStatus(camera_model.isOn(), camera_model.getLocal(), camera_model.getPessoa());
        }
        
        public String getPessoa() {
            return pessoa.getText();
        }
        
        public String getLocal() {
            return (String) local.getSelectedItem();
        }
        
        public void updateStatus(boolean ligada, String local, String pessoa) {
            ligadaStatus.setText("Câmera: " + (ligada ? "Ligada" : "Desligada"));
            localStatus.setText("Local: " + (local != null ? local : "desconhecido"));
            pessoaStatus.setText("Pessoa: " + (pessoa != null ? pessoa : "nenhuma"));
        }
    }
}