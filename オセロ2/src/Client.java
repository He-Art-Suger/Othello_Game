
//パッケージのインポート
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Client extends JFrame implements MouseListener {
	private JButton buttonArray[];// オセロ盤用のボタン配列
	private JButton enable; // 強調表示切り替えボタン
	private JLabel handLabel; // 色表示用ラベル
	private JLabel colorLabel; // 色表示用ラベル
	private JLabel turnLabel; // 手番表示用ラベル
	private Container c; // コンテナ
	private ImageIcon blackIcon, whiteIcon, emptyIcon, enableIcon; // アイコン
	private PrintWriter out;// データ送信用オブジェクト
	private Receiver receiver; // データ受信用オブジェクト
	private Othello game; // Othelloオブジェクト
	private Player player; // Playerオブジェクト
	private Result result;	//Resultオブジェクト
	private static Login login;
	private boolean highlight; // 強調表示の有無

	// コンストラクタ
	public Client(Othello game, Player player) { // OthelloオブジェクトとPlayerオブジェクトを引数とする
		this.game = game; // 引数のOthelloオブジェクトを渡す
		this.player = player; // 引数のPlayerオブジェクトを渡す
		String[][] board = game.getBoard(); // getGridメソッドにより局面情報を取得
		int row = game.getRow(); // getRowメソッドによりオセロ盤の縦横マスの数を取得
		// ウィンドウ設定
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// ウィンドウを閉じる場合の処理
		setTitle("ネットワーク対戦型オセロゲーム");// ウィンドウのタイトル
		setSize(row * 45 + 10, row * 45 + 200);// ウィンドウのサイズを設定
		c = getContentPane();// フレームのペインを取得
		// アイコン設定(画像ファイルをアイコンとして使う)
		whiteIcon = new ImageIcon("White.jpg");
		blackIcon = new ImageIcon("Black.jpg");
		emptyIcon = new ImageIcon("GreenFrame.jpeg");
		enableIcon = new ImageIcon("EnableFrame.jpeg");
		c.setLayout(null);//
		// オセロ盤の生成
		buttonArray = new JButton[row * row];// ボタンの配列を作成
		for (int i = 0; i < row * row; i++) {
			if (board[i / row][i % row].equals("black")) {
				buttonArray[i] = new JButton(blackIcon);
			} // 盤面状態に応じたアイコンを設定
			if (board[i / row][i % row].equals("white")) {
				buttonArray[i] = new JButton(whiteIcon);
			} // 盤面状態に応じたアイコンを設定
			if (board[i / row][i % row].equals("empty")) {
				buttonArray[i] = new JButton(emptyIcon);
			} // 盤面状態に応じたアイコンを設定
			c.add(buttonArray[i]);// ボタンの配列をペインに貼り付け
			// ボタンを配置する
			int x = (i % row) * 45;
			int y = (int) (i / row) * 45;
			buttonArray[i].setBounds(x, y, 45, 45);// ボタンの大きさと位置を設定する．
			buttonArray[i].addMouseListener(this);// マウス操作を認識できるようにする
			buttonArray[i].setActionCommand(Integer.toString(i));// ボタンを識別するための名前(番号)を付加する
		}
		// 強調表示切り替えボタン
				highlight = true; // 最初は強調表示on
				enable = new JButton("強調表示ON");// 強調表示切り替えを作成
				c.add(enable); // ペインに貼り付け
				enable.setBounds((row * 45 + 10) / 4, row * 45 + 30, (row * 45 + 10) / 2, 30);// 終了ボタンの境界を設定
				enable.addMouseListener(this);// マウス操作を認識できるようにする
				enable.setActionCommand("enable");// ボタンを識別するための名前を付加する
				// ハンデ情報表示用ラベル
				handLabel = new JLabel("");// ハンデ情報を表示するためのラベルを作成
				handLabel.setBounds(10, row * 45, row * 45 + 10, 30);// 境界を設定
				c.add(handLabel);// ペインに貼り付け
				// 色表示用ラベル
				colorLabel = new JLabel("待機中");// 色情報を表示するためのラベルを作成
				colorLabel.setBounds(10, row * 45 + 60, row * 45 + 10, 30);// 境界を設定
				c.add(colorLabel);// 色表示用ラベルをペインに貼り付け
				// 手番表示用ラベル
				turnLabel = new JLabel("");// 手番情報を表示するためのラベルを作成
				turnLabel.setBounds(10, row * 45 + 120, row * 45 + 10, 30);// 境界を設定
				c.add(turnLabel);// 手番情報ラベルをペインに貼り付け
	}
	

	// サーバに接続するメソッド
	public boolean connectServer(String ipAddress, int port, int hopeHand) {
			Socket socket = null;
		try {
			socket = new Socket(ipAddress, port); // サーバ(ipAddress, port)に接続
			out = new PrintWriter(socket.getOutputStream(), true); // データ送信用オブジェクトの用意

			// 希望ハンデレベルをサーバへ送信
			out.println(hopeHand);
			out.flush();
			System.out.println("サーバに希望ハンデレベル " + hopeHand + " を送信しました");

			// 受信用オブジェクトを用意
			// ここで、ハンデレベルと先手後手情報を受け取る
			receiver = new Receiver(socket);

			// 受信用オブジェクトのスレッドを起動する
			receiver.start();
			return true;
		} catch (UnknownHostException e) {
			System.err.println("ホストのIPアドレスが判定できません: " + e);
			// System.exit(-1);
			return false;
		} catch (IOException e) {
			System.err.println("サーバ接続時にエラーが発生しました: " + e);
			return false;
			// System.exit(-1);
		}
	}

	// データ受信用スレッド(内部クラス)
	class Receiver extends Thread {
		private InputStreamReader sisr; // 受信データ用文字ストリーム
		private BufferedReader br; // 文字ストリーム用のバッファ

		// 内部クラスReceiverのコンストラクタ
		Receiver(Socket socket) {
			try {
				sisr = new InputStreamReader(socket.getInputStream()); // 受信したバイトデータを文字ストリームに
				br = new BufferedReader(sisr);// 文字ストリームをバッファリングする
			} catch (IOException e) {
				System.err.println("データ受信時にエラーが発生しました: " + e);
			}
		}

		// 内部クラス Receiverのrunメソッド
		public void run() {

			// 最初はハンデレベルと自分の色を受け取るための無限ループを行う
			try {
				// ハンデレベルを受け取るための無限ループ
				String handLevel;
				while (true) {
					handLevel = br.readLine();// 受信データを一行分読み込む
					if (handLevel != null) {// データを受信したら
						System.out.println("クライアントはハンデレベル" + handLevel + "を受信しました");
						game.setHandLevel(handLevel);
						break; // 無限ループを抜ける
					}
				}
				// 先手後手情報を受け取るための無限ループ
				String myColor;
				while (true) {
					myColor = br.readLine();// 受信データを一行分読み込む
					if (myColor != null) {// データを受信したら
						System.out.println("クライアントは自分の色が " + myColor + " であると受信しました");
						player.setColor(myColor);
						setVisible(true); // 対局画面を表示
						login.setVisible(false); //ログイン画面の非表示
						break; // 無限ループを抜ける
					}

				}
			} catch (IOException e) {
				System.err.println("データ受信時にエラーが発生しました: " + e);
			}
			// 自分の色が黒の場合
			if (player.getColor().equals("black")) {
				player.setOppColor("white");
				colorLabel.setText(player.getName() + " さんの色は黒です");
			}
			// 自分の色が白の場合
			else {
				player.setOppColor("black");
				colorLabel.setText(player.getName() + " さんの色は白です");
			}

			// ハンデ説明の出力
			printHand();

			// 盤面を更新
			updateDisp();

			try {
				String inputLine;
				while (true) {// データを受信し続ける
					inputLine = br.readLine();// 受信データを一行分読み込む
					
					if (inputLine != null) {// データを受信したら
						
						if(inputLine.equals("disconnect")) {
							System.out.println("対戦相手の接続が切れました。");
							turnLabel.setForeground(Color.red);
							turnLabel.setText("対戦相手の接続が切れました。");
							win();
							break;
						}
												
						receiveMessage(inputLine);// データ受信用メソッドを呼び出す
					}
				}
			} catch (IOException e) {
				System.err.println("データ受信時にエラーが発生しました: " + e);
			}
		}
	}

	// ハンデレベルの説明を出力するメソッド
	public void printHand() {
		// ハンデレベル0
		if (game.getHandLevel().equals("0")) {
			handLabel.setText("ハンデなし");
		}
		// ハンデレベル1
		else if (game.getHandLevel().equals("1")) {
			handLabel.setText("ハンデレベル1：石の数が同等の場合黒の勝ちとする");
		}
		// ハンデレベル2
		if (game.getHandLevel().equals("2")) {
			handLabel.setText("ハンデレベル2：左上隅に黒石を置く");
		}
		// ハンデレベル3
		if (game.getHandLevel().equals("3")) {
			handLabel.setText("ハンデレベル3：左上隅と右下隅に黒石を置く");
		}
		// ハンデレベル4
		if (game.getHandLevel().equals("4")) {
			handLabel.setText("ハンデレベル4：左上隅，右下隅，右上隅に黒石を置く");
		}
		// ハンデレベル5
		if (game.getHandLevel().equals("5")) {
			handLabel.setText("ハンデレベル5：４か所全ての隅に黒石を置く");
		} else
			;
	}

	// サーバに操作情報を送信したときに使われるメソッド
	public void sendMessage(String msg) {
		out.println(msg);// 送信データをバッファに書き出す
		out.flush();// 送信データを送る
		System.out.println("サーバにメッセージ " + msg + " を送信しました");
	}

	// 盤面情報を取得し、画面を更新するメソッドupdateDisp
	public void updateDisp() {
		String[][] newBoard = game.getBoard(); // getGridメソッドにより局面情報を取得
		int row = game.getRow();
		// 1マスずつ色を確認する
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < row; j++) {
				// [i][j] マスが黒の場合、画像を黒コマへ変更
				if (newBoard[i][j].equals("black")) {
					buttonArray[i * row + j].setIcon(blackIcon);
				}
				// [i][j] マスが白の場合、画像を白コマへ変更
				else if (newBoard[i][j].equals("white")) {
					buttonArray[i * row + j].setIcon(whiteIcon);
				}
				// [i][j] マスに何も置かれていない場合
				else {
					// 自分のターン かつ 強調表示がon かつ コマをおける場合、強調表示のアイコンへ変更
					if (game.getTurn().equals(player.getColor()) && highlight
							&& game.judge(i * row + j, game.getTurn())) {
						buttonArray[i * row + j].setIcon(enableIcon);
					}
					// それ以外の場合、空のアイコンへ変更
					else {
						buttonArray[i * row + j].setIcon(emptyIcon);
					}
				}
			}
		}

		if (highlight) {
			enable.setText("強調表示ON");
		} else {
			enable.setText("強調表示OFF");
		}
		
		turnLabel.setForeground(Color.black);

		// 自分の手番の場合
		if (game.getTurn().equals(player.getColor())) {
			turnLabel.setText(player.getName() + " さんの番です.");
		}
		// 相手の手番の場合
		else if (game.getTurn().equals(player.getOppColor())) {
			turnLabel.setText("相手の番です.");
		}

		// 決着が付いた場合、勝敗情報を表示する
		else {
			int myNum = game.count(player.getColor());
			int oppNum = game.count(player.getOppColor());
			// 自分のコマの方が多い場合
			if (myNum > oppNum) {
				win();
				turnLabel.setText("あなたの勝ちです.");
			}
			// 相手のコマの方が多い場合
			else if (myNum < oppNum) {
				lose();
				turnLabel.setText("あなたの負けです.");
			}
			// コマ数が同じ場合
			else {
				// ハンデレベルが1の場合
				if (game.getHandLevel().equals("1")) {
					// 自分が黒の場合
					if (player.getColor().equals("black")) {
						win();
						turnLabel.setText("黒：" + myNum + " 白：" + myNum + " あなたの勝ちです");
					}
					// 自分が白の場合
					else {
						lose();
						turnLabel.setText("黒：" + myNum + " 白：" + myNum + " あなたの負けです");
					}

				}
				// ハンデレベルが1でない場合
				else {
					draw();
					turnLabel.setText("引き分けです");
				}
			}
		}

		System.out.println("盤面を更新しました");

	}
	
	//勝利時の結果画面を表示
	public void win() {
		result = new Result(this,player.getColor(),1,game.count("black"),game.count("white"));
		
	}
	//敗北時の結果画面を表示
	public void lose() {
		result = new Result(this,player.getColor(),2,game.count("black"),game.count("white"));
		

	}
	//引き分け時の結果画面を表示
	public void draw() {
		result = new Result(this,player.getColor(),0,game.count("black"),game.count("white"));
		
	}

	// サーバから操作情報を受信したときに使われるメソッドreceiveMessage
	public void receiveMessage(String msg) {
		System.out.println("サーバからメッセージ " + msg + " を受信しました"); // テスト用標準出力
		
		// 指定したマスにコマを置き、盤面情報を更新する
		game.putStone(Integer.parseInt(msg), game.getTurn());
		updateDisp();
	
		System.out.println("現在の手番は" + game.getTurn() + "です");
		//turnLabel.setText("あなたの手番です.");
		
	}

	public void acceptOperation(String command) { // プレイヤの操作を受付
	}

	// マウスクリック時の処理
		public void mouseClicked(MouseEvent e) {
			JButton theButton = (JButton) e.getComponent();// クリックしたオブジェクトを得る．キャストを忘れずに
			String command = theButton.getActionCommand();// ボタンの名前を取り出す
			System.out.println("マウスがクリックされました。押されたボタンは " + command + "です。");
	
			// 強調表示の切り替えボタンを押した場合
			if (command.equals("enable")) {
				// on off の切り替え
				highlight = !(highlight);
				// 盤面情報の更新
				updateDisp();
			}
	
			// 自分の手番かつ、クリックしたマスにコマを置くことのできる場合 → 盤面情報を更新し、操作情報をサーバへ送る
			if (((player.getColor().equals(game.getTurn())) == true)){
				if(game.putStone(Integer.parseInt(command), game.getTurn()) == true) {
					// 盤面情報の更新
					updateDisp();
	
					// 操作情報をサーバに送る
					sendMessage(command);
					
				}else if(game.putStone(Integer.parseInt(command), game.getTurn()) == false){
						
					turnLabel.setForeground(Color.blue);
					turnLabel.setText("その場所に石を置くことはできません");
				}
			}
		}
		
	//対局終了後に呼び出される	
	public void restart() {
		turnLabel.setText("See you NextGame!!");
		
		//ゲームが終わったことを伝えるメッセージを送信
		sendMessage("GameSet");
		
		//画面の非表示化
		result.setVisible(false); //リザルト画面
		this.setVisible(false);  //対局画面
		
		//オブジェクトの初期化
		result = null;
		player = null;
		game = null;
		receiver = null;
		out = null;
		
		// 新たなプレイヤオブジェクトの用意
		player = new Player();
		// 新たなオセロオブジェクトの用意
		game = new Othello();
		
		//ログイン画面
		login.allEnabled(true);
		login.messageLabel.setText("名前を入力し、希望するハンデレベルを選択してください.");	
		login.setVisible(true);
	}
				

	public void mouseEntered(MouseEvent e) {
	}// マウスがオブジェクトに入ったときの処理

	public void mouseExited(MouseEvent e) {
	}// マウスがオブジェクトから出たときの処理

	public void mousePressed(MouseEvent e) {
	}// マウスでオブジェクトを押したときの処理

	public void mouseReleased(MouseEvent e) {
	}// マウスで押していたオブジェクトを離したときの処理

	// mainメソッド
	public static void main(String args[]) {
		// プレイヤオブジェクトの用意
		Player player = new Player();

		// オセロオブジェクトの用意
		Othello game = new Othello();

		// クライアントオブジェクトの用意
		Client oclient = new Client(game, player); // 引数としてオセロオブジェクト、プレイヤオブジェクトを渡す

		// Login オブジェクトの用意
		login = oclient.new Login(args[0], 10000);// サーバのIPアドレス、ポート番号を与える
		
		
	}
	
	

	// 内部クラスLogin
	class Login extends JFrame {

		// フィールド
		JTextField nameTF; // 名前を入力するテキストフィールド
		JButton nameButton; // 入力した名前を消去するボタン
		JComboBox handCombobox; // 希望ハンデレベルを選択するcombobox
		JButton okButton; // OKボタン
		JLabel messageLabel; // メッセージを表示するラベル

		String serverIP;
		int port;

		// 内部クラスLoginのコンストラクタ
		Login(String serverIP, int port) {
			// 引数を与える
			this.serverIP = serverIP;
			this.port = port;

			JPanel c = (JPanel) getContentPane();
			c.setBackground(Color.white);// 背景は白
			c.setLayout(null); // レイアウトは無効（自分で位置座標を設定）

			// メッセージを表示するラベル
			messageLabel = new JLabel("名前を入力し、希望するハンデレベルを選択してください.");
			messageLabel.setLocation(40, 10);
			messageLabel.setSize(400, 20);
			c.add(messageLabel);

			// "名前" と表示するラベル
			JLabel nameLabel = new JLabel("プレイヤー名：");
			nameLabel.setLocation(40, 40);
			nameLabel.setSize(200, 20);
			c.add(nameLabel);

			// 名前を入力するテキストフィールド
			nameTF = new JTextField(16);
			nameTF.setLocation(130, 40);
			nameTF.setSize(200, 20);
			c.add(nameTF);

			// 入力された名前を消去するボタン
			nameButton = new JButton("取消");
			nameButton.setLocation(330, 40);
			nameButton.setSize(60, 20);
			nameButton.addActionListener(new myListener1());
			c.add(nameButton);

			// "ハンデレベル" と表示するラベル
			JLabel handLabel = new JLabel("ハンデレベル：");
			handLabel.setLocation(40, 70);
			handLabel.setSize(200, 20);
			c.add(handLabel);

			// ハンデレベルを選択するコンボボックス
			String[] combodata = { "レベル0", "レベル1", "レベル2", "レベル3", "レベル4", "レベル5" };
			handCombobox = new JComboBox(combodata);
			handCombobox.setLocation(130, 70);
			handCombobox.setSize(90, 20);
			c.add(handCombobox);

			// 上の境界線を表示するラベル
			JLabel lineLabel1 = new JLabel(
					"____________________________________________________________________________");
			lineLabel1.setLocation(0, 90);
			lineLabel1.setSize(520, 20);
			c.add(lineLabel1);
			
			// 下の境界線を表示するラベル
						JLabel lineLabel3 = new JLabel(
								"____________________________________________________________________________");
						lineLabel3.setLocation(0, 390);
						lineLabel3.setSize(520, 20);
						c.add(lineLabel3);

			// 説明文のパネル
			JPanel rulePanel = new JPanel();
			rulePanel.setLayout(new BoxLayout(rulePanel, BoxLayout.Y_AXIS)); // 縦に自動で並ぶ
			JLabel label1 = new JLabel("< ハンデレベル >");
			JLabel label2 = new JLabel(" ハンデレベルに応じて、以下のハンデを適用します.");
			JLabel label3 = new JLabel("　レベル0：ハンデなし");
			JLabel label4 = new JLabel("　レベル1：石の数が同等の場合黒の勝ちとする");
			JLabel label5 = new JLabel("　レベル2：左上隅に黒石を置く");
			JLabel label6 = new JLabel("　レベル3：左上隅と右下隅に黒石を置く");
			JLabel label7 = new JLabel("　レベル4：左上隅，右下隅，右上隅に黒石を置く");
			JLabel label8 = new JLabel("　レベル5：４か所全ての隅に黒石を置く");
			JLabel label9 = new JLabel("< ハンデのルール >");
			JLabel label10 = new JLabel("対局を行うプレイヤー同士が希望するハンデレベルの差分を取って,");
			JLabel label11 = new JLabel("レベルが高い方に差分のハンデを適応します.");
			JLabel label12 = new JLabel("(例) プレイヤーA の希望ハンデレベル → 3");
			JLabel label13 = new JLabel("       プレイヤーB の希望ハンデレベル → 1   の場合,");
			JLabel label14 = new JLabel("       ハンデレベル 2 がプレイヤーA に適用されます.");

			rulePanel.add(Box.createRigidArea(new Dimension(10, 10))); // コンポーネント間の間隔
			rulePanel.add(label1);
			rulePanel.add(label2);
			rulePanel.add(Box.createRigidArea(new Dimension(10, 10))); // コンポーネント間の間隔
			rulePanel.add(label3);
			rulePanel.add(label4);
			rulePanel.add(label5);
			rulePanel.add(label6);
			rulePanel.add(label7);
			rulePanel.add(label8);
			rulePanel.add(Box.createRigidArea(new Dimension(10, 25))); // コンポーネント間の間隔
			rulePanel.add(label9);
			rulePanel.add(label10);
			rulePanel.add(label11);
			rulePanel.add(Box.createRigidArea(new Dimension(10, 10))); // コンポーネント間の間隔
			rulePanel.add(label12);
			rulePanel.add(label13);
			rulePanel.add(label14);

			rulePanel.setBackground(Color.white);// 背景は白
			rulePanel.setLocation(20, 110);
			rulePanel.setSize(500, 300);
			c.add(rulePanel);

			// OKボタン
			// okButton.setBounds(230, 520, 60, 20);
			okButton = new JButton("OK");
			okButton.setLocation(170, 420);
			okButton.setSize(60, 20);
			okButton.addActionListener(new myListener2());
			c.add(okButton);

			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setTitle("ログイン画面");// ウィンドウのタイトル
			setSize(460, 500); // ログイン画面の大きさ
			setVisible(true); // ログイン画面を表示

		}
		
		// true を与えるとボタンが押せるようになり、false を与えるとボタンを押せなくなるメソッド
		public void allEnabled(boolean f) {
			nameTF.setEditable(f);
			nameButton.setEnabled(f);
			okButton.setEnabled(f);
			handCombobox.setEnabled(f);
		}

		// 名前消去ボタンが押されたとき
		public class myListener1 implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				nameTF.setText("");
			}
		}

		// OKボタンが押されたとき
		public class myListener2 implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				String cmd = nameTF.getText(); // テキストフィールドに入力されている文字列を取得

				// テキストフィールドに何も入力されていない場合
				if (cmd.equals("") == true) {
					// 専用のメッセージを表示
					messageLabel.setForeground(Color.red);
					messageLabel.setText("ログイン名が入力されていません");
				}
				// テキストフィールドに名前が入力されている場合
				else {
					messageLabel.setForeground(Color.black);
					System.out.println("待機中");
					allEnabled(false); // ボタンの反応をoffにする
					messageLabel.setText("待機中...");
					messageLabel.paintImmediately(messageLabel.getVisibleRect());// 「待機中...」を強制表示

					player.setName(cmd); // 名前をセット
					player.setHopeHand(handCombobox.getSelectedIndex());// 希望ハンデレベルをセット
					// サーバに繋がらなかった場合
					if (connectServer(serverIP, port, player.getHopeHand()) == false) {
						messageLabel.setForeground(Color.red);
						messageLabel.setText("接続に失敗しました。もう一度ログインしてください。");
						allEnabled(true);
					}
					// サーバに繋がった場合
					else {
						
					}
					
				}
			}

		}
	}

}


