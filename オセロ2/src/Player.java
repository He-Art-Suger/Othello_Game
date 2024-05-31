import java.io.Serializable;


public	class Player implements Serializable{

		private String myName = ""; //プレイヤ名
		private String myColor = ""; //自分の先手後手情報(白黒)
		private String oppColor = ""; //相手の先手後手情報(白黒)
		private int hopeHand=0;//希望ハンデレベル（0～5）
		private boolean drawflag = false;	//true:引き分け勝ちあり
		
		Player(){
			
		}

		// メソッド
		public void setName(String name){ // プレイヤ名を受付
			myName = name;
		}
		public String getName(){	// プレイヤ名を取得
			return myName;
		}
		
		public void setHopeHand(int hopeHand){ // 希望ハンデレベルを受付
			this.hopeHand = hopeHand;
			if(hopeHand >= 1) {
				drawflag = true;
			}else {
				drawflag = false;
			}
			
		}
		public int getHopeHand(){	// 希望ハンデレベルを取得
			return hopeHand;
		}
		public void setColor(String c){ // 自分の先手後手情報の受付
			myColor = c;
		}
		public String getColor(){ // 自分の先手後手情報の取得
			return myColor;
		}
		public void setOppColor(String c){ // 相手の先手後手情報の受付
			oppColor = c;
		}
		public String getOppColor(){ // 相手の先手後手情報の取得
			return oppColor;
		}
		
		
		public boolean getDrawFlag() {
			return drawflag;
		}
	}
