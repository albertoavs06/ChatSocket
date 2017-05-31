package br.edu.ifrn.chat;

import br.edu.ifrn.chat.service.ServidorService;
import static javax.swing.JOptionPane.PLAIN_MESSAGE;
import static javax.swing.JOptionPane.showInputDialog;

public class Servidor {
       
    
    public static void main(String args[]) {
        String Porta = showInputDialog(null, "Informe a Porta do Servidor: ", "", PLAIN_MESSAGE);
       if(Porta != null){
           int n = Integer.parseInt(Porta);
          if (n >= 5000){
            ServidorService.getInstance(n); 
           }else{
           System.out.println("Porta Inválida. Utilize porta acima de 5000.");   
           System.out.println("Falha na inicialização do Servidor.");
          }
       } else {
           System.out.println("Porta Padrão do Servidor : 7896.");
           // Porta Padrão do 
          ServidorService.getInstance(7896);  
       }                      
    }
}
