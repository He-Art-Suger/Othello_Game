

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Result extends JFrame implements ActionListener {
    JLabel label;
    JComboBox combobox;
    int judge;
    String color = "White";
    int blackStone;
    int whiteStone;
    ImageIcon icon1;
    JPanel c = (JPanel)getContentPane();
    JButton b1;

    Result(String title, String color, int judge, int bs, int ws) {
            super(title);
            this.color = color;
            blackStone = bs;
            whiteStone = ws;
            this.judge = judge;
            
            c.setLayout(null);
            
            c.setBackground(new Color(255,255,255));
            
            if(judge==0) {
            	icon1 = new ImageIcon("draw.png");
            	System.out.println("drawでした");
            }else if(judge==1){
            	icon1 = new ImageIcon("win.png");
            	System.out.println("winでした");
            }else if(judge==2){
            	icon1 = new ImageIcon("lose.png");
            	System.out.println("loseでした");
            }/*nを勝敗情報を管理する変数として仮においてる*/
            
            Image smallImg = icon1.getImage().getScaledInstance((int) (icon1.getIconWidth() * 0.5), -1,Image.SCALE_SMOOTH);
            ImageIcon smallIcon = new ImageIcon(smallImg);
            JLabel label1 = new JLabel(smallIcon);
            label1.setBounds(0,50,500,300);
            c.add(label1);
            
            ImageIcon icon2 = new ImageIcon("Black.jpg");
            JLabel label2 = new JLabel(icon2);
            label2.setBounds(45,280,40,40);
            c.add(label2);
            
            ImageIcon icon3 = new ImageIcon("White.jpg");
            JLabel label3 = new JLabel(icon3);
            label3.setBounds(405,280,40,40);
            c.add(label3);
            
            label = new JLabel(String.valueOf(blackStone));
            Font f =new Font("",Font.PLAIN,30);
            label.setFont(f);
            label.setBounds(45,330,40,40);
            c.add(label);
            
            label = new JLabel(String.valueOf(whiteStone));
            label.setFont(f);
            label.setBounds(405,330,40,40);
            c.add(label);
            
            b1 = new JButton("終了");
            b1.setBounds(220,380,60,20);
            b1.addActionListener(this);
            c.add(b1);
            
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setVisible(true);
            setSize(520, 600);
    }

    public void actionPerformed(ActionEvent e){
    	if(e.getSource() == b1) {
    		
    		
    		
    	}
    }

}

