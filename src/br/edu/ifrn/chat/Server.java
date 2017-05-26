package br.edu.ifrn.chat;

import br.edu.ifrn.chat.service.ServerService;
import static javax.swing.JOptionPane.PLAIN_MESSAGE;
import static javax.swing.JOptionPane.showInputDialog;

public class Server {
      static int Porta = 0; 
    
    public static void main(String args[]) {
          Porta = Integer.parseInt(showInputDialog(null, "Informe a Porta do Servidor: ", "", PLAIN_MESSAGE));
       if (Porta >= 5000){
            ServerService.getInstance(Porta); 
       }else{
           System.out.println("Falha na inicialização do Servidor.");
       }
           
                      
  
    }
}
