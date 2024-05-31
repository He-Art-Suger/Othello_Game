
//パッケージのインポート
import java.awt.Container;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

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
	private boolean highlight; // 強調表示の有無
	private Result result;

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
	public void connectServer(String ipAddress, int port, int hopeHand) {
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

			// 自分の色が黒の場合
			if (player.getColor().equals("black")) {
				player.setOppColor("white");
				colorLabel.setText(player.getName()+" さんの色は黒です");
			}
			// 自分の色が白の場合
			else {
				player.setOppColor("black");
				colorLabel.setText(player.getName()+" さんの色は白です");
			}
			
			//ハンデ説明の出力
			printHand();

			// 盤面を更新
			updateDisp();

			// 受信用オブジェクトのスレッドを起動する
			receiver.start();
		} catch (UnknownHostException e) {
			System.err.println("ホストのIPアドレスが判定できません: " + e);
			System.exit(-1);
		} catch (IOException e) {
			System.err.println("サーバ接続時にエラーが発生しました: " + e);
			System.exit(-1);
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

				// ハンデレベルを受け取るための無限ループ
				String handLevel;
				while (true) {
					handLevel = br.readLine();// 受信データを一行分読み込む
					if (handLevel != null) {// データを受信したら
						System.out.println("クライアントはハンデレベル" + handLevel + "を受信しました");
						game.setHandLevel(handLevel);
						break;
					}
				}
				// 先手後手情報を受け取るための無限ループ
				String myColor;
				while (true) {
					myColor = br.readLine();// 受信データを一行分読み込む
					if (myColor != null) {// データを受信したら
						System.out.println("クライアントは自分の色が " + myColor + " であると受信しました");
						player.setColor(myColor);
						break;
					}

				}
			} catch (IOException e) {
				System.err.println("データ受信時にエラーが発生しました: " + e);
			}
		}

		// 内部クラス Receiverのメソッド
		public void run() {
			try {
				String inputLine;
				while (true) {// データを受信し続ける
					inputLine = br.readLine();// 受信データを一行分読み込む
					if (inputLine != null) {// データを受信したら
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
			handLabel.setText("ハンデ：石の数が同等の場合黒の勝ちとする");
		}
		// ハンデレベル2
		if (game.getHandLevel().equals("2")) {
			handLabel.setText("ハンデ：左上隅に黒石を置く");
		}
		// ハンデレベル3
		if (game.getHandLevel().equals("3")) {
			handLabel.setText("ハンデ：左上隅と右下隅に黒石を置く");
		}
		// ハンデレベル4
		if (game.getHandLevel().equals("4")) {
			handLabel.setText("ハンデ：左上隅，右下隅，右上隅に黒石を置く");
		}
		// ハンデレベル5
		if (game.getHandLevel().equals("5")) {
			handLabel.setText("ハンデ：４か所全ての隅に黒石を置く");
		}
		else;
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

		// 自分の手番の場合
		if (game.getTurn().equals(player.getColor())) {
			turnLabel.setText(player.getName()+" さんの番です.");
		}
		// 相手の手番の場合
		else if (game.getTurn().equals(player.getOppColor())) {
			turnLabel.setText("相手の番です.");
		}

		// 決着が付いた場合、勝敗情報を表示する
		else {
			
			int judge;//0 = 引き分け, 1 = 勝ち, 2 = 負け
			
			if(game.count("black") > game.count("white"))
			{
				if(player.getColor().equals("black"))
				{
					judge = 1;
					System.out.println("black win, and you are black.");
				}
				else
				{
					judge = 2;
					System.out.println("black win, and you are white.");
				}
			}
			else if(game.count("black") < game.count("white"))
			{
				if(player.getColor().equals("white"))
				{
					judge = 1;
					System.out.println("white win, and you are white.");
				}
				else
				{
					judge = 2;
					System.out.println("white win, and you are black.");
				}
			}
			else
			{
				if(player.getDrawFlag() == true)
				{
					if(player.getColor().equals("black"))
					{
						judge = 1;
						System.out.println("draw, drawflag is true, and you are black.");
					}
					else
					{
						judge = 2;
						System.out.println("draw, drawflag is true, and you are white.");
					}
				}
				else
				{
					judge = 0;
					System.out.println("draw, and drawflag is false.");
				}
				
			}
			
			System.out.println(judge);
			result = new Result("勝敗結果", player.getColor() ,judge ,game.count("black") ,game.count("white"));
			result.c.setVisible(true);
			
			int myNum = game.count(player.getColor());
			int oppNum = game.count(player.getOppColor());
			// 自分のコマの方が多い場合
			if (myNum > oppNum) {
				turnLabel.setText("あなたの勝ちです.");
			}
			// 相手のコマの方が多い場合
			else if (myNum < oppNum) {
				turnLabel.setText("あなたの負けです.");
			}
			// コマ数が同じ場合
			else {
				// ハンデレベルが1の場合
				if (game.getHandLevel().equals("1")) {
					// 自分が黒の場合
					if (player.getColor().equals("black")) {
						turnLabel.setText("黒：" + myNum + " 白：" + myNum + " あなたの勝ちです");
					}
					// 自分が白の場合
					else {
						turnLabel.setText("黒：" + myNum + " 白：" + myNum + " あなたの負けです");
					}

				}
				// ハンデレベルが1でない場合
				else {
					turnLabel.setText("引き分けです");
				}
			}
		}

		System.out.println("盤面を更新しました");

	}

	// サーバから操作情報を受信したときに使われるメソッドreceiveMessage
	public void receiveMessage(String msg) {
		System.out.println("サーバからメッセージ " + msg + " を受信しました"); // テスト用標準出力

		// 指定したマスにコマを置き、盤面情報を更新する
		game.putStone(Integer.parseInt(msg), game.getTurn());
		updateDisp();

		System.out.println("現在の手番は" + game.getTurn() + "です");
		turnLabel.setText("あなたの手番です.");

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
		else if (((player.getColor().equals(game.getTurn())) == true)
				&& (game.putStone(Integer.parseInt(command), game.getTurn())) == true) {
			// 盤面情報の更新
			updateDisp();

			// 操作情報をサーバに送る
			sendMessage(command);
		}

		else {

		}
		System.out.println("現在の手番は" + game.getTurn() + "です");
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
		// 名前の入力
		String myName = JOptionPane.showInputDialog(null, "名前を入力してください", "名前の入力", JOptionPane.QUESTION_MESSAGE);
		if (myName.equals("")) {
			myName = "No name";// 名前がないときは，"No name"とする
		}
		// 希望ハンデレベルの選択
		String selectvalues[] = { "0", "1", "2", "3", "4", "5" };
		Object hopeHand = JOptionPane.showInputDialog(null, "希望のハンデレベルを入力してください", "ハンデレベルの入力",
				JOptionPane.INFORMATION_MESSAGE, null, selectvalues, selectvalues[0]);

		// プレイヤオブジェクトの用意
		Player player = new Player();
		player.setName(myName); // 名前をセット
		player.setHopeHand(Integer.parseInt(hopeHand.toString()));// 希望ハンデレベルをセット

		// オセロオブジェクトの用意
		Othello game = new Othello();

		// クライアントオブジェクトの用意
		Client oclient = new Client(game, player); // 引数としてオセロオブジェクト、プレイヤオブジェクトを渡す
		oclient.setVisible(true);
		oclient.connectServer(args[0], 10000, player.getHopeHand());// サーバ接続（サーバのIPアドレス、ポート番号、希望ハンデレベルを与える）
	}
}