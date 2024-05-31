import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
//結果表示
	public class Kekka
	{
		private ImageIcon winIcon = new ImageIcon("win.png");
		private ImageIcon loseIcon = new ImageIcon("lose.png");
		private ImageIcon drawIcon = new ImageIcon("draw.png");
		
		private JLabel Lwin = new JLabel(winIcon);
		private JLabel Llose = new JLabel(loseIcon);
		private JLabel Ldraw = new JLabel(drawIcon);
		private JLabel info = new JLabel();
		private JLabel blacknum = new JLabel();
		private JLabel whitenum = new JLabel();
		private JButton endgame = new JButton();
		
		public Kekka()
		{			
			int row = game.getRow();
			
			Lwin.setLayout(null);
			Llose.setLayout(null);
			Ldraw.setLayout(null);
			info.setLayout(null);
			
			c.add(Lwin);
			c.add(Llose);
			c.add(Ldraw);
			c.add(info);
			
			info.add(blacknum);
			info.add(whitenum);
			info.add(endgame);
			
			Lwin.setBounds(row * 5, row * 5, row * 45, row * 15);
			Llose.setBounds(row * 5, row * 5, row * 45, row * 15);
			Ldraw.setBounds(row * 5, row * 5, row * 45, row * 15);
			blacknum.setBounds(row * 5, row * 25, 20, 10);
			whitenum.setBounds(row * 40, row * 25, 20, 10);
			endgame.setBounds(row * 5, row * 45, 20, 10);
			endgame.setText("終了");
			
			Lwin.setVisible(false);
			Llose.setVisible(false);
			Ldraw.setVisible(false);
			info.setVisible(false);
			
		}
		
		public void enddisp()
		{
			if(game.count("black") > game.count("white"))
			{
				if(player.myColor == "black")
				{
					//win.jpegを表示
					Lwin.setVisible(true);
				}
				else
				{
					//lose.jpegを表示
					Llose.setVisible(true);
				}
			}
			else if(game.count("black") < game.count("white"))
			{
				if(player.myColor == "white")
				{
					//win.jpegを表示
					Lwin.setVisible(true);
				}
				else
				{
					//lose.jpegを表示
					Llose.setVisible(true);
				}
			}
			else
			{
				//draw.jpegを表示
				Ldraw.setVisible(true);
			}
			blacknum.setText(String.valueOf(game.count("black")));
			whitenum.setText(String.valueOf(game.count("white")));
			info.setVisible(true);
		}
	}