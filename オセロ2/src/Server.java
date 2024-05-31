import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	private int port; // サーバの待ち受けポート
	private boolean[] online = new boolean[2]; // 接続状態管理用配列
	private PrintWriter[] out = new PrintWriter[2]; // データ送信用オブジェクト
	private Receiver[] receiver = new Receiver[2]; // データ受信用オブジェクト（内部クラスReceiverのオブジェクト）
	private int[] hopeHand = new int[2];
	private int handLevel;

	// コンストラクタ
	public Server(int port) { // 待ち受けポートを引数とする
		this.port = port; // 待ち受けポートを渡す
		online[0] = false; // クライアント0,1との接続は、コンストラクタを呼ぶ時点ではまだないため、どちらも false
		online[1] = false;
		hopeHand[0] = 0; // 両者の希望ハンデレベルを0で初期化する
		hopeHand[1] = 0;
		handLevel = 0;
	}

	// 内部クラス Receiver（データ受信用スレッドを定義する）
	class Receiver extends Thread {
		private InputStreamReader sisr; // 受信データ用文字ストリーム
		private BufferedReader br; // 文字ストリーム用のバッファ
		private int playerNo; // プレイヤ自身を識別するための番号（0か1）
		private int AnoPlayerNo; // もう片方のプレイヤを識別するための番号（0か1）

		// 内部クラスReceiverのコンストラクタ
		Receiver(Socket socket, int playerNo) {
			try {
				this.playerNo = playerNo; // プレイヤを識別するための番号を割り振る（0か1）
				AnoPlayerNo = 1 - playerNo; // 自分の識別番号が0の場合相手の識別番号は1、また自分の識別番号が1の場合相手の識別番号は0
				online[playerNo] = true;
				sisr = new InputStreamReader(socket.getInputStream());
				br = new BufferedReader(sisr);
				System.out.println("クライアント" + playerNo + " からデータを受信する準備ができました。");
				hopeHand[playerNo] = Integer.parseInt(br.readLine());
				System.out.println("クライアント" + playerNo + " から希望ハンデレベルを受け取りました");
			} catch (IOException e) {
				System.err.println("データ受信時にエラーが発生しました: " + e);
				online[playerNo] = false;
			}
		}

		// 内部クラス Receiverのrunメソッド(start によりスレッドが動く)
		public void run() {
			try {
				System.out.println("サーバの、クライアント" + playerNo +"からデータを受信するスレッドが動きました");
				String inputLine;
				while (true) {// データを受信し続ける
					inputLine = br.readLine();// データを一行分読み込む
					
					if(inputLine.equals("GameSet")) {	//ゲーム終了のメッセージを受け取ったら
						System.out.println("クライアント" + playerNo + "からメッセージGameSetが届きました．クライアント" + playerNo + "との接続を切ります.");
						// 接続状態をfalseにする
						online[playerNo] = false;
						// データ受信ストリームを閉じる（br, sisr は内部クラスReceiver のフィールド）
						br.close();
						sisr.close();
						//ループを抜けてThreadを終了
						break;
					}
					
					if (inputLine != null) { // データを受信したら
						System.out.println("クライアント" + playerNo + "からメッセージ " + inputLine + " が届きました．クライアント" + AnoPlayerNo
								+ "へ転送します.");
						out[AnoPlayerNo].println(inputLine); // 受信データを対戦相手クライアントのバッファに書き出す
						out[AnoPlayerNo].flush(); // 受信データを対戦相手へ転送する
					}
				}
			} catch (IOException e) { // 接続が切れたとき
				System.err.println("クライアント" + playerNo + "との接続が切れました．");
				online[playerNo] = false;
				// 対戦相手へ文字列 "disconnect" を転送する
				out[AnoPlayerNo].println("disconnect"); // 受信データを対戦相手クライアントのバッファに書き出す
				out[AnoPlayerNo].flush(); // 受信データを対戦相手へ転送する
			}
		}
	}

	// ハンデの折衝を行い、両方のクライアントへ決まったハンデレベル、コマの色を送信するメソッド
	public void decideHand() {
		if (hopeHand[0] >= hopeHand[1]) {
			handLevel = hopeHand[0] - hopeHand[1];
			out[0].println(handLevel + "\n" + "black");
			out[0].flush();
			out[1].println(handLevel + "\n" + "white");
			out[1].flush();
		} else {
			handLevel = hopeHand[1] - hopeHand[0];
			out[0].println(handLevel + "\n" + "white");
			out[0].flush();
			out[1].println(handLevel + "\n" + "black");
			out[1].flush();
		}
		System.out.println("両方のクライアントへハンデ情報と先手後手情報を送りました");

	}

	// 2つのクライアントの接続を行う(サーバを起動する)メソッド acceptClient
	public void acceptClient() {
		try {
			System.out.println("サーバが起動しました．");
			ServerSocket ss = new ServerSocket(port); // サーバソケットを用意

			// ここから無限ループを使うかは後で検討
			// 1つめのクライアントと接続
			Socket socket0 = ss.accept(); // 1つ目のクライアントから新規接続を受け付けるまでプログラムは停止する
			System.out.println("クライアント0と接続しました．");
			out[0] = new PrintWriter(socket0.getOutputStream(), true);// データ送信オブジェクトを用意
			receiver[0] = new Receiver(socket0, 0);// データ受信オブジェクト(スレッド)を用意

			// 2つめのクライアントと接続
			Socket socket1 = ss.accept(); // 2つ目のクライアントから新規接続を受け付けるまでプログラムは停止する
			System.out.println("クライアント1と接続しました．");
			out[1] = new PrintWriter(socket1.getOutputStream(), true);// データ送信オブジェクトを用意
			receiver[1] = new Receiver(socket1, 1);// データ受信オブジェクト(スレッド)を用意

			decideHand(); // ハンデレベルを決定し、両者に送る
			receiver[0].start(); // クライアント0からのデータ送信オブジェクト(スレッド)を起動
			receiver[1].start(); // クライアント1からのデータ送信オブジェクト(スレッド)を起動
			
			
			
			receiver[0].join();	//receiverの両Threadが終わるまで待機。
			receiver[1].join();
			
			//ソケットを閉じる
			socket0.close();
			socket1.close();
			ss.close();
			
			//サーバのオブジェクトを初期化し、クライアントと再接続を行う
			reset();

		} catch (Exception e) {
			System.err.println("ソケット作成時にエラーが発生しました: " + e);
		}
	}
	
	//サーバオブジェクトの初期化するメソッド（実際は初期化していない）
	public void reset() {
		
		//各フィールド変数の初期化
		for(int i=0;i<2;i++) {
			hopeHand[i] = 0;
			out[i] = null;
			receiver[i] = null;
		}

		handLevel = 0;
		
		//クライアントとの接続を開始
		System.out.println("サーバを再起動します");
		acceptClient();
	}

	// mainメソッド
	public static void main(String[] args) {
		Server server = new Server(10000); // 待ち受けポート10000番でサーバオブジェクトを準備
		server.acceptClient(); // クライアント受け入れを開始
	}
}