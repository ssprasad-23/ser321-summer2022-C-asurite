package Assignment3Starter.udp;

import Assignment3Starter.udp.services.SessionService;
import Assignment3Starter.udp.socket.SocketService;
import Assignment3Starter.udp.socket.UDPSocketService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class StartDialog extends JDialog
{

    private SessionService sessionService;
    private JTextField name;

    public StartDialog()
    {
        super(new JFrame("Start Game"), "Start Game");

        this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        this.setMinimumSize(new Dimension(300, 200));
        this.name = new JTextField();

        JButton add = new JButton("Start");
        add.addActionListener(
                new ActionListener() {
                    //PROGRAMM STARTS HERE
                    public void actionPerformed(ActionEvent arg0) {

                        //Here we choose which protocol to use
                        SocketService socketService = new UDPSocketService();
                        sessionService = new SessionService(socketService);
                        ClientGui main = new ClientGui(socketService, sessionService);

                        String txt = getName();
                        System.out.println(txt);

                        sessionService.createSession(getName());

                        setVisible(false);
                        main.newGame(1);
                        main.show(true);
                        close();
                    }
                });

        this.setLayout(new GridLayout(4,1));
        this.add(new JLabel("Enter your name"));
        this.add(name);
        this.add(add);
        this.pack();
    }


    private void close()
    {
        this.dispose();
    }

    public String getName()
    {

        return this.name.getText();

    }

}